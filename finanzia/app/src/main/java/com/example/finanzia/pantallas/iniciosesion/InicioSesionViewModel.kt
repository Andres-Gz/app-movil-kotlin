package com.example.finanzia.pantallas.iniciosesion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/** Mantiene el estado del formulario de inicio de sesión y reacciona a lo que hace el usuario. */
class InicioSesionViewModel : ViewModel() {

    var estado by mutableStateOf(EstadoInicioSesion())
        private set

    fun alCambiarCorreo(correo: String) {
        estado = estado.copy(correo = correo, errorCorreo = null)
    }

    fun alCambiarContrasena(contrasena: String) {
        estado = estado.copy(contrasena = contrasena, errorContrasena = null)
    }

    fun alCambiarRecordarme(recordarme: Boolean) {
        estado = estado.copy(recordarme = recordarme)
    }

    fun alAlternarVisibilidadContrasena() {
        estado = estado.copy(contrasenaVisible = !estado.contrasenaVisible)
    }

    /** Valida el formulario y muestra los errores. Devuelve `true` si se puede iniciar sesión. */
    fun alPulsarEntrar(): Boolean {
        estado = estado.copy(
            errorCorreo = ValidadorInicioSesion.validarCorreo(estado.correo),
            errorContrasena = ValidadorInicioSesion.validarContrasena(estado.contrasena),
        )
        return estado.errorCorreo == null && estado.errorContrasena == null
    }
}
