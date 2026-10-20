package com.example.finanzia.pantallas.metas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.example.finanzia.R
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos

/** "¡Vas con paso firme!": cuántas quincenas seguidas lleva aportando a sus metas. */
@Composable
internal fun TarjetaRacha(quincenas: Int) {
    val forma = RoundedCornerShape(Dimensiones.radioBoton)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(Dimensiones.elevacionCampo, forma, ambientColor = Colores.sombra, spotColor = Colores.sombra)
            .background(Brush.horizontalGradient(Colores.degradadoRacha), forma)
            .padding(horizontal = Dimensiones.rellenoHorizontalRacha, vertical = Dimensiones.rellenoVerticalRacha),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Mascota()
        Spacer(Modifier.width(Dimensiones.espacioMediano))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(if (quincenas > 0) R.string.metas_racha_titulo else R.string.metas_racha_vacia_titulo),
                style = EstilosTexto.tituloAviso,
                color = Colores.tinta,
            )
            Text(
                text = if (quincenas > 0) {
                    pluralStringResource(R.plurals.metas_racha, quincenas, quincenas)
                } else {
                    stringResource(R.string.metas_racha_vacia)
                },
                style = EstilosTexto.ayuda,
                color = Colores.textoApagado,
            )
        }
        Spacer(Modifier.width(Dimensiones.espacioPequeno))
        Icon(Iconos.tendencia, null, tint = Colores.verdeMenta, modifier = Modifier.size(Dimensiones.tamanoBaseIcono))
    }
}

/** La mascota en su círculo con la insignia verde de cumplido. El diseño trae una ilustración; aquí va un emoji. */
@Composable
private fun Mascota() {
    Box {
        Box(
            modifier = Modifier
                .size(Dimensiones.tamanoMascota)
                .background(Colores.celesteMascota, CircleShape)
                .border(Dimensiones.grosorBordeMascota, Colores.superficie, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = stringResource(R.string.metas_mascota), style = EstilosTexto.titulo)
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = Dimensiones.espacioMinimo, y = -Dimensiones.espacioMinimo)
                .size(Dimensiones.tamanoInsigniaMascota)
                .background(Colores.verdeSeguro, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Iconos.logroCumplido, null, tint = Colores.textoSobreColor, modifier = Modifier.size(Dimensiones.tamanoMarcaInsignia))
        }
    }
}
