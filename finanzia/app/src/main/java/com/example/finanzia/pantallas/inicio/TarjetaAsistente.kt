package com.example.finanzia.pantallas.inicio

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.example.finanzia.R
import com.example.finanzia.componentes.Pildora
import com.example.finanzia.componentes.TextoEnlace
import com.example.finanzia.configuracion.Constantes
import com.example.finanzia.datos.modelos.MovimientoReciente
import com.example.finanzia.datos.modelos.ResumenInicio
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos
import com.example.finanzia.utilidades.formatearMonto

/** Finanzia IA con un consejo hecho con los datos del usuario. */
@Composable
internal fun TarjetaAsistente(resumen: ResumenInicio, visibles: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Colores.rellenoCampo, RoundedCornerShape(Dimensiones.radioBoton))
            .padding(Dimensiones.rellenoTarjetaIa),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box {
                Box(
                    modifier = Modifier
                        .size(Dimensiones.tamanoRecuadroRobot)
                        .background(Colores.lavandaInsignia, RoundedCornerShape(Dimensiones.radioCampo)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Iconos.robot, null, tint = Colores.moradoAyuda, modifier = Modifier.size(Dimensiones.tamanoIcono))
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = Dimensiones.espacioMinimo, y = -Dimensiones.espacioMinimo)
                        .size(Dimensiones.tamanoInsigniaRobot)
                        .background(Colores.verdeSeguro, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Iconos.asistente, null, tint = Colores.textoSobreColor, modifier = Modifier.size(Dimensiones.tamanoIconoInsignia))
                }
            }
            Spacer(Modifier.width(Dimensiones.espacioMediano))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(R.string.inicio_ia), style = EstilosTexto.tituloAviso, color = Colores.tinta)
                    Spacer(Modifier.width(Dimensiones.espacioPequeno))
                    Pildora(
                        texto = stringResource(R.string.inicio_copiloto_corto),
                        colorFondo = Colores.lavandaInsignia,
                        colorContenido = Colores.moradoNoche,
                        rellenoHorizontal = Dimensiones.rellenoHorizontalInsignia,
                        rellenoVertical = Dimensiones.rellenoVerticalInsignia,
                        forma = RoundedCornerShape(Dimensiones.radioInsignia),
                        mayusculas = false,
                    )
                }
                Text(text = stringResource(R.string.inicio_ia_subtitulo), style = EstilosTexto.ayudaFuerte, color = Colores.verdeSeguro)
            }
            Icon(Iconos.cerrar, null, tint = Colores.moradoAyuda, modifier = Modifier.size(Dimensiones.tamanoIcono))
        }
        Spacer(Modifier.height(Dimensiones.espacioMediano))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Colores.superficie, RoundedCornerShape(Dimensiones.radioCampo))
                .padding(Dimensiones.rellenoTarjetaIa),
        ) {
            Text(text = consejo(resumen, visibles), style = EstilosTexto.cuerpo, color = Colores.tinta)
            Spacer(Modifier.height(Dimensiones.espacioMediano))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Iniciales(resumen.movimientos)
                Spacer(Modifier.width(Dimensiones.espacioPequeno))
                Text(
                    text = pluralStringResource(R.plurals.inicio_analizados, resumen.totalMovimientos, resumen.totalMovimientos),
                    style = EstilosTexto.ayuda,
                    color = Colores.textoApagado,
                    modifier = Modifier.weight(1f),
                )
                // Aún no existe la pantalla del plan.
                TextoEnlace(texto = stringResource(R.string.inicio_ver_plan), alPulsar = {}, color = Colores.azulIntenso)
            }
        }
    }
}

/** El consejo: cuánto se ahorraría recortando los gastos, con las cifras reales del usuario. */
@Composable
private fun consejo(resumen: ResumenInicio, visibles: Boolean) = buildAnnotatedString {
    withStyle(EstilosTexto.cuerpoDestacado.toSpanStyle().copy(color = Colores.azulIntenso)) {
        append(stringResource(R.string.inicio_consejo_titulo))
    }
    if (resumen.gastos <= 0) {
        append(stringResource(R.string.inicio_consejo_sin_gastos))
        return@buildAnnotatedString
    }
    append(stringResource(R.string.inicio_consejo_inicio))
    withStyle(EstilosTexto.titulo.toSpanStyle().copy(color = Colores.verdeSeguro)) {
        val porcentaje = (Constantes.Formatos.RECORTE_SUGERIDO * Constantes.Formatos.CIEN).toInt()
        append(stringResource(R.string.inicio_consejo_resaltado, porcentaje.toString()))
    }
    val ahorro = if (visibles) formatearMonto(CalculosInicio.ahorroSugerido(resumen)) else stringResource(R.string.inicio_monto_oculto)
    append(stringResource(R.string.inicio_consejo_fin, ahorro))
}

/** Círculos superpuestos con la inicial de los últimos movimientos. */
@Composable
private fun Iniciales(movimientos: List<MovimientoReciente>) {
    val fondos = listOf(Colores.azulPildora, Colores.verdeMenta, Colores.lavandaInsignia)
    Row {
        movimientos.take(Constantes.Formatos.INICIALES_VISIBLES).forEachIndexed { indice, movimiento ->
            val inicial = (movimiento.descripcion ?: movimiento.categoria ?: movimiento.tipo).take(1).uppercase()
            Box(
                modifier = Modifier
                    .offset(x = Dimensiones.solapeIniciales * indice)
                    .size(Dimensiones.tamanoInicial)
                    .background(fondos[indice % fondos.size], CircleShape)
                    .border(Dimensiones.grosorBorde, Colores.superficie, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = inicial, style = EstilosTexto.ayudaFuerte, color = Colores.tinta)
            }
        }
    }
}
