package com.example.finanzia.utilidades

import android.util.Log
import com.example.finanzia.configuracion.Constantes
import io.github.jan.supabase.exceptions.RestException

/** Deja una advertencia en Logcat con la etiqueta de la app. */
fun registrarAdvertencia(mensaje: String, detalle: Any?) {
    Log.w(Constantes.Registro.ETIQUETA, "$mensaje: $detalle")
}

/** El motivo de un error de Supabase sin el mensaje completo, que incluye el token de sesión. */
fun RestException.resumen(): String = "$error (HTTP $statusCode). ${description.orEmpty()}"
