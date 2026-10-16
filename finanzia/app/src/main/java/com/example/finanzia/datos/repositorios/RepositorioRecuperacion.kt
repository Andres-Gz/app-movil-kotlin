package com.example.finanzia.datos.repositorios

/**
 * Contrato para recuperar la contraseña: quien lo usa no sabe qué servicio hay detrás.
 * Primero se manda un enlace al correo; al abrirlo, la app queda con una sesión temporal
 * con la que se puede cambiar la contraseña.
 */
interface RepositorioRecuperacion {
    suspend fun enviarEnlace(correo: String): ResultadoRecuperacion
    suspend fun cambiarContrasena(contrasena: String): ResultadoRecuperacion
}
