package com.example.finanzia.pantallas.asistente

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.finanzia.R
import com.example.finanzia.componentes.BotonDegradado
import com.example.finanzia.datos.modelos.AccionAsistente
import com.example.finanzia.datos.modelos.CategoriaGasto
import com.example.finanzia.datos.modelos.EstadoAccion
import com.example.finanzia.datos.modelos.Frecuencia
import com.example.finanzia.datos.modelos.TipoAccion
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos
import com.example.finanzia.utilidades.formatearPesos

/**
 * Lo que propuso la IA (un gasto, un ingreso o un aporte), dentro de su respuesta. Mientras está
 * pendiente lleva Confirmar y Cancelar: nada se guarda hasta que el usuario confirma.
 */
@Composable
internal fun TarjetaAccion(
    accion: AccionAsistente,
    estadoAccion: EstadoAccion?,
    enCurso: Boolean,
    alConfirmar: () -> Unit,
    alCancelar: () -> Unit,
) {
    val esGasto = accion.tipo == TipoAccion.GASTO
    Spacer(Modifier.height(Dimensiones.espacioMediano))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Colores.rellenoCampo, RoundedCornerShape(Dimensiones.radioCampo))
            .padding(Dimensiones.espacioMediano),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(Dimensiones.tamanoBotonEscritura)
                    .background(if (esGasto) Colores.rosaNieve else Colores.verdeNieve, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = when (accion.tipo) {
                        TipoAccion.GASTO -> Iconos.recibo
                        TipoAccion.INGRESO -> Iconos.cobro
                        TipoAccion.APORTE -> Iconos.alcancia
                    },
                    contentDescription = null,
                    tint = if (esGasto) Colores.rosaOscuro else Colores.verdeSeguro,
                    modifier = Modifier.size(Dimensiones.tamanoIcono),
                )
            }
            Spacer(Modifier.width(Dimensiones.espacioMediano))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = stringResource(titulo(accion.tipo)), style = EstilosTexto.notaFuerte, color = Colores.tinta)
                detalle(accion)?.let { Text(text = it, style = EstilosTexto.ayuda, color = Colores.textoApagado) }
            }
            Text(
                text = stringResource(R.string.asistente_monto, formatearPesos(accion.monto)),
                style = EstilosTexto.tituloAviso,
                color = if (esGasto) Colores.rosaOscuro else Colores.verdeSeguro,
            )
        }
        Spacer(Modifier.height(Dimensiones.espacioMediano))
        when (estadoAccion) {
            EstadoAccion.CONFIRMADA -> Resultado(Iconos.confirmado, R.string.asistente_accion_confirmada, Colores.verdeSeguro)
            EstadoAccion.CANCELADA -> Resultado(Iconos.borrarTexto, R.string.asistente_accion_cancelada, Colores.grisContorno)
            EstadoAccion.PENDIENTE, null -> Row(horizontalArrangement = Arrangement.spacedBy(Dimensiones.espacioPequeno)) {
                BotonCancelar(enCurso = enCurso, alPulsar = alCancelar, modifier = Modifier.weight(1f))
                BotonDegradado(
                    texto = stringResource(R.string.asistente_confirmar),
                    alPulsar = { if (!enCurso) alConfirmar() },
                    cargando = enCurso,
                    radio = Dimensiones.radioBotonMeta,
                    altura = Dimensiones.alturaBotonMeta,
                    colorSombra = Colores.azulIntenso,
                    colores = Colores.degradadoBotonFrio,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@StringRes
private fun titulo(tipo: TipoAccion): Int = when (tipo) {
    TipoAccion.GASTO -> R.string.asistente_accion_gasto
    TipoAccion.INGRESO -> R.string.asistente_accion_ingreso
    TipoAccion.APORTE -> R.string.asistente_accion_aporte
}

/** "Comida · Almuerzo", "Quincenal · Freelance" o el nombre de la meta. */
@Composable
private fun detalle(accion: AccionAsistente): String? {
    val tipo = when (accion.tipo) {
        TipoAccion.GASTO -> when (CategoriaGasto.entries.firstOrNull { it.clave == accion.categoria }) {
            CategoriaGasto.GASTOS_FIJOS -> R.string.gasto_fijos
            CategoriaGasto.COMIDA -> R.string.gasto_comida
            CategoriaGasto.DIVERSION -> R.string.gasto_diversion
            CategoriaGasto.TRANSPORTE -> R.string.gasto_transporte
            null -> null
        }
        TipoAccion.INGRESO -> when (Frecuencia.entries.firstOrNull { it.clave == accion.frecuencia }) {
            Frecuencia.MENSUAL -> R.string.ingreso_mensual
            Frecuencia.QUINCENAL -> R.string.ingreso_quincenal
            Frecuencia.SEMANAL -> R.string.ingreso_semanal
            Frecuencia.DIARIO -> R.string.ingreso_diario
            null -> null
        }
        TipoAccion.APORTE -> return accion.nombreMeta
    }?.let { stringResource(it) }
    val descripcion = accion.descripcion?.takeIf { it.isNotBlank() }
    return when {
        tipo != null && descripcion != null -> stringResource(R.string.asistente_detalle, tipo, descripcion)
        else -> tipo ?: descripcion
    }
}

@Composable
private fun BotonCancelar(enCurso: Boolean, alPulsar: () -> Unit, modifier: Modifier) {
    val forma = RoundedCornerShape(Dimensiones.radioBotonMeta)
    Box(
        modifier = modifier
            .height(Dimensiones.alturaBotonMeta)
            .clip(forma)
            .background(Colores.superficie)
            .border(Dimensiones.grosorBorde, Colores.bordeCampo, forma)
            .clickable(enabled = !enCurso, role = Role.Button, onClick = alPulsar),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = stringResource(R.string.asistente_cancelar), style = EstilosTexto.botonSecundario, color = Colores.textoApagado)
    }
}

@Composable
private fun Resultado(icono: ImageVector, @StringRes texto: Int, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, null, tint = color, modifier = Modifier.size(Dimensiones.tamanoIconoMediano))
        Spacer(Modifier.width(Dimensiones.espacioMinimo))
        Text(text = stringResource(texto), style = EstilosTexto.notaFuerte, color = color)
    }
}
