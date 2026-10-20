package com.example.finanzia.datos.modelos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Lo que devuelve la función `resumen_metas` (supabase/metas.sql): todo lo que muestra la pantalla de metas. */
@Serializable
data class ResumenMetas(
    /** Las metas activas, de la fecha límite más cercana a la más lejana (las que no tienen fecha, al final). */
    val metas: List<Meta> = emptyList(),
    /** Fechas de los aportes del último año, en formato ISO 8601, de la más nueva a la más vieja. */
    @SerialName("fechas_aportes") val fechasAportes: List<String> = emptyList(),
)

/** Una meta de la tabla `metas` con lo que lleva ahorrado (suma de `aportes`). */
@Serializable
data class Meta(
    @SerialName("id_meta") val idMeta: Long,
    val nombre: String,
    val objetivo: Double,
    val ahorrado: Double = 0.0,
    /** Fecha en formato ISO 8601, o `null` si la meta no tiene fecha límite. */
    @SerialName("fecha_limite") val fechaLimite: String? = null,
    val categoria: String? = null,
    /** Un emoji. */
    val icono: String? = null,
    /** Regla de aporte: cuánto se aporta y cada cuánto (`semanal`, `quincenal` o `mensual`). */
    @SerialName("monto_aporte") val montoAporte: Double? = null,
    @SerialName("frecuencia_aporte") val frecuenciaAporte: String? = null,
)
