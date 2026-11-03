package com.example.finanzia.datos.facturas

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.finanzia.configuracion.Constantes
import java.io.File

/**
 * Dónde guarda la cámara la foto de la factura: un archivo de la caché de la app que se comparte con la
 * cámara (el proveedor de AndroidManifest.xml). Es siempre el mismo, así las fotos no se acumulan.
 */
fun direccionFotoFactura(contexto: Context): Uri {
    val carpeta = File(contexto.cacheDir, Constantes.Facturas.CARPETA_FOTOS).apply { mkdirs() }
    return FileProvider.getUriForFile(
        contexto,
        contexto.packageName + Constantes.Facturas.SUFIJO_PROVEEDOR,
        File(carpeta, Constantes.Facturas.ARCHIVO_FOTO),
    )
}
