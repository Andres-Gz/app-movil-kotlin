package com.example.finanzia.pantallas.inicio

import androidx.compose.foundation.background
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.example.finanzia.R
import com.example.finanzia.componentes.Pildora
import com.example.finanzia.datos.modelos.MetaActual
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos
import com.example.finanzia.utilidades.formatearMontoEntero

/** La meta más reciente con su avance, o un aviso de que pronto habrá metas. */
@Composable
internal fun TarjetaMeta(meta: MetaActual?, visibles: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Colores.azulBotonClaro, RoundedCornerShape(Dimensiones.radioBoton))
            .padding(Dimensiones.rellenoTarjetaIa),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Pildora(
                texto = if (meta != null) stringResource(R.string.inicio_meta, meta.nombre) else stringResource(R.string.inicio_sin_meta_pildora),
                colorFondo = Colores.verdeSeguro,
                colorContenido = Colores.textoSobreColor,
                rellenoHorizontal = Dimensiones.rellenoHorizontalContador,
                rellenoVertical = Dimensiones.rellenoVerticalInsignia,
                mayusculas = false,
            )
            Spacer(Modifier.height(Dimensiones.espacioPequeno))
            if (meta != null) {
                Text(
                    text = stringResource(
                        R.string.inicio_meta_progreso,
                        if (visibles) formatearMontoEntero(meta.ahorrado) else stringResource(R.string.inicio_monto_oculto),
                        if (visibles) formatearMontoEntero(meta.objetivo) else stringResource(R.string.inicio_monto_oculto),
                    ),
                    style = EstilosTexto.titulo,
                    color = Colores.tinta,
                )
                Spacer(Modifier.height(Dimensiones.espacioPequeno))
                LinearProgressIndicator(
                    progress = { CalculosInicio.progreso(meta) },
                    color = Colores.azulIntenso,
                    trackColor = Colores.azulPildora,
                    drawStopIndicator = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimensiones.alturaBarraMeta)
                        .clip(CircleShape),
                )
                Spacer(Modifier.height(Dimensiones.espacioMinimo))
                val falta = CalculosInicio.falta(meta)
                Text(
                    text = if (falta > 0) {
                        stringResource(R.string.inicio_meta_falta, if (visibles) formatearMontoEntero(falta) else stringResource(R.string.inicio_monto_oculto))
                    } else {
                        stringResource(R.string.inicio_meta_lograda)
                    },
                    style = EstilosTexto.ayuda,
                    color = Colores.textoApagado,
                )
            } else {
                Text(text = stringResource(R.string.inicio_sin_meta_titulo), style = EstilosTexto.tituloAviso, color = Colores.tinta)
                Text(text = stringResource(R.string.inicio_sin_meta_detalle), style = EstilosTexto.ayuda, color = Colores.textoApagado)
            }
        }
        Spacer(Modifier.width(Dimensiones.espacioMediano))
        Box {
            Box(
                modifier = Modifier
                    .size(Dimensiones.tamanoIconoMeta)
                    .background(Colores.mentaClara, RoundedCornerShape(Dimensiones.radioBoton)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Iconos.metaAhorro, null, tint = Colores.verdeSeguro, modifier = Modifier.size(Dimensiones.tamanoBaseIcono))
            }
            Icon(
                imageVector = Iconos.favorito,
                contentDescription = null,
                tint = Colores.azulIntenso,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = Dimensiones.espacioMinimo, y = -Dimensiones.espacioMinimo)
                    .size(Dimensiones.tamanoIconoPequeno),
            )
        }
    }
}
