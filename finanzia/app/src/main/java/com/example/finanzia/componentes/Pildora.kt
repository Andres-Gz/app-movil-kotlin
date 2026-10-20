package com.example.finanzia.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto

/**
 * Etiqueta redondeada con un texto corto y un icono opcional, por ejemplo el paso actual.
 * El texto va en mayúsculas salvo que [mayusculas] sea `false`. Los colores, el alto y la forma
 * cambian según el diseño de cada pantalla.
 */
@Composable
fun Pildora(
    texto: String,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null,
    colorFondo: Color = Colores.rellenoCampo,
    colorContenido: Color = Colores.verdeSeguro,
    rellenoHorizontal: Dp = Dimensiones.rellenoHorizontalPildora,
    rellenoVertical: Dp = Dimensiones.rellenoVerticalPildora,
    tamanoIcono: Dp = Dimensiones.tamanoIconoMediano,
    mayusculas: Boolean = true,
    forma: Shape = CircleShape,
) {
    Row(
        modifier = modifier
            .background(colorFondo, forma)
            .padding(horizontal = rellenoHorizontal, vertical = rellenoVertical),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icono != null) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = colorContenido,
                modifier = Modifier.size(tamanoIcono),
            )
            Spacer(Modifier.width(Dimensiones.espacioIconoTextoPildora))
        }
        Text(
            text = if (mayusculas) texto.uppercase() else texto,
            style = EstilosTexto.ayudaFuerte,
            color = colorContenido,
        )
    }
}
