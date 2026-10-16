package com.example.finanzia.pantallas.primerospasos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.safeDrawingPadding
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.finanzia.R
import com.example.finanzia.componentes.Pildora
import com.example.finanzia.componentes.Tarjeta
import com.example.finanzia.componentes.TextoEnlace
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos
import com.example.finanzia.tema.TemaFinanzia

/**
 * Primer paso tras la bienvenida: Finanzia se presenta y pregunta por dónde empezar.
 * No tiene estado propio; solo avisa qué opción se eligió.
 */
@Composable
fun PantallaPrimerosPasos(
    alRegistrarIngreso: () -> Unit,
    alRegistrarGasto: () -> Unit,
    alExplorarDespues: () -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Colores.fondo)
            .safeDrawingPadding(),
    ) {
        // Arriba la presentación y abajo las opciones; si la pantalla es baja, todo se desplaza.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = maxHeight)
                .padding(horizontal = Dimensiones.margenPantallaAmplio),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Presentacion()
            Opciones(
                alRegistrarIngreso = alRegistrarIngreso,
                alRegistrarGasto = alRegistrarGasto,
                alExplorarDespues = alExplorarDespues,
            )
        }
    }
}

@Composable
private fun Presentacion() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(Dimensiones.espacioSuperiorBienvenida))
        PildoraPaso()
        Spacer(Modifier.height(Dimensiones.espacioPildoraLogo))
        Logo()
        Spacer(Modifier.height(Dimensiones.espacioLogoTitulo))
        Text(text = stringResource(R.string.primeros_titulo), style = EstilosTexto.titulo, color = Colores.azulProfundo)
        Spacer(Modifier.height(Dimensiones.espacioTituloEslogan))
        Text(text = stringResource(R.string.primeros_eslogan), style = EstilosTexto.nota, color = Colores.verdeOscuro)
        Spacer(Modifier.height(Dimensiones.espacioEsloganDescripcion))
        Text(
            text = stringResource(R.string.primeros_descripcion),
            style = EstilosTexto.cuerpo,
            color = Colores.textoMedio,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Dimensiones.espacioDescripcionSugerencia))
        TarjetaSugerencia()
        Spacer(Modifier.height(Dimensiones.espacioDescripcionSugerencia))
    }
}

@Composable
private fun Opciones(alRegistrarIngreso: () -> Unit, alRegistrarGasto: () -> Unit, alExplorarDespues: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Opcion(
            texto = stringResource(R.string.primeros_ingreso),
            icono = Iconos.sumar,
            alPulsar = alRegistrarIngreso,
            fondo = Brush.linearGradient(Colores.degradadoBoton),
            colorTexto = Colores.textoSobreColor,
            fondoIcono = Colores.blancoTranslucido,
            colorIcono = Colores.textoSobreColor,
            colorFlecha = Colores.textoSobreColor,
            sombra = Colores.verdeMarca,
        )
        Spacer(Modifier.height(Dimensiones.espacioEntreOpciones))
        Opcion(
            texto = stringResource(R.string.primeros_gasto),
            icono = Iconos.restar,
            alPulsar = alRegistrarGasto,
            fondo = SolidColor(Colores.superficie),
            colorTexto = Colores.textoCampo,
            fondoIcono = Colores.errorFondo,
            colorIcono = Colores.rosaIntenso,
            colorFlecha = Colores.textoTenue,
            sombra = Colores.sombra,
            elevacion = Dimensiones.elevacionCampo,
            borde = BorderStroke(Dimensiones.grosorBorde, Colores.bordeCampo),
        )
        Spacer(Modifier.height(Dimensiones.espacioOpcionesEnlace))
        TextoEnlace(
            texto = stringResource(R.string.primeros_explorar),
            alPulsar = alExplorarDespues,
            color = Colores.textoTenue,
        )
        Spacer(Modifier.height(Dimensiones.espacioEnlacePie))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Iconos.escudoListo,
                contentDescription = null,
                tint = Colores.verdeMarca,
                modifier = Modifier.size(Dimensiones.tamanoIconoPequeno),
            )
            Spacer(Modifier.width(Dimensiones.espacioPequeno))
            Text(text = stringResource(R.string.primeros_pie), style = EstilosTexto.ayuda, color = Colores.textoTenue)
        }
        Spacer(Modifier.height(Dimensiones.espacioPieInferiorAmplio))
    }
}

/** "Paso 1 de 2 • Primeros pasos" con un punto verde, sobre verde muy claro. */
@Composable
private fun PildoraPaso() {
    Row(
        modifier = Modifier
            .background(Colores.verdeNieve, CircleShape)
            .border(Dimensiones.grosorBorde, Colores.verdeNieveBorde, CircleShape)
            .padding(horizontal = Dimensiones.rellenoHorizontalPildora, vertical = Dimensiones.rellenoVerticalPildoraMediana),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(Dimensiones.tamanoPuntoEstado)
                .background(Colores.verdeDestello, CircleShape),
        )
        Spacer(Modifier.width(Dimensiones.espacioPuntoEstado))
        Text(text = stringResource(R.string.primeros_paso), style = EstilosTexto.nota, color = Colores.verdeOscuro)
    }
}

/** "FINANZIA" con la insignia "IA" en azul. */
@Composable
private fun Logo() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(R.string.nombre_app).uppercase(),
            style = EstilosTexto.logoCompacto,
            color = Colores.azulProfundo,
        )
        Spacer(Modifier.width(Dimensiones.espacioLogoInsignia))
        Pildora(
            texto = stringResource(R.string.nombre_app_resaltado),
            colorFondo = Colores.azulSuave,
            colorContenido = Colores.azulMarca,
            rellenoHorizontal = Dimensiones.rellenoHorizontalInsignia,
            rellenoVertical = Dimensiones.rellenoVerticalInsignia,
            forma = RoundedCornerShape(Dimensiones.radioInsignia),
        )
    }
}

/** Tarjeta blanca con un rayo: "¿Por dónde te gustaría empezar?" y la sugerencia. */
@Composable
private fun TarjetaSugerencia() {
    Tarjeta(
        relleno = PaddingValues(
            horizontal = Dimensiones.rellenoHorizontalSugerencia,
            vertical = Dimensiones.rellenoVerticalSugerencia,
        ),
    ) {
        Row {
            Box(
                modifier = Modifier
                    .size(Dimensiones.tamanoRecuadroIcono)
                    .background(Colores.rellenoCampo, RoundedCornerShape(Dimensiones.radioRecuadroIcono)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Iconos.rayo,
                    contentDescription = null,
                    tint = Colores.azulMarca,
                    modifier = Modifier.size(Dimensiones.tamanoIconoPequeno),
                )
            }
            Spacer(Modifier.width(Dimensiones.espacioRecuadroTextoSugerencia))
            Column {
                Text(
                    text = stringResource(R.string.primeros_pregunta),
                    style = EstilosTexto.cuerpoDestacado,
                    color = Colores.textoCampo,
                )
                Text(
                    text = stringResource(R.string.primeros_sugerencia),
                    style = EstilosTexto.ayuda,
                    color = Colores.textoSecundario,
                )
            }
        }
    }
}

/** Botón grande de opción: icono en un recuadro, texto en dos líneas y una flecha al final. */
@Composable
private fun Opcion(
    texto: String,
    icono: ImageVector,
    alPulsar: () -> Unit,
    fondo: Brush,
    colorTexto: Color,
    fondoIcono: Color,
    colorIcono: Color,
    colorFlecha: Color,
    sombra: Color,
    elevacion: Dp = Dimensiones.elevacionBoton,
    borde: BorderStroke? = null,
) {
    val forma = RoundedCornerShape(Dimensiones.radioBoton)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = Dimensiones.alturaOpcion)
            .shadow(elevacion, forma, ambientColor = sombra, spotColor = sombra)
            .background(fondo, forma)
            .then(if (borde != null) Modifier.border(borde, forma) else Modifier)
            .clip(forma)
            .clickable(role = Role.Button, onClick = alPulsar)
            .padding(start = Dimensiones.rellenoInicioOpcion, end = Dimensiones.rellenoFinOpcion),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(Dimensiones.tamanoRecuadroIcono)
                .background(fondoIcono, RoundedCornerShape(Dimensiones.radioRecuadroIcono)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(Dimensiones.tamanoIcono))
        }
        Spacer(Modifier.width(Dimensiones.espacioIconoTextoOpcion))
        Text(text = texto, style = EstilosTexto.boton, color = colorTexto, modifier = Modifier.weight(1f))
        Icon(
            imageVector = Iconos.abrir,
            contentDescription = null,
            tint = colorFlecha,
            modifier = Modifier.size(Dimensiones.tamanoIconoCompacto),
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun VistaPreviaPrimerosPasos() {
    TemaFinanzia {
        PantallaPrimerosPasos(alRegistrarIngreso = {}, alRegistrarGasto = {}, alExplorarDespues = {})
    }
}
