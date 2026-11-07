package com.example.finanzia.fotos

import kotlin.math.cos
import kotlin.math.sin
import org.junit.Assert.assertEquals
import org.junit.Test

class RenglonesTest {

    private fun linea(texto: String, x: Double, y: Double, angulo: Double = 0.0) =
        LineaLeida(texto, x, y, ancho = texto.length * 20.0, alto = 30.0, angulo = angulo)

    /** La misma línea en una foto girada [grados] alrededor del origen, como cuando el teléfono queda torcido. */
    private fun girada(texto: String, x: Double, y: Double, grados: Double): LineaLeida {
        val giro = Math.toRadians(grados)
        return linea(texto, x * cos(giro) - y * sin(giro), x * sin(giro) + y * cos(giro), grados)
    }

    @Test
    fun juntaLasColumnasEnSuRenglon() {
        val texto = armarRenglones(
            listOf(
                linea("SUBTOTAL", 100.0, 100.0),
                linea("IVA", 100.0, 150.0),
                linea("TOTAL", 100.0, 200.0),
                linea("49.500", 500.0, 102.0),
                linea("9.400", 500.0, 148.0),
                linea("58.900", 500.0, 201.0),
            ),
        )

        assertEquals("SUBTOTAL  49.500\nIVA  9.400\nTOTAL  58.900", texto)
    }

    @Test
    fun unaFotoTorcida_noMezclaLosValores() {
        // Con 5 grados, el precio queda 35 puntos más abajo que su nombre: más que la distancia entre renglones.
        val lineas = listOf(
            girada("IVA", 100.0, 150.0, 5.0),
            girada("TOTAL", 100.0, 180.0, 5.0),
            girada("9.400", 500.0, 150.0, 5.0),
            girada("58.900", 500.0, 180.0, 5.0),
        )

        assertEquals("IVA  9.400\nTOTAL  58.900", armarRenglones(lineas))
    }

    @Test
    fun lasLineasCortasQueLleganSinInclinacion_noTuercenElResultado() {
        // Así llegan del OCR: en una foto torcida, las cortas traen 0 grados y son la mayoría.
        val lineas = listOf(
            girada("SUPERMERCADO EXITO DE COLOMBIA", 100.0, 100.0, 5.0),
            girada("IVA", 100.0, 150.0, 5.0).copy(angulo = 0.0),
            girada("TOTAL", 100.0, 180.0, 5.0).copy(angulo = 0.0),
            girada("9.400", 500.0, 150.0, 5.0).copy(angulo = 0.0),
            girada("58.900", 500.0, 180.0, 5.0).copy(angulo = 0.0),
        )

        assertEquals("SUPERMERCADO EXITO DE COLOMBIA\nIVA  9.400\nTOTAL  58.900", armarRenglones(lineas))
    }

    @Test
    fun unaLineaAtravesada_noTuerceAlResto() {
        val lineas = listOf(
            girada("IVA", 100.0, 150.0, 5.0),
            girada("9.400", 500.0, 150.0, 5.0),
            // Un sello torcido, por ejemplo.
            girada("PAGADO", 100.0, 300.0, 5.0).copy(angulo = -40.0),
        )

        assertEquals("IVA  9.400\nPAGADO", armarRenglones(lineas))
    }

    @Test
    fun cadaRenglonVaDeIzquierdaADerecha() {
        val texto = armarRenglones(listOf(linea("58.900", 500.0, 100.0), linea("TOTAL", 100.0, 100.0)))

        assertEquals("TOTAL  58.900", texto)
    }

    @Test
    fun sinLineas_noHayTexto() {
        assertEquals("", armarRenglones(emptyList()))
    }
}
