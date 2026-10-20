package com.example.finanzia.pantallas.nuevameta

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanzia.R
import com.example.finanzia.configuracion.Constantes
import com.example.finanzia.datos.modelos.CategoriaMeta
import com.example.finanzia.datos.modelos.Frecuencia
import com.example.finanzia.datos.modelos.Meta
import com.example.finanzia.datos.modelos.MetaNueva
import com.example.finanzia.datos.repositorios.ErrorDatos
import com.example.finanzia.datos.repositorios.RepositorioMetas
import com.example.finanzia.datos.repositorios.ResultadoMeta
import com.example.finanzia.pantallas.metas.CalculosMetas
import com.example.finanzia.utilidades.limpiarMonto
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Recibe los datos de una meta nueva (o de una que se edita), calcula el aporte que hace falta para
 * lograrla a tiempo y la guarda. [hoy] se puede cambiar en las pruebas.
 */
class NuevaMetaViewModel(
    private val repositorio: RepositorioMetas,
    private val hoy: () -> Calendar = { Calendar.getInstance() },
) : ViewModel() {

    var estado by mutableStateOf(EstadoNuevaMeta())
        private set

    /** La meta que se edita, o `null` si es nueva. */
    private var original: Meta? = null

    /** Cómo quedó el formulario al abrirlo, para saber si cambió el plan de ahorro. */
    private var inicial = EstadoNuevaMeta()

    /** Deja el formulario vacío para una meta nueva, o con los datos de [meta] para editarla. */
    fun alAbrir(meta: Meta?) {
        original = meta
        estado = if (meta == null) {
            EstadoNuevaMeta()
        } else {
            EstadoNuevaMeta(
                editando = true,
                nombre = meta.nombre,
                categoria = CategoriaMeta.desdeNombre(meta.categoria) ?: CategoriaMeta.VIAJES,
                monto = limpiarMonto(meta.objetivo.toLong().toString()),
                mes = CalculosMetas.mesYAno(meta, hoy())?.let { (mes, ano) -> MesAno(mes, ano) },
                frecuencia = CalculosMetas.frecuencia(meta)?.takeIf { it == Frecuencia.MENSUAL } ?: Frecuencia.QUINCENAL,
            )
        }
        inicial = estado
    }

    fun alCambiarNombre(texto: String) {
        estado = estado.copy(nombre = texto.take(Constantes.Metas.LARGO_MAXIMO_NOMBRE), error = null)
    }

    fun alBorrarNombre() {
        estado = estado.copy(nombre = "")
    }

    fun alElegirCategoria(categoria: CategoriaMeta) {
        estado = estado.copy(categoria = categoria)
    }

    fun alCambiarMonto(texto: String) {
        estado = estado.copy(monto = limpiarMonto(texto), incrementoElegido = null, error = null)
    }

    /** Suma uno de los botones "+$…" al monto. */
    fun alSumarMonto(incremento: Long) {
        val suma = (estado.monto.toLongOrNull() ?: 0L) + incremento
        estado = estado.copy(monto = limpiarMonto(suma.toString()), incrementoElegido = incremento, error = null)
    }

    fun alAbrirSelector() {
        estado = estado.copy(selectorAbierto = true)
    }

    fun alCerrarSelector() {
        estado = estado.copy(selectorAbierto = false)
    }

    fun alElegirMes(mes: MesAno) {
        estado = estado.copy(mes = mes, selectorAbierto = false, error = null)
    }

    fun alElegirFrecuencia(frecuencia: Frecuencia) {
        estado = estado.copy(frecuencia = frecuencia)
    }

    /** Revisa el formulario y, si está completo, guarda la meta con el aporte calculado. */
    fun alPulsarContinuar() {
        if (estado.cargando) return
        val hoy = hoy()
        val error = ValidadorNuevaMeta.validar(estado, hoy)
        estado = estado.copy(error = error, errorDelServidor = false)
        val mes = estado.mes
        if (error != null || mes == null) return

        val meta = MetaNueva(
            nombre = estado.nombre.trim(),
            categoria = estado.categoria,
            objetivo = estado.monto.toLong(),
            fechaLimite = CalculosNuevaMeta.fechaLimite(mes, hoy),
            frecuencia = estado.frecuencia,
            aporte = aporte(mes, hoy),
        )
        val original = original
        estado = estado.copy(cargando = true)
        viewModelScope.launch {
            val resultado = if (original == null) repositorio.crearMeta(meta) else repositorio.editarMeta(original.idMeta, meta)
            estado = when (resultado) {
                is ResultadoMeta.Exitoso -> estado.copy(cargando = false, guardada = true)
                is ResultadoMeta.Fallido -> estado.copy(cargando = false, error = mensajeDe(resultado.error), errorDelServidor = true)
            }
        }
    }

    /** La pantalla ya avisó que se guardó: queda en blanco por si se crea otra. */
    fun alAvisarGuardada() {
        alAbrir(null)
    }

    /**
     * Lo que falta (todo el monto si es nueva) repartido en los aportes que caen hasta el mes elegido.
     * Al editar sin tocar el monto, el mes ni la frecuencia, se conserva la regla que ya tenía.
     */
    private fun aporte(mes: MesAno, hoy: Calendar): Long {
        val original = original
        val reglaActual = original?.montoAporte?.toLong()?.takeIf { it > 0 }
        val mismoPlan = estado.monto == inicial.monto && estado.mes == inicial.mes && estado.frecuencia == inicial.frecuencia
        if (reglaActual != null && mismoPlan) return reglaActual
        val falta = estado.monto.toLong() - (original?.ahorrado ?: 0.0)
        if (falta <= 0) return reglaActual ?: Constantes.Metas.REDONDEO_APORTE
        return CalculosMetas.montoAporte(falta, CalculosNuevaMeta.aportes(mes, estado.frecuencia, hoy))
    }

    @StringRes
    private fun mensajeDe(error: ErrorDatos): Int = when (error) {
        ErrorDatos.SIN_SESION -> R.string.error_sesion_vencida
        ErrorDatos.SIN_CONEXION -> R.string.error_sin_conexion
        ErrorDatos.DESCONOCIDO -> if (original == null) R.string.error_meta_no_guardada else R.string.error_meta_cambio_no_guardado
    }
}
