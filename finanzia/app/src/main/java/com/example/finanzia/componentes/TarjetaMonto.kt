package com.example.finanzia.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.finanzia.R
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.utilidades.SeparadorMiles

/** Separador de miles de los montos, como en los diseños (3,200). */
private const val SEPARADOR_MILES = ','

/**
 * Tarjeta con un monto grande: "$" del color de [acento], las cifras subrayadas y una nota con icono
 * debajo. Si hay [mensajeError], el subrayado se pone rojo y el error reemplaza la nota.
 */
@Composable
fun TarjetaMonto(
    etiqueta: String,
    monto: String,
    alCambiar: (String) -> Unit,
    alConfirmar: () -> Unit,
    acento: Color,
    nota: String,
    iconoNota: ImageVector,
    mensajeError: String? = null,
) {
    Tarjeta(relleno = PaddingValues(Dimensiones.rellenoTarjetaMonto)) {
        Text(text = etiqueta.uppercase(), style = EstilosTexto.etiquetaExterna, color = Colores.textoMedio)
        Spacer(Modifier.height(Dimensiones.espacioEtiquetaMonto))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.monto_simbolo_moneda), style = EstilosTexto.monto, color = acento)
            Spacer(Modifier.width(Dimensiones.espacioPequeno))
            BasicTextField(
                value = monto,
                onValueChange = alCambiar,
                singleLine = true,
                textStyle = EstilosTexto.monto.copy(color = Colores.tinta),
                cursorBrush = SolidColor(acento),
                visualTransformation = SeparadorMiles(SEPARADOR_MILES),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { alConfirmar() }),
                modifier = Modifier.weight(1f),
                decorationBox = { campo ->
                    Box {
                        if (monto.isEmpty()) {
                            Text(text = stringResource(R.string.monto_ejemplo), style = EstilosTexto.monto, color = Colores.grisClaro)
                        }
                        campo()
                    }
                },
            )
        }
        Spacer(Modifier.height(Dimensiones.espacioMontoSubrayado))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimensiones.grosorSubrayadoMonto)
                .background(if (mensajeError != null) Colores.errorOscuro else acento),
        )
        Spacer(Modifier.height(Dimensiones.espacioMontoAyuda))
        if (mensajeError != null) {
            Text(text = mensajeError, style = EstilosTexto.ayudaDestacada, color = Colores.errorOscuro)
        } else {
            Row {
                Icon(
                    imageVector = iconoNota,
                    contentDescription = null,
                    tint = acento,
                    modifier = Modifier.padding(top = Dimensiones.espacioMinimo).size(Dimensiones.tamanoIconoPequeno),
                )
                Spacer(Modifier.width(Dimensiones.espacioIconoAyudaMonto))
                Text(text = nota, style = EstilosTexto.ayuda, color = Colores.textoTenue)
            }
        }
    }
}
