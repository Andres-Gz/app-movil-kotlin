package com.example.finanzia.componentes

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.finanzia.R
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos

/** Barra superior: botón de volver, título y acciones opcionales a la derecha. */
@Composable
fun BarraSuperior(
    titulo: String,
    alVolver: () -> Unit,
    modifier: Modifier = Modifier,
    acciones: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(Dimensiones.alturaBarraSuperior)
            .padding(start = Dimensiones.espacioPequeno, end = Dimensiones.espacioMediano),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BotonIcono(
            icono = Iconos.volver,
            descripcion = stringResource(R.string.accion_volver),
            alPulsar = alVolver,
            color = Colores.textoPrincipal,
        )
        Spacer(Modifier.width(Dimensiones.espacioPequeno))
        Text(
            text = titulo,
            style = EstilosTexto.tituloBarra,
            color = Colores.textoPrincipal,
            modifier = Modifier.weight(1f),
        )
        acciones()
    }
}
