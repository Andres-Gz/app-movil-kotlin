package com.example.finanzia.datos.modelos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Lo que devuelve la función `resumen_inicio` (supabase/inicio.sql): todo lo que muestra el inicio. */
@Serializable
data class ResumenInicio(
    val nombre: String,
    /** Nombre de la cuenta principal, o `null` si el usuario aún no tiene cuentas. */
    val cuenta: String? = null,
    @SerialName("saldo_inicial") val saldoInicial: Double = 0.0,
    val ingresos: Double = 0.0,
    val gastos: Double = 0.0,
    @SerialName("total_movimientos") val totalMovimientos: Int = 0,
    /** Los últimos movimientos, del más nuevo al más viejo. */
    val movimientos: List<MovimientoReciente> = emptyList(),
    /** La meta más reciente, o `null` si el usuario aún no tiene metas. */
    val meta: MetaActual? = null,
) {
    val balance: Double get() = saldoInicial + ingresos - gastos
}

/** Un movimiento de la tabla `movimientos`, con el nombre de su categoría y su cuenta. */
@Serializable
data class MovimientoReciente(
    @SerialName("id_movimiento") val idMovimiento: Long,
    val tipo: String,
    val monto: Double,
    val descripcion: String? = null,
    /** Fecha en formato ISO 8601, tal como la manda Postgres. */
    val fecha: String,
    val categoria: String? = null,
    val cuenta: String? = null,
)

/** Una meta de la tabla `metas` con lo que lleva ahorrado (suma de `aportes`). */
@Serializable
data class MetaActual(
    val nombre: String,
    val objetivo: Double,
    val ahorrado: Double = 0.0,
)
