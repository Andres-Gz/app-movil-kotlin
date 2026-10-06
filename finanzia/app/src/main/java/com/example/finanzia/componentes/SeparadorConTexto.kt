package com.example.finanzia.componentes

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto

/** Línea divisoria horizontal con un texto en el centro. */
@Composable
fun SeparadorConTexto(texto: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = Dimensiones.grosorBorde,
            color = Colores.divisor,
        )
        Text(
            text = texto,
            style = EstilosTexto.nota,
            color = Colores.textoTenue,
            modifier = Modifier.padding(horizontal = Dimensiones.espacioMediano),
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = Dimensiones.grosorBorde,
            color = Colores.divisor,
        )
    }
}
