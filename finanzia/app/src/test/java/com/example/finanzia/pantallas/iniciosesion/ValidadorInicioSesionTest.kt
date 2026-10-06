package com.example.finanzia.pantallas.iniciosesion

import com.example.finanzia.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidadorInicioSesionTest {

    @Test
    fun correoVacio_devuelveErrorDeCorreoVacio() {
        assertEquals(R.string.error_correo_vacio, ValidadorInicioSesion.validarCorreo("   "))
    }

    @Test
    fun correoSinArroba_devuelveErrorDeFormato() {
        assertEquals(R.string.error_correo_invalido, ValidadorInicioSesion.validarCorreo("ana.correo.com"))
    }

    @Test
    fun correoSinDominio_devuelveErrorDeFormato() {
        assertEquals(R.string.error_correo_invalido, ValidadorInicioSesion.validarCorreo("ana@correo"))
    }

    @Test
    fun correoValidoConEspacios_noDevuelveError() {
        assertNull(ValidadorInicioSesion.validarCorreo("  ana@correo.com "))
    }

    @Test
    fun contrasenaVacia_devuelveError() {
        assertEquals(R.string.error_contrasena_vacia, ValidadorInicioSesion.validarContrasena(""))
    }

    @Test
    fun contrasenaConTexto_noDevuelveError() {
        assertNull(ValidadorInicioSesion.validarContrasena("secreta123"))
    }
}
