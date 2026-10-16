package com.example.finanzia.componentes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones

/**
 * Fondo del inicio de sesión: el color de fondo, un brillo verde en cada esquina
 * de abajo y dos olas verdes en la esquina inferior derecha. Llega hasta el borde de la
 * pantalla, por detrás de las barras del sistema.
 */
@Composable
fun FondoConOlas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(Colores.fondo)
        drawRect(
            Brush.radialGradient(
                *Colores.brilloEsquinaIzquierda,
                center = Offset(0f, size.height),
                radius = Dimensiones.radioBrilloIzquierdo.toPx(),
            ),
        )
        drawRect(
            Brush.radialGradient(
                *Colores.brilloEsquinaDerecha,
                center = Offset(size.width, size.height),
                radius = Dimensiones.radioBrilloDerecho.toPx(),
            ),
        )
        dibujarOla(Dimensiones.anchoOlaClara, Dimensiones.altoOlaClara, Colores.olaClara)
        dibujarOla(Dimensiones.anchoOlaOscura, Dimensiones.altoOlaOscura, Colores.olaOscura)
    }
}

/** Ola que sube desde el borde de abajo ([ancho] antes de la esquina) hasta el borde derecho ([alto]). */
private fun DrawScope.dibujarOla(ancho: Dp, alto: Dp, color: Color) {
    val ola = Path().apply {
        moveTo(size.width - ancho.toPx(), size.height)
        lineTo(size.width, size.height - alto.toPx())
        lineTo(size.width, size.height)
        close()
    }
    drawPath(ola, color)
}
