package com.example.finanzia.pantallas.nuevacontrasena

import com.example.finanzia.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidadorNuevaContrasenaTest {

    @Test
    fun contrasenaVacia_devuelveError() {
        assertEquals(R.string.error_contrasena_vacia, ValidadorNuevaContrasena.validarContrasena(""))
    }

    @Test
    fun contrasenaSinSimbolo_noCumpleLosCuatroRequisitos() {
        assertEquals(R.string.error_contrasena_requisitos, ValidadorNuevaContrasena.validarContrasena("Finanzia2024"))
    }

    @Test
    fun contrasenaCompleta_noDevuelveError() {
        assertNull(ValidadorNuevaContrasena.validarContrasena("Finanzia2024!"))
    }

    @Test
    fun confirmacionVaciaODistinta_devuelveSuError() {
        assertEquals(R.string.error_confirmacion_vacia, ValidadorNuevaContrasena.validarConfirmacion("Finanzia2024!", ""))
        assertEquals(
            R.string.error_contrasenas_distintas,
            ValidadorNuevaContrasena.validarConfirmacion("Finanzia2024!", "Finanzia2024?"),
        )
    }

    @Test
    fun aviso_sigueElOrdenCamposVaciosSeguridadYCoincidencia() {
        assertEquals(
            R.string.error_faltan_datos to R.string.error_faltan_datos_detalle,
            ValidadorNuevaContrasena.aviso("", "x"),
        )
        assertEquals(
            R.string.error_contrasena_insegura to R.string.error_contrasena_insegura_detalle,
            ValidadorNuevaContrasena.aviso("corta", "otra"),
        )
        assertEquals(
            R.string.error_contrasenas_no_coinciden to R.string.error_contrasenas_no_coinciden_detalle,
            ValidadorNuevaContrasena.aviso("Finanzia2024!", "Finanzia2024?"),
        )
        assertNull(ValidadorNuevaContrasena.aviso("Finanzia2024!", "Finanzia2024!"))
    }
}
