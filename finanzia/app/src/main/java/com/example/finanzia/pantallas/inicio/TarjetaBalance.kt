package com.example.finanzia.pantallas.inicio

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import com.example.finanzia.R
import com.example.finanzia.datos.modelos.ResumenInicio
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos

/** Tarjeta azul con el balance total, la curva decorativa y los totales de ingresos y gastos. */
@Composable
internal fun TarjetaBalance(resumen: ResumenInicio, visibles: Boolean, alAlternarMontos: () -> Unit) {
    val forma = RoundedCornerShape(Dimensiones.radioTarjetaGrande)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(Dimensiones.elevacionBoton, forma, ambientColor = Colores.azulMarca, spotColor = Colores.azulMarca)
            .background(Brush.linearGradient(Colores.degradadoBalance), forma)
            .padding(Dimensiones.rellenoBalance),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Iconos.banco, null, tint = Colores.blancoTenue, modifier = Modifier.size(Dimensiones.tamanoIconoMediano))
            Spacer(Modifier.width(Dimensiones.espacioPequeno))
            Text(
                text = stringResource(R.string.inicio_balance).uppercase(),
                style = EstilosTexto.etiquetaExterna,
                color = Colores.blancoTenue,
                modifier = Modifier.weight(1f),
            )
            Box(
                modifier = Modifier
                    .size(Dimensiones.tamanoBotonOjo)
                    .clip(CircleShape)
                    .background(Colores.blancoVelo)
                    .clickable(role = Role.Button, onClick = alAlternarMontos),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (visibles) Iconos.mostrarMaterial else Iconos.ocultarMaterial,
                    contentDescription = stringResource(if (visibles) R.string.accion_ocultar_montos else R.string.accion_mostrar_montos),
                    tint = Colores.textoSobreColor,
                    modifier = Modifier.size(Dimensiones.tamanoIconoCompacto),
                )
            }
        }
        Spacer(Modifier.height(Dimensiones.espacioPequeno))
        Row(verticalAlignment = Alignment.Bottom) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = textoMonto(resumen.balance, visibles),
                    style = EstilosTexto.monto,
                    color = Colores.textoSobreColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Iconos.escudoVerificado, null, tint = Colores.blancoTenue, modifier = Modifier.size(Dimensiones.tamanoIconoPequeno))
                    Spacer(Modifier.width(Dimensiones.espacioMinimo))
                    Text(
                        text = resumen.cuenta ?: stringResource(R.string.inicio_sin_cuenta),
                        style = EstilosTexto.ayuda,
                        color = Colores.blancoTenue,
                    )
                }
            }
            CurvaDecorativa()
        }
        Spacer(Modifier.height(Dimensiones.espacioBalanceResumen))
        HorizontalDivider(thickness = Dimensiones.grosorBorde, color = Colores.blancoVelo)
        Spacer(Modifier.height(Dimensiones.espacioBalanceResumen))
        Row(horizontalArrangement = Arrangement.spacedBy(Dimensiones.espacioPequeno)) {
            ResumenBalance(
                etiqueta = stringResource(R.string.inicio_ingresos),
                monto = textoMonto(resumen.ingresos, visibles, conSigno = true),
                icono = Iconos.entrada,
                fondoIcono = Colores.verdeMenta,
                colorIcono = Colores.verdeSobreMenta,
            )
            ResumenBalance(
                etiqueta = stringResource(R.string.inicio_gastos),
                monto = textoMonto(-resumen.gastos, visibles, conSigno = true),
                icono = Iconos.salida,
                fondoIcono = Colores.rosaNieveBorde,
                colorIcono = Colores.rosaIntenso,
            )
        }
    }
}

/** La curva verde de la tarjeta del balance: solo adorna, como en el diseño. */
@Composable
private fun CurvaDecorativa() {
    Canvas(modifier = Modifier.size(Dimensiones.anchoCurvaBalance, Dimensiones.altoCurvaBalance)) {
        val ancho = size.width
        val alto = size.height
        val curva = Path().apply {
            moveTo(0f, alto * 0.85f)
            cubicTo(ancho * 0.25f, alto * 0.95f, ancho * 0.35f, alto * 0.45f, ancho * 0.55f, alto * 0.6f)
            cubicTo(ancho * 0.75f, alto * 0.75f, ancho * 0.8f, alto * 0.1f, ancho * 0.95f, alto * 0.12f)
        }
        drawPath(curva, Colores.verdeMenta, style = Stroke(width = Dimensiones.grosorCurvaBalance.toPx(), cap = StrokeCap.Round))
        drawCircle(Colores.textoSobreColor, radius = Dimensiones.radioPuntoCurva.toPx(), center = Offset(ancho * 0.95f, alto * 0.12f))
    }
}

/** Recuadro translúcido con el total de ingresos o de gastos. */
@Composable
private fun RowScope.ResumenBalance(etiqueta: String, monto: String, icono: ImageVector, fondoIcono: Color, colorIcono: Color) {
    Row(
        modifier = Modifier
            .weight(1f)
            .background(Colores.blancoVelo, RoundedCornerShape(Dimensiones.radioCampo))
            .padding(Dimensiones.rellenoResumenBalance),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(Dimensiones.tamanoIconoResumen)
                .background(fondoIcono, RoundedCornerShape(Dimensiones.radioCasilla)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icono, null, tint = colorIcono, modifier = Modifier.size(Dimensiones.tamanoIconoMediano))
        }
        Spacer(Modifier.width(Dimensiones.espacioPequeno))
        Column {
            Text(text = etiqueta, style = EstilosTexto.ayuda, color = Colores.blancoTenue)
            Text(
                text = monto,
                style = EstilosTexto.boton,
                color = Colores.textoSobreColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
