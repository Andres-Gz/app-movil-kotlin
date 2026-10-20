package com.example.finanzia.pantallas.inicio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finanzia.R
import com.example.finanzia.componentes.AvisoError
import com.example.finanzia.componentes.BotonDegradado
import com.example.finanzia.componentes.OrigenError
import com.example.finanzia.componentes.Pildora
import com.example.finanzia.componentes.TextoEnlace
import com.example.finanzia.configuracion.Constantes
import com.example.finanzia.datos.modelos.MovimientoReciente
import com.example.finanzia.datos.modelos.ResumenInicio
import com.example.finanzia.datos.repositorios.RepositorioInicioSupabase
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos
import com.example.finanzia.tema.TemaFinanzia
import java.text.DecimalFormat
import kotlin.math.abs

/**
 * Inicio (dashboard): balance, consejo, meta y últimos movimientos, todo leído de Supabase.
 * Se recarga cada vez que se abre, para mostrar lo último que se registró.
 */
@Composable
fun PantallaInicio(
    alAgregar: () -> Unit,
    alEditarMovimiento: (MovimientoReciente) -> Unit,
    alAbrirMetas: () -> Unit,
    viewModel: InicioViewModel = viewModel { InicioViewModel(RepositorioInicioSupabase()) },
) {
    LaunchedEffect(Unit) { viewModel.cargar() }
    ContenidoInicio(
        estado = viewModel.estado,
        alAlternarMontos = viewModel::alAlternarMontos,
        alReintentar = viewModel::cargar,
        alAgregar = alAgregar,
        alEditarMovimiento = alEditarMovimiento,
        alAbrirMetas = alAbrirMetas,
    )
}

/** Contenido sin estado: el resumen se desplaza y la barra de navegación queda fija abajo. */
@Composable
private fun ContenidoInicio(
    estado: EstadoInicio,
    alAlternarMontos: () -> Unit,
    alReintentar: () -> Unit,
    alAgregar: () -> Unit,
    alEditarMovimiento: (MovimientoReciente) -> Unit,
    alAbrirMetas: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colores.fondoLavanda)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        BarraSuperiorInicio()
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimensiones.margenInicio),
        ) {
            val resumen = estado.resumen
            if (estado.error != null) {
                Spacer(Modifier.height(Dimensiones.espacioBarraSaludo))
                AvisoError(mensaje = stringResource(estado.error), origen = OrigenError.SERVIDOR)
                TextoEnlace(texto = stringResource(R.string.inicio_reintentar), alPulsar = alReintentar)
            }
            if (resumen != null) {
                Spacer(Modifier.height(Dimensiones.espacioBarraSaludo))
                Saludo(resumen)
                Spacer(Modifier.height(Dimensiones.espacioEntreSecciones))
                TarjetaBalance(resumen = resumen, visibles = estado.montosVisibles, alAlternarMontos = alAlternarMontos)
                Spacer(Modifier.height(Dimensiones.espacioEntreSecciones))
                TarjetaAsistente(resumen = resumen, visibles = estado.montosVisibles)
                Spacer(Modifier.height(Dimensiones.espacioEntreSecciones))
                TarjetaMeta(meta = resumen.meta, visibles = estado.montosVisibles)
                Spacer(Modifier.height(Dimensiones.espacioEntreSecciones))
                Movimientos(movimientos = resumen.movimientos, visibles = estado.montosVisibles, alEditar = alEditarMovimiento)
                Spacer(Modifier.height(Dimensiones.espacioEntreSecciones))
                BotonDegradado(
                    texto = stringResource(R.string.inicio_conversar),
                    // Aún no existe el chat con Finanzia IA.
                    alPulsar = {},
                    iconoInicial = Iconos.conversar,
                    iconoFinal = Iconos.adelante,
                    radio = Dimensiones.radioBotonCompacto,
                    colorSombra = Colores.azulMarca,
                    colores = Colores.degradadoConversar,
                )
                Spacer(Modifier.height(Dimensiones.espacioEntreSecciones))
            }
        }
        BarraNavegacion(alAgregar = alAgregar, alAbrirMetas = alAbrirMetas)
    }
}

/** Logo con "Finanzia / Copiloto IA", la campana y el perfil. */
@Composable
private fun BarraSuperiorInicio() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimensiones.alturaBarraInicio)
            .padding(horizontal = Dimensiones.margenInicio),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(Dimensiones.tamanoLogoApp)
                .background(Brush.linearGradient(Colores.degradadoBotonFrio), RoundedCornerShape(Dimensiones.radioLogoApp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Iconos.asistente, null, tint = Colores.textoSobreColor, modifier = Modifier.size(Dimensiones.tamanoIcono))
        }
        Spacer(Modifier.width(Dimensiones.espacioPequeno))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = stringResource(R.string.nombre_app), style = EstilosTexto.tituloAviso, color = Colores.tinta)
            Text(text = stringResource(R.string.inicio_copiloto), style = EstilosTexto.ayudaFuerte, color = Colores.verdeSeguro)
        }
        // Las notificaciones y el perfil aún no tienen pantalla.
        Box(modifier = Modifier.size(Dimensiones.tamanoBotonVolver), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Iconos.notificaciones,
                contentDescription = stringResource(R.string.accion_notificaciones),
                tint = Colores.tinta,
                modifier = Modifier.size(Dimensiones.tamanoIcono),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = Dimensiones.espacioPuntoEstado, y = -Dimensiones.espacioPuntoEstado)
                    .size(Dimensiones.tamanoPuntoAviso)
                    .background(Colores.azulMarca, CircleShape),
            )
        }
        Spacer(Modifier.width(Dimensiones.espacioPequeno))
        Box(
            modifier = Modifier
                .size(Dimensiones.tamanoBotonIcono)
                .background(Colores.azulIntenso, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Iconos.persona,
                contentDescription = stringResource(R.string.accion_perfil),
                tint = Colores.textoSobreColor,
                modifier = Modifier.size(Dimensiones.tamanoIconoCompacto),
            )
        }
    }
}

/** "¡Hola, Paula! 👋", el resumen y el estado de salud financiera. */
@Composable
private fun Saludo(resumen: ResumenInicio) {
    val primerNombre = resumen.nombre.trim().substringBefore(' ')
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(R.string.inicio_saludo, primerNombre),
            style = EstilosTexto.titulo,
            color = Colores.tinta,
            modifier = Modifier.weight(1f),
        )
        val porcentaje = CalculosInicio.porcentajeAhorro(resumen)
        if (porcentaje != null) {
            val texto = DecimalFormat(Constantes.Formatos.PATRON_PORCENTAJE).format(abs(porcentaje))
            val saludable = porcentaje >= 0
            Pildora(
                texto = stringResource(if (saludable) R.string.inicio_estado_saludable else R.string.inicio_estado_alerta, texto),
                icono = Iconos.tendencia,
                colorFondo = if (saludable) Colores.mentaClara else Colores.rosaNieveBorde,
                colorContenido = if (saludable) Colores.verdeSeguro else Colores.rosaOscuro,
                rellenoVertical = Dimensiones.rellenoVerticalPildoraMediana,
                mayusculas = false,
            )
        }
    }
    Text(
        text = stringResource(R.string.inicio_resumen),
        style = EstilosTexto.cuerpo,
        color = Colores.textoApagado,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Preview(showSystemUi = true)
@Composable
private fun VistaPreviaInicio() {
    TemaFinanzia {
        ContenidoInicio(
            estado = EstadoInicio(
                resumen = ResumenInicio(
                    nombre = "Paula Gómez",
                    cuenta = "Cuenta principal",
                    ingresos = 2800.0,
                    gastos = 64.5,
                    totalMovimientos = 2,
                    movimientos = listOf(
                        MovimientoReciente(1, "gasto", 64.5, "Comida", "2026-10-13T14:32:00+00:00", "Comida", "Cuenta principal"),
                        MovimientoReciente(2, "ingreso", 2800.0, "Ingreso mensual", "2026-10-12T09:00:00+00:00", null, "Cuenta principal"),
                    ),
                ),
            ),
            alAlternarMontos = {},
            alReintentar = {},
            alAgregar = {},
            alEditarMovimiento = {},
            alAbrirMetas = {},
        )
    }
}
