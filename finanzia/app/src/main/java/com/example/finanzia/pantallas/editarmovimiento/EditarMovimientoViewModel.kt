package com.example.finanzia.pantallas.editarmovimiento

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanzia.R
import com.example.finanzia.configuracion.Constantes
import com.example.finanzia.datos.modelos.CategoriaGasto
import com.example.finanzia.datos.modelos.MovimientoReciente
import com.example.finanzia.datos.repositorios.ErrorDatos
import com.example.finanzia.datos.repositorios.RepositorioMovimientos
import com.example.finanzia.datos.repositorios.ResultadoMovimiento
import com.example.finanzia.utilidades.limpiarMonto
import com.example.finanzia.utilidades.validarMonto
import kotlinx.coroutines.launch
import kotlin.math.roundToLong

/** Edita o elimina [movimiento] en Supabase. Arranca con sus datos actuales. */
class EditarMovimientoViewModel(
    private val repositorio: RepositorioMovimientos,
    private val movimiento: MovimientoReciente,
) : ViewModel() {

    var estado by mutableStateOf(
        EstadoEditarMovimiento(
            esIngreso = movimiento.tipo == Constantes.BaseDatos.TIPO_INGRESO,
            // La app guarda pesos enteros; si un dato viejo trae centavos, se redondea.
            monto = movimiento.monto.roundToLong().toString(),
            descripcion = movimiento.descripcion ?: movimiento.categoria.orEmpty(),
            categoria = CategoriaGasto.desdeNombre(movimiento.categoria),
        ),
    )
        private set

    fun alCambiarMonto(texto: String) {
        estado = estado.copy(monto = limpiarMonto(texto), errorMonto = null, errorGeneral = null)
    }

    fun alCambiarDescripcion(texto: String) {
        estado = estado.copy(descripcion = texto, errorDescripcion = null, errorGeneral = null)
    }

    fun alElegirCategoria(categoria: CategoriaGasto) {
        estado = estado.copy(categoria = categoria, errorGeneral = null)
    }

    fun alPulsarGuardar() {
        if (estado.cargando) return
        val errorMonto = validarMonto(estado.monto)
        val errorDescripcion = if (estado.descripcion.isBlank()) R.string.error_descripcion_vacia else null
        estado = estado.copy(errorMonto = errorMonto, errorDescripcion = errorDescripcion, errorGeneral = null)
        if (errorMonto != null || errorDescripcion != null) return
        ejecutar(ResultadoEdicion.GUARDADO) {
            repositorio.editarMovimiento(
                id = movimiento.idMovimiento,
                monto = estado.monto.toLong(),
                descripcion = estado.descripcion.trim(),
                categoria = if (estado.esIngreso) null else estado.categoria,
            )
        }
    }

    fun alPulsarEliminar() {
        estado = estado.copy(confirmandoEliminar = true)
    }

    fun alCancelarEliminar() {
        estado = estado.copy(confirmandoEliminar = false)
    }

    fun alConfirmarEliminar() {
        estado = estado.copy(confirmandoEliminar = false)
        if (estado.cargando) return
        ejecutar(ResultadoEdicion.ELIMINADO) { repositorio.eliminarMovimiento(movimiento.idMovimiento) }
    }

    private fun ejecutar(alTerminar: ResultadoEdicion, accion: suspend () -> ResultadoMovimiento) {
        estado = estado.copy(cargando = true)
        viewModelScope.launch {
            estado = when (val resultado = accion()) {
                is ResultadoMovimiento.Exitoso -> estado.copy(cargando = false, resultado = alTerminar)
                is ResultadoMovimiento.Fallido -> estado.copy(
                    cargando = false,
                    errorGeneral = when (resultado.error) {
                        ErrorDatos.SIN_SESION -> R.string.error_sesion_vencida
                        ErrorDatos.SIN_CONEXION -> R.string.error_sin_conexion
                        ErrorDatos.DESCONOCIDO -> R.string.error_movimiento_no_guardado
                    },
                )
            }
        }
    }
}
