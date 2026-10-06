package com.example.finanzia.pantallas.iniciosesion

import androidx.annotation.StringRes

/** Todo lo que muestra la pantalla de inicio de sesión. */
data class EstadoInicioSesion(
    val correo: String = "",
    val contrasena: String = "",
    val recordarme: Boolean = false,
    val contrasenaVisible: Boolean = false,
    @param:StringRes val errorCorreo: Int? = null,
    @param:StringRes val errorContrasena: Int? = null,
)
