package com.example.finanzia.pantallas.iniciosesion

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanzia.R
import com.example.finanzia.componentes.OrigenError
import com.example.finanzia.datos.repositorios.ErrorSesion
import com.example.finanzia.datos.repositorios.RepositorioSesion
import com.example.finanzia.datos.repositorios.ResultadoSesion
import com.example.finanzia.utilidades.validarCorreo
import kotlinx.coroutines.launch

/** Mantiene el estado del formulario de inicio de sesión y reacciona a lo que hace el usuario. */
class InicioSesionViewModel(
    private val repositorio: RepositorioSesion,
) : ViewModel() {

    var estado by mutableStateOf(EstadoInicioSesion())
        private set

    fun alCambiarCorreo(correo: String) {
        estado = estadoAlEditar().copy(correo = correo, errorCorreo = null)
    }

    fun alCambiarContrasena(contrasena: String) {
        estado = estadoAlEditar().copy(contrasena = contrasena, errorContrasena = null)
    }

    fun alAlternarVisibilidadContrasena() {
        estado = estado.copy(contrasenaVisible = !estado.contrasenaVisible)
    }

    /** Valida el formulario y, si está bien, intenta iniciar sesión. */
    fun alPulsarEntrar() {
        if (estado.cargando) return
        val errorCorreo = validarCorreo(estado.correo)
        val errorContrasena = ValidadorInicioSesion.validarContrasena(estado.contrasena)
        estado = estado.copy(
            errorCorreo = errorCorreo,
            errorContrasena = errorContrasena,
            errorGeneral = ValidadorInicioSesion.validarFormulario(estado.correo, estado.contrasena),
            origenError = OrigenError.FORMULARIO,
        )
        if (errorCorreo != null || errorContrasena != null) return

        estado = estado.copy(cargando = true)
        viewModelScope.launch {
            estado = when (val resultado = repositorio.iniciarSesion(estado.correo.trim(), estado.contrasena)) {
                is ResultadoSesion.Exitoso -> estado.copy(cargando = false, usuario = resultado.usuario)
                is ResultadoSesion.Fallido -> estado.copy(
                    cargando = false,
                    errorGeneral = mensajeDe(resultado.error),
                    errorContrasena = if (resultado.error == ErrorSesion.CREDENCIALES_INCORRECTAS) {
                        R.string.error_contrasena_no_coincide
                    } else {
                        null
                    },
                    origenError = OrigenError.SERVIDOR,
                )
            }
        }
    }

    /**
     * La pantalla ya avisó que se inició sesión: el formulario queda limpio (sin la contraseña) y,
     * si el usuario vuelve a esta pantalla, no se avisa otra vez.
     */
    fun alAvisarSesionIniciada() {
        estado = EstadoInicioSesion()
    }

    /**
     * Al editar un campo se quita el aviso. Si los errores los dio el servidor, ya no aplican
     * a los datos nuevos y se quitan todos.
     */
    private fun estadoAlEditar(): EstadoInicioSesion = when (estado.origenError) {
        OrigenError.FORMULARIO -> estado.copy(errorGeneral = null)
        OrigenError.SERVIDOR -> estado.copy(errorGeneral = null, errorCorreo = null, errorContrasena = null)
    }

    @StringRes
    private fun mensajeDe(error: ErrorSesion): Int = when (error) {
        ErrorSesion.CREDENCIALES_INCORRECTAS -> R.string.error_credenciales_incorrectas
        ErrorSesion.CORREO_SIN_CONFIRMAR -> R.string.error_correo_sin_confirmar
        ErrorSesion.PERFIL_NO_ENCONTRADO -> R.string.error_perfil_no_encontrado
        ErrorSesion.SIN_CONEXION -> R.string.error_sin_conexion
        ErrorSesion.DESCONOCIDO -> R.string.error_desconocido
    }
}
