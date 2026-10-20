package com.example.finanzia.pantallas.metas

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.example.finanzia.R
import com.example.finanzia.componentes.BarraProgreso
import com.example.finanzia.datos.modelos.Frecuencia
import com.example.finanzia.datos.modelos.Meta
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos
import com.example.finanzia.utilidades.formatearPesos
import kotlin.math.abs

/** Tarjeta del ahorro acumulado en todas las metas, con su avance y el consejo de Finanzia IA. */
@Composable
internal fun TarjetaAhorroMetas(metas: List<Meta>, consejo: ConsejoMetas) {
    val forma = RoundedCornerShape(Dimensiones.radioBoton)
    val ahorrado = CalculosMetas.totalAhorrado(metas)
    val objetivo = CalculosMetas.totalObjetivo(metas)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(Dimensiones.elevacionTarjetaMetas, forma, ambientColor = Colores.sombra, spotColor = Colores.sombra)
            .background(Colores.superficie, forma)
            .clip(forma)
            .drawBehind {
                // Brillo menta arriba a la derecha y lavanda abajo a la izquierda, como en el diseño.
                drawRect(
                    Brush.radialGradient(
                        *Colores.brilloMentaAhorro,
                        center = Offset(size.width, 0f),
                        radius = Dimensiones.radioBrilloMentaAhorro.toPx(),
                    ),
                )
                drawRect(
                    Brush.radialGradient(
                        *Colores.brilloLavandaAhorro,
                        center = Offset(0f, size.height),
                        radius = Dimensiones.radioBrilloLavandaAhorro.toPx(),
                    ),
                )
            }
            .padding(Dimensiones.rellenoTarjetaAhorro),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(Dimensiones.anchoRecuadroAlcancia, Dimensiones.altoRecuadroAlcancia)
                    .background(Colores.azulPildora, RoundedCornerShape(Dimensiones.radioRecuadroPequeno)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Iconos.alcancia, null, tint = Colores.azulIntenso, modifier = Modifier.size(Dimensiones.tamanoIconoMediano))
            }
            Spacer(Modifier.width(Dimensiones.espacioPequeno))
            Text(
                text = stringResource(R.string.metas_ahorro_acumulado),
                style = EstilosTexto.botonSecundario,
                color = Colores.textoApagado,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(Dimensiones.espacioPequeno))
            Text(
                text = stringResource(R.string.metas_completado, CalculosMetas.porcentaje(ahorrado, objetivo)),
                style = EstilosTexto.cuerpoDestacado,
                color = Colores.verdeSobreMenta,
                modifier = Modifier
                    .background(Colores.verdeMenta, CircleShape)
                    .padding(horizontal = Dimensiones.rellenoHorizontalPildoraAvance, vertical = Dimensiones.rellenoVerticalPildoraPequena),
            )
        }
        Spacer(Modifier.height(Dimensiones.espacioMediano))
        Row {
            Text(
                text = stringResource(R.string.metas_monto, formatearPesos(ahorrado)),
                style = EstilosTexto.monto,
                color = Colores.tinta,
                modifier = Modifier.alignByBaseline(),
            )
            Spacer(Modifier.width(Dimensiones.espacioPequeno))
            Text(
                text = stringResource(R.string.metas_de_objetivo, formatearPesos(objetivo)),
                style = EstilosTexto.cuerpo,
                color = Colores.textoApagado,
                modifier = Modifier.alignByBaseline(),
            )
        }
        Spacer(Modifier.height(Dimensiones.espacioGrande))
        BarraProgreso(
            progreso = CalculosMetas.progreso(ahorrado, objetivo),
            colores = Colores.degradadoAhorroTotal,
            colorFondo = Colores.azulPildora,
            altura = Dimensiones.alturaBarraMeta,
        )
        Spacer(Modifier.height(Dimensiones.espacioGrande))
        ConsejoIa(consejo)
    }
}

/** Recuadro "Consejo de Finanzia IA" con el consejo calculado sobre las metas. */
@Composable
private fun ConsejoIa(consejo: ConsejoMetas) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Colores.rellenoCampo, RoundedCornerShape(Dimensiones.radioCampo))
            .padding(Dimensiones.espacioMediano),
    ) {
        Box(
            modifier = Modifier
                .size(Dimensiones.tamanoCirculoConsejo)
                .background(Colores.azulBruma, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Iconos.asistente, null, tint = Colores.azulIntenso, modifier = Modifier.size(Dimensiones.tamanoIconoMediano))
        }
        Spacer(Modifier.width(Dimensiones.espacioCirculoConsejo))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(R.string.metas_consejo_titulo), style = EstilosTexto.notaFuerte, color = Colores.azulIntenso)
                Spacer(Modifier.width(Dimensiones.espacioPuntoEstado))
                Box(
                    modifier = Modifier
                        .size(Dimensiones.tamanoPuntoConsejo)
                        .background(Colores.verdeSeguro, CircleShape),
                )
            }
            Text(text = textoConsejo(consejo), style = EstilosTexto.ayuda, color = Colores.textoApagado)
        }
    }
}

/** El texto del consejo, con el nombre de la meta resaltado como en el diseño. */
@Composable
private fun textoConsejo(consejo: ConsejoMetas): AnnotatedString = when (consejo) {
    ConsejoMetas.SinMetas -> AnnotatedString(stringResource(R.string.metas_consejo_sin_metas))
    ConsejoMetas.TodasLogradas -> AnnotatedString(stringResource(R.string.metas_consejo_logradas))
    ConsejoMetas.SinRegla -> AnnotatedString(stringResource(R.string.metas_consejo_sin_regla))
    is ConsejoMetas.Proyeccion -> {
        val habito = stringResource(habitoDe(consejo.frecuencia))
        val inicio = stringResource(if (consejo.dias >= 0) R.string.metas_consejo_alcanzaras else R.string.metas_consejo_la_meta)
        val nombre = stringResource(R.string.metas_consejo_nombre, consejo.meta.nombre)
        val fin = when {
            consejo.dias > 0 -> pluralStringResource(R.plurals.metas_consejo_antes, consejo.dias, consejo.dias, habito)
            consejo.dias == 0 -> stringResource(R.string.metas_consejo_a_tiempo, habito)
            else -> pluralStringResource(R.plurals.metas_consejo_tarde, abs(consejo.dias), abs(consejo.dias), habito)
        }
        buildAnnotatedString {
            append(inicio)
            withStyle(EstilosTexto.ayudaFuerte.toSpanStyle().copy(color = Colores.tinta)) { append(nombre) }
            append(fin)
        }
    }
}

/** "quincenal", "mensual"…, para "gracias al hábito quincenal". */
private fun habitoDe(frecuencia: Frecuencia): Int = when (frecuencia) {
    Frecuencia.DIARIO -> R.string.metas_habito_diario
    Frecuencia.SEMANAL -> R.string.metas_habito_semanal
    Frecuencia.QUINCENAL -> R.string.metas_habito_quincenal
    Frecuencia.MENSUAL -> R.string.metas_habito_mensual
}
