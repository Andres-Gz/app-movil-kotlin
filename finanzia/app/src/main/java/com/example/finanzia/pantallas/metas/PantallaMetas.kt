package com.example.finanzia.pantallas.metas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finanzia.R
import com.example.finanzia.componentes.AvisoError
import com.example.finanzia.componentes.OrigenError
import com.example.finanzia.componentes.TextoEnlace
import com.example.finanzia.datos.modelos.Meta
import com.example.finanzia.datos.modelos.ResumenMetas
import com.example.finanzia.datos.repositorios.RepositorioMetasSupabase
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos
import com.example.finanzia.tema.TemaFinanzia
import java.util.Calendar

/**
 * Metas financieras: el ahorro acumulado con el consejo de Finanzia IA, la racha de quincenas
 * y cada meta activa, todo leído de Supabase. Se recarga cada vez que se abre. Aportar, ajustar
 * la regla y eliminar se hacen aquí mismo en una ventana; editar abre el formulario de la meta.
 * [alAvisar] muestra un mensaje cuando algo se guardó.
 */
@Composable
fun PantallaMetas(
    alVolver: () -> Unit,
    alNuevaMeta: () -> Unit,
    alEditarMeta: (Meta) -> Unit,
    alAvisar: (Int) -> Unit,
    viewModel: MetasViewModel = viewModel { MetasViewModel(RepositorioMetasSupabase()) },
) {
    LaunchedEffect(Unit) { viewModel.cargar() }
    val estado = viewModel.estado
    val alAvisarActual by rememberUpdatedState(alAvisar)
    LaunchedEffect(estado.aviso) {
        val aviso = estado.aviso ?: return@LaunchedEffect
        alAvisarActual(aviso)
        viewModel.alAvisoMostrado()
    }
    ContenidoMetas(
        estado = estado,
        alVolver = alVolver,
        alNuevaMeta = alNuevaMeta,
        alReintentar = viewModel::cargar,
        alAbrirOrden = viewModel::alAbrirOrden,
        alCerrarOrden = viewModel::alCerrarOrden,
        alElegirOrden = viewModel::alElegirOrden,
        acciones = AccionesMeta(
            alAportar = viewModel::alPedirAporte,
            alAjustarRegla = viewModel::alPedirAjusteRegla,
            alAbrirMenu = viewModel::alAbrirMenu,
            alCerrarMenu = viewModel::alCerrarMenu,
            alEditar = alEditarMeta,
            alEliminar = viewModel::alPedirEliminar,
        ),
        accionesDialogo = AccionesDialogo(
            alCambiarMonto = viewModel::alCambiarMontoDialogo,
            alUsarMonto = viewModel::alUsarMonto,
            alElegirFrecuencia = viewModel::alElegirFrecuenciaDialogo,
            alConfirmar = viewModel::alConfirmarDialogo,
            alCerrar = viewModel::alCerrarDialogo,
        ),
    )
}

/** Contenido sin estado: el encabezado queda fijo y lo demás se desplaza. */
@Composable
private fun ContenidoMetas(
    estado: EstadoMetas,
    alVolver: () -> Unit,
    alNuevaMeta: () -> Unit,
    alReintentar: () -> Unit,
    alAbrirOrden: () -> Unit,
    alCerrarOrden: () -> Unit,
    alElegirOrden: (OrdenMetas) -> Unit,
    acciones: AccionesMeta,
    accionesDialogo: AccionesDialogo,
) {
    val hoy = remember { Calendar.getInstance() }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colores.fondoLavanda)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        EncabezadoMetas(alVolver = alVolver, alNuevaMeta = alNuevaMeta)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimensiones.margenPantallaAmplio),
        ) {
            Spacer(Modifier.height(Dimensiones.espacioEncabezadoMetas))
            if (estado.error != null) {
                AvisoError(mensaje = stringResource(estado.error), origen = OrigenError.SERVIDOR)
                TextoEnlace(texto = stringResource(R.string.inicio_reintentar), alPulsar = alReintentar)
                Spacer(Modifier.height(Dimensiones.espacioGrande))
            }
            val resumen = estado.resumen
            if (resumen != null) {
                TarjetaAhorroMetas(metas = resumen.metas, consejo = CalculosMetas.consejo(resumen.metas, hoy))
                Spacer(Modifier.height(Dimensiones.espacioAhorroRacha))
                TarjetaRacha(quincenas = CalculosMetas.quincenasSeguidas(resumen.fechasAportes, hoy))
                Spacer(Modifier.height(Dimensiones.espacioRachaLista))
                EncabezadoLista(
                    cantidad = resumen.metas.size,
                    orden = estado.orden,
                    ordenAbierto = estado.ordenAbierto,
                    alAbrirOrden = alAbrirOrden,
                    alCerrarOrden = alCerrarOrden,
                    alElegirOrden = alElegirOrden,
                )
                Spacer(Modifier.height(Dimensiones.espacioMediano))
                if (resumen.metas.isEmpty()) {
                    Text(
                        text = stringResource(R.string.metas_sin_metas),
                        style = EstilosTexto.cuerpo,
                        color = Colores.textoApagado,
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(Dimensiones.elevacionCampo, RoundedCornerShape(Dimensiones.radioBoton), ambientColor = Colores.sombra, spotColor = Colores.sombra)
                            .background(Colores.superficie, RoundedCornerShape(Dimensiones.radioBoton))
                            .padding(Dimensiones.rellenoTarjetaMeta),
                    )
                }
                CalculosMetas.ordenar(resumen.metas, estado.orden).forEach { meta ->
                    TarjetaDetalleMeta(meta = meta, hoy = hoy, menuAbierto = estado.menuDe == meta.idMeta, acciones = acciones)
                    Spacer(Modifier.height(Dimensiones.espacioGrande))
                }
                Spacer(Modifier.height(Dimensiones.espacioFinalMetas))
            }
        }
    }
    DialogoMetaAbierto(estado = estado, hoy = hoy, acciones = accionesDialogo)
}

/** Flecha de volver, "Metas Financieras" con su sello y el lema, y el botón "+ Nueva Meta". */
@Composable
private fun EncabezadoMetas(alVolver: () -> Unit, alNuevaMeta: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimensiones.alturaEncabezadoMetas)
            .padding(horizontal = Dimensiones.margenEncabezadoMetas),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(Dimensiones.tamanoBotonVolver)
                .clip(CircleShape)
                .clickable(role = Role.Button, onClick = alVolver),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Iconos.atras,
                contentDescription = stringResource(R.string.accion_volver),
                tint = Colores.tinta,
                modifier = Modifier.size(Dimensiones.tamanoBaseIcono),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.metas_titulo),
                    style = EstilosTexto.titulo,
                    color = Colores.tinta,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Spacer(Modifier.width(Dimensiones.espacioPequeno))
                Icon(Iconos.verificado, null, tint = Colores.verdeSeguro, modifier = Modifier.size(Dimensiones.tamanoIconoCompacto))
            }
            Text(
                text = stringResource(R.string.metas_subtitulo),
                style = EstilosTexto.nota,
                color = Colores.textoApagado,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.width(Dimensiones.espacioMinimo))
        Row(
            modifier = Modifier
                .height(Dimensiones.alturaBotonNuevaMeta)
                .clip(CircleShape)
                .background(Colores.rellenoCampo)
                .clickable(role = Role.Button, onClick = alNuevaMeta)
                .padding(horizontal = Dimensiones.espacioGrande),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Iconos.agregarCirculo, null, tint = Colores.azulIntenso, modifier = Modifier.size(Dimensiones.tamanoIconoMediano))
            Spacer(Modifier.width(Dimensiones.espacioIconoTextoPildora))
            Text(text = stringResource(R.string.metas_nueva), style = EstilosTexto.ayudaFuerte, color = Colores.azulIntenso)
        }
    }
}

/** "Metas Activas (3)" con la bandera, y "Ordenar" con su menú. */
@Composable
private fun EncabezadoLista(
    cantidad: Int,
    orden: OrdenMetas,
    ordenAbierto: Boolean,
    alAbrirOrden: () -> Unit,
    alCerrarOrden: () -> Unit,
    alElegirOrden: (OrdenMetas) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Iconos.bandera, null, tint = Colores.azulIntenso, modifier = Modifier.size(Dimensiones.tamanoIconoMediano))
        Spacer(Modifier.width(Dimensiones.espacioIconoTituloLista))
        Text(
            text = stringResource(R.string.metas_activas, cantidad),
            style = EstilosTexto.tituloSeccion,
            color = Colores.tinta,
            modifier = Modifier.weight(1f),
        )
        Box {
            Row(
                modifier = Modifier.clickable(role = Role.Button, onClick = alAbrirOrden),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = stringResource(R.string.metas_ordenar), style = EstilosTexto.ayudaDestacada, color = Colores.azulIntenso)
                Spacer(Modifier.width(Dimensiones.espacioOrdenarFlecha))
                Icon(Iconos.desplegar, null, tint = Colores.azulIntenso, modifier = Modifier.size(Dimensiones.tamanoIconoPequeno))
            }
            DropdownMenu(expanded = ordenAbierto, onDismissRequest = alCerrarOrden, containerColor = Colores.superficie) {
                OrdenMetas.entries.forEach { opcion ->
                    val elegida = opcion == orden
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(textoDe(opcion)),
                                style = if (elegida) EstilosTexto.boton else EstilosTexto.botonSecundario,
                                color = if (elegida) Colores.azulIntenso else Colores.tinta,
                            )
                        },
                        onClick = { alElegirOrden(opcion) },
                    )
                }
            }
        }
    }
}

private fun textoDe(orden: OrdenMetas): Int = when (orden) {
    OrdenMetas.FECHA_LIMITE -> R.string.metas_orden_fecha
    OrdenMetas.MAYOR_AVANCE -> R.string.metas_orden_avance
    OrdenMetas.NOMBRE -> R.string.metas_orden_nombre
}

@Preview(showSystemUi = true)
@Composable
private fun VistaPreviaMetas() {
    TemaFinanzia {
        ContenidoMetas(
            estado = EstadoMetas(
                resumen = ResumenMetas(
                    metas = listOf(
                        Meta(1, "Vacaciones San Andrés 2025", 4_000_000.0, 2_800_000.0, "2025-06-30T05:00:00+00:00", "Viajes & Ocio", "🏝️", 200_000.0, "quincenal"),
                        Meta(2, "Fondo de Emergencia (6 meses)", 18_000_000.0, 12_050_000.0, null, "Ahorro", "🛟", 500_000.0, "mensual"),
                    ),
                ),
            ),
            alVolver = {},
            alNuevaMeta = {},
            alReintentar = {},
            alAbrirOrden = {},
            alCerrarOrden = {},
            alElegirOrden = {},
            acciones = AccionesMeta({}, {}, {}, {}, {}, {}),
            accionesDialogo = AccionesDialogo({}, {}, {}, {}, {}),
        )
    }
}
