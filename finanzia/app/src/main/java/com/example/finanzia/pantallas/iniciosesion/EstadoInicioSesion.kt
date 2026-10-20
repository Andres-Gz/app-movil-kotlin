package com.example.finanzia.pantallas.iniciosesion

import androidx.annotation.StringRes
import com.example.finanzia.componentes.OrigenError
import com.example.finanzia.datos.modelos.Usuario

/** Todo lo que muestra la pantalla de inicio de sesión. */
data class EstadoInicioSesion(
    val correo: String = "",
    val contrasena: String = "",
    val contrasenaVisible: Boolean = false,
    val cargando: Boolean = false,
    @param:StringRes val errorCorreo: Int? = null,
    @param:StringRes val errorContrasena: Int? = null,
    /** Mensaje del aviso rojo que aparece sobre los campos. */
    @param:StringRes val errorGeneral: Int? = null,
    /** De dónde vienen los errores que se muestran: cambia cómo se pintan. */
    val origenError: OrigenError = OrigenError.FORMULARIO,
    /** Se llena cuando el inicio de sesión sale bien. */
    val usuario: Usuario? = null,
)
