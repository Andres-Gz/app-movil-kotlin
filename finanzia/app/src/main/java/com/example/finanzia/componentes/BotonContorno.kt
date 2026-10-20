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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto

/** Botón secundario de ancho completo: blanco, con sombra suave y un icono opcional al inicio. */
@Composable
fun BotonContorno(
    texto: String,
    alPulsar: () -> Unit,
    modifier: Modifier = Modifier,
    iconoInicial: ImageVector? = null,
) {
    val forma = RoundedCornerShape(Dimensiones.radioBotonCompacto)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimensiones.alturaBoton)
            .shadow(Dimensiones.elevacionCampo, forma, ambientColor = Colores.sombra, spotColor = Colores.sombra)
            .background(Colores.superficie, forma)
            .clip(forma)
            .clickable(role = Role.Button, onClick = alPulsar),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (iconoInicial != null) {
            Icon(
                imageVector = iconoInicial,
                contentDescription = null,
                tint = Colores.grisContorno,
                modifier = Modifier.size(Dimensiones.tamanoIconoMediano),
            )
            Spacer(Modifier.width(Dimensiones.espacioPequeno))
        }
        Text(text = texto, style = EstilosTexto.boton, color = Colores.tinta)
    }
}
