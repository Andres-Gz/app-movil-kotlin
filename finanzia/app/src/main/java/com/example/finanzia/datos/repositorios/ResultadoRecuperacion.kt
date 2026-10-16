package com.example.finanzia.datos.repositorios

/** Respuesta de un paso de la recuperación de contraseña. */
sealed interface ResultadoRecuperacion {
    data object Exitoso : ResultadoRecuperacion
    data class Fallido(val error: ErrorRecuperacion) : ResultadoRecuperacion
}

/** Por qué no se pudo seguir con la recuperación. */
enum class ErrorRecuperacion {
    DEMASIADOS_INTENTOS,
    CORREO_RECHAZADO,
    SIN_SESION,
    MISMA_CONTRASENA,
    CONTRASENA_DEBIL,
    SIN_CONEXION,
    DESCONOCIDO,
}
