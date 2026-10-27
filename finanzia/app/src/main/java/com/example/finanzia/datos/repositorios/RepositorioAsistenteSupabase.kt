package com.example.finanzia.datos.repositorios

import com.example.finanzia.configuracion.Constantes
import com.example.finanzia.datos.ClienteSupabase
import com.example.finanzia.datos.modelos.ConversacionAsistente
import com.example.finanzia.datos.modelos.IntercambioAsistente
import com.example.finanzia.datos.modelos.MensajeAsistente
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.postgrest
import io.ktor.client.plugins.timeout
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Pregunta a la Edge Function `asistente` (supabase/functions/asistente), que habla con la IA, y lee y
 * resuelve el chat con las funciones de supabase/asistente.sql. Todo va con el token de la sesión.
 */
class RepositorioAsistenteSupabase(
    private val cliente: SupabaseClient = ClienteSupabase.cliente,
) : RepositorioAsistente {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun preguntar(idConversacion: Long?, pregunta: String): Llamada<IntercambioAsistente> =
        llamarConSesion(cliente) {
            val respuesta = cliente.functions.invoke(Constantes.Asistente.FUNCION) {
                contentType(ContentType.Application.Json)
                setBody(
                    buildJsonObject {
                        put(Constantes.Asistente.CAMPO_CONVERSACION, idConversacion)
                        put(Constantes.Asistente.CAMPO_MENSAJE, pregunta)
                    }.toString(),
                )
                // La IA puede tardar más que los 10 s que Supabase espera por defecto.
                timeout { requestTimeoutMillis = Constantes.Asistente.ESPERA_MAXIMA_MILISEGUNDOS }
            }
            json.decodeFromString<IntercambioAsistente>(respuesta.bodyAsText())
        }

    override suspend fun conversaciones(): Llamada<List<ConversacionAsistente>> =
        llamarConSesion(cliente) {
            cliente.postgrest.rpc(Constantes.BaseDatos.FUNCION_CONVERSACIONES_ASISTENTE).decodeAs<List<ConversacionAsistente>>()
        }

    override suspend fun mensajes(idConversacion: Long): Llamada<List<MensajeAsistente>> =
        llamarConSesion(cliente) {
            cliente.postgrest.rpc(
                Constantes.BaseDatos.FUNCION_MENSAJES_ASISTENTE,
                buildJsonObject { put(Constantes.BaseDatos.PARAMETRO_CONVERSACION, idConversacion) },
            ).decodeAs<List<MensajeAsistente>>()
        }

    override suspend fun confirmarAccion(idMensaje: Long): Llamada<MensajeAsistente> =
        resolverAccion(Constantes.BaseDatos.FUNCION_CONFIRMAR_ACCION, idMensaje)

    override suspend fun cancelarAccion(idMensaje: Long): Llamada<MensajeAsistente> =
        resolverAccion(Constantes.BaseDatos.FUNCION_CANCELAR_ACCION, idMensaje)

    override suspend fun eliminarConversacion(idConversacion: Long): Llamada<Unit> =
        llamarConSesion(cliente) {
            cliente.postgrest.rpc(
                Constantes.BaseDatos.FUNCION_ELIMINAR_CONVERSACION,
                buildJsonObject { put(Constantes.BaseDatos.PARAMETRO_ID, idConversacion) },
            )
            Unit
        }

    private suspend fun resolverAccion(funcion: String, idMensaje: Long): Llamada<MensajeAsistente> =
        llamarConSesion(cliente) {
            cliente.postgrest.rpc(
                funcion,
                buildJsonObject { put(Constantes.BaseDatos.PARAMETRO_MENSAJE, idMensaje) },
            ).decodeAs<MensajeAsistente>()
        }
}
