package com.example.finanzia.fotos

import android.content.ActivityNotFoundException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.example.finanzia.configuracion.Constantes
import java.io.File

/**
 * Devuelve la función que abre la cámara y avisa con el archivo de la foto. La toma la app de cámara del
 * teléfono, por eso Finanzia no necesita el permiso de cámara: solo le presta un archivo propio. Siempre es
 * el mismo, así nunca queda más de una foto guardada.
 */
@Composable
fun recordarCamara(alTomarFoto: (File) -> Unit, alNoHaberCamara: () -> Unit): () -> Unit {
    val contexto = LocalContext.current
    val foto = remember { File(File(contexto.cacheDir, Constantes.Fotos.CARPETA), Constantes.Fotos.ARCHIVO) }
    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { tomada ->
        if (tomada) alTomarFoto(foto) else foto.delete()
    }
    return {
        foto.parentFile?.mkdirs()
        try {
            camara.launch(FileProvider.getUriForFile(contexto, contexto.packageName + Constantes.Fotos.SUFIJO_PROVEEDOR, foto))
        } catch (_: ActivityNotFoundException) {
            alNoHaberCamara()
        }
    }
}
