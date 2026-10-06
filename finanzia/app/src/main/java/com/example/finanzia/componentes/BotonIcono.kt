package com.example.finanzia.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones

/** Botón redondo con un icono. Con [conFondo] se dibuja sobre un círculo blanco con sombra. */
@Composable
fun BotonIcono(
    icono: ImageVector,
    descripcion: String,
    alPulsar: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Colores.textoSecundario,
    conFondo: Boolean = false,
) {
    val fondo = if (conFondo) {
        Modifier
            .shadow(Dimensiones.elevacionBotonIcono, CircleShape, ambientColor = Colores.sombra, spotColor = Colores.sombra)
            .background(Colores.superficie, CircleShape)
    } else {
        Modifier
    }
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .size(Dimensiones.tamanoBotonIcono)
            .then(fondo)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClick = alPulsar),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icono,
            contentDescription = descripcion,
            tint = color,
            modifier = Modifier.size(Dimensiones.tamanoIcono),
        )
    }
}
