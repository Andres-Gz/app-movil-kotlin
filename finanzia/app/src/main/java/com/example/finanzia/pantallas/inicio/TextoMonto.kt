package com.example.finanzia.pantallas.inicio

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.finanzia.R
import com.example.finanzia.utilidades.formatearMonto
import com.example.finanzia.utilidades.formatearMontoEntero

/** Un monto con su signo, o puntos si los montos están ocultos. */
@Composable
internal fun textoMonto(valor: Double, visibles: Boolean, conSigno: Boolean = false, entero: Boolean = false): String {
    if (!visibles) return stringResource(R.string.inicio_monto_oculto)
    val cifras = if (entero) formatearMontoEntero(valor) else formatearMonto(valor)
    return when {
        !conSigno && valor >= 0 -> stringResource(R.string.inicio_monto, cifras)
        valor < 0 -> stringResource(R.string.inicio_monto_negativo, cifras)
        else -> stringResource(R.string.inicio_monto_positivo, cifras)
    }
}
