package com.example.finanzia.pantallas.nuevameta

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finanzia.R
import com.example.finanzia.componentes.AvisoError
import com.example.finanzia.componentes.BotonDegradado
import com.example.finanzia.componentes.OrigenError
import com.example.finanzia.componentes.Pildora
import com.example.finanzia.datos.modelos.CategoriaMeta
import com.example.finanzia.datos.modelos.Frecuencia
import com.example.finanzia.datos.modelos.Meta
import com.example.finanzia.datos.repositorios.RepositorioMetasSupabase
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos
import com.example.finanzia.tema.TemaFinanzia
import java.util.Calendar

/**
 * Nueva meta, paso 1: nombre, categoría, monto, mes y frecuencia del aporte. Con [meta] sirve para
 * editarla: el formulario llega lleno. El estado vive en [NuevaMetaViewModel]; cuando Supabase guarda
 * la meta, avisa con [alMetaGuardada].
 */
@Composable
fun PantallaNuevaMeta(
    meta: Meta?,
    alVolver: () -> Unit,
    alMetaGuardada: () -> Unit,
    viewModel: NuevaMetaViewModel = viewModel { NuevaMetaViewModel(RepositorioMetasSupabase()) },
) {
    // El formulario se prepara al entrar a la pantalla, pero no al girarla (se perdería lo escrito).
    var preparada by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!preparada) {
            viewModel.alAbrir(meta)
            preparada = true
        }
    }
    val estado = viewModel.estado
    val alMetaGuardadaActual by rememberUpdatedState(alMetaGuardada)
    LaunchedEffect(estado.guardada) {
        if (estado.guardada) {
            alMetaGuardadaActual()
            viewModel.alAvisarGuardada()
        }
    }
    ContenidoNuevaMeta(
        estado = estado,
        alVolver = alVolver,
        alCambiarNombre = viewModel::alCambiarNombre,
        alBorrarNombre = viewModel::alBorrarNombre,
        alElegirCategoria = viewModel::alElegirCategoria,
        alCambiarMonto = viewModel::alCambiarMonto,
        alSumarMonto = viewModel::alSumarMonto,
        alAbrirSelector = viewModel::alAbrirSelector,
        alCerrarSelector = viewModel::alCerrarSelector,
        alElegirMes = viewModel::alElegirMes,
        alElegirFrecuencia = viewModel::alElegirFrecuencia,
        alContinuar = viewModel::alPulsarContinuar,
    )
}

/** Contenido sin estado: el formulario se desplaza y "Continuar" queda fijo abajo. */
@Composable
private fun ContenidoNuevaMeta(
    estado: EstadoNuevaMeta,
    alVolver: () -> Unit,
    alCambiarNombre: (String) -> Unit,
    alBorrarNombre: () -> Unit,
    alElegirCategoria: (CategoriaMeta) -> Unit,
    alCambiarMonto: (String) -> Unit,
    alSumarMonto: (Long) -> Unit,
    alAbrirSelector: () -> Unit,
    alCerrarSelector: () -> Unit,
    alElegirMes: (MesAno) -> Unit,
    alElegirFrecuencia: (Frecuencia) -> Unit,
    alContinuar: () -> Unit,
) {
    val hoy = remember { Calendar.getInstance() }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colores.fondoLavanda)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
    ) {
        EncabezadoNuevaMeta(editando = estado.editando, alVolver = alVolver)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimensiones.margenInicio),
        ) {
            Spacer(Modifier.height(Dimensiones.espacioEncabezadoObjetivo))
            TarjetaObjetivo()
            Spacer(Modifier.height(Dimensiones.espacioEntreSeccionesMeta))
            EtiquetaSeccion(
                texto = stringResource(R.string.nueva_meta_etiqueta_nombre),
                detalle = stringResource(R.string.nueva_meta_obligatorio),
                estiloDetalle = EstilosTexto.ayuda,
                colorDetalle = Colores.textoApagado,
            )
            Spacer(Modifier.height(Dimensiones.espacioEtiquetaSeccionMeta))
            CampoNombre(nombre = estado.nombre, alCambiar = alCambiarNombre, alBorrar = alBorrarNombre)
            Spacer(Modifier.height(Dimensiones.espacioEntreSeccionesMeta))
            EtiquetaSeccion(
                texto = stringResource(R.string.nueva_meta_etiqueta_categoria),
                detalle = pluralStringResource(R.plurals.nueva_meta_disponibles, CategoriaMeta.entries.size, CategoriaMeta.entries.size),
                estiloDetalle = EstilosTexto.ayudaFuerte,
                colorDetalle = Colores.verdeSeguro,
            )
            Spacer(Modifier.height(Dimensiones.espacioEtiquetaSeccionMeta))
            CuadriculaCategoriasMeta(elegida = estado.categoria, alElegir = alElegirCategoria)
            Spacer(Modifier.height(Dimensiones.espacioEntreSeccionesMeta))
            TarjetaMontoMeta(
                monto = estado.monto,
                incrementoElegido = estado.incrementoElegido,
                alCambiar = alCambiarMonto,
                alSumar = alSumarMonto,
            )
            Spacer(Modifier.height(Dimensiones.espacioEntreSeccionesMeta))
            TarjetaFechaMeta(
                mes = estado.mes,
                frecuencia = estado.frecuencia,
                hoy = hoy,
                alAbrirSelector = alAbrirSelector,
                alElegirFrecuencia = alElegirFrecuencia,
            )
            Spacer(Modifier.height(Dimensiones.espacioEntreSeccionesMeta))
        }
        // El diseño se corta en "Frecuencia del aporte": este botón es una suposición hasta ver el resto.
        Column(
            modifier = Modifier.padding(horizontal = Dimensiones.margenInicio, vertical = Dimensiones.rellenoVerticalBarraInferior),
            verticalArrangement = Arrangement.spacedBy(Dimensiones.espacioPequeno),
        ) {
            if (estado.error != null) {
                AvisoError(
                    mensaje = stringResource(estado.error),
                    origen = if (estado.errorDelServidor) OrigenError.SERVIDOR else OrigenError.FORMULARIO,
                )
            }
            BotonDegradado(
                texto = stringResource(if (estado.editando) R.string.nueva_meta_guardar_cambios else R.string.nueva_meta_continuar),
                alPulsar = alContinuar,
                iconoFinal = if (estado.editando) Iconos.marcaVerificacion else Iconos.adelante,
                cargando = estado.cargando,
                radio = Dimensiones.radioBotonCompacto,
                colorSombra = Colores.azulIntenso,
                colores = Colores.degradadoBotonFrio,
            )
        }
    }
    if (estado.selectorAbierto) {
        SelectorMes(elegido = estado.mes, hoy = hoy, alElegir = alElegirMes, alCerrar = alCerrarSelector)
    }
}

/** Volver, "Nueva Meta" (o "Editar Meta") con el paso centrado, y el botón de ayuda. */
@Composable
private fun EncabezadoNuevaMeta(editando: Boolean, alVolver: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimensiones.alturaEncabezadoMetas)
            .padding(horizontal = Dimensiones.margenInicio),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(Dimensiones.tamanoBotonEncabezadoMeta)
                .clip(CircleShape)
                .background(Colores.azulBotonClaro)
                .clickable(role = Role.Button, onClick = alVolver),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Iconos.atras,
                contentDescription = stringResource(R.string.accion_volver),
                tint = Colores.tinta,
                modifier = Modifier.size(Dimensiones.tamanoIcono),
            )
        }
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(if (editando) R.string.nueva_meta_titulo_editar else R.string.nueva_meta_titulo),
                style = EstilosTexto.titulo,
                color = Colores.tinta,
            )
            Text(
                text = stringResource(if (editando) R.string.nueva_meta_paso_editar else R.string.nueva_meta_paso),
                style = EstilosTexto.ayuda,
                color = Colores.textoApagado,
                textAlign = TextAlign.Center,
            )
        }
        // Aún no existe la pantalla de ayuda.
        Box(
            modifier = Modifier
                .size(Dimensiones.tamanoBotonEncabezadoMeta)
                .background(Colores.rellenoCampo, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Iconos.ayuda,
                contentDescription = stringResource(R.string.accion_ayuda),
                tint = Colores.azulIntenso,
                modifier = Modifier.size(Dimensiones.tamanoIcono),
            )
        }
    }
}

/** "Asistido por Finanzia IA", "Optimizado COP", el título y la explicación. */
@Composable
private fun TarjetaObjetivo() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Colores.rellenoCampo, RoundedCornerShape(Dimensiones.radioBoton))
            .padding(Dimensiones.rellenoTarjetaObjetivo),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(Dimensiones.espacioEntrePildorasObjetivo)) {
            Pildora(
                texto = stringResource(R.string.nueva_meta_asistido),
                icono = Iconos.asistente,
                colorFondo = Colores.verdeMenta,
                colorContenido = Colores.verdeSobreMenta,
                rellenoHorizontal = Dimensiones.rellenoHorizontalPildoraPequena,
                rellenoVertical = Dimensiones.rellenoVerticalPildoraPequena,
                mayusculas = false,
                modifier = Modifier.height(Dimensiones.alturaPildoraObjetivo),
            )
            Pildora(
                texto = stringResource(R.string.nueva_meta_optimizado),
                colorFondo = Colores.azulBruma,
                colorContenido = Colores.azulIntenso,
                rellenoHorizontal = Dimensiones.rellenoHorizontalPildoraPequena,
                rellenoVertical = Dimensiones.rellenoVerticalPildoraPequena,
                mayusculas = false,
                modifier = Modifier.height(Dimensiones.alturaPildoraObjetivo),
            )
        }
        Spacer(Modifier.height(Dimensiones.espacioPildorasTitulo))
        Text(text = stringResource(R.string.nueva_meta_encabezado), style = EstilosTexto.titulo, color = Colores.tinta)
        Spacer(Modifier.height(Dimensiones.espacioTituloDescripcionObjetivo))
        Text(text = stringResource(R.string.nueva_meta_descripcion), style = EstilosTexto.cuerpo, color = Colores.textoApagado)
    }
}

/** Título de una sección con un detalle a la derecha ("Obligatorio", "6 disponibles"). */
@Composable
private fun EtiquetaSeccion(
    texto: String,
    detalle: String,
    estiloDetalle: TextStyle,
    colorDetalle: Color,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = texto, style = EstilosTexto.botonSecundario, color = Colores.tinta, modifier = Modifier.weight(1f))
        Text(text = detalle, style = estiloDetalle, color = colorDetalle)
    }
}

/** Campo blanco del nombre, con la bandera a la izquierda y el botón de borrar a la derecha. */
@Composable
private fun CampoNombre(nombre: String, alCambiar: (String) -> Unit, alBorrar: () -> Unit) {
    val forma = RoundedCornerShape(Dimensiones.radioBoton)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimensiones.alturaCampoNombreMeta)
            .shadow(Dimensiones.elevacionCampo, forma, ambientColor = Colores.sombra, spotColor = Colores.sombra)
            .background(Colores.superficie, forma)
            .padding(horizontal = Dimensiones.rellenoCampoNombreMeta),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(Dimensiones.tamanoRecuadroBandera)
                .background(Colores.azulBotonClaro, RoundedCornerShape(Dimensiones.radioRecuadroBandera)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Iconos.banderaContorno, null, tint = Colores.azulIntenso, modifier = Modifier.size(Dimensiones.tamanoIcono))
        }
        Spacer(Modifier.width(Dimensiones.espacioMediano))
        BasicTextField(
            value = nombre,
            onValueChange = alCambiar,
            singleLine = true,
            textStyle = EstilosTexto.valorCampo.copy(color = Colores.tinta),
            cursorBrush = SolidColor(Colores.azulIntenso),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Next),
            modifier = Modifier.weight(1f),
            decorationBox = { campo ->
                Box {
                    if (nombre.isEmpty()) {
                        Text(text = stringResource(R.string.nueva_meta_ejemplo_nombre), style = EstilosTexto.ejemploCampo, color = Colores.textoTenue)
                    }
                    campo()
                }
            },
        )
        if (nombre.isNotEmpty()) {
            Spacer(Modifier.width(Dimensiones.espacioPequeno))
            Icon(
                imageVector = Iconos.borrarTexto,
                contentDescription = stringResource(R.string.accion_borrar_nombre),
                tint = Colores.grisContorno,
                modifier = Modifier
                    .size(Dimensiones.tamanoIcono)
                    .clip(CircleShape)
                    .clickable(role = Role.Button, onClick = alBorrar),
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun VistaPreviaNuevaMeta() {
    TemaFinanzia {
        ContenidoNuevaMeta(
            estado = EstadoNuevaMeta(
                nombre = "Vacaciones en San Andrés",
                monto = "3500000",
                incrementoElegido = 1_000_000L,
                mes = MesAno(Calendar.NOVEMBER, 2026),
            ),
            alVolver = {},
            alCambiarNombre = {},
            alBorrarNombre = {},
            alElegirCategoria = {},
            alCambiarMonto = {},
            alSumarMonto = {},
            alAbrirSelector = {},
            alCerrarSelector = {},
            alElegirMes = {},
            alElegirFrecuencia = {},
            alContinuar = {},
        )
    }
}
