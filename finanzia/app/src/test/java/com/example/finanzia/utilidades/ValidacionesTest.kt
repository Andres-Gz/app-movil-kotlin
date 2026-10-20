package com.example.finanzia.utilidades

import com.example.finanzia.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidacionesTest {

    @Test
    fun correoVacio_devuelveErrorDeCorreoVacio() {
        assertEquals(R.string.error_correo_vacio, validarCorreo("   "))
    }

    @Test
    fun correoSinArroba_devuelveErrorDeFormato() {
        assertEquals(R.string.error_correo_invalido, validarCorreo("ana.correo.com"))
    }

    @Test
    fun correoSinDominio_devuelveErrorDeFormato() {
        assertEquals(R.string.error_correo_invalido, validarCorreo("ana@correo"))
    }

    @Test
    fun correoValidoConEspacios_noDevuelveError() {
        assertNull(validarCorreo("  ana@correo.com "))
    }

    @Test
    fun requisitos_marcanCadaUnoPorSeparado() {
        assertEquals(RequisitosContrasena(longitud = false, numero = false, mayuscula = false, simbolo = false), revisarRequisitos(""))
        assertEquals(RequisitosContrasena(longitud = true, numero = false, mayuscula = false, simbolo = false), revisarRequisitos("abcdefgh"))
        assertEquals(RequisitosContrasena(longitud = false, numero = true, mayuscula = true, simbolo = true), revisarRequisitos("Ab1!"))
    }

    @Test
    fun losBasicosNoPidenSimboloYTodosSi() {
        assertTrue(revisarRequisitos("Secreta12").cumpleBasicos)
        assertFalse(revisarRequisitos("Secreta12").cumpleTodos)
        assertTrue(revisarRequisitos("Secreta12!").cumpleTodos)
    }

    @Test
    fun unEspacioNoCuentaComoSimbolo() {
        assertFalse(revisarRequisitos("Secreta 12").simbolo)
    }
}
