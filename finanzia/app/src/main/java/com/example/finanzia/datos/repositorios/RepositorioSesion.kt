package com.example.finanzia.datos.repositorios

/** Contrato para manejar la sesión: quien lo usa no sabe qué servicio hay detrás. */
interface RepositorioSesion {
    suspend fun iniciarSesion(correo: String, contrasena: String): ResultadoSesion
}
