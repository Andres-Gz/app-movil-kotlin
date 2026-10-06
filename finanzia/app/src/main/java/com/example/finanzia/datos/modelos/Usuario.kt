package com.example.finanzia.datos.modelos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Fila de la tabla `usuarios` (equivale a una @Entity de Spring).
 * La contraseña no se mapea: el inicio de sesión lo hace Supabase Auth.
 */
@Serializable
data class Usuario(
    @SerialName("id_usuario") val idUsuario: Long,
    val nombre: String,
    val correo: String,
)
