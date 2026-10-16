package com.example.finanzia.componentes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto

/**
 * Campo de texto con la etiqueta en mayúsculas encima de la caja. La [apariencia] decide cómo se ve
 * la caja según el diseño de cada pantalla (ver [AparienciaCampo]).
 * [extraEtiqueta] se dibuja al final de la fila de la etiqueta (por ejemplo, "Seguro SSL").
 * Con [mensajeError] se pinta de rojo y muestra el mensaje debajo.
 */
@Composable
fun CampoConEtiqueta(
    etiqueta: String,
    valor: String,
    alCambiar: (String) -> Unit,
    ejemplo: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    apariencia: AparienciaCampo = AparienciaCampo.TARJETA,
    mensajeError: String? = null,
    origenError: OrigenError = OrigenError.FORMULARIO,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    capitalizacion: KeyboardCapitalization = KeyboardCapitalization.None,
    accionTeclado: ImeAction = ImeAction.Next,
    alConfirmar: () -> Unit = {},
    transformacionVisual: VisualTransformation = VisualTransformation.None,
    estiloValor: TextStyle = EstilosTexto.valorCampo,
    extraEtiqueta: (@Composable () -> Unit)? = null,
    botonFinal: (@Composable () -> Unit)? = null,
) {
    val conError = mensajeError != null
    val enfoque = remember { FocusRequester() }
    val estilo = apariencia.estilo
    val elevado = apariencia == AparienciaCampo.ELEVADO

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = Dimensiones.espacioMinimo),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = etiqueta.uppercase(),
                style = EstilosTexto.etiquetaExterna,
                color = if (conError) Colores.error else estilo.colorEtiqueta,
                modifier = Modifier.weight(1f),
            )
            extraEtiqueta?.invoke()
        }
        Spacer(Modifier.height(estilo.espacioEtiqueta))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(estilo.altura)
                .cajaCampo(apariencia, conError, origenError, enfoque)
                .padding(start = estilo.rellenoInicio),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = when {
                    conError && elevado -> Colores.errorIntenso
                    conError -> Colores.errorClaro
                    else -> estilo.colorNeutro
                },
                modifier = Modifier.size(estilo.tamanoIcono),
            )
            Spacer(Modifier.width(estilo.espacioIconoTexto))
            EntradaTexto(
                valor = valor,
                alCambiar = alCambiar,
                ejemplo = ejemplo,
                estiloValor = estiloValor,
                estiloEjemplo = EstilosTexto.ejemploCampo,
                colorEjemplo = estilo.colorNeutro,
                enfoque = enfoque,
                opcionesTeclado = KeyboardOptions(
                    capitalization = capitalizacion,
                    keyboardType = tipoTeclado,
                    imeAction = accionTeclado,
                ),
                alConfirmar = alConfirmar,
                transformacionVisual = transformacionVisual,
                modifier = Modifier.weight(1f),
                colorValor = if (conError && elevado) Colores.errorProfundo else Colores.textoCampo,
            )
            if (elevado) {
                FinalCampoElevado(conError = conError, botonFinal = botonFinal)
            } else {
                if (mostrarIconoError(conError, botonFinal != null, origenError)) {
                    IconoErrorCampo()
                }
                if (botonFinal != null) {
                    botonFinal()
                    Spacer(Modifier.width(Dimensiones.rellenoFinalCampoCompactoConBoton))
                } else {
                    Spacer(Modifier.width(estilo.rellenoFinal))
                }
            }
        }
        if (mensajeError != null) {
            MensajeError(mensaje = mensajeError, origen = origenError, apariencia = apariencia)
        }
    }
}

/** Final del campo elevado: el botón (el ojo) y, si hay error, la insignia roja casi en el borde. */
@Composable
private fun FinalCampoElevado(conError: Boolean, botonFinal: (@Composable () -> Unit)?) {
    botonFinal?.invoke()
    if (conError) {
        Spacer(Modifier.width(Dimensiones.espacioOjoInsignia))
        InsigniaErrorCampo()
        Spacer(Modifier.width(Dimensiones.rellenoFinalInsignia))
    } else {
        Spacer(Modifier.width(Dimensiones.rellenoFinalCampoElevado))
    }
}
