package com.example.finanzia.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto

/**
 * Botón principal de ancho completo con fondo en degradado e iconos opcionales al inicio y al final.
 * Con [cargando] muestra un indicador de progreso en lugar del icono y no se puede pulsar.
 */
@Composable
fun BotonDegradado(
    texto: String,
    alPulsar: () -> Unit,
    modifier: Modifier = Modifier,
    iconoFinal: ImageVector? = null,
    iconoInicial: ImageVector? = null,
    cargando: Boolean = false,
    tamanoIcono: Dp = Dimensiones.tamanoIconoMediano,
    espacioIcono: Dp = Dimensiones.espacioPequeno,
    radio: Dp = Dimensiones.radioBoton,
    altura: Dp = Dimensiones.alturaBoton,
    colorSombra: Color = Colores.sombra,
    colores: List<Color> = Colores.degradadoBoton,
) {
    val forma = RoundedCornerShape(radio)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(altura)
            .shadow(Dimensiones.elevacionBoton, forma, ambientColor = colorSombra, spotColor = colorSombra)
            .background(Brush.horizontalGradient(colores), forma)
            .clip(forma)
            .clickable(enabled = !cargando, role = Role.Button, onClick = alPulsar),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (iconoInicial != null) {
            Icon(
                imageVector = iconoInicial,
                contentDescription = null,
                tint = Colores.textoSobreColor,
                modifier = Modifier.size(tamanoIcono),
            )
            Spacer(Modifier.width(espacioIcono))
        }
        Text(text = texto, style = EstilosTexto.boton, color = Colores.textoSobreColor)
        if (cargando) {
            Spacer(Modifier.width(espacioIcono))
            CircularProgressIndicator(
                color = Colores.textoSobreColor,
                strokeWidth = Dimensiones.grosorIndicadorCarga,
                modifier = Modifier.size(tamanoIcono),
            )
        } else if (iconoFinal != null) {
            Spacer(Modifier.width(espacioIcono))
            Icon(
                imageVector = iconoFinal,
                contentDescription = null,
                tint = Colores.textoSobreColor,
                modifier = Modifier.size(tamanoIcono),
            )
        }
    }
}
