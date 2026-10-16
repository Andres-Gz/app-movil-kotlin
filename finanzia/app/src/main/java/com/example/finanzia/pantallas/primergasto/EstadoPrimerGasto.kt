package com.example.finanzia.pantallas.primergasto

import androidx.annotation.StringRes
import com.example.finanzia.datos.modelos.CategoriaGasto

/** Todo lo que muestra la pantalla de registrar el primer gasto. */
data class EstadoPrimerGasto(
    /** Solo las cifras del monto, sin separadores (los pone la pantalla al mostrarlo). */
    val monto: String = "",
    val categoria: CategoriaGasto = CategoriaGasto.GASTOS_FIJOS,
    val cargando: Boolean = false,
    @param:StringRes val errorMonto: Int? = null,
    /** Aviso cuando Supabase no pudo guardar el gasto. */
    @param:StringRes val errorGeneral: Int? = null,
    /** Se vuelve `true` cuando el gasto quedó guardado. */
    val guardado: Boolean = false,
)
