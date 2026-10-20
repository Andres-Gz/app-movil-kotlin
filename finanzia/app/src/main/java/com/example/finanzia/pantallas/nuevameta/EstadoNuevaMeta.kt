package com.example.finanzia.pantallas.nuevameta

import androidx.annotation.StringRes
import com.example.finanzia.configuracion.Constantes
import com.example.finanzia.datos.modelos.CategoriaMeta
import com.example.finanzia.datos.modelos.Frecuencia
import com.example.finanzia.pantallas.metas.CalculosMetas
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/** Un mes de un año; [mes] va de 0 (enero) a 11, como en [Calendar]. */
data class MesAno(val mes: Int, val ano: Int)

/** Todo lo que muestra la pantalla de nueva meta (o de editar una meta, si [editando]). */
data class EstadoNuevaMeta(
    val editando: Boolean = false,
    val nombre: String = "",
    val categoria: CategoriaMeta = CategoriaMeta.VIAJES,
    /** Solo las cifras del monto, sin separadores (los pone la pantalla al mostrarlo). */
    val monto: String = "",
    /** El último botón "+$…" que se pulsó, para resaltarlo; `null` si el monto se escribió a mano. */
    val incrementoElegido: Long? = null,
    /** El mes en que se quiere lograr la meta; `null` mientras no se elige. */
    val mes: MesAno? = null,
    val frecuencia: Frecuencia = Frecuencia.QUINCENAL,
    /** Si está abierto el selector del mes. */
    val selectorAbierto: Boolean = false,
    val cargando: Boolean = false,
    @param:StringRes val error: Int? = null,
    /** El error viene de Supabase (y no de un campo vacío): cambia el aspecto del aviso. */
    val errorDelServidor: Boolean = false,
    /** Se vuelve `true` cuando la meta quedó guardada. */
    val guardada: Boolean = false,
)

/** Cuentas de la nueva meta, aparte para probarlas sin pantalla. */
object CalculosNuevaMeta {

    /** El primer mes que se puede elegir: el siguiente al de [hoy], para que haya al menos un aporte. */
    fun primerMes(hoy: Calendar): MesAno {
        val siguiente = (hoy.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, 1)
        }
        return MesAno(siguiente.get(Calendar.MONTH), siguiente.get(Calendar.YEAR))
    }

    /** Si [mes] se puede elegir: desde el siguiente al de [hoy]. */
    fun disponible(mes: MesAno, hoy: Calendar): Boolean = numeroMes(mes) >= numeroMes(primerMes(hoy))

    /** Meses que faltan desde el de [hoy] hasta [mes] (de marzo a noviembre: 8). */
    fun mesesRestantes(mes: MesAno, hoy: Calendar): Int =
        numeroMes(mes) - numeroMes(MesAno(hoy.get(Calendar.MONTH), hoy.get(Calendar.YEAR)))

    /** Cuántos aportes de la [frecuencia] caen desde mañana hasta el último día de [mes]. */
    fun aportes(mes: MesAno, frecuencia: Frecuencia, hoy: Calendar): Int =
        CalculosMetas.aportesHasta(ultimoDia(mes, hoy), frecuencia, hoy)

    /** La fecha límite que se guarda: el último día de [mes], en formato `yyyy-MM-dd`. */
    fun fechaLimite(mes: MesAno, hoy: Calendar): String =
        SimpleDateFormat(Constantes.Metas.PATRON_FECHA_LIMITE, Locale.US)
            .apply { timeZone = hoy.timeZone }
            .format(ultimoDia(mes, hoy).time)

    private fun ultimoDia(mes: MesAno, hoy: Calendar): Calendar = Calendar.getInstance(hoy.timeZone).apply {
        clear()
        set(mes.ano, mes.mes, 1)
        set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
    }

    private fun numeroMes(mes: MesAno): Int = mes.ano * Constantes.Metas.MESES_POR_ANO + mes.mes
}
