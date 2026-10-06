package com.example.finanzia.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos

/** Casilla de verificación redondeada con su texto; toda la fila es pulsable. */
@Composable
fun CasillaVerificacion(
    texto: String,
    marcada: Boolean,
    alCambiar: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val forma = RoundedCornerShape(Dimensiones.radioCasilla)
    val aspecto = if (marcada) {
        Modifier.background(Colores.verdeMarca, forma)
    } else {
        Modifier
            .background(Colores.superficie, forma)
            .border(Dimensiones.grosorBordeCasilla, Colores.textoTenue, forma)
    }
    Row(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .toggleable(value = marcada, role = Role.Checkbox, onValueChange = alCambiar),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(Dimensiones.tamanoCasilla)
                .then(aspecto),
            contentAlignment = Alignment.Center,
        ) {
            if (marcada) {
                Icon(
                    imageVector = Iconos.marcaVerificacion,
                    contentDescription = null,
                    tint = Colores.textoSobreColor,
                    modifier = Modifier.size(Dimensiones.tamanoIconoPequeno),
                )
            }
        }
        Spacer(Modifier.width(Dimensiones.espacioPequeno))
        Text(text = texto, style = EstilosTexto.etiqueta, color = Colores.textoSecundario)
    }
}
