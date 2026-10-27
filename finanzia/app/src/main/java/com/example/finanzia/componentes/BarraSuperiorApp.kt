package com.example.finanzia.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import com.example.finanzia.R
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos

/**
 * Logo con "Finanzia / Copiloto IA", la campana y el perfil. En el inicio el logo va a la izquierda y
 * los botones a la derecha; [centrada] lo junta todo al centro, como en el diseño del asistente.
 */
@Composable
fun BarraSuperiorApp(centrada: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimensiones.alturaBarraInicio)
            .padding(horizontal = Dimensiones.margenInicio),
        horizontalArrangement = if (centrada) Arrangement.Center else Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(Dimensiones.tamanoLogoApp)
                .background(Brush.linearGradient(Colores.degradadoBotonFrio), RoundedCornerShape(Dimensiones.radioLogoApp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Iconos.asistente, null, tint = Colores.textoSobreColor, modifier = Modifier.size(Dimensiones.tamanoIcono))
        }
        Spacer(Modifier.width(Dimensiones.espacioPequeno))
        Column(modifier = if (centrada) Modifier else Modifier.weight(1f)) {
            Text(text = stringResource(R.string.nombre_app), style = EstilosTexto.tituloAviso, color = Colores.tinta)
            Text(text = stringResource(R.string.inicio_copiloto), style = EstilosTexto.ayudaFuerte, color = Colores.verdeSeguro)
        }
        if (centrada) Spacer(Modifier.width(Dimensiones.espacioMediano))
        // Las notificaciones y el perfil aún no tienen pantalla.
        Box(modifier = Modifier.size(Dimensiones.tamanoBotonVolver), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Iconos.notificaciones,
                contentDescription = stringResource(R.string.accion_notificaciones),
                tint = Colores.tinta,
                modifier = Modifier.size(Dimensiones.tamanoIcono),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = Dimensiones.espacioPuntoEstado, y = -Dimensiones.espacioPuntoEstado)
                    .size(Dimensiones.tamanoPuntoAviso)
                    .background(Colores.azulMarca, CircleShape),
            )
        }
        Spacer(Modifier.width(Dimensiones.espacioPequeno))
        Box(
            modifier = Modifier
                .size(Dimensiones.tamanoBotonIcono)
                .background(Colores.azulIntenso, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Iconos.persona,
                contentDescription = stringResource(R.string.accion_perfil),
                tint = Colores.textoSobreColor,
                modifier = Modifier.size(Dimensiones.tamanoIconoCompacto),
            )
        }
    }
}
