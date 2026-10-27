package com.example.finanzia.datos.modelos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Un mensaje del chat con Finanzia IA, como lo devuelven las funciones de supabase/asistente.sql. */
@Serializable
data class MensajeAsistente(
    @SerialName("id_mensaje") val idMensaje: Long,
    val rol: RolMensaje,
    val contenido: String,
    /** Fecha en formato ISO 8601. */
    val fecha: String,
    /** Lo que la IA propuso guardar en esta respuesta, o `null`. */
    val accion: AccionAsistente? = null,
    @SerialName("estado_accion") val estadoAccion: EstadoAccion? = null,
)

@Serializable
enum class RolMensaje {
    @SerialName("user") USUARIO,
    @SerialName("assistant") ASISTENTE,
}

/**
 * Un gasto, un ingreso o un aporte que propuso la IA. No está guardado: se guarda solo si el usuario
 * pulsa "Confirmar" (confirmar_accion_asistente).
 */
@Serializable
data class AccionAsistente(
    val tipo: TipoAccion,
    val monto: Double,
    /** Clave de la categoría del gasto (`comida`, `transporte`…). */
    val categoria: String? = null,
    /** Frecuencia del ingreso (`mensual`, `quincenal`…). */
    val frecuencia: String? = null,
    val descripcion: String? = null,
    @SerialName("id_meta") val idMeta: Long? = null,
    @SerialName("nombre_meta") val nombreMeta: String? = null,
)

@Serializable
enum class TipoAccion {
    @SerialName("gasto") GASTO,
    @SerialName("ingreso") INGRESO,
    @SerialName("aporte") APORTE,
}

@Serializable
enum class EstadoAccion {
    @SerialName("pendiente") PENDIENTE,
    @SerialName("confirmada") CONFIRMADA,
    @SerialName("cancelada") CANCELADA,
}

/** Una conversación de la lista, como la devuelve `conversaciones_asistente`. */
@Serializable
data class ConversacionAsistente(
    @SerialName("id_conversacion") val idConversacion: Long,
    val titulo: String? = null,
    /** Fecha del último mensaje (o de la creación, si no tiene), en formato ISO 8601. */
    @SerialName("fecha_ultimo") val fechaUltimo: String,
    @SerialName("ultimo_mensaje") val ultimoMensaje: String? = null,
)

/** Lo que devuelve la Edge Function `asistente`: la pregunta y la respuesta, ya guardadas. */
@Serializable
data class IntercambioAsistente(
    @SerialName("id_conversacion") val idConversacion: Long,
    val pregunta: MensajeAsistente,
    val respuesta: MensajeAsistente,
)
