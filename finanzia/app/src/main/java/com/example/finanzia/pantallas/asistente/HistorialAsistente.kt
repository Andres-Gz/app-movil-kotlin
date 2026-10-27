package com.example.finanzia.pantallas.asistente

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import com.example.finanzia.R
import com.example.finanzia.componentes.AvisoError
import com.example.finanzia.componentes.BotonSuave
import com.example.finanzia.componentes.OrigenError
import com.example.finanzia.datos.modelos.ConversacionAsistente
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos
import java.util.Date

/** Las conversaciones guardadas, en una hoja que sube desde abajo. Tocar una la abre. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HistorialAsistente(estado: EstadoAsistente, acciones: AccionesAsistente, ahora: Date) {
    ModalBottomSheet(onDismissRequest = acciones.alCerrarHistorial, containerColor = Colores.superficie) {
        Column(modifier = Modifier.padding(horizontal = Dimensiones.margenPantalla)) {
            Text(text = stringResource(R.string.asistente_historial_titulo), style = EstilosTexto.titulo, color = Colores.tinta)
            Spacer(Modifier.height(Dimensiones.espacioMediano))
            BotonSuave(
                texto = stringResource(R.string.asistente_menu_nueva),
                icono = Iconos.conversar,
                alPulsar = {
                    acciones.alCerrarHistorial()
                    acciones.alNuevaConversacion()
                },
            )
            Spacer(Modifier.height(Dimensiones.espacioGrande))
            when {
                estado.cargandoHistorial -> Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Colores.azulIntenso)
                }
                estado.errorHistorial != null -> AvisoError(mensaje = stringResource(estado.errorHistorial), origen = OrigenError.SERVIDOR)
                estado.conversaciones.isEmpty() -> Text(
                    text = stringResource(R.string.asistente_historial_vacio),
                    style = EstilosTexto.cuerpo,
                    color = Colores.textoApagado,
                )
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(Dimensiones.espacioPequeno)) {
                    items(estado.conversaciones, key = { it.idConversacion }) { conversacion ->
                        FilaConversacion(
                            conversacion = conversacion,
                            abierta = conversacion.idConversacion == estado.idConversacion,
                            ahora = ahora,
                            alPulsar = { acciones.alAbrirConversacion(conversacion.idConversacion) },
                        )
                    }
                }
            }
            Spacer(Modifier.height(Dimensiones.espacioPieInferior))
        }
    }
}

@Composable
private fun FilaConversacion(conversacion: ConversacionAsistente, abierta: Boolean, ahora: Date, alPulsar: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimensiones.radioCampo))
            .background(if (abierta) Colores.azulPildora else Colores.rellenoCampo)
            .clickable(role = Role.Button, onClick = alPulsar)
            .padding(Dimensiones.espacioMediano),
    ) {
        Text(
            text = conversacion.titulo?.takeIf { it.isNotBlank() } ?: stringResource(R.string.asistente_historial_sin_titulo),
            style = EstilosTexto.tituloAviso,
            color = Colores.tinta,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        conversacion.ultimoMensaje?.let { ultimo ->
            Text(
                text = FormatoMensaje.vistaPrevia(ultimo),
                style = EstilosTexto.ayuda,
                color = Colores.textoApagado,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        FormatoMensaje.antiguedad(conversacion.fechaUltimo, ahora)?.let {
            Text(text = textoAntiguedad(it), style = EstilosTexto.ayuda, color = Colores.grisContorno)
        }
    }
}

/** Pide confirmar antes de eliminar la conversación abierta. */
@Composable
internal fun DialogoEliminarConversacion(acciones: AccionesAsistente) {
    AlertDialog(
        onDismissRequest = acciones.alCerrarEliminar,
        title = { Text(text = stringResource(R.string.asistente_eliminar_titulo), style = EstilosTexto.tituloAviso, color = Colores.tinta) },
        text = { Text(text = stringResource(R.string.asistente_eliminar_texto), style = EstilosTexto.cuerpo, color = Colores.textoApagado) },
        confirmButton = {
            TextButton(onClick = acciones.alConfirmarEliminar) {
                Text(text = stringResource(R.string.asistente_eliminar_confirmar), style = EstilosTexto.boton, color = Colores.errorOscuro)
            }
        },
        dismissButton = {
            TextButton(onClick = acciones.alCerrarEliminar) {
                Text(text = stringResource(R.string.asistente_cancelar), style = EstilosTexto.botonSecundario, color = Colores.textoApagado)
            }
        },
        containerColor = Colores.superficie,
    )
}
