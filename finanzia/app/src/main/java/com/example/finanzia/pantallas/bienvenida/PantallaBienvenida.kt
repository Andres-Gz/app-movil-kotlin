package com.example.finanzia.pantallas.bienvenida

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.finanzia.R
import com.example.finanzia.componentes.BotonDegradado
import com.example.finanzia.componentes.BotonTexto
import com.example.finanzia.componentes.Pildora
import com.example.finanzia.componentes.Tarjeta
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos
import com.example.finanzia.tema.TemaFinanzia

/** Un punto de la lista "Todo listo para comenzar", con una insignia opcional al lado del título. */
private class Logro(
    @param:StringRes val titulo: Int,
    @param:StringRes val detalle: Int,
    @param:StringRes val insignia: Int? = null,
)

private val logros = listOf(
    Logro(R.string.bienvenida_boveda, R.string.bienvenida_boveda_detalle),
    Logro(R.string.bienvenida_asistente, R.string.bienvenida_asistente_detalle),
    Logro(R.string.bienvenida_bono, R.string.bienvenida_bono_detalle, insignia = R.string.bienvenida_gratis),
)

/**
 * Bienvenida que se muestra al iniciar sesión. No tiene estado propio: solo muestra el [correo]
 * de la cuenta (el que guarda Supabase) y avisa qué botón se pulsó.
 */
@Composable
fun PantallaBienvenida(
    correo: String,
    alComenzar: () -> Unit,
    alPersonalizarMetas: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colores.fondoLavanda)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = Dimensiones.margenPantallaAmplio),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(Dimensiones.espacioSuperiorBienvenida))
        PildoraCuentaCreada()
        Spacer(Modifier.height(Dimensiones.espacioPildoraTitulo))
        Text(
            text = stringResource(R.string.bienvenida_titulo),
            style = EstilosTexto.titulo,
            color = Colores.tinta,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Dimensiones.espacioTituloDescripcionBienvenida))
        Text(
            text = stringResource(R.string.bienvenida_descripcion),
            style = EstilosTexto.cuerpo,
            color = Colores.textoApagado,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Dimensiones.espacioDescripcionLista))
        TarjetaLogros()
        Spacer(Modifier.height(Dimensiones.espacioListaCorreo))
        AvisoCorreo(correo = correo)
        Spacer(Modifier.height(Dimensiones.espacioCorreoBotones))
        BotonDegradado(
            texto = stringResource(R.string.bienvenida_boton_comenzar),
            alPulsar = alComenzar,
            iconoFinal = Iconos.adelante,
            espacioIcono = Dimensiones.espacioIconoBotonBienvenida,
            radio = Dimensiones.radioBotonPequeno,
            altura = Dimensiones.alturaBotonAlto,
            colorSombra = Colores.azulIntenso,
            colores = Colores.degradadoBotonFrio,
        )
        Spacer(Modifier.height(Dimensiones.espacioEntreBotonesBienvenida))
        BotonTexto(
            texto = stringResource(R.string.bienvenida_boton_personalizar),
            alPulsar = alPersonalizarMetas,
            fondo = Colores.superficie,
            colorTexto = Colores.tinta,
            altura = Dimensiones.alturaBotonMediano,
            radio = Dimensiones.radioBotonPequeno,
        )
        Spacer(Modifier.height(Dimensiones.espacioBotonesPieBienvenida))
        PieSeguridad()
        Spacer(Modifier.height(Dimensiones.espacioPieInferiorAmplio))
    }
}

/** Píldora azul de arriba: cohete, "¡Cuenta creada con éxito!" y una flecha, en verde. */
@Composable
private fun PildoraCuentaCreada() {
    Row(
        modifier = Modifier
            .height(Dimensiones.alturaPildoraBienvenida)
            .shadow(Dimensiones.elevacionCampo, CircleShape, ambientColor = Colores.sombra, spotColor = Colores.sombra)
            .background(Colores.azulPildora, CircleShape)
            .padding(start = Dimensiones.rellenoInicioPildoraBienvenida, end = Dimensiones.rellenoFinPildoraBienvenida),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = stringResource(R.string.bienvenida_cohete), style = EstilosTexto.cuerpo)
        Spacer(Modifier.width(Dimensiones.espacioEmojiPildora))
        Text(
            text = stringResource(R.string.bienvenida_cuenta_creada).uppercase(),
            style = EstilosTexto.ayudaFuerte,
            color = Colores.verdeSeguro,
        )
        Spacer(Modifier.width(Dimensiones.espacioTextoFlechaPildora))
        Icon(
            imageVector = Iconos.flechaPildora,
            contentDescription = null,
            tint = Colores.verdeSeguro,
            modifier = Modifier.size(Dimensiones.tamanoFlechaPildora),
        )
    }
}

/** Tarjeta blanca con el encabezado "Todo listo para comenzar" y los logros, todos cumplidos. */
@Composable
private fun TarjetaLogros() {
    Tarjeta(relleno = PaddingValues(Dimensiones.rellenoTarjetaLista)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(Dimensiones.tamanoRecuadroIcono)
                    .background(Colores.azulBotonClaro, RoundedCornerShape(Dimensiones.radioRecuadroPequeno)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Iconos.escudoListo,
                    contentDescription = null,
                    tint = Colores.azulIntenso,
                    modifier = Modifier.size(Dimensiones.tamanoIconoCompacto),
                )
            }
            Spacer(Modifier.width(Dimensiones.espacioRecuadroTitulo))
            Text(
                text = stringResource(R.string.bienvenida_todo_listo),
                style = EstilosTexto.cuerpoDestacado,
                color = Colores.tinta,
                modifier = Modifier.weight(1f),
            )
            Pildora(
                texto = stringResource(R.string.bienvenida_progreso, logros.size, logros.size),
                colorFondo = Colores.verdeMenta,
                colorContenido = Colores.verdeNoche,
                rellenoHorizontal = Dimensiones.rellenoHorizontalContador,
                rellenoVertical = Dimensiones.rellenoVerticalInsignia,
                mayusculas = false,
            )
        }
        Spacer(Modifier.height(Dimensiones.espacioEncabezadoLista))
        logros.forEachIndexed { indice, logro ->
            if (indice > 0) Spacer(Modifier.height(Dimensiones.espacioEntreLogros))
            FilaLogro(logro)
        }
    }
}

/** Un logro: círculo verde con la marca, título (con su insignia, si tiene) y detalle. */
@Composable
private fun FilaLogro(logro: Logro) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Colores.fondoLista, RoundedCornerShape(Dimensiones.radioRecuadroPequeno))
            .padding(
                start = Dimensiones.rellenoInicioLogro,
                end = Dimensiones.rellenoFinLogro,
                top = Dimensiones.rellenoVerticalLogro,
                bottom = Dimensiones.rellenoVerticalLogro,
            ),
    ) {
        Box(
            modifier = Modifier
                .padding(top = Dimensiones.rellenoSuperiorCirculoLogro)
                .size(Dimensiones.tamanoCirculoLogro)
                .background(Colores.verdeMenta, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Iconos.logroCumplido,
                contentDescription = null,
                tint = Colores.verdeSobreMenta,
                modifier = Modifier.size(Dimensiones.tamanoIconoPequeno),
            )
        }
        Spacer(Modifier.width(Dimensiones.espacioCirculoTextoLogro))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(logro.titulo),
                    style = EstilosTexto.notaFuerte,
                    color = Colores.tinta,
                    modifier = Modifier.weight(1f),
                )
                if (logro.insignia != null) {
                    Pildora(
                        texto = stringResource(logro.insignia),
                        colorFondo = Colores.lavandaInsignia,
                        colorContenido = Colores.moradoNoche,
                        rellenoHorizontal = Dimensiones.rellenoHorizontalInsignia,
                        rellenoVertical = Dimensiones.rellenoVerticalInsignia,
                        forma = RoundedCornerShape(Dimensiones.radioInsignia),
                    )
                }
            }
            Text(text = stringResource(logro.detalle), style = EstilosTexto.ayuda, color = Colores.textoApagado)
        }
    }
}

/** Recuadro azul con el sobre, "Correo de confirmación enviado" y el correo de la cuenta. */
@Composable
private fun AvisoCorreo(correo: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = Dimensiones.alturaAvisoCorreo)
            .background(Colores.fondoConfirmacion, RoundedCornerShape(Dimensiones.radioAviso))
            .padding(start = Dimensiones.rellenoInicioAvisoCorreo, end = Dimensiones.rellenoFinAvisoCorreo),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(Dimensiones.tamanoCirculoCorreo)
                .background(Colores.superficie, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Iconos.correoEnviado,
                contentDescription = null,
                tint = Colores.azulIntenso,
                modifier = Modifier.size(Dimensiones.tamanoIcono),
            )
        }
        Spacer(Modifier.width(Dimensiones.espacioCirculoTextoCorreo))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.bienvenida_correo_enviado),
                style = EstilosTexto.notaFuerte,
                color = Colores.tinta,
            )
            // Si el correo no cabe, se corta con "…", como en el diseño.
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.bienvenida_revisa_bandeja))
                    withStyle(EstilosTexto.ayudaFuerte.toSpanStyle().copy(color = Colores.azulIntenso)) {
                        append(correo)
                    }
                },
                style = EstilosTexto.ayuda,
                color = Colores.textoApagado,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.width(Dimensiones.espacioTextoIconoCorreo))
        Icon(
            imageVector = Iconos.sincronizar,
            contentDescription = null,
            tint = Colores.azulIntenso,
            modifier = Modifier.size(Dimensiones.tamanoIconoCompacto),
        )
    }
}

/** Nota de seguridad al pie, con un candado verde. */
@Composable
private fun PieSeguridad() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Iconos.candadoMaterial,
            contentDescription = null,
            tint = Colores.verdeSeguro,
            modifier = Modifier.size(Dimensiones.tamanoIconoMediano),
        )
        Spacer(Modifier.width(Dimensiones.espacioPequeno))
        Text(
            text = stringResource(R.string.bienvenida_pie),
            style = EstilosTexto.ayuda,
            color = Colores.textoApagado,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun VistaPreviaBienvenida() {
    TemaFinanzia {
        PantallaBienvenida(correo = "paula.gomez@ejemplo.com", alComenzar = {}, alPersonalizarMetas = {})
    }
}
