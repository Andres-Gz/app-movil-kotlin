package com.example.finanzia.pantallas.inicio

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanzia.R
import com.example.finanzia.datos.repositorios.ErrorDatos
import com.example.finanzia.datos.repositorios.RepositorioInicio
import com.example.finanzia.datos.repositorios.ResultadoInicio
import kotlinx.coroutines.launch

/** Pide a Supabase el resumen del inicio y guarda si los montos se ven u ocultan. */
class InicioViewModel(
    private val repositorio: RepositorioInicio,
) : ViewModel() {

    var estado by mutableStateOf(EstadoInicio())
        private set

    /** Carga (o recarga) el resumen. Lo que ya se mostraba se queda mientras llega el nuevo. */
    fun cargar() {
        if (estado.cargando) return
        estado = estado.copy(cargando = true, error = null)
        viewModelScope.launch {
            estado = when (val resultado = repositorio.obtenerResumen()) {
                is ResultadoInicio.Exitoso -> estado.copy(cargando = false, resumen = resultado.resumen)
                is ResultadoInicio.Fallido -> estado.copy(
                    cargando = false,
                    error = when (resultado.error) {
                        ErrorDatos.SIN_SESION -> R.string.error_sesion_vencida
                        ErrorDatos.SIN_CONEXION -> R.string.error_sin_conexion
                        ErrorDatos.DESCONOCIDO -> R.string.error_inicio_no_cargado
                    },
                )
            }
        }
    }

    fun alAlternarMontos() {
        estado = estado.copy(montosVisibles = !estado.montosVisibles)
    }
}
