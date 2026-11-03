package com.example.finanzia.datos.facturas

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.example.finanzia.configuracion.Constantes
import com.example.finanzia.utilidades.registrarAdvertencia
import com.google.android.gms.tasks.Task
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.IOException
import java.io.InputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

/**
 * Lee las facturas con el reconocimiento de texto de ML Kit (Google), en el mismo teléfono: no necesita
 * internet y la foto nunca sale del teléfono. A la IA solo le llega el texto.
 */
class LectorFacturasMlKit(contexto: Context) : LectorFacturas {

    private val contexto = contexto.applicationContext

    override suspend fun leer(imagen: String): LecturaFactura = try {
        val foto = withContext(Dispatchers.IO) { cargarFoto(Uri.parse(imagen)) }
        val texto = textoEnRenglones(reconocer(foto)).trim().take(Constantes.Facturas.LARGO_MAXIMO_TEXTO)
        if (texto.isEmpty()) LecturaFactura.SinTexto else LecturaFactura.Leida(texto)
    } catch (error: IOException) {
        fallida(error)
    } catch (error: SecurityException) {
        // La app ya no tiene permiso para leer esa foto de la galería.
        fallida(error)
    } catch (error: MlKitException) {
        fallida(error)
    }

    private fun fallida(error: Exception): LecturaFactura {
        registrarAdvertencia(Constantes.Registro.FACTURA_NO_LEIDA, error.message)
        return LecturaFactura.Fallida
    }

    /**
     * La foto lista para leer: achicada si es muy grande ([Constantes.Facturas.LADO_MAXIMO_FOTO]) y girada
     * como se tomó, porque la cámara guarda el giro aparte (en los datos EXIF) y el texto quedaría de lado.
     */
    private fun cargarFoto(direccion: Uri): InputImage {
        val medidas = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        abrir(direccion) { BitmapFactory.decodeStream(it, null, medidas) }
        val ladoMayor = maxOf(medidas.outWidth, medidas.outHeight)
        if (ladoMayor <= 0) throw IOException("No es una imagen: $direccion")

        // inSampleSize solo achica en potencias de 2: 1 (igual), 2 (la mitad), 4 (la cuarta parte)…
        var reduccion = 1
        while (ladoMayor / reduccion > Constantes.Facturas.LADO_MAXIMO_FOTO) reduccion *= FACTOR_REDUCCION
        val opciones = BitmapFactory.Options().apply { inSampleSize = reduccion }
        val foto = abrir(direccion) { BitmapFactory.decodeStream(it, null, opciones) }
            ?: throw IOException("No se pudo abrir la imagen: $direccion")

        // Si la imagen no trae datos EXIF que se puedan leer (algunas de la galería), se toma como derecha.
        val giro = try {
            abrir(direccion) { ExifInterface(it).rotationDegrees }
        } catch (_: IOException) {
            SIN_GIRO
        }
        if (giro == SIN_GIRO) return InputImage.fromBitmap(foto, SIN_GIRO)
        // Se gira aquí y no con el giro de InputImage, para que las posiciones del texto ya vengan derechas.
        val giroFoto = Matrix().apply { postRotate(giro.toFloat()) }
        val derecha = Bitmap.createBitmap(foto, 0, 0, foto.width, foto.height, giroFoto, true)
        if (derecha !== foto) foto.recycle()
        return InputImage.fromBitmap(derecha, SIN_GIRO)
    }

    private fun <T> abrir(direccion: Uri, leer: (InputStream) -> T): T {
        val flujo = contexto.contentResolver.openInputStream(direccion) ?: throw IOException("No se pudo abrir $direccion")
        return flujo.use(leer)
    }

    private suspend fun reconocer(foto: InputImage): Text {
        val reconocedor = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        return try {
            reconocedor.process(foto).esperar()
        } finally {
            reconocedor.close()
        }
    }

    /** El texto renglón por renglón ([unirRenglones]); si algún trozo no trae su posición, tal como lo entrega ML Kit. */
    private fun textoEnRenglones(leido: Text): String {
        val lineas = leido.textBlocks.flatMap { it.lines }
        val trozos = lineas.mapNotNull { linea ->
            linea.boundingBox?.let { caja -> TrozoLeido(linea.text, caja.left, caja.top, caja.bottom) }
        }
        return if (trozos.size == lineas.size) unirRenglones(trozos) else leido.text
    }
}

private const val FACTOR_REDUCCION = 2
private const val SIN_GIRO = 0

/** Espera el resultado de una tarea de ML Kit sin bloquear el hilo. */
private suspend fun <T> Task<T>.esperar(): T = suspendCancellableCoroutine { continuacion ->
    addOnSuccessListener { continuacion.resume(it) }
    addOnFailureListener { continuacion.resumeWithException(it) }
    addOnCanceledListener { continuacion.cancel() }
}
