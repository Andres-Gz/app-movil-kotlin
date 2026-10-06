package com.example.finanzia.componentes

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.EstilosTexto

/** Texto pulsable con aspecto de enlace. */
@Composable
fun TextoEnlace(
    texto: String,
    alPulsar: () -> Unit,
    modifier: Modifier = Modifier,
    estilo: TextStyle = EstilosTexto.etiquetaDestacada,
) {
    Text(
        text = texto,
        style = estilo,
        color = Colores.azulMarca,
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clickable(role = Role.Button, onClick = alPulsar),
    )
}
