package com.example.finanzia.pantallas.asistente

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finanzia.R
import com.example.finanzia.componentes.AvisoError
import com.example.finanzia.componentes.BarraNavegacion
import com.example.finanzia.componentes.BarraSuperiorApp
import com.example.finanzia.componentes.OrigenError
import com.example.finanzia.componentes.SeccionNavegacion
import com.example.finanzia.configuracion.Constantes
import com.example.finanzia.datos.modelos.AccionAsistente
import com.example.finanzia.datos.modelos.EstadoAccion
import com.example.finanzia.datos.modelos.MensajeAsistente
import com.example.finanzia.datos.modelos.RolMensaje
import com.example.finanzia.datos.modelos.TipoAccion
import com.example.finanzia.datos.repositorios.RepositorioAsistenteSupabase
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.TemaFinanzia
import java.util.Date

/** Lo que se puede hacer en el chat. La pantalla solo avisa; el ViewModel decide. */
data class AccionesAsistente(
    val alVolver: () -> Unit = {},
    val alAbrirInicio: () -> Unit = {},
    val alAbrirMetas: () -> Unit = {},
    val alCambiarTexto: (String) -> Unit = {},
    val alEnviar: () -> Unit = {},
    val alDictar: () -> Unit = {},
    val alElegirSugerencia: (String) -> Unit = {},
    val alAbrirAtajos: () -> Unit = {},
    val alCerrarAtajos: () -> Unit = {},
    val alElegirAtajo: (String) -> Unit = {},
    val alConfirmarAccion: (MensajeAsistente) -> Unit = {},
    val alCancelarAccion: (MensajeAsistente) -> Unit = {},
    val alAbrirMenu: () -> Unit = {},
    val alCerrarMenu: () -> Unit = {},
    val alNuevaConversacion: () -> Unit = {},
    val alPedirEliminar: () -> Unit = {},
    val alCerrarEliminar: () -> Unit = {},
    val alConfirmarEliminar: () -> Unit = {},
    val alAbrirHistorial: () -> Unit = {},
    val alCerrarHistorial: () -> Unit = {},
    val alAbrirConversacion: (Long) -> Unit = {},
)

/**
 * El chat con Finanzia IA. Las respuestas las arma la IA con los datos del usuario (Edge Function
 * `asistente`); lo que propone guardar aparece con Confirmar y Cancelar. La conversación sigue abierta
 * al volver a la pantalla, porque el ViewModel vive mientras la app esté abierta.
 */
@Composable
fun PantallaAsistente(
    alVolver: () -> Unit,
    alAbrirInicio: () -> Unit,
    alAbrirMetas: () -> Unit,
    alAvisar: (Int) -> Unit,
    viewModel: AsistenteViewModel = viewModel { AsistenteViewModel(RepositorioAsistenteSupabase()) },
) {
    val estado = viewModel.estado
    LaunchedEffect(estado.aviso) {
        estado.aviso?.let {
            alAvisar(it)
            viewModel.alAvisoMostrado()
        }
    }
    // El dictado por voz lo hace el reconocedor del teléfono; devuelve lo que entendió.
    val dictado = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
        val texto = resultado.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        if (resultado.resultCode == Activity.RESULT_OK && texto != null) viewModel.alDictar(texto)
    }
    val indicacionDictado = stringResource(R.string.asistente_dictado_indicacion)
    ContenidoAsistente(
        estado = estado,
        ahora = Date(),
        acciones = AccionesAsistente(
            alVolver = alVolver,
            alAbrirInicio = alAbrirInicio,
            alAbrirMetas = alAbrirMetas,
            alCambiarTexto = viewModel::alCambiarTexto,
            alEnviar = viewModel::alEnviar,
            alDictar = {
                val pedido = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                    .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    .putExtra(RecognizerIntent.EXTRA_LANGUAGE, Constantes.Asistente.IDIOMA_DICTADO)
                    .putExtra(RecognizerIntent.EXTRA_PROMPT, indicacionDictado)
                try {
                    dictado.launch(pedido)
                } catch (_: ActivityNotFoundException) {
                    alAvisar(R.string.asistente_sin_dictado)
                }
            },
            alElegirSugerencia = viewModel::alElegirSugerencia,
            alAbrirAtajos = viewModel::alAbrirAtajos,
            alCerrarAtajos = viewModel::alCerrarAtajos,
            alElegirAtajo = viewModel::alElegirAtajo,
            alConfirmarAccion = viewModel::alConfirmarAccion,
            alCancelarAccion = viewModel::alCancelarAccion,
            alAbrirMenu = viewModel::alAbrirMenu,
            alCerrarMenu = viewModel::alCerrarMenu,
            alNuevaConversacion = viewModel::alNuevaConversacion,
            alPedirEliminar = viewModel::alPedirEliminar,
            alCerrarEliminar = viewModel::alCerrarEliminar,
            alConfirmarEliminar = viewModel::alConfirmarEliminar,
            alAbrirHistorial = viewModel::alAbrirHistorial,
            alCerrarHistorial = viewModel::alCerrarHistorial,
            alAbrirConversacion = viewModel::alAbrirConversacion,
        ),
    )
}

/** Contenido sin estado: encabezado, sugerencias, los mensajes, la barra de escritura y la de navegación. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ContenidoAsistente(estado: EstadoAsistente, ahora: Date, acciones: AccionesAsistente) {
    // Con el teclado abierto se esconde la barra de navegación para dejarle espacio al chat.
    val tecladoAbierto = WindowInsets.isImeVisible
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colores.fondoLavanda)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
    ) {
        BarraSuperiorApp(centrada = true)
        EncabezadoAsistente(estado, acciones)
        Spacer(Modifier.height(Dimensiones.espacioPequeno))
        SugerenciasAsistente(alElegir = acciones.alElegirSugerencia)
        val sugerenciaVisible = estado.texto.isEmpty() && !estado.respondiendo
        ListaMensajes(
            estado = estado,
            ahora = ahora,
            acciones = acciones,
            sugerenciaVisible = sugerenciaVisible,
            modifier = Modifier.weight(1f),
        )
        if (sugerenciaVisible) {
            SugerenciaFlotante(alElegir = acciones.alElegirSugerencia)
            Spacer(Modifier.height(Dimensiones.espacioPequeno))
        }
        BarraEscritura(estado, acciones)
        Spacer(Modifier.height(Dimensiones.espacioBarraNavegacion))
        if (!tecladoAbierto) {
            BarraNavegacion(
                seleccionada = SeccionNavegacion.ASISTENTE,
                alAbrirInicio = acciones.alAbrirInicio,
                alAbrirMetas = acciones.alAbrirMetas,
            )
        }
    }
    if (estado.historialAbierto) HistorialAsistente(estado, acciones, ahora)
    if (estado.confirmandoEliminar) DialogoEliminarConversacion(acciones)
}

@Composable
private fun ListaMensajes(
    estado: EstadoAsistente,
    ahora: Date,
    acciones: AccionesAsistente,
    sugerenciaVisible: Boolean,
    modifier: Modifier,
) {
    val lista = rememberLazyListState()
    val bienvenida = estado.mensajes.isEmpty() && !estado.respondiendo
    val elementos = (if (bienvenida) 1 else 0) + estado.mensajes.size +
        (if (estado.respondiendo) 2 else 0) + (if (estado.error != null) 1 else 0)
    // Cada mensaje nuevo, la pregunta que se está mandando o un error dejan la lista al final. También cuando
    // aparece la sugerencia flotante, que le quita espacio a la lista.
    LaunchedEffect(estado.mensajes.size, estado.respondiendo, estado.error, sugerenciaVisible) {
        if (elementos > 0) lista.animateScrollToItem(elementos - 1)
    }
    if (estado.cargandoMensajes) {
        Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Colores.azulIntenso)
        }
        return
    }
    LazyColumn(
        state = lista,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = Dimensiones.margenPantallaAmplio, vertical = Dimensiones.espacioGrande),
        verticalArrangement = Arrangement.spacedBy(Dimensiones.espacioEntreMensajes),
    ) {
        if (bienvenida) {
            item {
                TarjetaRespuesta(
                    titulo = stringResource(R.string.asistente_bienvenida_titulo),
                    texto = stringResource(R.string.asistente_bienvenida),
                    antiguedad = null,
                )
            }
        }
        items(estado.mensajes, key = { it.idMensaje }) { mensaje ->
            when (mensaje.rol) {
                RolMensaje.USUARIO -> BurbujaUsuario(texto = mensaje.contenido, pie = pieEntregado(mensaje))
                RolMensaje.ASISTENTE -> RespuestaGuardada(mensaje, ahora) {
                    mensaje.accion?.let { accion ->
                        TarjetaAccion(
                            accion = accion,
                            estadoAccion = mensaje.estadoAccion,
                            enCurso = estado.accionEnCurso == mensaje.idMensaje,
                            alConfirmar = { acciones.alConfirmarAccion(mensaje) },
                            alCancelar = { acciones.alCancelarAccion(mensaje) },
                        )
                    }
                }
            }
        }
        estado.preguntaEnviada?.let { pregunta ->
            item { BurbujaUsuario(texto = pregunta, pie = stringResource(R.string.asistente_enviando)) }
            item { TarjetaPensando() }
        }
        estado.error?.let { error ->
            item { AvisoError(mensaje = stringResource(error), origen = OrigenError.SERVIDOR) }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun VistaPreviaAsistente() {
    TemaFinanzia {
        ContenidoAsistente(
            estado = EstadoAsistente(
                idConversacion = 1,
                mensajes = listOf(
                    MensajeAsistente(
                        1,
                        RolMensaje.USUARIO,
                        "¿Cómo van mis proyecciones y cuánto puedo gastar esta semana sin salirme del presupuesto?",
                        "2026-10-27T14:38:00+00:00",
                    ),
                    MensajeAsistente(
                        2,
                        RolMensaje.ASISTENTE,
                        "# Diagnóstico presupuestario\n¡Hola Paula! Revisé tus ingresos y gastos de las últimas 4 semanas. " +
                            "Tienes un margen saludable de **$32.000 / día** disponible.",
                        "2026-10-27T14:39:00+00:00",
                    ),
                    MensajeAsistente(3, RolMensaje.USUARIO, "Gasté 20 mil en almuerzo", "2026-10-27T14:40:00+00:00"),
                    MensajeAsistente(
                        4,
                        RolMensaje.ASISTENTE,
                        "# Gasto listo\nTe dejé el gasto preparado: confírmalo abajo.",
                        "2026-10-27T14:40:00+00:00",
                        accion = AccionAsistente(TipoAccion.GASTO, 20000.0, categoria = "comida", descripcion = "Almuerzo"),
                        estadoAccion = EstadoAccion.PENDIENTE,
                    ),
                ),
            ),
            ahora = Date(),
            acciones = AccionesAsistente(),
        )
    }
}
