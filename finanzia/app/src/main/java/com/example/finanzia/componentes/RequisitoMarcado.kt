package com.example.finanzia.componentes

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
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
import com.example.finanzia.tema.Iconos

/** Un requisito con un círculo al inicio: verde con marca cuando se cumple, vacío y gris mientras no. */
@Composable
fun RequisitoMarcado(
    texto: String,
    cumplido: Boolean,
    modifier: Modifier = Modifier,
) {
    val estado = stringResource(if (cumplido) R.string.estado_requisito_cumplido else R.string.estado_requisito_pendiente)
    Row(
        modifier = modifier.semantics(mergeDescendants = true) { stateDescription = estado },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = if (cumplido) Iconos.requisitoCumplido else Iconos.requisitoPendiente,
            contentDescription = null,
            tint = if (cumplido) Colores.verdeSeguro else Colores.grisContorno,
            modifier = Modifier.size(Dimensiones.tamanoIconoMediano),
        )
        Spacer(Modifier.width(Dimensiones.espacioIconoRequisito))
        Text(
            text = texto,
            style = EstilosTexto.ayuda,
            color = if (cumplido) Colores.verdeSeguro else Colores.textoApagado,
        )
    }
}
