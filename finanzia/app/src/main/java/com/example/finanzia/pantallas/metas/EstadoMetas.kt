package com.example.finanzia.pantallas.metas

import androidx.annotation.StringRes
import com.example.finanzia.configuracion.Constantes
import com.example.finanzia.datos.modelos.Frecuencia
import com.example.finanzia.datos.modelos.Meta
import com.example.finanzia.datos.modelos.ResumenMetas
import com.example.finanzia.utilidades.leerFechaIso
import java.util.Calendar
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/** Cómo se ordena la lista de metas. */
enum class OrdenMetas { FECHA_LIMITE, MAYOR_AVANCE, NOMBRE }

/** Todo lo que muestra la pantalla de metas. Mientras no llega el resumen, [resumen] es `null`. */
data class EstadoMetas(
    val cargando: Boolean = false,
    val resumen: ResumenMetas? = null,
    @param:StringRes val error: Int? = null,
    val orden: OrdenMetas = OrdenMetas.FECHA_LIMITE,
    /** Si está abierto el menú de "Ordenar". */
    val ordenAbierto: Boolean = false,
    /** La meta que tiene abierto su menú ⋮, o `null`. */
    val menuDe: Long? = null,
    val dialogo: DialogoMeta? = null,
    /** Lo que se escribe en "Aportar" o "Ajustar regla": solo las cifras, sin separadores. */
    val montoDialogo: String = "",
    val frecuenciaDialogo: Frecuencia = Frecuencia.QUINCENAL,
    /** Mientras Supabase guarda lo que se pidió en la ventana abierta. */
    val guardando: Boolean = false,
    @param:StringRes val errorDialogo: Int? = null,
    /** Mensaje que la pantalla muestra una vez (un Toast) cuando algo se guardó. */
    @param:StringRes val aviso: Int? = null,
)

/** Ventana abierta sobre una meta. */
sealed interface DialogoMeta {
    val meta: Meta

    data class Aportar(override val meta: Meta) : DialogoMeta
    data class AjustarRegla(override val meta: Meta) : DialogoMeta

    /** Pide confirmar antes de eliminar. */
    data class Eliminar(override val meta: Meta) : DialogoMeta
}

/** El consejo de Finanzia IA sobre las metas, ya calculado; la pantalla lo convierte en texto. */
sealed interface ConsejoMetas {
    data object SinMetas : ConsejoMetas
    data object TodasLogradas : ConsejoMetas

    /** Ninguna meta pendiente tiene fecha límite y regla de aporte, así que no hay cómo proyectar. */
    data object SinRegla : ConsejoMetas

    /** Con la regla de aporte, [meta] se logra [dias] días antes de su fecha límite (si es negativo, después). */
    data class Proyeccion(val meta: Meta, val dias: Int, val frecuencia: Frecuencia) : ConsejoMetas
}

/** Cuentas que hace la pantalla de metas, aparte para probarlas sin pantalla. */
object CalculosMetas {

    fun totalAhorrado(metas: List<Meta>): Double = metas.sumOf { it.ahorrado }

    fun totalObjetivo(metas: List<Meta>): Double = metas.sumOf { it.objetivo }

    /** Avance entre 0 y 1. */
    fun progreso(ahorrado: Double, objetivo: Double): Float =
        if (objetivo > 0) min(ahorrado / objetivo, 1.0).toFloat() else 0f

    /**
     * Avance en porcentaje entero, sin redondear hacia arriba (67,5 % → 67), para no anunciar lo que aún no se tiene.
     * Se calcula con los montos y no con [progreso]: en Float, 2.800.000 de 4.000.000 da 69,99… y saldría 69 %.
     */
    fun porcentaje(ahorrado: Double, objetivo: Double): Int =
        if (objetivo > 0) (min(ahorrado, objetivo) * Constantes.Formatos.CIEN / objetivo).toInt() else 0

    fun falta(meta: Meta): Double = max(meta.objetivo - meta.ahorrado, 0.0)

    /** La frecuencia de la regla de aporte, o `null` si la meta no tiene regla. */
    fun frecuencia(meta: Meta): Frecuencia? = Frecuencia.entries.firstOrNull { it.clave == meta.frecuenciaAporte }

    /** Las metas en el [orden] pedido. Por fecha límite no se reordenan: así llegan de la base. */
    fun ordenar(metas: List<Meta>, orden: OrdenMetas): List<Meta> = when (orden) {
        OrdenMetas.FECHA_LIMITE -> metas
        OrdenMetas.MAYOR_AVANCE -> metas.sortedByDescending { progreso(it.ahorrado, it.objetivo) }
        OrdenMetas.NOMBRE -> metas.sortedBy { it.nombre.lowercase() }
    }

    /**
     * El consejo: de las metas pendientes que tienen fecha límite y regla de aporte, la de fecha más cercana,
     * con cuántos días antes (o después) se lograría siguiendo la regla desde [hoy].
     */
    fun consejo(metas: List<Meta>, hoy: Calendar): ConsejoMetas {
        if (metas.isEmpty()) return ConsejoMetas.SinMetas
        val pendientes = metas.filter { falta(it) > 0 }
        if (pendientes.isEmpty()) return ConsejoMetas.TodasLogradas
        val proyecciones = pendientes.mapNotNull { meta ->
            val frecuencia = frecuencia(meta) ?: return@mapNotNull null
            val aporte = meta.montoAporte?.takeIf { it > 0 } ?: return@mapNotNull null
            val limite = meta.fechaLimite?.let { leerFechaIso(it) } ?: return@mapNotNull null
            val diaLimite = inicioDelDia(Calendar.getInstance(hoy.timeZone).apply { time = limite })
            val diaLogro = fechaProyectada(falta(meta), aporte, frecuencia, hoy)
            diaLimite to ConsejoMetas.Proyeccion(meta, diasEntre(diaLogro, diaLimite), frecuencia)
        }
        return proyecciones.minByOrNull { it.first.timeInMillis }?.second ?: ConsejoMetas.SinRegla
    }

    /** El día en que se completaría [falta] aportando [aporte] en cada fecha de la [frecuencia], desde [hoy]. */
    fun fechaProyectada(falta: Double, aporte: Double, frecuencia: Frecuencia, hoy: Calendar): Calendar {
        val fecha = inicioDelDia(hoy)
        repeat(ceil(falta / aporte).toInt()) { pasarAlSiguienteAporte(fecha, frecuencia) }
        return fecha
    }

    /** Lo que hay que aportar cada vez para juntar [falta] en [aportes] veces, redondeado hacia arriba a miles. */
    fun montoAporte(falta: Double, aportes: Int): Long {
        val redondeo = Constantes.Metas.REDONDEO_APORTE
        return ceil(falta / aportes.coerceAtLeast(1) / redondeo).toLong() * redondeo
    }

    /**
     * Cuánto aportar con la [frecuencia] para lograr [meta] en su fecha límite. `null` si no tiene fecha,
     * ya está lograda o la fecha ya pasó.
     */
    fun aporteSugerido(meta: Meta, frecuencia: Frecuencia, hoy: Calendar): Long? {
        val falta = falta(meta)
        val limite = meta.fechaLimite?.let { leerFechaIso(it) }
        if (falta <= 0 || limite == null) return null
        val aportes = aportesHasta(Calendar.getInstance(hoy.timeZone).apply { time = limite }, frecuencia, hoy)
        return if (aportes > 0) montoAporte(falta, aportes) else null
    }

    /** Cuántas fechas de aporte de la [frecuencia] caen después de [hoy] y hasta [limite], incluido. */
    fun aportesHasta(limite: Calendar, frecuencia: Frecuencia, hoy: Calendar): Int {
        val fecha = inicioDelDia(hoy)
        val ultimoDia = inicioDelDia(limite)
        var aportes = 0
        while (true) {
            pasarAlSiguienteAporte(fecha, frecuencia)
            if (fecha.after(ultimoDia)) return aportes
            aportes++
        }
    }

    /**
     * Cuántas quincenas seguidas tienen al menos un aporte, contando hacia atrás desde la de [hoy].
     * Si la quincena actual aún no tiene aporte no rompe la cuenta (todavía hay tiempo): se empieza por la anterior.
     */
    fun quincenasSeguidas(fechasAportes: List<String>, hoy: Calendar): Int {
        val conAporte = fechasAportes
            .mapNotNull { leerFechaIso(it) }
            .map { numeroQuincena(Calendar.getInstance(hoy.timeZone).apply { time = it }) }
            .toSet()
        var quincena = numeroQuincena(hoy)
        if (quincena !in conAporte) quincena--
        var seguidas = 0
        while (quincena in conAporte) {
            seguidas++
            quincena--
        }
        return seguidas
    }

    /** Mes (0 = enero) y año de la fecha límite en la zona de [hoy], o `null` si no tiene. */
    fun mesYAno(meta: Meta, hoy: Calendar): Pair<Int, Int>? {
        val limite = meta.fechaLimite?.let { leerFechaIso(it) } ?: return null
        val fecha = Calendar.getInstance(hoy.timeZone).apply { time = limite }
        return fecha.get(Calendar.MONTH) to fecha.get(Calendar.YEAR)
    }

    /** Mueve [fecha] a la siguiente fecha de aporte: las quincenas son el 15 y el último día; los meses, el último día. */
    private fun pasarAlSiguienteAporte(fecha: Calendar, frecuencia: Frecuencia) {
        val dia = fecha.get(Calendar.DAY_OF_MONTH)
        val ultimoDia = fecha.getActualMaximum(Calendar.DAY_OF_MONTH)
        when (frecuencia) {
            Frecuencia.DIARIO -> fecha.add(Calendar.DAY_OF_YEAR, 1)
            Frecuencia.SEMANAL -> fecha.add(Calendar.DAY_OF_YEAR, Constantes.Metas.DIAS_POR_SEMANA)
            Frecuencia.QUINCENAL -> when {
                dia < Constantes.Metas.DIA_PRIMERA_QUINCENA -> fecha.set(Calendar.DAY_OF_MONTH, Constantes.Metas.DIA_PRIMERA_QUINCENA)
                dia < ultimoDia -> fecha.set(Calendar.DAY_OF_MONTH, ultimoDia)
                else -> {
                    fecha.set(Calendar.DAY_OF_MONTH, 1)
                    fecha.add(Calendar.MONTH, 1)
                    fecha.set(Calendar.DAY_OF_MONTH, Constantes.Metas.DIA_PRIMERA_QUINCENA)
                }
            }
            Frecuencia.MENSUAL -> {
                if (dia == ultimoDia) {
                    fecha.set(Calendar.DAY_OF_MONTH, 1)
                    fecha.add(Calendar.MONTH, 1)
                }
                fecha.set(Calendar.DAY_OF_MONTH, fecha.getActualMaximum(Calendar.DAY_OF_MONTH))
            }
        }
    }

    /** Número de la quincena contando desde el año 0: dos por mes (del 1 al 15 y del 16 al final). */
    private fun numeroQuincena(fecha: Calendar): Int {
        val mes = fecha.get(Calendar.YEAR) * Constantes.Metas.MESES_POR_ANO + fecha.get(Calendar.MONTH)
        val segunda = if (fecha.get(Calendar.DAY_OF_MONTH) > Constantes.Metas.DIA_PRIMERA_QUINCENA) 1 else 0
        return mes * Constantes.Metas.QUINCENAS_POR_MES + segunda
    }

    private fun inicioDelDia(fecha: Calendar): Calendar = (fecha.clone() as Calendar).apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    /** Días de [desde] a [hasta], los dos a medianoche (se redondea por si en medio hay cambio de horario). */
    private fun diasEntre(desde: Calendar, hasta: Calendar): Int =
        ((hasta.timeInMillis - desde.timeInMillis).toDouble() / Constantes.Metas.MILISEGUNDOS_POR_DIA).roundToInt()
}
