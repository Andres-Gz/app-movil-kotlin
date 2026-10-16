package com.example.finanzia.pantallas.registro

import com.example.finanzia.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidadorRegistroTest {

    @Test
    fun nombreEnBlanco_devuelveError() {
        assertEquals(R.string.error_nombre_vacio, ValidadorRegistro.validarNombre("  "))
    }

    @Test
    fun nombreConTexto_noDevuelveError() {
        assertNull(ValidadorRegistro.validarNombre("Paula Gómez"))
    }

    @Test
    fun contrasenaSinSimbolo_bastaParaElRegistro() {
        assertNull(ValidadorRegistro.validarContrasena("Secreta12"))
    }

    @Test
    fun contrasenaVacia_devuelveErrorDeVacia() {
        assertEquals(R.string.error_contrasena_vacia, ValidadorRegistro.validarContrasena(""))
    }

    @Test
    fun contrasenaSinMayuscula_devuelveErrorDeRequisitos() {
        assertEquals(R.string.error_contrasena_requisitos, ValidadorRegistro.validarContrasena("secreta12"))
    }

    @Test
    fun contrasenaCompleta_noDevuelveError() {
        assertNull(ValidadorRegistro.validarContrasena("Secreta12"))
    }

    @Test
    fun formularioConUnCampoVacio_avisaCamposIncompletosAntesQueLosTerminos() {
        assertEquals(
            R.string.error_campos_incompletos,
            ValidadorRegistro.validarFormulario("", "ana@correo.com", "Secreta12", aceptaTerminos = false),
        )
    }

    @Test
    fun formularioLlenoSinTerminos_avisaLosTerminos() {
        assertEquals(
            R.string.error_terminos,
            ValidadorRegistro.validarFormulario("Ana", "ana@correo.com", "Secreta12", aceptaTerminos = false),
        )
    }

    @Test
    fun formularioCompleto_noDevuelveAviso() {
        assertNull(ValidadorRegistro.validarFormulario("Ana", "ana@correo.com", "Secreta12", aceptaTerminos = true))
    }
}
