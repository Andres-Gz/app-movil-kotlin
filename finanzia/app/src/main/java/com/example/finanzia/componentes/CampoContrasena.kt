package com.example.finanzia.componentes

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.finanzia.R
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Iconos

/** Campo de contraseña: oculta el texto y permite mostrarlo con el icono del ojo. */
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
        tipoTeclado = KeyboardType.Password,
        accionTeclado = ImeAction.Done,
        alConfirmar = alConfirmar,
        transformacionVisual = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        iconoFinal = {
            BotonIcono(
                icono = if (visible) Iconos.ocultar else Iconos.mostrar,
                descripcion = stringResource(
                    if (visible) R.string.accion_ocultar_contrasena else R.string.accion_mostrar_contrasena,
                ),
                alPulsar = alAlternarVisibilidad,
                color = Colores.textoTenue,
            )
        },
    )
}
