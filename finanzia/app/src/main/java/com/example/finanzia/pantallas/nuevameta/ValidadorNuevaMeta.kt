package com.example.finanzia.pantallas.nuevameta

import androidx.annotation.StringRes
import com.example.finanzia.R
import java.util.Calendar

/** Revisa el formulario de la meta; devuelve el primer problema o `null` si está completo. */
object ValidadorNuevaMeta {

    /** El mes ya pasado solo puede aparecer al editar una meta vencida: hay que elegir otro. */
    @StringRes
    fun validar(estado: EstadoNuevaMeta, hoy: Calendar): Int? {
        val mes = estado.mes
        return when {
            estado.nombre.isBlank() -> R.string.error_meta_nombre_vacio
            (estado.monto.toLongOrNull() ?: 0L) <= 0L -> R.string.error_meta_monto_vacio
            mes == null -> R.string.error_meta_fecha_vacia
            !CalculosNuevaMeta.disponible(mes, hoy) -> R.string.error_meta_fecha_pasada
            else -> null
        }
    }
}
