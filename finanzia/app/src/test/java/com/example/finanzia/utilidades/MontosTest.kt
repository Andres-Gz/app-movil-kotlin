package com.example.finanzia.utilidades

import com.example.finanzia.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MontosTest {

    @Test
    fun limpiarMonto_quitaLetrasSeparadoresYCerosIniciales() {
        assertEquals("18500", limpiarMonto("0$18.500"))
    }

    @Test
    fun limpiarMonto_noPasaDeDoceCifras() {
        assertEquals(12, limpiarMonto("12345678901234").length)
    }

    @Test
    fun validarMonto_pideUnMontoMayorQueCero() {
        assertEquals(R.string.error_monto_vacio, validarMonto(""))
        assertNull(validarMonto("18500"))
    }
}
