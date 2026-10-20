package com.example.finanzia.componentes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto

/**
 * Campo de texto del inicio de sesión: tarjeta con icono al inicio y la etiqueta en mayúsculas
 * sobre lo que se escribe. Con [mensajeError] se pinta de rojo y muestra el mensaje debajo.
 * Es la base de los campos más específicos, como [CampoContrasena].
 */
@Composable
fun CampoTexto(
    etiqueta: String,
    valor: String,
    alCambiar: (String) -> Unit,
    ejemplo: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    mensajeError: String? = null,
    origenError: OrigenError = OrigenError.FORMULARIO,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    accionTeclado: ImeAction = ImeAction.Next,
    alConfirmar: () -> Unit = {},
    transformacionVisual: VisualTransformation = VisualTransformation.None,
    estiloValor: TextStyle = EstilosTexto.valorCampo,
    estiloEjemplo: TextStyle = EstilosTexto.ejemploCampo,
    botonFinal: (@Composable () -> Unit)? = null,
) {
    val conError = mensajeError != null
    val enfoque = remember { FocusRequester() }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimensiones.alturaCampo)
                .cajaCampo(AparienciaCampo.TARJETA, conError, origenError, enfoque)
                .padding(start = Dimensiones.rellenoCampo),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = if (conError) Colores.errorClaro else Colores.iconoCampo,
                modifier = Modifier.size(Dimensiones.tamanoIcono),
            )
            Spacer(Modifier.width(Dimensiones.espacioMediano))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(top = Dimensiones.rellenoSuperiorTextosCampo),
            ) {
                Text(
                    text = etiqueta.uppercase(),
                    style = EstilosTexto.etiqueta,
                    color = if (conError) Colores.error else Colores.textoSecundario,
                )
                Spacer(Modifier.height(Dimensiones.espacioEtiquetaValor))
                EntradaTexto(
                    valor = valor,
                    alCambiar = alCambiar,
                    ejemplo = ejemplo,
                    estiloValor = estiloValor,
                    estiloEjemplo = estiloEjemplo,
                    colorEjemplo = Colores.textoEjemplo,
                    enfoque = enfoque,
                    opcionesTeclado = KeyboardOptions(keyboardType = tipoTeclado, imeAction = accionTeclado),
                    alConfirmar = alConfirmar,
                    transformacionVisual = transformacionVisual,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (mostrarIconoError(conError, botonFinal != null, origenError)) {
                IconoErrorCampo()
            }
            if (botonFinal != null) {
                botonFinal()
                Spacer(Modifier.width(Dimensiones.rellenoFinalCampoConBoton))
            } else {
                Spacer(Modifier.width(Dimensiones.rellenoCampo))
            }
        }
        if (mensajeError != null) {
            MensajeError(mensaje = mensajeError, origen = origenError)
        }
    }
}
