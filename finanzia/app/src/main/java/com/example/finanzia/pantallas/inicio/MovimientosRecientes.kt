package com.example.finanzia.pantallas.inicio

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import com.example.finanzia.R
import com.example.finanzia.componentes.TextoEnlace
import com.example.finanzia.configuracion.Constantes
import com.example.finanzia.datos.modelos.MovimientoReciente
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos
import com.example.finanzia.utilidades.DiaRelativo
import com.example.finanzia.utilidades.leerFechaMovimiento

/** "Movimientos recientes" con la lista, o un aviso si todavía no hay ninguno. */
@Composable
internal fun Movimientos(movimientos: List<MovimientoReciente>, visibles: Boolean, alEditar: (MovimientoReciente) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(R.string.inicio_movimientos),
            style = EstilosTexto.tituloAviso,
            color = Colores.tinta,
            modifier = Modifier.weight(1f),
        )
        // Aún no existe la lista completa de movimientos.
        TextoEnlace(texto = stringResource(R.string.inicio_ver_todos), alPulsar = {}, color = Colores.azulIntenso)
    }
    Spacer(Modifier.height(Dimensiones.espacioPequeno))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(Dimensiones.elevacionCampo, RoundedCornerShape(Dimensiones.radioTarjeta), ambientColor = Colores.sombra, spotColor = Colores.sombra)
            .background(Colores.superficie, RoundedCornerShape(Dimensiones.radioTarjeta))
            .padding(vertical = Dimensiones.espacioMinimo),
    ) {
        if (movimientos.isEmpty()) {
            Text(
                text = stringResource(R.string.inicio_sin_movimientos),
                style = EstilosTexto.cuerpo,
                color = Colores.textoApagado,
                modifier = Modifier.padding(Dimensiones.rellenoMovimiento),
            )
        }
        movimientos.forEach { movimiento -> FilaMovimiento(movimiento, visibles, alEditar = { alEditar(movimiento) }) }
    }
}

/** Un movimiento: icono de su categoría, nombre, fecha y cuenta, y el monto con su categoría. Al tocarlo se edita. */
@Composable
private fun FilaMovimiento(movimiento: MovimientoReciente, visibles: Boolean, alEditar: () -> Unit) {
    val esIngreso = movimiento.tipo == Constantes.BaseDatos.TIPO_INGRESO
    val apariencia = aparienciaDe(movimiento, esIngreso)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = alEditar)
            .padding(Dimensiones.rellenoMovimiento),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(Dimensiones.tamanoIconoMovimiento)
                .background(apariencia.fondo, RoundedCornerShape(Dimensiones.radioCampo)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(apariencia.icono, null, tint = apariencia.color, modifier = Modifier.size(Dimensiones.tamanoIcono))
        }
        Spacer(Modifier.width(Dimensiones.espacioMediano))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = movimiento.descripcion ?: movimiento.categoria.orEmpty(),
                style = EstilosTexto.botonSecundario,
                color = Colores.tinta,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = detalleDe(movimiento),
                style = EstilosTexto.ayuda,
                color = Colores.textoApagado,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.width(Dimensiones.espacioPequeno))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = textoMonto(if (esIngreso) movimiento.monto else -movimiento.monto, visibles, conSigno = true),
                style = EstilosTexto.boton,
                color = if (esIngreso) Colores.verdeSeguro else Colores.tinta,
            )
            Text(
                text = movimiento.categoria ?: stringResource(if (esIngreso) R.string.inicio_tipo_ingreso else R.string.inicio_tipo_gasto),
                style = EstilosTexto.ayuda,
                color = if (esIngreso) Colores.verdeSeguro else Colores.textoApagado,
            )
        }
    }
}

/** "Hoy, 14:32 • Cuenta principal". */
@Composable
private fun detalleDe(movimiento: MovimientoReciente): String {
    val fecha = leerFechaMovimiento(movimiento.fecha)
    val meses = stringArrayResource(R.array.inicio_meses)
    val cuando = when (fecha?.dia) {
        null -> ""
        DiaRelativo.HOY -> stringResource(R.string.inicio_hoy, fecha.hora)
        DiaRelativo.AYER -> stringResource(R.string.inicio_ayer, fecha.hora)
        DiaRelativo.ANTES -> stringResource(R.string.inicio_fecha, fecha.diaDelMes, meses[fecha.mes])
    }
    val cuenta = movimiento.cuenta ?: return cuando
    return stringResource(R.string.inicio_detalle_movimiento, cuando, cuenta)
}

/** Icono y colores de un movimiento según su tipo y su categoría. */
private class AparienciaMovimiento(val icono: ImageVector, val fondo: Color, val color: Color)

private fun aparienciaDe(movimiento: MovimientoReciente, esIngreso: Boolean): AparienciaMovimiento = when {
    esIngreso -> AparienciaMovimiento(Iconos.cobro, Colores.verdeMenta, Colores.verdeSobreMenta)
    else -> when (movimiento.categoria?.lowercase()) {
        Constantes.BaseDatos.CATEGORIA_FIJOS -> AparienciaMovimiento(Iconos.hogar, Colores.rosaNieveBorde, Colores.rosaIntenso)
        Constantes.BaseDatos.CATEGORIA_COMIDA -> AparienciaMovimiento(Iconos.compras, Colores.azulPildora, Colores.azulIntenso)
        Constantes.BaseDatos.CATEGORIA_DIVERSION -> AparienciaMovimiento(Iconos.celebracion, Colores.lavandaInsignia, Colores.moradoAyuda)
        Constantes.BaseDatos.CATEGORIA_TRANSPORTE -> AparienciaMovimiento(Iconos.carro, Colores.indigoClaro, Colores.azulIntenso)
        else -> AparienciaMovimiento(Iconos.recibo, Colores.azulPildora, Colores.azulIntenso)
    }
}
