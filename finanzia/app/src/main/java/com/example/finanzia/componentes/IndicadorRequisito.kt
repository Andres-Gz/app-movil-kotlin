package com.example.finanzia.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import com.example.finanzia.R
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto

/** Un requisito con un punto al inicio: verde cuando se cumple, gris mientras no. */
@Composable
fun IndicadorRequisito(
    texto: String,
    cumplido: Boolean,
    modifier: Modifier = Modifier,
) {
    val estado = stringResource(if (cumplido) R.string.estado_requisito_cumplido else R.string.estado_requisito_pendiente)
    Row(
        modifier = modifier.semantics(mergeDescendants = true) { stateDescription = estado },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(Dimensiones.tamanoPuntoRequisito)
                .background(if (cumplido) Colores.verdeMarca else Colores.grisClaro, CircleShape),
        )
        Spacer(Modifier.width(Dimensiones.espacioMinimo))
        Text(text = texto, style = EstilosTexto.ayuda, color = Colores.textoSecundario)
    }
}
