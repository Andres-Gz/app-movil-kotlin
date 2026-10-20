package com.example.finanzia.pantallas.primeringreso

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanzia.R
import com.example.finanzia.datos.modelos.Frecuencia
import com.example.finanzia.datos.repositorios.ErrorDatos
import com.example.finanzia.datos.repositorios.RepositorioMovimientos
import com.example.finanzia.datos.repositorios.ResultadoMovimiento
import com.example.finanzia.utilidades.limpiarMonto
import com.example.finanzia.utilidades.validarMonto
import kotlinx.coroutines.launch

/** Recibe el monto y la frecuencia del primer ingreso y lo guarda en Supabase. */
class PrimerIngresoViewModel(
    private val repositorio: RepositorioMovimientos,
) : ViewModel() {

    var estado by mutableStateOf(EstadoPrimerIngreso())
        private set

    fun alCambiarMonto(texto: String) {
        estado = estado.copy(monto = limpiarMonto(texto), errorMonto = null, errorGeneral = null)
    }

    fun alElegirFrecuencia(frecuencia: Frecuencia) {
        estado = estado.copy(frecuencia = frecuencia, errorGeneral = null)
    }

    /** Revisa el monto y, si está bien, guarda el ingreso. */
    fun alPulsarGuardar() {
        if (estado.cargando) return
        val errorMonto = validarMonto(estado.monto)
        estado = estado.copy(errorMonto = errorMonto, errorGeneral = null)
        if (errorMonto != null) return

        estado = estado.copy(cargando = true)
        viewModelScope.launch {
            estado = when (val resultado = repositorio.registrarIngreso(estado.monto.toLong(), estado.frecuencia)) {
                is ResultadoMovimiento.Exitoso -> estado.copy(cargando = false, guardado = true)
                is ResultadoMovimiento.Fallido -> estado.copy(cargando = false, errorGeneral = mensajeDe(resultado.error))
            }
        }
    }

    /** La pantalla ya avisó que se guardó: queda lista por si se vuelve a abrir. */
    fun alAvisarGuardado() {
        estado = EstadoPrimerIngreso()
    }

    @StringRes
    private fun mensajeDe(error: ErrorDatos): Int = when (error) {
        ErrorDatos.SIN_SESION -> R.string.error_sesion_vencida
        ErrorDatos.SIN_CONEXION -> R.string.error_sin_conexion
        ErrorDatos.DESCONOCIDO -> R.string.error_ingreso_no_guardado
    }
}
