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
import com.example.finanzia.R
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto

/** Logo de texto: el nombre de la app en mayúsculas seguido de un punto de acento. */
@Composable
fun LogoMarca(modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(R.string.nombre_app).uppercase(),
            style = EstilosTexto.logo,
            color = Colores.azulMarca,
        )
        Spacer(Modifier.width(Dimensiones.espacioMinimo))
        Box(
            modifier = Modifier
                .size(Dimensiones.tamanoPuntoLogo)
                .background(Colores.verdeAcento, CircleShape),
        )
    }
}
