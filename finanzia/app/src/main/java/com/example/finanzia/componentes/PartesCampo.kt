package com.example.finanzia.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos

// Piezas que comparten los campos de texto (CampoTexto y CampoConEtiqueta).

/** Medidas y colores que cambian según la apariencia del campo. */
internal class EstiloCaja(
    val altura: Dp,
    val rellenoInicio: Dp,
    val tamanoIcono: Dp,
    val espacioIconoTexto: Dp,
    val rellenoFinal: Dp,
    val espacioEtiqueta: Dp,
    /** Color del icono y del texto de ejemplo cuando no hay error. */
    val colorNeutro: Color,
    val colorEtiqueta: Color,
)

internal val AparienciaCampo.estilo: EstiloCaja
    get() = when (this) {
        AparienciaCampo.TARJETA -> EstiloCaja(
            altura = Dimensiones.alturaCampoCompacto,
            rellenoInicio = Dimensiones.rellenoCampoCompacto,
            tamanoIcono = Dimensiones.tamanoIconoCompacto,
            espacioIconoTexto = Dimensiones.espacioIconoTextoCompacto,
            rellenoFinal = Dimensiones.rellenoCampoCompacto,
            espacioEtiqueta = Dimensiones.espacioEtiquetaCampo,
            colorNeutro = Colores.textoTenue,
            colorEtiqueta = Colores.textoSecundario,
        )
        AparienciaCampo.RELLENO -> EstiloCaja(
            altura = Dimensiones.alturaCampoRelleno,
            rellenoInicio = Dimensiones.rellenoCampo,
            tamanoIcono = Dimensiones.tamanoIcono,
            espacioIconoTexto = Dimensiones.espacioIconoTextoRelleno,
            rellenoFinal = Dimensiones.rellenoCampo,
            espacioEtiqueta = Dimensiones.espacioEtiquetaCampoRelleno,
            colorNeutro = Colores.grisContorno,
            colorEtiqueta = Colores.textoApagado,
        )
        AparienciaCampo.ELEVADO -> EstiloCaja(
            altura = Dimensiones.alturaCampoElevado,
            rellenoInicio = Dimensiones.rellenoCampo,
            tamanoIcono = Dimensiones.tamanoIcono,
            espacioIconoTexto = Dimensiones.espacioIconoTextoElevado,
            rellenoFinal = Dimensiones.rellenoFinalCampoElevado,
            espacioEtiqueta = Dimensiones.espacioEtiquetaCampoElevado,
            colorNeutro = Colores.textoApagado,
            colorEtiqueta = Colores.tinta,
        )
    }

/**
 * Caja del campo según su [apariencia]. Con error, la tarjeta y la rellena se pintan rojizas con
 * borde rojo; la elevada se rellena de rosa, sin borde. Al tocarla, enfoca el campo.
 */
internal fun Modifier.cajaCampo(
    apariencia: AparienciaCampo,
    conError: Boolean,
    origenError: OrigenError,
    enfoque: FocusRequester,
): Modifier {
    val forma = RoundedCornerShape(Dimensiones.radioCampo)
    val fondo = when {
        conError && apariencia == AparienciaCampo.ELEVADO -> Colores.errorContenedor
        conError -> Colores.errorFondoCampo
        apariencia == AparienciaCampo.RELLENO -> Colores.rellenoCampo
        else -> Colores.superficie
    }
    val sombra = if (apariencia != AparienciaCampo.RELLENO) {
        Modifier.shadow(Dimensiones.elevacionCampo, forma, ambientColor = Colores.sombra, spotColor = Colores.sombra)
    } else {
        Modifier
    }
    val conBorde = apariencia == AparienciaCampo.TARJETA || (conError && apariencia == AparienciaCampo.RELLENO)
    val borde = if (conBorde) {
        Modifier.border(Dimensiones.grosorBorde, colorBordeCampo(conError, origenError), forma)
    } else {
        Modifier
    }
    return this
        .then(sombra)
        .background(fondo, forma)
        .then(borde)
        .clickable(interactionSource = null, indication = null) { enfoque.requestFocus() }
}

private fun colorBordeCampo(conError: Boolean, origen: OrigenError) = when {
    !conError -> Colores.bordeCampo
    origen == OrigenError.FORMULARIO -> Colores.error
    else -> Colores.errorClaro
}

/** En los errores del formulario, un campo que ya tiene botón al final (el ojo) no lleva el icono de error. */
internal fun mostrarIconoError(conError: Boolean, tieneBotonFinal: Boolean, origen: OrigenError) =
    conError && (!tieneBotonFinal || origen == OrigenError.SERVIDOR)

/** Lo que el usuario escribe, con el texto de ejemplo mientras está vacío. */
@Composable
internal fun EntradaTexto(
    valor: String,
    alCambiar: (String) -> Unit,
    ejemplo: String,
    estiloValor: TextStyle,
    estiloEjemplo: TextStyle,
    colorEjemplo: Color,
    enfoque: FocusRequester,
    opcionesTeclado: KeyboardOptions,
    alConfirmar: () -> Unit,
    transformacionVisual: VisualTransformation,
    modifier: Modifier = Modifier,
    colorValor: Color = Colores.textoCampo,
) {
    BasicTextField(
        value = valor,
        onValueChange = alCambiar,
        modifier = modifier.focusRequester(enfoque),
        textStyle = estiloValor.copy(color = colorValor),
        keyboardOptions = opcionesTeclado,
        keyboardActions = KeyboardActions(onDone = { alConfirmar() }),
        singleLine = true,
        visualTransformation = transformacionVisual,
        cursorBrush = SolidColor(Colores.verdeMarca),
        decorationBox = { campo ->
            Box {
                if (valor.isEmpty()) {
                    Text(text = ejemplo, style = estiloEjemplo, color = colorEjemplo)
                }
                campo()
            }
        },
    )
}

/** Icono rojo de error al final del campo. */
@Composable
internal fun IconoErrorCampo() {
    Spacer(Modifier.width(Dimensiones.espacioPequeno))
    Icon(
        imageVector = Iconos.error,
        contentDescription = null,
        tint = Colores.error,
        modifier = Modifier.size(Dimensiones.tamanoIconoMediano),
    )
}

/** Insignia roja grande de error, la del campo elevado. */
@Composable
internal fun InsigniaErrorCampo() {
    Icon(
        imageVector = Iconos.error,
        contentDescription = null,
        tint = Colores.errorIntenso,
        modifier = Modifier.size(Dimensiones.tamanoInsigniaError),
    )
}

/**
 * Mensaje bajo el campo. En la tarjeta y la rellena, los errores del formulario llevan un círculo
 * y los del servidor, solo texto. En la elevada lleva siempre un triángulo de advertencia.
 */
@Composable
internal fun MensajeError(
    mensaje: String,
    origen: OrigenError,
    apariencia: AparienciaCampo = AparienciaCampo.TARJETA,
) {
    val elevado = apariencia == AparienciaCampo.ELEVADO
    val delFormulario = origen == OrigenError.FORMULARIO
    val icono = when {
        elevado -> Iconos.advertencia
        delFormulario -> Iconos.error
        else -> null
    }
    val color = when {
        elevado -> Colores.errorIntenso
        delFormulario -> Colores.errorOscuro
        else -> Colores.error
    }
    Spacer(
        Modifier.height(
            when {
                elevado -> Dimensiones.espacioCampoMensajeElevado
                delFormulario -> Dimensiones.espacioCampoMensajeFormulario
                else -> Dimensiones.espacioCampoMensajeServidor
            },
        ),
    )
    Row(
        modifier = Modifier.padding(start = Dimensiones.espacioMinimo),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icono != null) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = if (elevado) Colores.errorIntenso else Colores.error,
                modifier = Modifier.size(if (elevado) Dimensiones.tamanoIconoMediano else Dimensiones.tamanoIconoPequeno),
            )
            Spacer(Modifier.width(Dimensiones.espacioMinimo))
        }
        Text(
            text = mensaje,
            style = if (icono != null) EstilosTexto.ayudaDestacada else EstilosTexto.ayuda,
            color = color,
        )
    }
}
