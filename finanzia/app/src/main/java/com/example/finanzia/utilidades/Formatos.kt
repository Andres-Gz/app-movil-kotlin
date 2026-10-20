package com.example.finanzia.utilidades

import com.example.finanzia.configuracion.Constantes
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs

/** Un monto con separador de miles y dos decimales, sin signo ni símbolo (12450.5 → 12,450.50). */
fun formatearMonto(valor: Double): String =
    DecimalFormat(Constantes.Formatos.PATRON_MONTO, DecimalFormatSymbols(Locale.US)).format(abs(valor))

/** Un monto sin decimales (1800.0 → 1,800), para las metas. */
fun formatearMontoEntero(valor: Double): String =
    DecimalFormat(Constantes.Formatos.PATRON_MONTO_ENTERO, DecimalFormatSymbols(Locale.US)).format(abs(valor))

/** Un monto en pesos, sin decimales y con puntos de miles (14850000.0 → 14.850.000), como en la pantalla de metas. */
fun formatearPesos(valor: Double): String {
    val simbolos = DecimalFormatSymbols(Locale.US).apply { groupingSeparator = Constantes.Formatos.SEPARADOR_MILES_PESOS }
    return DecimalFormat(Constantes.Formatos.PATRON_MONTO_ENTERO, simbolos).format(abs(valor))
}

/** Lee una fecha ISO 8601 de Postgres (en UTC, p. ej. 2026-10-13T18:45:12.123+00:00); `null` si no se entiende. */
fun leerFechaIso(iso: String): Date? {
    val lector = SimpleDateFormat(Constantes.Formatos.PATRON_FECHA_ISO, Locale.US).apply {
        timeZone = TimeZone.getTimeZone(Constantes.Formatos.ZONA_BASE)
    }
    return runCatching { lector.parse(iso.take(Constantes.Formatos.LARGO_FECHA_ISO)) }.getOrNull()
}

/** Qué día fue un movimiento respecto a hoy, para escribir "Hoy", "Ayer" o la fecha. */
enum class DiaRelativo { HOY, AYER, ANTES }

/** Fecha de un movimiento ya convertida a la hora del teléfono. */
data class FechaMovimiento(val dia: DiaRelativo, val hora: String, val diaDelMes: Int, val mes: Int)

/**
 * Lee la fecha ISO 8601 que manda Postgres (en UTC, p. ej. 2026-10-13T18:45:12.123+00:00).
 * Devuelve `null` si no se entiende. [ahora] y [zona] se pueden cambiar en las pruebas.
 */
fun leerFechaMovimiento(
    iso: String,
    ahora: Date = Date(),
    zona: TimeZone = TimeZone.getDefault(),
): FechaMovimiento? {
    val fecha = leerFechaIso(iso) ?: return null
    val calendario = Calendar.getInstance(zona).apply { time = fecha }
    val hoy = Calendar.getInstance(zona).apply { time = ahora }
    val ayer = (hoy.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }
    val dia = when {
        mismoDia(calendario, hoy) -> DiaRelativo.HOY
        mismoDia(calendario, ayer) -> DiaRelativo.AYER
        else -> DiaRelativo.ANTES
    }
    val hora = SimpleDateFormat(Constantes.Formatos.PATRON_HORA, Locale.US).apply { timeZone = zona }.format(fecha)
    return FechaMovimiento(dia, hora, calendario.get(Calendar.DAY_OF_MONTH), calendario.get(Calendar.MONTH))
}

private fun mismoDia(a: Calendar, b: Calendar): Boolean =
    a.get(Calendar.YEAR) == b.get(Calendar.YEAR) && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
