package com.example.finanzia.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto

/** Botón pequeño de fondo azul claro con un icono y un texto, para acciones de apoyo. */
@Composable
fun BotonSuave(
    texto: String,
    icono: ImageVector,
    alPulsar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(Dimensiones.alturaBotonSuave)
            .clip(RoundedCornerShape(Dimensiones.radioCampo))
            .background(Colores.azulBotonClaro)
            .clickable(role = Role.Button, onClick = alPulsar)
            .padding(horizontal = Dimensiones.rellenoHorizontalBotonSuave),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = Colores.azulIntenso,
            modifier = Modifier.size(Dimensiones.tamanoIcono),
        )
        Spacer(Modifier.width(Dimensiones.espacioPequeno))
        Text(text = texto, style = EstilosTexto.botonSecundario, color = Colores.azulIntenso)
    }
}
