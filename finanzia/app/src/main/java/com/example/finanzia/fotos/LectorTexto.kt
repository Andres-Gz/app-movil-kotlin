package com.example.finanzia.fotos

import java.io.File

/** Lee el texto de una foto. */
interface LectorTexto {
    /** El texto, vacío si la foto no trae, o `null` si no se pudo leer. La foto se borra al terminar. */
    suspend fun leer(foto: File): String?
}
