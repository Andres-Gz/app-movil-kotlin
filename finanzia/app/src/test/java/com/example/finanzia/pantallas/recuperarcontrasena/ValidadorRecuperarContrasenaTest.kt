package com.example.finanzia.pantallas.recuperarcontrasena

import com.example.finanzia.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidadorRecuperarContrasenaTest {

    @Test
    fun usuarioEnBlanco_devuelveError() {
        assertEquals(R.string.error_usuario_vacio, ValidadorRecuperarContrasena.validarUsuario("  "))
    }

    @Test
    fun usuarioConTexto_noDevuelveError() {
        assertNull(ValidadorRecuperarContrasena.validarUsuario("carlos_mendoza"))
    }

    @Test
    fun formularioConUnCampoVacio_devuelveElAvisoDeCamposIncompletos() {
        assertEquals(R.string.error_campos_incompletos, ValidadorRecuperarContrasena.validarFormulario("", "ana@correo.com"))
        assertEquals(R.string.error_campos_incompletos, ValidadorRecuperarContrasena.validarFormulario("ana", " "))
    }

    @Test
    fun formularioLleno_noDevuelveAviso() {
        assertNull(ValidadorRecuperarContrasena.validarFormulario("ana", "ana.correo.com"))
    }
}
