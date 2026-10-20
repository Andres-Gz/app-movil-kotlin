package com.example.finanzia.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto

/** Botón redondeado con un monto ("+$500.000", "Usar $195.000"…); el elegido va en verde menta. */
@Composable
fun ChipMonto(texto: String, alPulsar: () -> Unit, modifier: Modifier = Modifier, elegido: Boolean = false) {
    Box(
        modifier = modifier
            .height(Dimensiones.alturaIncremento)
            .clip(CircleShape)
            .background(if (elegido) Colores.verdeMenta else Colores.azulBotonClaro)
            .clickable(role = Role.Button, onClick = alPulsar)
            .padding(horizontal = Dimensiones.rellenoHorizontalIncremento),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = texto, style = EstilosTexto.nota, color = if (elegido) Colores.verdeSobreMenta else Colores.tinta)
    }
}
