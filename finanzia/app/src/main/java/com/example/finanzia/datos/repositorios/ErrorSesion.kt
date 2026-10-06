package com.example.finanzia.datos.repositorios

/** Por qué no se pudo iniciar sesión. */
enum class ErrorSesion {
    CREDENCIALES_INCORRECTAS,
    CORREO_SIN_CONFIRMAR,
    PERFIL_NO_ENCONTRADO,
    SIN_CONEXION,
    DESCONOCIDO,
}
