package com.example.finanzia.pantallas.primeringreso

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finanzia.R
import com.example.finanzia.componentes.AvisoError
import com.example.finanzia.componentes.BotonDegradado
import com.example.finanzia.componentes.ColoresOpcion
import com.example.finanzia.componentes.EncabezadoPaso
import com.example.finanzia.componentes.OrigenError
import com.example.finanzia.componentes.Tarjeta
import com.example.finanzia.componentes.TarjetaOpcion
import com.example.finanzia.datos.modelos.Frecuencia
import com.example.finanzia.datos.repositorios.RepositorioMovimientosSupabase
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos
import com.example.finanzia.tema.TemaFinanzia
import com.example.finanzia.utilidades.SeparadorMiles

/** Separador de miles del monto, como en el diseño (18,500). */
private const val SEPARADOR_MILES = ','

/** Cómo se ve cada frecuencia: nombre, detalle, emoji y fondo del emoji. */
private class OpcionFrecuencia(
    val frecuencia: Frecuencia,
    @param:StringRes val titulo: Int,
    @param:StringRes val detalle: Int,
    @param:StringRes val emoji: Int,
    val fondoEmoji: Color,
)

/** La frecuencia elegida se marca en verde. */
private val coloresFrecuencia = ColoresOpcion(
    acento = Colores.verdeMarca,
    fondoElegida = Colores.verdeNieve,
    fondoEmojiElegida = Colores.verdeMarca,
)

private val opciones = listOf(
    OpcionFrecuencia(Frecuencia.MENSUAL, R.string.ingreso_mensual, R.string.ingreso_mensual_detalle, R.string.ingreso_emoji_mensual, Colores.rellenoCampo),
    OpcionFrecuencia(Frecuencia.QUINCENAL, R.string.ingreso_quincenal, R.string.ingreso_quincenal_detalle, R.string.ingreso_emoji_quincenal, Colores.rellenoCampo),
    OpcionFrecuencia(Frecuencia.SEMANAL, R.string.ingreso_semanal, R.string.ingreso_semanal_detalle, R.string.ingreso_emoji_semanal, Colores.indigoNieve),
    OpcionFrecuencia(Frecuencia.DIARIO, R.string.ingreso_diario, R.string.ingreso_diario_detalle, R.string.ingreso_emoji_diario, Colores.amarilloNieve),
)

/**
 * Registrar el primer ingreso: monto y frecuencia. El estado vive en [PrimerIngresoViewModel];
 * cuando Supabase lo guarda, avisa con [alIngresoGuardado].
 */
@Composable
fun PantallaPrimerIngreso(
    alVolver: () -> Unit,
    alIngresoGuardado: () -> Unit,
    viewModel: PrimerIngresoViewModel = viewModel { PrimerIngresoViewModel(RepositorioMovimientosSupabase()) },
) {
    val estado = viewModel.estado
    val alIngresoGuardadoActual by rememberUpdatedState(alIngresoGuardado)
    LaunchedEffect(estado.guardado) {
        if (estado.guardado) {
            alIngresoGuardadoActual()
            viewModel.alAvisarGuardado()
        }
    }
    ContenidoPrimerIngreso(
        estado = estado,
        alCambiarMonto = viewModel::alCambiarMonto,
        alElegirFrecuencia = viewModel::alElegirFrecuencia,
        alGuardar = viewModel::alPulsarGuardar,
        alVolver = alVolver,
    )
}

/** Contenido sin estado: el formulario se desplaza y el botón queda fijo abajo. */
@Composable
private fun ContenidoPrimerIngreso(
    estado: EstadoPrimerIngreso,
    alCambiarMonto: (String) -> Unit,
    alElegirFrecuencia: (Frecuencia) -> Unit,
    alGuardar: () -> Unit,
    alVolver: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colores.fondo)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimensiones.margenPantallaAmplio),
        ) {
            Spacer(Modifier.height(Dimensiones.espacioSuperiorIngreso))
            EncabezadoPaso(
                paso = stringResource(R.string.ingreso_paso),
                descripcionVolver = stringResource(R.string.accion_volver),
                alVolver = alVolver,
                colorTexto = Colores.verdeOscuro,
                colorFondo = Colores.verdeNieve,
                colorBorde = Colores.verdeNieveBorde,
            )
            Spacer(Modifier.height(Dimensiones.espacioBarraTitulo))
            Titulo()
            Spacer(Modifier.height(Dimensiones.espacioTituloDescripcionIngreso))
            Text(
                text = stringResource(R.string.ingreso_descripcion),
                style = EstilosTexto.cuerpo,
                color = Colores.textoSecundario,
            )
            Spacer(Modifier.height(Dimensiones.espacioDescripcionMonto))
            TarjetaMonto(estado = estado, alCambiarMonto = alCambiarMonto, alGuardar = alGuardar)
            Spacer(Modifier.height(Dimensiones.espacioMontoFrecuencias))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.ingreso_etiqueta_frecuencia).uppercase(),
                    style = EstilosTexto.etiquetaExterna,
                    color = Colores.textoFuerte,
                    modifier = Modifier.weight(1f),
                )
                Text(text = stringResource(R.string.ingreso_selecciona_uno), style = EstilosTexto.nota, color = Colores.verdeOscuro)
            }
            Spacer(Modifier.height(Dimensiones.espacioEncabezadoFrecuencias))
            opciones.chunked(2).forEachIndexed { fila, par ->
                if (fila > 0) Spacer(Modifier.height(Dimensiones.espacioEntreFrecuencias))
                Row(
                    modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(Dimensiones.espacioEntreFrecuencias),
                ) {
                    par.forEach { opcion ->
                        TarjetaOpcion(
                            emoji = stringResource(opcion.emoji),
                            titulo = stringResource(opcion.titulo),
                            detalle = stringResource(opcion.detalle),
                            elegida = opcion.frecuencia == estado.frecuencia,
                            alElegir = { alElegirFrecuencia(opcion.frecuencia) },
                            fondoEmoji = opcion.fondoEmoji,
                            colores = coloresFrecuencia,
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                        )
                    }
                }
            }
            Spacer(Modifier.height(Dimensiones.espacioFrecuenciasConsejo))
            Consejo()
            if (estado.errorGeneral != null) {
                Spacer(Modifier.height(Dimensiones.espacioFrecuenciasConsejo))
                AvisoError(mensaje = stringResource(estado.errorGeneral), origen = OrigenError.SERVIDOR)
            }
            Spacer(Modifier.height(Dimensiones.espacioFrecuenciasConsejo))
        }
        BotonDegradado(
            texto = stringResource(R.string.ingreso_boton_guardar),
            alPulsar = alGuardar,
            iconoFinal = Iconos.adelante,
            cargando = estado.cargando,
            tamanoIcono = Dimensiones.tamanoIcono,
            altura = Dimensiones.alturaBotonAlto,
            colorSombra = Colores.azulMarca,
            modifier = Modifier.padding(
                horizontal = Dimensiones.margenPantallaAmplio,
                vertical = Dimensiones.rellenoVerticalBarraInferior,
            ),
        )
    }
}

/** Flecha de entrada en verde y "Registra tu primer ingreso". */
@Composable
private fun Titulo() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(Dimensiones.tamanoRecuadroTitulo)
                .background(Colores.verdeNieveBorde, RoundedCornerShape(Dimensiones.radioRecuadroPequeno)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Iconos.entrada,
                contentDescription = null,
                tint = Colores.verdeMarca,
                modifier = Modifier.size(Dimensiones.tamanoIconoPequeno),
            )
        }
        Spacer(Modifier.width(Dimensiones.espacioPequeno))
        Text(text = stringResource(R.string.ingreso_titulo), style = EstilosTexto.titulo, color = Colores.azulProfundo)
    }
}

/** Tarjeta del monto: "$" en verde y el número grande, con separador de miles. */
@Composable
private fun TarjetaMonto(estado: EstadoPrimerIngreso, alCambiarMonto: (String) -> Unit, alGuardar: () -> Unit) {
    Tarjeta(relleno = PaddingValues(Dimensiones.rellenoTarjetaMonto)) {
        Text(
            text = stringResource(R.string.ingreso_etiqueta_monto).uppercase(),
            style = EstilosTexto.etiquetaExterna,
            color = Colores.textoMedio,
        )
        Spacer(Modifier.height(Dimensiones.espacioEtiquetaMonto))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.monto_simbolo_moneda), style = EstilosTexto.monto, color = Colores.verdeOscuro)
            Spacer(Modifier.width(Dimensiones.espacioSimboloMonto))
            val colorBorde = if (estado.errorMonto != null) Colores.error else Colores.verdeMarca
            BasicTextField(
                value = estado.monto,
                onValueChange = alCambiarMonto,
                singleLine = true,
                textStyle = EstilosTexto.monto.copy(color = Colores.tinta),
                cursorBrush = SolidColor(Colores.verdeMarca),
                visualTransformation = SeparadorMiles(SEPARADOR_MILES),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { alGuardar() }),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = Dimensiones.alturaCampoMonto)
                    .background(Colores.superficie)
                    .border(Dimensiones.grosorBorde, colorBorde)
                    .padding(horizontal = Dimensiones.rellenoCampoCompacto),
                decorationBox = { campo ->
                    Box(modifier = Modifier.heightIn(min = Dimensiones.alturaCampoMonto), contentAlignment = Alignment.CenterStart) {
                        if (estado.monto.isEmpty()) {
                            Text(text = stringResource(R.string.monto_ejemplo), style = EstilosTexto.monto, color = Colores.grisClaro)
                        }
                        campo()
                    }
                },
            )
        }
        Spacer(Modifier.height(Dimensiones.espacioMontoAyuda))
        if (estado.errorMonto != null) {
            Text(text = stringResource(estado.errorMonto), style = EstilosTexto.ayudaDestacada, color = Colores.errorOscuro)
        } else {
            Row {
                Icon(
                    imageVector = Iconos.confirmado,
                    contentDescription = null,
                    tint = Colores.verdeMarca,
                    modifier = Modifier.padding(top = Dimensiones.espacioMinimo).size(Dimensiones.tamanoIconoPequeno),
                )
                Spacer(Modifier.width(Dimensiones.espacioIconoAyudaMonto))
                Text(text = stringResource(R.string.ingreso_ayuda_monto), style = EstilosTexto.ayuda, color = Colores.textoTenue)
            }
        }
    }
}

/** Recuadro con degradado azul a verde y el consejo de Finanzia. */
@Composable
private fun Consejo() {
    val forma = RoundedCornerShape(Dimensiones.radioAviso)
    Text(
        text = buildAnnotatedString {
            withStyle(EstilosTexto.notaFuerte.toSpanStyle().copy(color = Colores.azulMarca)) {
                append(stringResource(R.string.ingreso_consejo_titulo))
            }
            append(stringResource(R.string.ingreso_consejo))
        },
        style = EstilosTexto.nota,
        color = Colores.textoMedio,
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.horizontalGradient(Colores.degradadoConsejo), forma)
            .border(Dimensiones.grosorBorde, Colores.azulSuave, forma)
            .padding(Dimensiones.rellenoConsejo),
    )
}

@Preview(showSystemUi = true)
@Composable
private fun VistaPreviaPrimerIngreso() {
    TemaFinanzia {
        ContenidoPrimerIngreso(
            estado = EstadoPrimerIngreso(monto = "18500"),
            alCambiarMonto = {},
            alElegirFrecuencia = {},
            alGuardar = {},
            alVolver = {},
        )
    }
}
