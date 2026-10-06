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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto

/**
 * Botón principal de ancho completo con fondo en degradado y un icono opcional al final.
 * Con [cargando] muestra un indicador de progreso en lugar del icono y no se puede pulsar.
 */
@Composable
fun BotonDegradado(
    texto: String,
    alPulsar: () -> Unit,
    modifier: Modifier = Modifier,
    iconoFinal: ImageVector? = null,
    cargando: Boolean = false,
) {
    val forma = RoundedCornerShape(Dimensiones.radioBoton)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimensiones.alturaBoton)
            .shadow(Dimensiones.elevacionBoton, forma, ambientColor = Colores.sombraBoton, spotColor = Colores.sombraBoton)
            .background(Brush.horizontalGradient(Colores.degradadoBoton), forma)
            .clip(forma)
            .clickable(enabled = !cargando, role = Role.Button, onClick = alPulsar),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = texto, style = EstilosTexto.cuerpoDestacado, color = Colores.textoSobreColor)
        if (cargando) {
            Spacer(Modifier.width(Dimensiones.espacioPequeno))
            CircularProgressIndicator(
                color = Colores.textoSobreColor,
                strokeWidth = Dimensiones.grosorIndicadorCarga,
                modifier = Modifier.size(Dimensiones.tamanoIconoPequeno),
            )
        } else if (iconoFinal != null) {
            Spacer(Modifier.width(Dimensiones.espacioPequeno))
            Icon(
                imageVector = iconoFinal,
                contentDescription = null,
                tint = Colores.textoSobreColor,
                modifier = Modifier.size(Dimensiones.tamanoIcono),
            )
        }
    }
}
