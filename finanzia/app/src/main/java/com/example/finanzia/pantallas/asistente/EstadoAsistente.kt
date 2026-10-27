package com.example.finanzia.pantallas.asistente

import androidx.annotation.StringRes
import com.example.finanzia.datos.modelos.ConversacionAsistente
import com.example.finanzia.datos.modelos.MensajeAsistente

/** Todo lo que muestra el chat con Finanzia IA. */
data class EstadoAsistente(
    /** La conversación abierta, o `null` si es nueva (se crea al mandar la primera pregunta). */
    val idConversacion: Long? = null,
    val mensajes: List<MensajeAsistente> = emptyList(),
    /** Lo que se está escribiendo. */
    val texto: String = "",
    /** La pregunta que se mandó y todavía no tiene respuesta: se muestra mientras la IA piensa. */
    val preguntaEnviada: String? = null,
    /** Mientras se leen los mensajes de una conversación del historial. */
    val cargandoMensajes: Boolean = false,
    @param:StringRes val error: Int? = null,
    /** El mensaje cuya propuesta se está confirmando o cancelando. */
    val accionEnCurso: Long? = null,
    /** El menú ⋮ (nueva conversación, eliminar). */
    val menuAbierto: Boolean = false,
    /** El menú del "+" con frases para empezar a escribir ("Registra un gasto de…"). */
    val atajosAbiertos: Boolean = false,
    val confirmandoEliminar: Boolean = false,
    val historialAbierto: Boolean = false,
    val cargandoHistorial: Boolean = false,
    val conversaciones: List<ConversacionAsistente> = emptyList(),
    @param:StringRes val errorHistorial: Int? = null,
    /** Mensaje que la pantalla muestra una vez (un Toast). */
    @param:StringRes val aviso: Int? = null,
) {
    /** Mientras la IA responde no se puede mandar otra pregunta. */
    val respondiendo: Boolean get() = preguntaEnviada != null

    val puedeEnviar: Boolean get() = texto.isNotBlank() && !respondiendo && !cargandoMensajes
}
