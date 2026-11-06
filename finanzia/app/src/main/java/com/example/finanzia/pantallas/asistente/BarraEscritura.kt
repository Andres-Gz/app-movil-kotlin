package com.example.finanzia.pantallas.asistente

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.example.finanzia.R
import com.example.finanzia.configuracion.Constantes
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos

/** Una pregunta lista para mandar con un toque. */
private data class Sugerencia(val icono: ImageVector, val color: Color, @param:StringRes val texto: Int)

private val sugerencias = listOf(
    Sugerencia(Iconos.idea, Colores.verdeSeguro, R.string.asistente_sugerencia_gastar),
    Sugerencia(Iconos.tendencia, Colores.azulIntenso, R.string.asistente_sugerencia_proyeccion),
    Sugerencia(Iconos.metaAhorro, Colores.verdeSeguro, R.string.asistente_sugerencia_metas),
    Sugerencia(Iconos.recibo, Colores.azulIntenso, R.string.asistente_sugerencia_gasto),
)

/** El comienzo de una frase para que el usuario la complete, con su nombre en el menú del "+". */
private data class Atajo(@param:StringRes val nombre: Int, @param:StringRes val texto: Int)

private val atajos = listOf(
    Atajo(R.string.asistente_atajo_gasto, R.string.asistente_atajo_gasto_texto),
    Atajo(R.string.asistente_atajo_ingreso, R.string.asistente_atajo_ingreso_texto),
    Atajo(R.string.asistente_atajo_aporte, R.string.asistente_atajo_aporte_texto),
)

/** Fila de sugerencias que se desliza de lado, bajo el encabezado. */
@Composable
internal fun SugerenciasAsistente(alElegir: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = Dimensiones.margenPantallaAmplio),
        horizontalArrangement = Arrangement.spacedBy(Dimensiones.espacioPequeno),
    ) {
        sugerencias.forEach { sugerencia ->
            val texto = stringResource(sugerencia.texto)
            ChipSugerencia(sugerencia.icono, sugerencia.color, texto, Colores.rellenoCampo, elevado = false) { alElegir(texto) }
        }
    }
}

/** La sugerencia blanca que flota sobre la barra de escritura ("¿Puedo cenar fuera hoy?"). */
@Composable
internal fun SugerenciaFlotante(alElegir: (String) -> Unit) {
    val texto = stringResource(R.string.asistente_sugerencia_cenar)
    Row(modifier = Modifier.padding(horizontal = Dimensiones.margenPantallaAmplio)) {
        ChipSugerencia(Iconos.alcanciaContorno, Colores.verdeSeguro, texto, Colores.superficie, elevado = true) { alElegir(texto) }
    }
}

@Composable
private fun ChipSugerencia(icono: ImageVector, colorIcono: Color, texto: String, fondo: Color, elevado: Boolean, alPulsar: () -> Unit) {
    Row(
        modifier = Modifier
            .height(Dimensiones.alturaSugerencia)
            .then(if (elevado) Modifier.shadow(Dimensiones.elevacionCampo, CircleShape, ambientColor = Colores.sombra, spotColor = Colores.sombra) else Modifier)
            .clip(CircleShape)
            .background(fondo)
            .clickable(role = Role.Button, onClick = alPulsar)
            .padding(horizontal = Dimensiones.rellenoHorizontalChipAsistente),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icono, null, tint = colorIcono, modifier = Modifier.size(Dimensiones.tamanoIcono))
        Spacer(Modifier.width(Dimensiones.espacioPequeno))
        Text(text = texto, style = EstilosTexto.cuerpo, color = Colores.textoApagado, maxLines = 1)
    }
}

/** La barra blanca de abajo: el "+" con atajos, el campo, el dictado por voz y el botón de enviar. */
@Composable
internal fun BarraEscritura(estado: EstadoAsistente, acciones: AccionesAsistente) {
    val forma = RoundedCornerShape(Dimensiones.radioAvisoGrande)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensiones.margenPantallaAmplio)
            .shadow(Dimensiones.elevacionBoton, forma, ambientColor = Colores.sombra, spotColor = Colores.sombra)
            .background(Colores.superficie, forma)
            .padding(Dimensiones.rellenoBarraEscritura),
        verticalAlignment = Alignment.Bottom,
    ) {
        Box {
            BotonEscritura(Iconos.agregarCirculo, stringResource(R.string.accion_atajos), acciones.alAbrirAtajos)
            DropdownMenu(expanded = estado.atajosAbiertos, onDismissRequest = acciones.alCerrarAtajos, containerColor = Colores.superficie) {
                atajos.forEach { atajo ->
                    val texto = stringResource(atajo.texto)
                    OpcionMenu(Iconos.agregarCirculo, stringResource(atajo.nombre), Colores.tinta) { acciones.alElegirAtajo(texto) }
                }
            }
        }
        Spacer(Modifier.width(Dimensiones.espacioPequeno))
        CampoPregunta(
            texto = estado.texto,
            alCambiar = acciones.alCambiarTexto,
            modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
        )
        Spacer(Modifier.width(Dimensiones.espacioPequeno))
        BotonEscritura(Iconos.microfono, stringResource(R.string.accion_dictar), acciones.alDictar)
        Spacer(Modifier.width(Dimensiones.espacioPequeno))
        BotonEnviar(habilitado = estado.puedeEnviar, respondiendo = estado.respondiendo, alPulsar = acciones.alEnviar)
    }
}

@Composable
private fun CampoPregunta(texto: String, alCambiar: (String) -> Unit, modifier: Modifier) {
    BasicTextField(
        value = texto,
        onValueChange = alCambiar,
        textStyle = EstilosTexto.valorCampo.copy(color = Colores.tinta),
        cursorBrush = SolidColor(Colores.azulIntenso),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        maxLines = Constantes.Asistente.LINEAS_CAMPO,
        modifier = modifier,
        decorationBox = { campo ->
            Box {
                if (texto.isEmpty()) {
                    Text(text = stringResource(R.string.asistente_escribe), style = EstilosTexto.ejemploCampo, color = Colores.grisContorno)
                }
                campo()
            }
        },
    )
}

@Composable
private fun BotonEscritura(icono: ImageVector, descripcion: String, alPulsar: () -> Unit) {
    Box(
        modifier = Modifier
            .size(Dimensiones.tamanoBotonEscritura)
            .clip(RoundedCornerShape(Dimensiones.radioBotonMeta))
            .background(Colores.azulBotonClaro)
            .clickable(role = Role.Button, onClick = alPulsar),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icono, descripcion, tint = Colores.textoApagado, modifier = Modifier.size(Dimensiones.tamanoIcono))
    }
}

@Composable
private fun BotonEnviar(habilitado: Boolean, respondiendo: Boolean, alPulsar: () -> Unit) {
    Box(
        modifier = Modifier
            .size(Dimensiones.tamanoBotonEscritura)
            .clip(RoundedCornerShape(Dimensiones.radioBotonMeta))
            .background(Brush.linearGradient(if (habilitado || respondiendo) Colores.degradadoBotonFrio else Colores.degradadoBotonFrioApagado))
            .clickable(enabled = habilitado, role = Role.Button, onClick = alPulsar),
        contentAlignment = Alignment.Center,
    ) {
        if (respondiendo) {
            CircularProgressIndicator(
                color = Colores.textoSobreColor,
                strokeWidth = Dimensiones.grosorIndicadorCarga,
                modifier = Modifier.size(Dimensiones.tamanoIconoMediano),
            )
        } else {
            Icon(Iconos.enviar, stringResource(R.string.accion_enviar), tint = Colores.textoSobreColor, modifier = Modifier.size(Dimensiones.tamanoIcono))
        }
    }
}
