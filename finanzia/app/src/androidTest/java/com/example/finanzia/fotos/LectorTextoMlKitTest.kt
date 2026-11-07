package com.example.finanzia.fotos

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * El OCR de verdad (ML Kit) leyendo una factura dibujada, del tamaño de la foto de un teléfono. Va en el
 * emulador porque el modelo necesita Android; las pruebas del ViewModel usan un lector falso y no lo ven.
 */
class LectorTextoMlKitTest {

    private val contexto = InstrumentationRegistry.getInstrumentation().targetContext
    private val lector = LectorTextoMlKit(contexto)

    @Test
    fun leeLaFacturaYDejaCadaValorEnSuRenglon() = runBlocking {
        val texto = lector.leer(fotoDeFactura(giro = 0f)).orEmpty()

        assertTrue(texto, texto.contains("SUPERMERCADO"))
        assertRenglon(texto, "SUBTOTAL", "49.500")
        assertRenglon(texto, "IVA", "9.400")
        assertRenglon(texto, "TOTAL", "58.900")
    }

    @Test
    fun unaFotoTorcida_tambienDejaCadaValorEnSuRenglon() = runBlocking {
        val texto = lector.leer(fotoDeFactura(giro = 4f)).orEmpty()

        assertRenglon(texto, "SUBTOTAL", "49.500")
        assertRenglon(texto, "IVA", "9.400")
        assertRenglon(texto, "TOTAL", "58.900")
    }

    @Test
    fun unaFotoSinTexto_devuelveVacio() = runBlocking {
        assertEquals("", lector.leer(guardar(Bitmap.createBitmap(ANCHO, ALTO, Bitmap.Config.ARGB_8888).apply { eraseColor(Color.WHITE) })))
    }

    @Test
    fun unArchivoQueNoEsImagen_devuelveNull() = runBlocking {
        val foto = File(contexto.cacheDir, "no_es_imagen.jpg").apply { writeText("hola") }

        assertEquals(null, lector.leer(foto))
    }

    @Test
    fun alTerminar_borraLaFoto() = runBlocking {
        val foto = fotoDeFactura(giro = 0f)

        lector.leer(foto)

        assertFalse(foto.exists())
    }

    private fun assertRenglon(texto: String, nombre: String, valor: String) {
        val renglon = texto.lines().firstOrNull { it.startsWith(nombre) }
        assertTrue("\"$nombre\" y \"$valor\" no quedaron juntos en:\n$texto", renglon?.contains(valor) == true)
    }

    /** Una factura angosta en medio de una foto de 12 megapíxeles, con la letra de un recibo real: unos 38 px. */
    private fun fotoDeFactura(giro: Float): File {
        val imagen = Bitmap.createBitmap(ANCHO, ALTO, Bitmap.Config.ARGB_8888)
        val lienzo = Canvas(imagen)
        lienzo.drawColor(Color.DKGRAY)
        lienzo.rotate(giro, ANCHO / 2f, ALTO / 2f)
        val pincel = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 38f; color = Color.BLACK }
        val izquierda = 950f
        val derecha = 2050f
        lienzo.drawRect(izquierda - 60, 1100f, derecha + 60, 2900f, Paint().apply { color = Color.WHITE })
        val renglones = listOf(
            "SUPERMERCADO EXITO" to "",
            "NIT 890.900.608-9" to "",
            "Fecha 28/10/2026" to "",
            "" to "",
            "ARROZ DIANA 1KG" to "4.500",
            "LECHE ALQUERIA" to "5.000",
            "POLLO" to "40.000",
            "" to "",
            "SUBTOTAL" to "49.500",
            "IVA" to "9.400",
            "TOTAL" to "58.900",
            "EFECTIVO" to "60.000",
            "CAMBIO" to "1.100",
        )
        renglones.forEachIndexed { indice, (nombre, valor) ->
            val y = 1250f + indice * 100f
            lienzo.drawText(nombre, izquierda, y, pincel)
            lienzo.drawText(valor, derecha - pincel.measureText(valor), y, pincel)
        }
        return guardar(imagen)
    }

    private fun guardar(imagen: Bitmap): File {
        val foto = File(contexto.cacheDir, "factura_prueba.jpg")
        foto.outputStream().use { imagen.compress(Bitmap.CompressFormat.JPEG, CALIDAD_JPEG, it) }
        return foto
    }

    private companion object {
        const val ANCHO = 3024
        const val ALTO = 4032
        const val CALIDAD_JPEG = 90
    }
}
