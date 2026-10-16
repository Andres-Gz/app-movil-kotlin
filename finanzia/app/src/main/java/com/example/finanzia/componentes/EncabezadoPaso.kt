package com.example.finanzia.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos

/** Botón blanco de volver a la izquierda y el paso actual centrado, en una píldora con borde. */
@Composable
fun EncabezadoPaso(
    paso: String,
    descripcionVolver: String,
    alVolver: () -> Unit,
    colorTexto: Color,
    colorFondo: Color,
    colorBorde: Color,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        val forma = RoundedCornerShape(Dimensiones.radioBotonVolverIngreso)
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(Dimensiones.tamanoBotonVolverIngreso)
                .shadow(Dimensiones.elevacionCampo, forma, ambientColor = Colores.sombra, spotColor = Colores.sombra)
                .background(Colores.superficie, forma)
                .clip(forma)
                .clickable(role = Role.Button, onClick = alVolver),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Iconos.cerrar,
                contentDescription = descripcionVolver,
                tint = Colores.textoPrincipal,
                modifier = Modifier.size(Dimensiones.tamanoIconoCompacto),
            )
        }
        Text(
            text = paso,
            style = EstilosTexto.nota,
            color = colorTexto,
            modifier = Modifier
                .background(colorFondo, CircleShape)
                .border(Dimensiones.grosorBorde, colorBorde, CircleShape)
                .padding(horizontal = Dimensiones.rellenoHorizontalPildora, vertical = Dimensiones.rellenoVerticalPildoraMediana),
        )
    }
}
