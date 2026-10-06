package com.example.finanzia.pantallas.iniciosesion

import androidx.annotation.StringRes
import com.example.finanzia.datos.modelos.Usuario

/** Todo lo que muestra la pantalla de inicio de sesión. */
data class EstadoInicioSesion(
    val correo: String = "",
    val contrasena: String = "",
    val recordarme: Boolean = false,
    val contrasenaVisible: Boolean = false,
    val cargando: Boolean = false,
    @param:StringRes val errorCorreo: Int? = null,
    @param:StringRes val errorContrasena: Int? = null,
    @param:StringRes val errorGeneral: Int? = null,
    /** Se llena cuando el inicio de sesión sale bien. */
    val usuario: Usuario? = null,
)
