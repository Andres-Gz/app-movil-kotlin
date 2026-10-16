package com.example.finanzia.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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

/**
 * Recuadro rojo con un icono y un mensaje, para errores que afectan a todo el formulario.
 * Los del servidor llevan el borde y el texto más intensos, como en el diseño.
 */
@Composable
fun AvisoError(
    mensaje: String,
    origen: OrigenError,
    modifier: Modifier = Modifier,
) {
    val forma = RoundedCornerShape(Dimensiones.radioAviso)
    val delFormulario = origen == OrigenError.FORMULARIO
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Colores.errorFondo, forma)
            .border(Dimensiones.grosorBorde, if (delFormulario) Colores.errorBordeSuave else Colores.errorClaro, forma)
            .padding(horizontal = Dimensiones.rellenoHorizontalAviso, vertical = Dimensiones.rellenoVerticalAviso)
            .semantics { liveRegion = LiveRegionMode.Polite },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Iconos.error,
            contentDescription = null,
            tint = Colores.error,
            modifier = Modifier.size(Dimensiones.tamanoIconoMediano),
        )
        Spacer(Modifier.width(Dimensiones.espacioPequeno))
        Text(
            text = mensaje,
            style = EstilosTexto.nota,
            color = if (delFormulario) Colores.errorMuyOscuro else Colores.errorOscuro,
        )
    }
}
