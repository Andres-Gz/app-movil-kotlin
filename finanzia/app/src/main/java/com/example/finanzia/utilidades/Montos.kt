package com.example.finanzia.utilidades

import androidx.annotation.StringRes
import com.example.finanzia.R
import com.example.finanzia.configuracion.Constantes

/** Deja solo las cifras de un monto escrito, sin ceros al inicio ni pasarse del máximo. */
fun limpiarMonto(texto: String): String =
    texto.filter { it.isDigit() }.trimStart('0').take(Constantes.Validacion.CIFRAS_MAXIMAS_MONTO)

/** Revisa un monto ya limpio: que exista y sea mayor que cero. */
@StringRes
fun validarMonto(monto: String): Int? = when {
    monto.isEmpty() -> R.string.error_monto_vacio
    (monto.toLongOrNull() ?: 0L) <= 0L -> R.string.error_monto_cero
    else -> null
}
