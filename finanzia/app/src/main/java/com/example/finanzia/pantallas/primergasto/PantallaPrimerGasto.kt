package com.example.finanzia.pantallas.primergasto

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finanzia.R
import com.example.finanzia.componentes.AvisoError
import com.example.finanzia.componentes.BotonDegradado
import com.example.finanzia.componentes.EncabezadoPaso
import com.example.finanzia.componentes.OrigenError
import com.example.finanzia.componentes.TarjetaMonto
import com.example.finanzia.componentes.TextoEnlace
import com.example.finanzia.datos.modelos.CategoriaGasto
import com.example.finanzia.datos.repositorios.RepositorioMovimientosSupabase
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos
import com.example.finanzia.tema.TemaFinanzia

/**
 * Registrar el primer gasto: monto y categoría. El estado vive en [PrimerGastoViewModel];
 * cuando Supabase lo guarda, avisa con [alGastoGuardado].
 */
@Composable
fun PantallaPrimerGasto(
    alVolver: () -> Unit,
    alGastoGuardado: () -> Unit,
    alOmitir: () -> Unit,
    alAdministrarCategorias: () -> Unit,
    viewModel: PrimerGastoViewModel = viewModel { PrimerGastoViewModel(RepositorioMovimientosSupabase()) },
) {
    val estado = viewModel.estado
    val alGastoGuardadoActual by rememberUpdatedState(alGastoGuardado)
    LaunchedEffect(estado.guardado) {
        if (estado.guardado) {
            alGastoGuardadoActual()
            viewModel.alAvisarGuardado()
        }
    }
    ContenidoPrimerGasto(
        estado = estado,
        alCambiarMonto = viewModel::alCambiarMonto,
        alElegirCategoria = viewModel::alElegirCategoria,
        alGuardar = viewModel::alPulsarGuardar,
        alVolver = alVolver,
        alOmitir = alOmitir,
        alAdministrarCategorias = alAdministrarCategorias,
    )
}

/** Contenido sin estado: el botón y "Omitir" van justo debajo del formulario; si no cabe, el formulario se desplaza. */
@Composable
private fun ContenidoPrimerGasto(
    estado: EstadoPrimerGasto,
    alCambiarMonto: (String) -> Unit,
    alElegirCategoria: (CategoriaGasto) -> Unit,
    alGuardar: () -> Unit,
    alVolver: () -> Unit,
    alOmitir: () -> Unit,
    alAdministrarCategorias: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colores.fondoLavanda)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
    ) {
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimensiones.margenPantallaAmplio),
        ) {
            Spacer(Modifier.height(Dimensiones.espacioSuperiorIngreso))
            EncabezadoPaso(
                paso = stringResource(R.string.gasto_paso),
                descripcionVolver = stringResource(R.string.accion_volver),
                alVolver = alVolver,
                colorTexto = Colores.rosaOscuro,
                colorFondo = Colores.rosaNieve,
                colorBorde = Colores.rosaNieveBorde,
            )
            Spacer(Modifier.height(Dimensiones.espacioBarraTitulo))
            Titulo()
            Spacer(Modifier.height(Dimensiones.espacioTituloDescripcionIngreso))
            Text(text = stringResource(R.string.gasto_descripcion), style = EstilosTexto.cuerpo, color = Colores.textoApagado)
            Spacer(Modifier.height(Dimensiones.espacioDescripcionMonto))
            TarjetaMonto(
                etiqueta = stringResource(R.string.gasto_etiqueta_monto),
                monto = estado.monto,
                alCambiar = alCambiarMonto,
                alConfirmar = alGuardar,
                acento = Colores.rosaIntenso,
                nota = stringResource(R.string.gasto_ayuda_monto),
                iconoNota = Iconos.informacion,
                mensajeError = estado.errorMonto?.let { stringResource(it) },
            )
            Spacer(Modifier.height(Dimensiones.espacioMontoFrecuencias))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.gasto_etiqueta_categoria).uppercase(),
                    style = EstilosTexto.etiquetaExterna,
                    color = Colores.textoFuerte,
                    modifier = Modifier.weight(1f),
                )
                AdministrarCategorias(alPulsar = alAdministrarCategorias)
            }
            Spacer(Modifier.height(Dimensiones.espacioEncabezadoFrecuencias))
            CuadriculaCategorias(elegida = estado.categoria, alElegir = alElegirCategoria)
            Spacer(Modifier.height(Dimensiones.espacioFrecuenciasConsejo))
            Dato()
            if (estado.errorGeneral != null) {
                Spacer(Modifier.height(Dimensiones.espacioFrecuenciasConsejo))
                AvisoError(mensaje = stringResource(estado.errorGeneral), origen = OrigenError.SERVIDOR)
            }
            Spacer(Modifier.height(Dimensiones.espacioFrecuenciasConsejo))
        }
        Column(
            modifier = Modifier.padding(
                horizontal = Dimensiones.margenPantallaAmplio,
                vertical = Dimensiones.rellenoVerticalBarraInferior,
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BotonDegradado(
                texto = stringResource(R.string.gasto_boton_guardar),
                alPulsar = alGuardar,
                iconoFinal = Iconos.adelante,
                cargando = estado.cargando,
                radio = Dimensiones.radioBotonCompacto,
                colorSombra = Colores.azulMarca,
                colores = listOf(Colores.azulMarca, Colores.azulMarca),
            )
            Spacer(Modifier.height(Dimensiones.espacioPequeno))
            TextoEnlace(texto = stringResource(R.string.gasto_omitir), alPulsar = alOmitir, color = Colores.textoTenue)
        }
    }
}

/** Flecha de salida en rosa y "Registra tu primer gasto". */
@Composable
private fun Titulo() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(Dimensiones.tamanoRecuadroCategoria)
                .background(Colores.rosaNieveBorde, RoundedCornerShape(Dimensiones.radioRecuadroPequeno)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Iconos.salida,
                contentDescription = null,
                tint = Colores.rosaIntenso,
                modifier = Modifier.size(Dimensiones.tamanoIconoPequeno),
            )
        }
        Spacer(Modifier.width(Dimensiones.espacioPequeno))
        Text(text = stringResource(R.string.gasto_titulo), style = EstilosTexto.titulo, color = Colores.tinta)
    }
}

/** Engranaje y "Administrar categorías". */
@Composable
private fun AdministrarCategorias(alPulsar: () -> Unit) {
    Row(
        modifier = Modifier.clickable(role = Role.Button, onClick = alPulsar),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Iconos.ajustes,
            contentDescription = null,
            tint = Colores.tinta,
            modifier = Modifier.size(Dimensiones.tamanoIconoPequeno),
        )
        Spacer(Modifier.width(Dimensiones.espacioIconoEnlace))
        Text(text = stringResource(R.string.gasto_administrar), style = EstilosTexto.notaFuerte, color = Colores.tinta)
    }
}

/** Recuadro rosa con el dato de Finanzia. */
@Composable
private fun Dato() {
    val forma = RoundedCornerShape(Dimensiones.radioAviso)
    Text(
        text = buildAnnotatedString {
            withStyle(EstilosTexto.notaFuerte.toSpanStyle().copy(color = Colores.rosaProfundo)) {
                append(stringResource(R.string.gasto_dato_titulo))
            }
            append(stringResource(R.string.gasto_dato))
        },
        style = EstilosTexto.nota,
        color = Colores.textoMedio,
        modifier = Modifier
            .fillMaxWidth()
            .background(Colores.rosaBruma, forma)
            .border(Dimensiones.grosorBorde, Colores.rosaNieveBorde, forma)
            .padding(Dimensiones.rellenoConsejo),
    )
}

@Preview(showSystemUi = true)
@Composable
private fun VistaPreviaPrimerGasto() {
    TemaFinanzia {
        ContenidoPrimerGasto(
            estado = EstadoPrimerGasto(monto = "3200"),
            alCambiarMonto = {},
            alElegirCategoria = {},
            alGuardar = {},
            alVolver = {},
            alOmitir = {},
            alAdministrarCategorias = {},
        )
    }
}
