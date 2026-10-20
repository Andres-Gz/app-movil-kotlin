package com.example.finanzia.pantallas.primeringreso

import androidx.annotation.StringRes
import com.example.finanzia.datos.modelos.Frecuencia

/** Todo lo que muestra la pantalla de registrar el primer ingreso. */
data class EstadoPrimerIngreso(
    /** Solo las cifras del monto, sin separadores (los pone la pantalla al mostrarlo). */
    val monto: String = "",
    val frecuencia: Frecuencia = Frecuencia.MENSUAL,
    val cargando: Boolean = false,
    @param:StringRes val errorMonto: Int? = null,
    /** Aviso cuando Supabase no pudo guardar el ingreso. */
    @param:StringRes val errorGeneral: Int? = null,
    /** Se vuelve `true` cuando el ingreso quedó guardado. */
    val guardado: Boolean = false,
)
