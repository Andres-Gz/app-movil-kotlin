package com.example.finanzia.componentes

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos

/** Campo de contraseña del inicio de sesión: oculta el texto con puntos separados y lo muestra con el ojo. */
@Composable
fun CampoContrasena(
    etiqueta: String,
    valor: String,
    alCambiar: (String) -> Unit,
    ejemplo: String,
    visible: Boolean,
    alAlternarVisibilidad: () -> Unit,
    modifier: Modifier = Modifier,
    mensajeError: String? = null,
    origenError: OrigenError = OrigenError.FORMULARIO,
    alConfirmar: () -> Unit = {},
) {
    CampoTexto(
        etiqueta = etiqueta,
        valor = valor,
        alCambiar = alCambiar,
        ejemplo = ejemplo,
        icono = Iconos.candado,
        modifier = modifier,
        mensajeError = mensajeError,
        origenError = origenError,
        tipoTeclado = KeyboardType.Password,
        accionTeclado = ImeAction.Done,
        alConfirmar = alConfirmar,
        transformacionVisual = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        estiloValor = if (visible) EstilosTexto.valorCampo else EstilosTexto.contrasenaOculta,
        estiloEjemplo = EstilosTexto.contrasenaOculta,
        botonFinal = { BotonVerContrasena(visible = visible, alAlternar = alAlternarVisibilidad) },
    )
}
