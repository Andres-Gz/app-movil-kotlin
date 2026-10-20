package com.example.finanzia.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones

/** Botón redondo con solo un icono. Con [fondo], el círculo se rellena de ese color y lleva sombra suave. */
@Composable
fun BotonIcono(
    icono: ImageVector,
    descripcion: String,
    alPulsar: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Colores.iconoCampo,
    tamano: Dp = Dimensiones.tamanoBotonIcono,
    tamanoIcono: Dp = Dimensiones.tamanoIcono,
    fondo: Color? = null,
) {
    val circulo = if (fondo != null) {
        Modifier
            .shadow(Dimensiones.elevacionCampo, CircleShape, ambientColor = Colores.sombra, spotColor = Colores.sombra)
            .background(fondo, CircleShape)
    } else {
        Modifier
    }
    Box(
        modifier = modifier
            .size(tamano)
            .then(circulo)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClick = alPulsar),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icono,
            contentDescription = descripcion,
            tint = color,
            modifier = Modifier.size(tamanoIcono),
        )
    }
}
