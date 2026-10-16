package com.example.finanzia.pantallas.inicio

import androidx.annotation.StringRes
import com.example.finanzia.configuracion.Constantes
import com.example.finanzia.datos.modelos.MetaActual
import com.example.finanzia.datos.modelos.ResumenInicio
import kotlin.math.max
import kotlin.math.min

/** Todo lo que muestra el inicio. Mientras no llega el resumen, [resumen] es `null`. */
data class EstadoInicio(
    val cargando: Boolean = false,
    val resumen: ResumenInicio? = null,
    @param:StringRes val error: Int? = null,
    /** El ojo del balance: muestra u oculta los montos. */
    val montosVisibles: Boolean = true,
)

/** Cuentas que hace el inicio con el resumen, aparte para probarlas sin pantalla. */
object CalculosInicio {

    /** Qué parte de los ingresos queda después de los gastos, en porcentaje; `null` sin ingresos. */
    fun porcentajeAhorro(resumen: ResumenInicio): Double? =
        if (resumen.ingresos > 0) (resumen.ingresos - resumen.gastos) / resumen.ingresos * Constantes.Formatos.CIEN else null

    /** Lo que se ahorraría recortando los gastos en [Constantes.Formatos.RECORTE_SUGERIDO]. */
    fun ahorroSugerido(resumen: ResumenInicio): Double = resumen.gastos * Constantes.Formatos.RECORTE_SUGERIDO

    /** Avance de la meta entre 0 y 1. */
    fun progreso(meta: MetaActual): Float =
        if (meta.objetivo > 0) min(meta.ahorrado / meta.objetivo, 1.0).toFloat() else 0f

    fun falta(meta: MetaActual): Double = max(meta.objetivo - meta.ahorrado, 0.0)
}
