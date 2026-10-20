package com.example.finanzia.pantallas.metas

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanzia.R
import com.example.finanzia.datos.modelos.Frecuencia
import com.example.finanzia.datos.modelos.Meta
import com.example.finanzia.datos.repositorios.ErrorDatos
import com.example.finanzia.datos.repositorios.RepositorioMetas
import com.example.finanzia.datos.repositorios.ResultadoMeta
import com.example.finanzia.datos.repositorios.ResultadoMetas
import com.example.finanzia.utilidades.limpiarMonto
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Pide a Supabase las metas del usuario, guarda cómo se ordenan y atiende lo que se hace sobre
 * cada meta: aportar, ajustar su regla y eliminarla. [hoy] se puede cambiar en las pruebas.
 */
class MetasViewModel(
    private val repositorio: RepositorioMetas,
    private val hoy: () -> Calendar = { Calendar.getInstance() },
) : ViewModel() {

    var estado by mutableStateOf(EstadoMetas())
        private set

    /** Carga (o recarga) las metas. Lo que ya se mostraba se queda mientras llega lo nuevo. */
    fun cargar() {
        if (estado.cargando) return
        estado = estado.copy(cargando = true, error = null)
        viewModelScope.launch {
            estado = when (val resultado = repositorio.obtenerResumen()) {
                is ResultadoMetas.Exitoso -> estado.copy(cargando = false, resumen = resultado.resumen)
                is ResultadoMetas.Fallido -> estado.copy(
                    cargando = false,
                    error = when (resultado.error) {
                        ErrorDatos.SIN_SESION -> R.string.error_sesion_vencida
                        ErrorDatos.SIN_CONEXION -> R.string.error_sin_conexion
                        ErrorDatos.DESCONOCIDO -> R.string.error_metas_no_cargadas
                    },
                )
            }
        }
    }

    fun alAbrirOrden() {
        estado = estado.copy(ordenAbierto = true)
    }

    fun alCerrarOrden() {
        estado = estado.copy(ordenAbierto = false)
    }

    fun alElegirOrden(orden: OrdenMetas) {
        estado = estado.copy(orden = orden, ordenAbierto = false)
    }

    fun alAbrirMenu(meta: Meta) {
        estado = estado.copy(menuDe = meta.idMeta)
    }

    fun alCerrarMenu() {
        estado = estado.copy(menuDe = null)
    }

    /** Abre "Aportar" con el monto de la regla ya escrito, si la meta tiene. */
    fun alPedirAporte(meta: Meta) {
        abrir(DialogoMeta.Aportar(meta), monto = meta.montoAporte?.toLong())
    }

    /** Abre "Ajustar regla" con la regla actual o, si no tiene, con el aporte que haría falta. */
    fun alPedirAjusteRegla(meta: Meta) {
        val frecuencia = CalculosMetas.frecuencia(meta)?.takeIf { it == Frecuencia.QUINCENAL || it == Frecuencia.MENSUAL }
            ?: Frecuencia.QUINCENAL
        val monto = meta.montoAporte?.toLong() ?: CalculosMetas.aporteSugerido(meta, frecuencia, hoy())
        abrir(DialogoMeta.AjustarRegla(meta), monto = monto, frecuencia = frecuencia)
    }

    fun alPedirEliminar(meta: Meta) {
        abrir(DialogoMeta.Eliminar(meta), monto = null)
    }

    fun alCambiarMontoDialogo(texto: String) {
        estado = estado.copy(montoDialogo = limpiarMonto(texto), errorDialogo = null)
    }

    /** Escribe un monto de los botones ("Tu regla", "Lo que falta", "Usar…"). */
    fun alUsarMonto(monto: Long) {
        estado = estado.copy(montoDialogo = limpiarMonto(monto.toString()), errorDialogo = null)
    }

    fun alElegirFrecuenciaDialogo(frecuencia: Frecuencia) {
        estado = estado.copy(frecuenciaDialogo = frecuencia)
    }

    fun alCerrarDialogo() {
        if (estado.guardando) return
        estado = estado.copy(dialogo = null, errorDialogo = null)
    }

    /** Guarda lo que pide la ventana abierta y, si sale bien, la cierra, avisa y recarga las metas. */
    fun alConfirmarDialogo() {
        val dialogo = estado.dialogo ?: return
        if (estado.guardando) return
        val monto = estado.montoDialogo.toLongOrNull() ?: 0L
        if (dialogo !is DialogoMeta.Eliminar && monto <= 0L) {
            estado = estado.copy(errorDialogo = R.string.error_meta_aporte_vacio)
            return
        }
        val frecuencia = estado.frecuenciaDialogo
        estado = estado.copy(guardando = true, errorDialogo = null)
        viewModelScope.launch {
            val id = dialogo.meta.idMeta
            val resultado = when (dialogo) {
                is DialogoMeta.Aportar -> repositorio.aportar(id, monto)
                is DialogoMeta.AjustarRegla -> repositorio.ajustarRegla(id, monto, frecuencia)
                is DialogoMeta.Eliminar -> repositorio.eliminarMeta(id)
            }
            when (resultado) {
                is ResultadoMeta.Exitoso -> {
                    estado = estado.copy(guardando = false, dialogo = null, aviso = avisoDe(dialogo, monto))
                    cargar()
                }
                is ResultadoMeta.Fallido -> estado = estado.copy(guardando = false, errorDialogo = mensajeDe(resultado.error))
            }
        }
    }

    /** La pantalla ya mostró el aviso. */
    fun alAvisoMostrado() {
        estado = estado.copy(aviso = null)
    }

    private fun abrir(dialogo: DialogoMeta, monto: Long?, frecuencia: Frecuencia = estado.frecuenciaDialogo) {
        estado = estado.copy(
            menuDe = null,
            dialogo = dialogo,
            montoDialogo = monto?.toString().orEmpty(),
            frecuenciaDialogo = frecuencia,
            errorDialogo = null,
        )
    }

    @StringRes
    private fun avisoDe(dialogo: DialogoMeta, monto: Long): Int = when (dialogo) {
        is DialogoMeta.Aportar ->
            if (dialogo.meta.ahorrado < dialogo.meta.objetivo && dialogo.meta.ahorrado + monto >= dialogo.meta.objetivo) {
                R.string.metas_aviso_lograda
            } else {
                R.string.metas_aviso_aporte
            }
        is DialogoMeta.AjustarRegla -> R.string.metas_aviso_regla
        is DialogoMeta.Eliminar -> R.string.metas_aviso_eliminada
    }

    @StringRes
    private fun mensajeDe(error: ErrorDatos): Int = when (error) {
        ErrorDatos.SIN_SESION -> R.string.error_sesion_vencida
        ErrorDatos.SIN_CONEXION -> R.string.error_sin_conexion
        ErrorDatos.DESCONOCIDO -> R.string.error_meta_cambio_no_guardado
    }
}
