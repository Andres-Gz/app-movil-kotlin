package com.example.finanzia.datos.repositorios

/** Por qué no se pudo crear la cuenta. */
enum class ErrorRegistro {
    CORREO_YA_REGISTRADO,
    CORREO_RECHAZADO,
    CONTRASENA_DEBIL,

    /** Supabase limita cuántos correos de confirmación manda por hora. */
    DEMASIADOS_INTENTOS,
    SIN_CONEXION,
    DESCONOCIDO,
}
