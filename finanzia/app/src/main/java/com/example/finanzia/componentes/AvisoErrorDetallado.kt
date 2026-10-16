package com.example.finanzia.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos

/** Aviso de error grande: fondo rosa, un círculo rojo con el icono, un título y una explicación debajo. */
@Composable
fun AvisoErrorDetallado(
    titulo: String,
    mensaje: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Colores.errorContenedor, RoundedCornerShape(Dimensiones.radioAvisoGrande))
            .padding(Dimensiones.rellenoAvisoDetallado)
            .semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite },
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(Dimensiones.tamanoCirculoAviso)
                .background(Colores.errorIntenso, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Iconos.errorContorno,
                contentDescription = null,
                tint = Colores.textoSobreColor,
                modifier = Modifier.size(Dimensiones.tamanoIconoCompacto),
            )
        }
        Spacer(Modifier.width(Dimensiones.espacioIconoTextoAviso))
        Column {
            Text(text = titulo, style = EstilosTexto.tituloAviso, color = Colores.errorProfundo)
            Spacer(Modifier.height(Dimensiones.espacioTituloMensajeAviso))
            Text(text = mensaje, style = EstilosTexto.cuerpo, color = Colores.errorProfundoSuave)
        }
    }
}
