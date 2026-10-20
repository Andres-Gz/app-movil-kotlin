package com.example.finanzia.pantallas.editarmovimiento

import androidx.compose.foundation.background
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finanzia.R
import com.example.finanzia.componentes.AvisoError
import com.example.finanzia.componentes.BotonDegradado
import com.example.finanzia.componentes.BotonTexto
import com.example.finanzia.componentes.CampoConEtiqueta
import com.example.finanzia.componentes.EncabezadoPaso
import com.example.finanzia.componentes.OrigenError
import com.example.finanzia.componentes.TarjetaMonto
import com.example.finanzia.datos.modelos.CategoriaGasto
import com.example.finanzia.datos.modelos.MovimientoReciente
import com.example.finanzia.datos.repositorios.RepositorioMovimientosSupabase
import com.example.finanzia.pantallas.primergasto.CuadriculaCategorias
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos
import com.example.finanzia.tema.TemaFinanzia

/**
 * Editar o eliminar un movimiento que se tocó en el inicio. Cuando Supabase confirma,
 * avisa con [alTerminar] para que se muestre el mensaje y se vuelva al inicio.
 */
@Composable
fun PantallaEditarMovimiento(
    movimiento: MovimientoReciente,
    alVolver: () -> Unit,
    alTerminar: (ResultadoEdicion) -> Unit,
    viewModel: EditarMovimientoViewModel = viewModel(key = movimiento.idMovimiento.toString()) {
        EditarMovimientoViewModel(RepositorioMovimientosSupabase(), movimiento)
    },
) {
    val estado = viewModel.estado
    val alTerminarActual by rememberUpdatedState(alTerminar)
    LaunchedEffect(estado.resultado) {
        estado.resultado?.let { alTerminarActual(it) }
    }
    ContenidoEditarMovimiento(
        estado = estado,
        alCambiarMonto = viewModel::alCambiarMonto,
        alCambiarDescripcion = viewModel::alCambiarDescripcion,
        alElegirCategoria = viewModel::alElegirCategoria,
        alGuardar = viewModel::alPulsarGuardar,
        alEliminar = viewModel::alPulsarEliminar,
        alConfirmarEliminar = viewModel::alConfirmarEliminar,
        alCancelarEliminar = viewModel::alCancelarEliminar,
        alVolver = alVolver,
    )
}

/** Contenido sin estado: mismo estilo que registrar el primer ingreso o gasto, con menos cosas. */
@Composable
private fun ContenidoEditarMovimiento(
    estado: EstadoEditarMovimiento,
    alCambiarMonto: (String) -> Unit,
    alCambiarDescripcion: (String) -> Unit,
    alElegirCategoria: (CategoriaGasto) -> Unit,
    alGuardar: () -> Unit,
    alEliminar: () -> Unit,
    alConfirmarEliminar: () -> Unit,
    alCancelarEliminar: () -> Unit,
    alVolver: () -> Unit,
) {
    // El color de acento dice si es ingreso (verde) o gasto (rosa), como en sus pantallas de registro.
    val acento = if (estado.esIngreso) Colores.verdeMarca else Colores.rosaIntenso
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
                paso = stringResource(if (estado.esIngreso) R.string.inicio_tipo_ingreso else R.string.inicio_tipo_gasto),
                descripcionVolver = stringResource(R.string.accion_volver),
                alVolver = alVolver,
                colorTexto = if (estado.esIngreso) Colores.verdeOscuro else Colores.rosaOscuro,
                colorFondo = if (estado.esIngreso) Colores.verdeNieve else Colores.rosaNieve,
                colorBorde = if (estado.esIngreso) Colores.verdeNieveBorde else Colores.rosaNieveBorde,
            )
            Spacer(Modifier.height(Dimensiones.espacioBarraTitulo))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(Dimensiones.tamanoRecuadroCategoria)
                        .background(
                            if (estado.esIngreso) Colores.verdeNieveBorde else Colores.rosaNieveBorde,
                            RoundedCornerShape(Dimensiones.radioRecuadroPequeno),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (estado.esIngreso) Iconos.entrada else Iconos.salida,
                        contentDescription = null,
                        tint = acento,
                        modifier = Modifier.size(Dimensiones.tamanoIconoPequeno),
                    )
                }
                Spacer(Modifier.width(Dimensiones.espacioPequeno))
                Text(
                    text = stringResource(if (estado.esIngreso) R.string.editar_titulo_ingreso else R.string.editar_titulo_gasto),
                    style = EstilosTexto.titulo,
                    color = Colores.tinta,
                )
            }
            Spacer(Modifier.height(Dimensiones.espacioTituloDescripcionIngreso))
            Text(text = stringResource(R.string.editar_descripcion), style = EstilosTexto.cuerpo, color = Colores.textoApagado)
            Spacer(Modifier.height(Dimensiones.espacioDescripcionMonto))
            TarjetaMonto(
                etiqueta = stringResource(R.string.editar_etiqueta_monto),
                monto = estado.monto,
                alCambiar = alCambiarMonto,
                alConfirmar = alGuardar,
                acento = acento,
                nota = stringResource(R.string.editar_nota_monto),
                iconoNota = Iconos.informacion,
                mensajeError = estado.errorMonto?.let { stringResource(it) },
            )
            Spacer(Modifier.height(Dimensiones.espacioEntreSecciones))
            CampoConEtiqueta(
                etiqueta = stringResource(R.string.editar_etiqueta_descripcion),
                valor = estado.descripcion,
                alCambiar = alCambiarDescripcion,
                ejemplo = stringResource(R.string.editar_ejemplo_descripcion),
                icono = Iconos.recibo,
                mensajeError = estado.errorDescripcion?.let { stringResource(it) },
                capitalizacion = KeyboardCapitalization.Sentences,
                accionTeclado = ImeAction.Done,
                alConfirmar = alGuardar,
            )
            if (!estado.esIngreso) {
                Spacer(Modifier.height(Dimensiones.espacioMontoFrecuencias))
                Text(
                    text = stringResource(R.string.editar_etiqueta_categoria).uppercase(),
                    style = EstilosTexto.etiquetaExterna,
                    color = Colores.textoFuerte,
                )
                Spacer(Modifier.height(Dimensiones.espacioEncabezadoFrecuencias))
                CuadriculaCategorias(elegida = estado.categoria, alElegir = alElegirCategoria)
            }
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
        ) {
            BotonDegradado(
                texto = stringResource(R.string.editar_boton_guardar),
                alPulsar = alGuardar,
                iconoFinal = Iconos.marcaVerificacion,
                cargando = estado.cargando,
                radio = Dimensiones.radioBotonCompacto,
                colorSombra = Colores.azulMarca,
            )
            BotonTexto(
                texto = stringResource(R.string.editar_boton_eliminar),
                alPulsar = alEliminar,
                colorTexto = Colores.errorOscuro,
            )
        }
    }
    if (estado.confirmandoEliminar) {
        AlertDialog(
            onDismissRequest = alCancelarEliminar,
            title = { Text(text = stringResource(R.string.editar_confirmar_titulo), style = EstilosTexto.tituloAviso) },
            text = { Text(text = stringResource(R.string.editar_confirmar_mensaje), style = EstilosTexto.cuerpo) },
            confirmButton = {
                TextButton(onClick = alConfirmarEliminar) {
                    Text(text = stringResource(R.string.editar_confirmar_si), style = EstilosTexto.boton, color = Colores.errorOscuro)
                }
            },
            dismissButton = {
                TextButton(onClick = alCancelarEliminar) {
                    Text(text = stringResource(R.string.editar_confirmar_no), style = EstilosTexto.botonSecundario, color = Colores.textoApagado)
                }
            },
            containerColor = Colores.superficie,
        )
    }
}

@Composable
private fun VistaPrevia(estado: EstadoEditarMovimiento) {
    TemaFinanzia {
        ContenidoEditarMovimiento(
            estado = estado,
            alCambiarMonto = {},
            alCambiarDescripcion = {},
            alElegirCategoria = {},
            alGuardar = {},
            alEliminar = {},
            alConfirmarEliminar = {},
            alCancelarEliminar = {},
            alVolver = {},
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun VistaPreviaEditarGasto() {
    VistaPrevia(EstadoEditarMovimiento(monto = "64500", descripcion = "Supermercado", categoria = CategoriaGasto.COMIDA))
}

@Preview(showSystemUi = true)
@Composable
private fun VistaPreviaEditarIngreso() {
    VistaPrevia(EstadoEditarMovimiento(esIngreso = true, monto = "2800000", descripcion = "Ingreso mensual"))
}
