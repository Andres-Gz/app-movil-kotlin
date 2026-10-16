package com.example.finanzia.pantallas.iniciosesion

import com.example.finanzia.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidadorInicioSesionTest {

    @Test
    fun contrasenaVacia_devuelveError() {
        assertEquals(R.string.error_contrasena_vacia, ValidadorInicioSesion.validarContrasena(""))
    }

    @Test
    fun contrasenaConTexto_noDevuelveError() {
        assertNull(ValidadorInicioSesion.validarContrasena("secreta123"))
    }

    @Test
    fun formularioConUnCampoVacio_devuelveElAvisoDeCamposIncompletos() {
        assertEquals(R.string.error_campos_incompletos, ValidadorInicioSesion.validarFormulario("", "secreta123"))
        assertEquals(R.string.error_campos_incompletos, ValidadorInicioSesion.validarFormulario("ana@correo.com", ""))
    }

    @Test
    fun formularioLleno_noDevuelveAviso() {
        assertNull(ValidadorInicioSesion.validarFormulario("ana.correo.com", "secreta123"))
    }
}
