package com.example.finanzia.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto

/**
 * Botón de ancho completo sin borde: solo el texto, para acciones como cancelar.
 * Por defecto no tiene fondo; con [fondo] queda como un botón liso de ese color.
 */
@Composable
fun BotonTexto(
    texto: String,
    alPulsar: () -> Unit,
    modifier: Modifier = Modifier,
    fondo: Color = Colores.transparente,
    colorTexto: Color = Colores.textoApagado,
    altura: Dp = Dimensiones.alturaBoton,
    radio: Dp = Dimensiones.radioBotonCompacto,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(altura)
            .clip(RoundedCornerShape(radio))
            .background(fondo)
            .clickable(role = Role.Button, onClick = alPulsar),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = texto, style = EstilosTexto.botonSecundario, color = colorTexto)
    }
}
