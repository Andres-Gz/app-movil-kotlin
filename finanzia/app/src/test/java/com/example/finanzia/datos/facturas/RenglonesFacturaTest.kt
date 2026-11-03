package com.example.finanzia.datos.facturas

import org.junit.Assert.assertEquals
import org.junit.Test

class RenglonesFacturaTest {

    @Test
    fun elNombreYElValor_quedanEnElMismoRenglonAunqueElOcrLosLeaEnBloquesDistintos() {
        // Como los entrega ML Kit: primero el bloque de la izquierda y después el de la derecha.
        val trozos = listOf(
            TrozoLeido("SUBTOTAL", izquierda = 10, arriba = 100, abajo = 120),
            TrozoLeido("TOTAL", izquierda = 10, arriba = 130, abajo = 150),
            TrozoLeido("38.571", izquierda = 300, arriba = 102, abajo = 121),
            TrozoLeido("45.900", izquierda = 300, arriba = 131, abajo = 152),
        )

        assertEquals("SUBTOTAL 38.571\nTOTAL 45.900", unirRenglones(trozos))
    }

    @Test
    fun cadaRenglon_vaDeIzquierdaADerecha() {
        val trozos = listOf(
            TrozoLeido("$4.500", izquierda = 400, arriba = 50, abajo = 70),
            TrozoLeido("x2", izquierda = 250, arriba = 52, abajo = 70),
            TrozoLeido("Pan", izquierda = 20, arriba = 48, abajo = 68),
        )

        assertEquals("Pan x2 $4.500", unirRenglones(trozos))
    }

    @Test
    fun losRenglones_vanDeArribaAAbajo() {
        val trozos = listOf(
            TrozoLeido("Gracias por su compra", izquierda = 10, arriba = 500, abajo = 520),
            TrozoLeido("TIENDA D1", izquierda = 10, arriba = 10, abajo = 30),
            TrozoLeido("NIT 900.276.962-1", izquierda = 10, arriba = 40, abajo = 60),
        )

        assertEquals("TIENDA D1\nNIT 900.276.962-1\nGracias por su compra", unirRenglones(trozos))
    }

    @Test
    fun sinTrozos_noHayTexto() {
        assertEquals("", unirRenglones(emptyList()))
    }
}
