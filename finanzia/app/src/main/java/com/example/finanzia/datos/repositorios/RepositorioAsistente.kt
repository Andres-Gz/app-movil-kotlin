package com.example.finanzia.datos.repositorios

import com.example.finanzia.datos.modelos.ConversacionAsistente
import com.example.finanzia.datos.modelos.IntercambioAsistente
import com.example.finanzia.datos.modelos.MensajeAsistente

/** El chat con Finanzia IA: preguntar, leer las conversaciones guardadas y resolver lo que propone la IA. */
interface RepositorioAsistente {
    /**
     * Manda la pregunta a la IA. Sin [idConversacion] empieza una conversación nueva. [textoFoto] es lo que
     * se leyó en una foto: lo recibe la IA, pero no se guarda en la conversación.
     */
    suspend fun preguntar(idConversacion: Long?, pregunta: String, textoFoto: String? = null): Llamada<IntercambioAsistente>

    /** Las conversaciones del usuario, la más reciente primero. */
    suspend fun conversaciones(): Llamada<List<ConversacionAsistente>>

    suspend fun mensajes(idConversacion: Long): Llamada<List<MensajeAsistente>>

    /** Guarda lo que propuso la IA en ese mensaje y devuelve el mensaje actualizado. */
    suspend fun confirmarAccion(idMensaje: Long): Llamada<MensajeAsistente>

    suspend fun cancelarAccion(idMensaje: Long): Llamada<MensajeAsistente>

    /** La quita de la lista (queda inactiva); no se borra. */
    suspend fun eliminarConversacion(idConversacion: Long): Llamada<Unit>
}
