package com.example.finanzia.componentes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones

/**
 * Fondo del registro: blanco, con un brillo lavanda en la esquina inferior izquierda y uno
 * menta en la inferior derecha. Llega hasta el borde de la pantalla, por detrás de las barras del sistema.
 */
@Composable
fun FondoConBrillos(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(Colores.superficie)
        drawRect(
            Brush.radialGradient(
                *Colores.brilloLavanda,
                center = Offset(0f, size.height),
                radius = Dimensiones.radioBrilloLavanda.toPx(),
            ),
        )
        drawRect(
            Brush.radialGradient(
                *Colores.brilloMenta,
                center = Offset(size.width, size.height),
                radius = Dimensiones.radioBrilloMenta.toPx(),
            ),
        )
    }
}
