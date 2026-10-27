package com.example.finanzia.pantallas.asistente

import com.example.finanzia.configuracion.Constantes
import com.example.finanzia.utilidades.leerFechaIso
import java.util.Date

/** Una respuesta de la IA separada en su título (la primera línea, "# …") y el texto. */
data class RespuestaSeparada(val titulo: String?, val texto: String)

/** Un pedazo del texto de una respuesta: normal o resaltado (lo que la IA escribe entre **). */
data class Trozo(val texto: String, val resaltado: Boolean)

/** Hace cuánto llegó una respuesta. */
sealed interface Antiguedad {
    data object Ahora : Antiguedad
    data class Minutos(val cantidad: Int) : Antiguedad
    data class Horas(val cantidad: Int) : Antiguedad
    data class Dias(val cantidad: Int) : Antiguedad
}

/** Cómo se muestra lo que escribe la IA. Aparte de la pantalla para probarlo sin emulador. */
object FormatoMensaje {

    /** La IA empieza cada respuesta con "# Título"; si no lo hizo, no hay título. */
    fun separarTitulo(contenido: String): RespuestaSeparada {
        val limpio = contenido.trim()
        if (!limpio.startsWith(Constantes.Asistente.MARCA_TITULO)) return RespuestaSeparada(null, limpio)
        val titulo = limpio.substringBefore('\n').removePrefix(Constantes.Asistente.MARCA_TITULO).trim()
        return RespuestaSeparada(titulo.ifEmpty { null }, limpio.substringAfter('\n', "").trim())
    }

    /** Parte el texto en trozos normales y resaltados. Un ** sin cerrar se deja tal cual. */
    fun trozos(texto: String): List<Trozo> {
        val marca = Constantes.Asistente.MARCA_NEGRITA
        val partes = texto.split(marca)
        return partes.mapIndexedNotNull { indice, parte ->
            val resaltado = indice % 2 == 1
            when {
                parte.isEmpty() -> null
                resaltado && indice == partes.lastIndex -> Trozo(marca + parte, resaltado = false)
                else -> Trozo(parte, resaltado)
            }
        }
    }

    /** El texto de una respuesta en una línea, sin el título ni las marcas, para el historial. */
    fun vistaPrevia(contenido: String): String {
        val separada = separarTitulo(contenido)
        return separada.texto.ifEmpty { separada.titulo.orEmpty() }
            .replace(Constantes.Asistente.MARCA_NEGRITA, "")
            .replace('\n', ' ')
    }

    /** Hace cuánto fue [fechaIso] respecto a [ahora]; `null` si la fecha no se entiende. */
    fun antiguedad(fechaIso: String, ahora: Date): Antiguedad? {
        val fecha = leerFechaIso(fechaIso) ?: return null
        val minutos = ((ahora.time - fecha.time) / Constantes.Asistente.MILISEGUNDOS_POR_MINUTO).coerceAtLeast(0)
        val horas = minutos / Constantes.Asistente.MINUTOS_POR_HORA
        val dias = horas / Constantes.Asistente.HORAS_POR_DIA
        return when {
            minutos < 1 -> Antiguedad.Ahora
            horas < 1 -> Antiguedad.Minutos(minutos.toInt())
            dias < 1 -> Antiguedad.Horas(horas.toInt())
            else -> Antiguedad.Dias(dias.toInt())
        }
    }
}
