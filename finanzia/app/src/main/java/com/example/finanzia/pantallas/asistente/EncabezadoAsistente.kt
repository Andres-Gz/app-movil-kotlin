package com.example.finanzia.pantallas.asistente

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import com.example.finanzia.R
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos

/** Volver, Finanzia IA con su estado, el historial y el menú ⋮ (nueva conversación o eliminar la actual). */
@Composable
internal fun EncabezadoAsistente(estado: EstadoAsistente, acciones: AccionesAsistente) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimensiones.alturaEncabezadoAsistente)
            .padding(horizontal = Dimensiones.margenPantallaAmplio),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BotonCircular(Iconos.volver, stringResource(R.string.accion_volver), acciones.alVolver)
        Spacer(Modifier.width(Dimensiones.espacioPequeno))
        AvatarIa(Dimensiones.tamanoAvatarEncabezado, activo = true)
        Spacer(Modifier.width(Dimensiones.espacioAvatarNombre))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(R.string.inicio_ia), style = EstilosTexto.tituloAviso, color = Colores.tinta)
                Spacer(Modifier.width(Dimensiones.espacioMinimo))
                Icon(Iconos.insigniaVerificada, null, tint = Colores.verdeSeguro, modifier = Modifier.size(Dimensiones.tamanoInsigniaNombre))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(Dimensiones.tamanoPuntoEstado).background(Colores.verdeSeguro, CircleShape))
                Spacer(Modifier.width(Dimensiones.espacioMinimo))
                Text(text = stringResource(R.string.asistente_estado), style = EstilosTexto.ayudaFuerte, color = Colores.verdeSeguro)
            }
        }
        BotonCircular(Iconos.historial, stringResource(R.string.accion_historial), acciones.alAbrirHistorial)
        Spacer(Modifier.width(Dimensiones.espacioPequeno))
        Box {
            BotonCircular(Iconos.masOpciones, stringResource(R.string.accion_mas_opciones), acciones.alAbrirMenu)
            DropdownMenu(
                expanded = estado.menuAbierto,
                onDismissRequest = acciones.alCerrarMenu,
                containerColor = Colores.superficie,
            ) {
                OpcionMenu(Iconos.conversar, stringResource(R.string.asistente_menu_nueva), Colores.tinta) {
                    acciones.alCerrarMenu()
                    acciones.alNuevaConversacion()
                }
                // Una conversación nueva todavía no está guardada: no hay qué eliminar.
                if (estado.idConversacion != null) {
                    OpcionMenu(Iconos.eliminar, stringResource(R.string.asistente_menu_eliminar), Colores.errorOscuro, acciones.alPedirEliminar)
                }
            }
        }
    }
}

/** El círculo de Finanzia IA. En el diseño va vacío (parece una imagen que no cargó). */
@Composable
internal fun AvatarIa(tamano: Dp, activo: Boolean = false) {
    Box(modifier = Modifier.size(tamano)) {
        Box(Modifier.size(tamano).background(Colores.azulPildora, CircleShape))
        if (activo) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(Dimensiones.tamanoPuntoActivo)
                    .border(Dimensiones.grosorBordePuntoActivo, Colores.fondoLavanda, CircleShape)
                    .padding(Dimensiones.grosorBordePuntoActivo)
                    .background(Colores.verdeSeguro, CircleShape),
            )
        }
    }
}

@Composable
private fun BotonCircular(icono: ImageVector, descripcion: String, alPulsar: () -> Unit) {
    Box(
        modifier = Modifier
            .size(Dimensiones.tamanoBotonVolver)
            .clip(CircleShape)
            .background(Colores.azulBotonClaro)
            .clickable(role = Role.Button, onClick = alPulsar),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icono, descripcion, tint = Colores.textoApagado, modifier = Modifier.size(Dimensiones.tamanoIcono))
    }
}

@Composable
internal fun OpcionMenu(icono: ImageVector, texto: String, color: Color, alPulsar: () -> Unit) {
    DropdownMenuItem(
        text = { Text(text = texto, style = EstilosTexto.botonSecundario, color = color) },
        leadingIcon = { Icon(icono, null, tint = color, modifier = Modifier.size(Dimensiones.tamanoIcono)) },
        onClick = alPulsar,
    )
}
