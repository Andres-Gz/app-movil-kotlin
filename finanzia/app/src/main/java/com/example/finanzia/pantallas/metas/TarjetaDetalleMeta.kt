package com.example.finanzia.pantallas.metas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
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
import java.util.Calendar

/** Una meta: icono, nombre, categoría y fecha; lo ahorrado contra la meta, el avance, la regla de aporte y sus botones. */
@Composable
internal fun TarjetaDetalleMeta(meta: Meta, hoy: Calendar, menuAbierto: Boolean, acciones: AccionesMeta) {
    val forma = RoundedCornerShape(Dimensiones.radioBoton)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(Dimensiones.elevacionTarjetaMetas, forma, ambientColor = Colores.sombra, spotColor = Colores.sombra)
            .background(Colores.superficie, forma)
            .padding(Dimensiones.rellenoTarjetaMeta),
    ) {
        Encabezado(meta, hoy, menuAbierto, acciones)
        Spacer(Modifier.height(Dimensiones.espacioGrande))
        Row {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = stringResource(R.string.metas_ahorrado), style = EstilosTexto.ayuda, color = Colores.textoApagado)
                Text(
                    text = stringResource(R.string.metas_monto, formatearPesos(meta.ahorrado)),
                    style = EstilosTexto.monto,
                    color = Colores.azulIntenso,
                    maxLines = 1,
                )
            }
            Spacer(Modifier.width(Dimensiones.espacioPequeno))
            Column(horizontalAlignment = Alignment.End) {
                Text(text = stringResource(R.string.metas_meta), style = EstilosTexto.ayuda, color = Colores.textoApagado)
                Text(
                    text = stringResource(R.string.metas_monto_cop, formatearPesos(meta.objetivo)),
                    style = EstilosTexto.botonSecundario,
                    color = Colores.tinta,
                )
            }
        }
        Spacer(Modifier.height(Dimensiones.espacioMediano))
        val falta = CalculosMetas.falta(meta)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (falta > 0) stringResource(R.string.metas_faltan, formatearPesos(falta)) else stringResource(R.string.metas_lograda),
                style = EstilosTexto.ayuda,
                color = Colores.textoApagado,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(R.string.metas_porcentaje, CalculosMetas.porcentaje(meta.ahorrado, meta.objetivo)),
                style = EstilosTexto.ayudaFuerte,
                color = Colores.verdeSeguro,
            )
        }
        Spacer(Modifier.height(Dimensiones.espacioMinimo))
        BarraProgreso(
            progreso = CalculosMetas.progreso(meta.ahorrado, meta.objetivo),
            colores = Colores.degradadoAvanceMeta,
            colorFondo = Colores.azulPildora,
            altura = Dimensiones.alturaBarraAvanceMeta,
        )
        Spacer(Modifier.height(Dimensiones.espacioBarraAporte))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Colores.rellenoCampo, RoundedCornerShape(Dimensiones.radioAporte))
                .padding(horizontal = Dimensiones.rellenoHorizontalAporte, vertical = Dimensiones.rellenoVerticalAporte),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Iconos.calendario, null, tint = Colores.verdeSeguro, modifier = Modifier.size(Dimensiones.tamanoIconoMediano))
            Spacer(Modifier.width(Dimensiones.espacioPequeno))
            Text(
                text = textoAporte(meta),
                style = EstilosTexto.ayuda,
                color = Colores.textoApagado,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.height(Dimensiones.espacioAporteBotones))
        Row(horizontalArrangement = Arrangement.spacedBy(Dimensiones.espacioPequeno)) {
            BotonMeta(
                texto = stringResource(R.string.metas_aportar),
                icono = Iconos.agregar,
                colorFondo = Colores.azulIntenso,
                colorContenido = Colores.textoSobreColor,
                alPulsar = { acciones.alAportar(meta) },
                modifier = Modifier.weight(1f),
            )
            BotonMeta(
                texto = stringResource(R.string.metas_ajustar_regla),
                icono = Iconos.ajustarRegla,
                colorFondo = Colores.azulPildora,
                colorContenido = Colores.azulIntenso,
                alPulsar = { acciones.alAjustarRegla(meta) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/** Lo que se puede hacer sobre una meta desde su tarjeta. */
internal class AccionesMeta(
    val alAportar: (Meta) -> Unit,
    val alAjustarRegla: (Meta) -> Unit,
    val alAbrirMenu: (Meta) -> Unit,
    val alCerrarMenu: () -> Unit,
    val alEditar: (Meta) -> Unit,
    val alEliminar: (Meta) -> Unit,
)

/** El emoji, el nombre, la categoría y el mes de la fecha límite, con el botón de más opciones. */
@Composable
private fun Encabezado(meta: Meta, hoy: Calendar, menuAbierto: Boolean, acciones: AccionesMeta) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(Dimensiones.anchoRecuadroEmoji, Dimensiones.altoRecuadroEmoji)
                .background(Colores.azulPildora, RoundedCornerShape(Dimensiones.radioCampo)),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = meta.icono ?: stringResource(R.string.metas_icono_predeterminado), style = EstilosTexto.titulo)
        }
        Spacer(Modifier.width(Dimensiones.espacioMediano))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = meta.nombre, style = EstilosTexto.tituloSeccion, color = Colores.tinta)
            Spacer(Modifier.height(Dimensiones.espacioTituloEtiquetasMeta))
            Row(verticalAlignment = Alignment.CenterVertically) {
                val categoria = meta.categoria
                if (!categoria.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .height(Dimensiones.alturaEtiquetaMeta)
                            .background(Colores.rellenoCampo, RoundedCornerShape(Dimensiones.radioRecuadroPequeno))
                            .padding(horizontal = Dimensiones.rellenoHorizontalEtiquetaMeta),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = categoria, style = EstilosTexto.ayudaDestacada, color = Colores.textoApagado)
                    }
                    Spacer(Modifier.width(Dimensiones.espacioPequeno))
                }
                val mesYAno = CalculosMetas.mesYAno(meta, hoy)
                if (mesYAno != null) {
                    val meses = stringArrayResource(R.array.metas_meses)
                    Icon(Iconos.reloj, null, tint = Colores.verdeSeguro, modifier = Modifier.size(Dimensiones.tamanoIconoFecha))
                    Spacer(Modifier.width(Dimensiones.espacioMinimo))
                    Text(
                        text = stringResource(R.string.metas_fecha_limite, meses[mesYAno.first], mesYAno.second),
                        style = EstilosTexto.ayudaDestacada,
                        color = Colores.verdeSeguro,
                    )
                }
            }
        }
        Box(modifier = Modifier.align(Alignment.Top)) {
            Icon(
                imageVector = Iconos.masOpciones,
                contentDescription = stringResource(R.string.accion_mas_opciones),
                tint = Colores.textoApagado,
                modifier = Modifier
                    .size(Dimensiones.tamanoBaseIcono)
                    .clip(CircleShape)
                    .clickable(role = Role.Button, onClick = { acciones.alAbrirMenu(meta) }),
            )
            MenuMeta(abierto = menuAbierto, meta = meta, acciones = acciones)
        }
    }
}

/** El menú ⋮ de una meta: editarla o eliminarla. No está en el diseño. */
@Composable
private fun MenuMeta(abierto: Boolean, meta: Meta, acciones: AccionesMeta) {
    DropdownMenu(expanded = abierto, onDismissRequest = acciones.alCerrarMenu, containerColor = Colores.superficie) {
        DropdownMenuItem(
            text = { Text(text = stringResource(R.string.metas_editar), style = EstilosTexto.botonSecundario, color = Colores.tinta) },
            leadingIcon = { Icon(Iconos.editar, null, tint = Colores.azulIntenso, modifier = Modifier.size(Dimensiones.tamanoIcono)) },
            onClick = {
                acciones.alCerrarMenu()
                acciones.alEditar(meta)
            },
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(R.string.metas_eliminar), style = EstilosTexto.botonSecundario, color = Colores.errorOscuro) },
            leadingIcon = { Icon(Iconos.eliminar, null, tint = Colores.errorOscuro, modifier = Modifier.size(Dimensiones.tamanoIcono)) },
            onClick = { acciones.alEliminar(meta) },
        )
    }
}

/** "Próximo aporte: $200.000 COP quincenal (15 y 30)", o un aviso si la meta no tiene regla de aporte. */
@Composable
private fun textoAporte(meta: Meta): AnnotatedString {
    val frecuencia = CalculosMetas.frecuencia(meta)
    val aporte = meta.montoAporte
    if (frecuencia == null || aporte == null || aporte <= 0) return AnnotatedString(stringResource(R.string.metas_sin_regla))
    val monto = stringResource(R.string.metas_monto_cop, formatearPesos(aporte))
    val cada = stringResource(
        when (frecuencia) {
            Frecuencia.DIARIO -> R.string.metas_aporte_diario
            Frecuencia.SEMANAL -> R.string.metas_aporte_semanal
            Frecuencia.QUINCENAL -> R.string.metas_aporte_quincenal
            Frecuencia.MENSUAL -> R.string.metas_aporte_mensual
        },
    )
    val inicio = stringResource(R.string.metas_proximo_aporte)
    return buildAnnotatedString {
        append(inicio)
        withStyle(EstilosTexto.ayudaFuerte.toSpanStyle().copy(color = Colores.tinta)) { append(monto) }
        append(cada)
    }
}

/** Botón de la tarjeta de una meta ("+ Aportar" y "Ajustar regla"): icono y texto centrados. */
@Composable
private fun BotonMeta(
    texto: String,
    icono: ImageVector,
    colorFondo: Color,
    colorContenido: Color,
    alPulsar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(Dimensiones.alturaBotonMeta)
            .clip(RoundedCornerShape(Dimensiones.radioBotonMeta))
            .background(colorFondo)
            .clickable(role = Role.Button, onClick = alPulsar),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icono, null, tint = colorContenido, modifier = Modifier.size(Dimensiones.tamanoIconoCompacto))
        Spacer(Modifier.width(Dimensiones.espacioIconoTextoBotonMeta))
        Text(text = texto, style = EstilosTexto.boton, color = colorContenido)
    }
}
