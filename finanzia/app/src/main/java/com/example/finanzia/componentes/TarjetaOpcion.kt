package com.example.finanzia.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos

/** Colores de una tarjeta de opción: el acento marca la elegida (borde y círculo), con sus fondos. */
class ColoresOpcion(
    val acento: Color,
    val fondoElegida: Color,
    val fondoEmojiElegida: Color,
)

/**
 * Tarjeta de una opción entre varias (por ejemplo, la frecuencia o la categoría): emoji arriba con
 * la marca a la derecha, y abajo el nombre y el detalle. La elegida se pinta con [colores].
 */
@Composable
fun TarjetaOpcion(
    emoji: String,
    titulo: String,
    detalle: String,
    elegida: Boolean,
    alElegir: () -> Unit,
    fondoEmoji: Color,
    colores: ColoresOpcion,
    modifier: Modifier = Modifier,
    alturaMinima: Dp = Dimensiones.alturaFrecuencia,
    relleno: Dp = Dimensiones.rellenoFrecuencia,
    tamanoRecuadro: Dp = Dimensiones.tamanoRecuadroIcono,
    radioRecuadro: Dp = Dimensiones.radioRecuadroFrecuencia,
) {
    val forma = RoundedCornerShape(Dimensiones.radioBoton)
    Column(
        modifier = modifier
            .heightIn(min = alturaMinima)
            .shadow(Dimensiones.elevacionCampo, forma, ambientColor = Colores.sombra, spotColor = Colores.sombra)
            .background(if (elegida) colores.fondoElegida else Colores.superficie, forma)
            .border(
                if (elegida) Dimensiones.grosorMarcaVacia else Dimensiones.grosorBorde,
                if (elegida) colores.acento else Colores.bordeCampo,
                forma,
            )
            .clip(forma)
            .selectable(selected = elegida, role = Role.RadioButton, onClick = alElegir)
            .padding(relleno),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(tamanoRecuadro)
                    .background(if (elegida) colores.fondoEmojiElegida else fondoEmoji, RoundedCornerShape(radioRecuadro)),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = emoji, style = EstilosTexto.cuerpo)
            }
            Spacer(Modifier.weight(1f))
            MarcaEleccion(elegida = elegida, acento = colores.acento)
        }
        Spacer(Modifier.weight(1f).heightIn(min = Dimensiones.espacioPequeno))
        Text(text = titulo, style = EstilosTexto.boton, color = Colores.azulProfundo)
        Text(text = detalle, style = EstilosTexto.ayuda, color = Colores.textoSecundario)
    }
}

/** Círculo del color de acento con la marca si está elegida; si no, solo el contorno gris. */
@Composable
private fun MarcaEleccion(elegida: Boolean, acento: Color) {
    if (elegida) {
        Box(
            modifier = Modifier
                .size(Dimensiones.tamanoMarcaFrecuencia)
                .background(acento, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Iconos.marcaVerificacion,
                contentDescription = null,
                tint = Colores.textoSobreColor,
                modifier = Modifier.size(Dimensiones.tamanoIconoPequeno),
            )
        }
    } else {
        Box(
            modifier = Modifier
                .size(Dimensiones.tamanoMarcaFrecuencia)
                .border(Dimensiones.grosorMarcaVacia, Colores.grisClaro, CircleShape),
        )
    }
}
