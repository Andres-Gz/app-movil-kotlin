package com.example.finanzia.datos.repositorios

/** Respuesta de un intento de crear una cuenta. */
sealed interface ResultadoRegistro {
    /** La cuenta se creó. Si [necesitaConfirmacion], el usuario debe abrir el correo que le llegó. */
    data class Exitoso(val necesitaConfirmacion: Boolean) : ResultadoRegistro
    data class Fallido(val error: ErrorRegistro) : ResultadoRegistro
}
