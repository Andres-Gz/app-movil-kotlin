package com.example.finanzia.datos.repositorios

import com.example.finanzia.datos.modelos.Usuario

/** Respuesta de un intento de inicio de sesión. */
sealed interface ResultadoSesion {
    data class Exitoso(val usuario: Usuario) : ResultadoSesion
    data class Fallido(val error: ErrorSesion) : ResultadoSesion
}
