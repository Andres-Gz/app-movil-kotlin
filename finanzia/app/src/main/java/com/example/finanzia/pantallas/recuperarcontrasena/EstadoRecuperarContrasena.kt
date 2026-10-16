package com.example.finanzia.pantallas.recuperarcontrasena

import androidx.annotation.StringRes
import com.example.finanzia.componentes.OrigenError

/** Todo lo que muestra la pantalla de recuperar contraseña (paso 1: pedir el enlace al correo). */
data class EstadoRecuperarContrasena(
    val usuario: String = "",
    val correo: String = "",
    val cargando: Boolean = false,
    @param:StringRes val errorUsuario: Int? = null,
    @param:StringRes val errorCorreo: Int? = null,
    /** Mensaje del aviso rojo que aparece sobre los campos. */
    @param:StringRes val errorGeneral: Int? = null,
    /** De dónde vienen los errores que se muestran: cambia cómo se pintan. */
    val origenError: OrigenError = OrigenError.FORMULARIO,
    /** Se pone en `true` cuando Supabase ya mandó el enlace al correo. */
    val enlaceEnviado: Boolean = false,
)
