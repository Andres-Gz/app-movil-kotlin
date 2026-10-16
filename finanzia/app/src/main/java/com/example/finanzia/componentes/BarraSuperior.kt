package com.example.finanzia.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.zIndex
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.Iconos

/**
 * Barra fija arriba de la pantalla: el botón para volver a la izquierda y el avatar de la cuenta
 * a la derecha. Su fondo llega por detrás de la barra de estado y su sombra cae sobre el contenido.
 */
@Composable
fun BarraSuperior(
    descripcionVolver: String,
    alVolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .zIndex(1f)
            .shadow(Dimensiones.elevacionCampo, RectangleShape, ambientColor = Colores.sombra, spotColor = Colores.sombra)
            .background(Colores.fondoLavanda)
            .statusBarsPadding()
            .height(Dimensiones.alturaBarraSuperior)
            .padding(start = Dimensiones.rellenoInicioBarra, end = Dimensiones.rellenoFinBarra),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BotonIcono(
            icono = Iconos.atras,
            descripcion = descripcionVolver,
            alPulsar = alVolver,
            color = Colores.tinta,
            tamano = Dimensiones.tamanoBotonVolverConFondo,
            tamanoIcono = Dimensiones.tamanoIconoGrande,
        )
        Spacer(Modifier.weight(1f))
        Avatar()
    }
}

/** Círculo azul con una persona: representa la cuenta. Es solo decorativo. */
@Composable
private fun Avatar() {
    Box(
        modifier = Modifier
            .size(Dimensiones.tamanoAvatar)
            .background(Colores.azulIntenso, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Iconos.persona,
            contentDescription = null,
            tint = Colores.textoSobreColor,
            modifier = Modifier.size(Dimensiones.tamanoIconoMediano),
        )
    }
}
