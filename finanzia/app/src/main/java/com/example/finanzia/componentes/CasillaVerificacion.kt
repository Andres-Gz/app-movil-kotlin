package com.example.finanzia.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.Iconos

/**
 * Casilla cuadrada: vacía con borde gris, o verde con una marca cuando está [marcada].
 * Solo se dibuja; quien la usa hace pulsable la fila completa (casilla y texto).
 */
@Composable
fun CasillaVerificacion(
    marcada: Boolean,
    modifier: Modifier = Modifier,
    conError: Boolean = false,
) {
    val forma = RoundedCornerShape(Dimensiones.radioCasilla)
    val colorBorde = when {
        marcada -> Colores.verdeMarca
        conError -> Colores.error
        else -> Colores.grisClaro
    }
    Box(
        modifier = modifier
            .size(Dimensiones.tamanoCasilla)
            .background(if (marcada) Colores.verdeMarca else Colores.superficie, forma)
            .border(Dimensiones.grosorBordeCasilla, colorBorde, forma),
        contentAlignment = Alignment.Center,
    ) {
        if (marcada) {
            Icon(
                imageVector = Iconos.marcaVerificacion,
                contentDescription = null,
                tint = Colores.textoSobreColor,
                modifier = Modifier.size(Dimensiones.tamanoMarcaCasilla),
            )
        }
    }
}
