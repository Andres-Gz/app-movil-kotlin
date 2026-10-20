package com.example.finanzia.pantallas.nuevameta

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.example.finanzia.R
import com.example.finanzia.componentes.ChipMonto
import com.example.finanzia.configuracion.Constantes
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.utilidades.SeparadorMiles
import com.example.finanzia.utilidades.formatearPesos

/** "Monto objetivo total": el monto escrito a la derecha y los botones que le suman. */
@Composable
internal fun TarjetaMontoMeta(
    monto: String,
    incrementoElegido: Long?,
    alCambiar: (String) -> Unit,
    alSumar: (Long) -> Unit,
) {
    val forma = RoundedCornerShape(Dimensiones.radioBoton)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(Dimensiones.elevacionCampo, forma, ambientColor = Colores.sombra, spotColor = Colores.sombra)
            .background(Colores.superficie, forma)
            .padding(Dimensiones.espacioGrande),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.nueva_meta_etiqueta_monto),
                style = EstilosTexto.botonSecundario,
                color = Colores.tinta,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(R.string.nueva_meta_moneda),
                style = EstilosTexto.ayuda,
                color = Colores.textoApagado,
                modifier = Modifier
                    .background(Colores.azulBotonClaro, RoundedCornerShape(Dimensiones.radioRecuadroPequeno))
                    .padding(horizontal = Dimensiones.rellenoHorizontalPildoraMoneda, vertical = Dimensiones.rellenoVerticalPildoraMoneda),
            )
        }
        Spacer(Modifier.height(Dimensiones.espacioEtiquetaMontoMeta))
        Row {
            Text(
                text = stringResource(R.string.nueva_meta_signo),
                style = EstilosTexto.monto,
                color = Colores.azulIntenso,
                modifier = Modifier.alignByBaseline(),
            )
            Spacer(Modifier.width(Dimensiones.espacioSignoMonto))
            BasicTextField(
                value = monto,
                onValueChange = alCambiar,
                singleLine = true,
                textStyle = EstilosTexto.monto.copy(color = Colores.tinta, textAlign = TextAlign.End),
                cursorBrush = SolidColor(Colores.azulIntenso),
                visualTransformation = SeparadorMiles(Constantes.Formatos.SEPARADOR_MILES_PESOS),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                modifier = Modifier
                    .weight(1f)
                    .alignByBaseline(),
                decorationBox = { campo ->
                    Box(contentAlignment = Alignment.CenterEnd) {
                        if (monto.isEmpty()) {
                            Text(text = stringResource(R.string.monto_ejemplo), style = EstilosTexto.monto, color = Colores.grisClaro)
                        }
                        campo()
                    }
                },
            )
            Spacer(Modifier.width(Dimensiones.espacioPequeno))
            Text(
                text = stringResource(R.string.nueva_meta_cop),
                style = EstilosTexto.nota,
                color = Colores.grisContorno,
                modifier = Modifier.alignByBaseline(),
            )
        }
        Spacer(Modifier.height(Dimensiones.espacioMontoIncrementos))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(Dimensiones.espacioPequeno),
        ) {
            Constantes.Metas.INCREMENTOS_MONTO.forEach { incremento ->
                ChipMonto(
                    texto = stringResource(R.string.nueva_meta_incremento, formatearPesos(incremento.toDouble())),
                    alPulsar = { alSumar(incremento) },
                    elegido = incremento == incrementoElegido,
                )
            }
        }
    }
}
