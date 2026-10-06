package com.example.finanzia.pantallas.iniciosesion

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanzia.R
import com.example.finanzia.datos.repositorios.ErrorSesion
import com.example.finanzia.datos.repositorios.RepositorioSesion
import com.example.finanzia.datos.repositorios.ResultadoSesion
import kotlinx.coroutines.launch

/** Mantiene el estado del formulario de inicio de sesión y reacciona a lo que hace el usuario. */
class InicioSesionViewModel(
    private val repositorio: RepositorioSesion,
) : ViewModel() {

    var estado by mutableStateOf(EstadoInicioSesion())
        private set

    fun alCambiarCorreo(correo: String) {
        estado = estado.copy(correo = correo, errorCorreo = null, errorGeneral = null)
    }

    fun alCambiarContrasena(contrasena: String) {
        estado = estado.copy(contrasena = contrasena, errorContrasena = null, errorGeneral = null)
    }

    fun alCambiarRecordarme(recordarme: Boolean) {
        estado = estado.copy(recordarme = recordarme)
    }

    fun alAlternarVisibilidadContrasena() {
        estado = estado.copy(contrasenaVisible = !estado.contrasenaVisible)
    }

    /** Valida el formulario y, si está bien, intenta iniciar sesión. */
    fun alPulsarEntrar() {
        if (estado.cargando) return
        val errorCorreo = ValidadorInicioSesion.validarCorreo(estado.correo)
        val errorContrasena = ValidadorInicioSesion.validarContrasena(estado.contrasena)
        estado = estado.copy(errorCorreo = errorCorreo, errorContrasena = errorContrasena, errorGeneral = null)
        if (errorCorreo != null || errorContrasena != null) return

        estado = estado.copy(cargando = true)
        viewModelScope.launch {
            estado = when (val resultado = repositorio.iniciarSesion(estado.correo.trim(), estado.contrasena)) {
                is ResultadoSesion.Exitoso -> estado.copy(cargando = false, usuario = resultado.usuario)
                is ResultadoSesion.Fallido -> estado.copy(cargando = false, errorGeneral = mensajeDe(resultado.error))
            }
        }
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
