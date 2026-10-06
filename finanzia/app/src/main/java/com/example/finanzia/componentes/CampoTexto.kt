package com.example.finanzia.componentes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto

/**
 * Campo de texto con etiqueta arriba, icono al inicio y mensaje de error debajo.
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
    tipoTeclado: KeyboardType = KeyboardType.Text,
    accionTeclado: ImeAction = ImeAction.Next,
    alConfirmar: () -> Unit = {},
    transformacionVisual: VisualTransformation = VisualTransformation.None,
    iconoFinal: (@Composable () -> Unit)? = null,
) {
    val forma = RoundedCornerShape(Dimensiones.radioCampo)
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = etiqueta, style = EstilosTexto.etiqueta, color = Colores.textoSecundario)
        Spacer(Modifier.height(Dimensiones.espacioPequeno))
        OutlinedTextField(
            value = valor,
            onValueChange = alCambiar,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = Dimensiones.alturaCampo)
                .shadow(Dimensiones.elevacionCampo, forma, ambientColor = Colores.sombra, spotColor = Colores.sombra),
            textStyle = EstilosTexto.cuerpo,
            placeholder = { Text(text = ejemplo, style = EstilosTexto.cuerpo) },
            leadingIcon = {
                Icon(imageVector = icono, contentDescription = null, modifier = Modifier.size(Dimensiones.tamanoIcono))
            },
            trailingIcon = iconoFinal,
            isError = mensajeError != null,
            visualTransformation = transformacionVisual,
            keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado, imeAction = accionTeclado),
            keyboardActions = KeyboardActions(onDone = { alConfirmar() }),
            singleLine = true,
            shape = forma,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Colores.textoPrincipal,
                unfocusedTextColor = Colores.textoPrincipal,
                errorTextColor = Colores.textoPrincipal,
                focusedContainerColor = Colores.superficie,
                unfocusedContainerColor = Colores.superficie,
                errorContainerColor = Colores.superficie,
                cursorColor = Colores.verdeMarca,
                errorCursorColor = Colores.error,
                focusedBorderColor = Colores.verdeMarca,
                unfocusedBorderColor = Colores.transparente,
                errorBorderColor = Colores.error,
                focusedLeadingIconColor = Colores.verdeMarca,
                unfocusedLeadingIconColor = Colores.textoTenue,
                errorLeadingIconColor = Colores.error,
                focusedPlaceholderColor = Colores.textoTenue,
                unfocusedPlaceholderColor = Colores.textoTenue,
                errorPlaceholderColor = Colores.textoTenue,
            ),
        )
        if (mensajeError != null) {
            Spacer(Modifier.height(Dimensiones.espacioMinimo))
            Text(text = mensajeError, style = EstilosTexto.nota, color = Colores.error)
        }
    }
}
