package com.example.finanzia.datos.repositorios

/** Contrato para crear cuentas: quien lo usa no sabe qué servicio hay detrás. */
interface RepositorioRegistro {
    suspend fun registrarse(nombre: String, correo: String, contrasena: String): ResultadoRegistro
}
