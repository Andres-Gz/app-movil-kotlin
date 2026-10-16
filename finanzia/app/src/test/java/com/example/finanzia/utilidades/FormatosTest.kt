package com.example.finanzia.utilidades

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.TimeZone

class FormatosTest {

    private val bogota = TimeZone.getTimeZone("America/Bogota")
    private val ahora = java.util.Date(1_760_400_000_000L) // 2025-10-14 00:00 UTC = 13 oct, 19:00 en Bogotá

    @Test
    fun formatearMonto_conMilesYDosDecimales() {
        assertEquals("12,450.00", formatearMonto(12450.0))
        assertEquals("1,850.50", formatearMonto(-1850.5))
        assertEquals("1,800", formatearMontoEntero(1800.0))
    }

    @Test
    fun fecha_deHoyEnLaHoraDelTelefono() {
        val fecha = leerFechaMovimiento("2025-10-13T19:32:00.123+00:00", ahora, bogota)!!
        assertEquals(DiaRelativo.HOY, fecha.dia)
        assertEquals("14:32", fecha.hora)
    }

    @Test
    fun fecha_deAyerYDeAntes() {
        assertEquals(DiaRelativo.AYER, leerFechaMovimiento("2025-10-12T14:00:00+00:00", ahora, bogota)!!.dia)
        val antes = leerFechaMovimiento("2025-03-12T14:00:00+00:00", ahora, bogota)!!
        assertEquals(DiaRelativo.ANTES, antes.dia)
        assertEquals(12, antes.diaDelMes)
        assertEquals(2, antes.mes)
    }

    @Test
    fun fecha_malEscrita_daNulo() {
        assertNull(leerFechaMovimiento("ayer", ahora, bogota))
    }
}
