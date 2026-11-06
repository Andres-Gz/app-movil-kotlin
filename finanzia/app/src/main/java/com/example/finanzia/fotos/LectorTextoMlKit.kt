package com.example.finanzia.fotos

import android.content.Context
import android.net.Uri
import com.example.finanzia.configuracion.Constantes
import com.example.finanzia.utilidades.registrarAdvertencia
import com.google.android.gms.tasks.Task
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.hypot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

/**
 * El OCR con ML Kit de Google. Corre en el teléfono, sin internet: la foto nunca sale de aquí y se borra
 * apenas se lee. El modelo viene dentro de la app, así que funciona desde la primera foto.
 */
class LectorTextoMlKit(private val contexto: Context) : LectorTexto {

    override suspend fun leer(foto: File): String? = withContext(Dispatchers.IO) {
        try {
            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS).use { reconocedor ->
                // fromFilePath respeta la rotación con que la cámara guardó la foto.
                val imagen = InputImage.fromFilePath(contexto, Uri.fromFile(foto))
                val lineas = reconocedor.process(imagen).esperar().textBlocks.flatMap { it.lines }
                armarRenglones(lineas.mapNotNull { it.aLineaLeida() })
            }
        } catch (error: IOException) {
            registrarAdvertencia(Constantes.Registro.FOTO_NO_LEIDA, error.message)
            null
        } catch (error: MlKitException) {
            registrarAdvertencia(Constantes.Registro.FOTO_NO_LEIDA, error.message)
            null
        } finally {
            foto.delete()
        }
    }

    /** Las esquinas van en orden: arriba a la izquierda, arriba a la derecha, abajo a la derecha, abajo a la izquierda. */
    private fun Text.Line.aLineaLeida(): LineaLeida? {
        val esquinas = cornerPoints?.takeIf { it.size == Constantes.Fotos.ESQUINAS_LINEA } ?: return null
        return LineaLeida(
            texto = text,
            centroX = esquinas.map { it.x }.average(),
            centroY = esquinas.map { it.y }.average(),
            ancho = hypot((esquinas[1].x - esquinas[0].x).toDouble(), (esquinas[1].y - esquinas[0].y).toDouble()),
            alto = hypot((esquinas[3].x - esquinas[0].x).toDouble(), (esquinas[3].y - esquinas[0].y).toDouble()),
            angulo = angle.toDouble(),
        )
    }

    private suspend fun <T> Task<T>.esperar(): T = suspendCancellableCoroutine { continuacion ->
        addOnSuccessListener { continuacion.resume(it) }
        addOnFailureListener { continuacion.resumeWithException(it) }
    }
}
