package com.example.finanzia.pantallas.asistente

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import com.example.finanzia.R
import com.example.finanzia.datos.modelos.MensajeAsistente
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos
import com.example.finanzia.utilidades.leerFechaMovimiento
import java.util.Date

/** La pregunta del usuario, en azul y a la derecha, con su hora debajo (o "Enviando…" mientras se manda). */
@Composable
internal fun BurbujaUsuario(texto: String, pie: String) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
        Text(
            text = texto,
            style = EstilosTexto.mensaje,
            color = Colores.textoSobreColor,
            modifier = Modifier
                .padding(start = Dimensiones.sangriaBurbuja)
                .background(Colores.azulIntenso, RoundedCornerShape(Dimensiones.radioCampo))
                .padding(horizontal = Dimensiones.rellenoHorizontalBurbuja, vertical = Dimensiones.rellenoVerticalBurbuja),
        )
        Spacer(Modifier.height(Dimensiones.espacioMinimo))
        Text(text = pie, style = EstilosTexto.ayuda, color = Colores.grisContorno)
    }
}

/** "09:38 • Entregado": la hora en que se guardó la pregunta. */
@Composable
internal fun pieEntregado(mensaje: MensajeAsistente): String =
    stringResource(R.string.asistente_entregado, leerFechaMovimiento(mensaje.fecha)?.hora.orEmpty())

/**
 * Una respuesta de Finanzia IA: su círculo a la izquierda y una tarjeta blanca con el título que puso
 * la IA, hace cuánto llegó y el texto, con las cifras resaltadas. [contenidoExtra] va al final (la propuesta).
 */
@Composable
internal fun TarjetaRespuesta(
    titulo: String,
    texto: String,
    antiguedad: String?,
    contenidoExtra: @Composable ColumnScope.() -> Unit = {},
) {
    val forma = RoundedCornerShape(Dimensiones.radioAvisoGrande)
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        AvatarIa(Dimensiones.tamanoAvatarMensaje)
        Spacer(Modifier.width(Dimensiones.espacioPequeno))
        Column(
            modifier = Modifier
                .weight(1f)
                .shadow(Dimensiones.elevacionCampo, forma, ambientColor = Colores.sombra, spotColor = Colores.sombra)
                .background(Colores.superficie, forma)
                .padding(Dimensiones.rellenoTarjetaRespuesta),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Iconos.asistente, null, tint = Colores.verdeSeguro, modifier = Modifier.size(Dimensiones.tamanoIcono))
                Spacer(Modifier.width(Dimensiones.espacioPequeno))
                Text(
                    text = titulo,
                    style = EstilosTexto.cuerpoDestacado,
                    color = Colores.verdeSeguro,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (antiguedad != null) {
                    Spacer(Modifier.width(Dimensiones.espacioMinimo))
                    Text(text = antiguedad, style = EstilosTexto.ayuda, color = Colores.grisContorno)
                }
            }
            if (texto.isNotEmpty()) {
                Spacer(Modifier.height(Dimensiones.espacioMediano))
                Text(text = textoConCifras(texto), style = EstilosTexto.mensaje, color = Colores.tinta)
            }
            contenidoExtra()
        }
    }
}

/** La respuesta guardada de la IA, separada en título y texto. */
@Composable
internal fun RespuestaGuardada(mensaje: MensajeAsistente, ahora: Date, contenidoExtra: @Composable ColumnScope.() -> Unit) {
    val separada = FormatoMensaje.separarTitulo(mensaje.contenido)
    TarjetaRespuesta(
        titulo = separada.titulo ?: stringResource(R.string.inicio_ia),
        texto = separada.texto,
        antiguedad = FormatoMensaje.antiguedad(mensaje.fecha, ahora)?.let { textoAntiguedad(it) },
        contenidoExtra = contenidoExtra,
    )
}

/** Mientras la IA responde: su tarjeta con un indicador de carga. */
@Composable
internal fun TarjetaPensando() {
    TarjetaRespuesta(titulo = stringResource(R.string.asistente_pensando), texto = "", antiguedad = null) {
        Spacer(Modifier.height(Dimensiones.espacioMediano))
        CircularProgressIndicator(
            color = Colores.verdeSeguro,
            strokeWidth = Dimensiones.grosorIndicadorCarga,
            modifier = Modifier.size(Dimensiones.tamanoIcono),
        )
    }
}

@Composable
internal fun textoAntiguedad(antiguedad: Antiguedad): String = when (antiguedad) {
    Antiguedad.Ahora -> stringResource(R.string.asistente_ahora)
    is Antiguedad.Minutos -> stringResource(R.string.asistente_hace_minutos, antiguedad.cantidad)
    is Antiguedad.Horas -> stringResource(R.string.asistente_hace_horas, antiguedad.cantidad)
    is Antiguedad.Dias -> pluralStringResource(R.plurals.asistente_hace_dias, antiguedad.cantidad, antiguedad.cantidad)
}

/** Lo que la IA escribió entre ** va en negrita y azul, como "$320.000 / día" en el diseño. */
private fun textoConCifras(texto: String): AnnotatedString = buildAnnotatedString {
    FormatoMensaje.trozos(texto).forEach { trozo ->
        if (trozo.resaltado) {
            withStyle(EstilosTexto.mensajeDestacado.toSpanStyle().copy(color = Colores.azulIntenso)) { append(trozo.texto) }
        } else {
            append(trozo.texto)
        }
    }
}
