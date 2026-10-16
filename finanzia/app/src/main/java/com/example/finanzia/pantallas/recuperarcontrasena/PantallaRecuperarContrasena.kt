package com.example.finanzia.pantallas.recuperarcontrasena

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finanzia.R
import com.example.finanzia.componentes.AparienciaCampo
import com.example.finanzia.componentes.AvisoAyuda
import com.example.finanzia.componentes.AvisoError
import com.example.finanzia.componentes.BotonContorno
import com.example.finanzia.componentes.BotonDegradado
import com.example.finanzia.componentes.BotonIcono
import com.example.finanzia.componentes.CampoConEtiqueta
import com.example.finanzia.componentes.LogoMarcaConPunto
import com.example.finanzia.componentes.Pildora
import com.example.finanzia.componentes.Tarjeta
import com.example.finanzia.datos.repositorios.RepositorioRecuperacionSupabase
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos
import com.example.finanzia.tema.TemaFinanzia

/**
 * Paso 1 de recuperar la contraseña: el usuario escribe su nombre de usuario y su correo, y
 * Supabase le manda un enlace. El estado vive en [RecuperarContrasenaViewModel]; la navegación
 * se delega a quien la muestra. Cuando el enlace sale, avisa con [alEnlaceEnviado] y el correo.
 */
@Composable
fun PantallaRecuperarContrasena(
    alVolver: () -> Unit,
    alEnlaceEnviado: (correo: String) -> Unit,
    alContactarSoporte: () -> Unit,
    viewModel: RecuperarContrasenaViewModel = viewModel {
        RecuperarContrasenaViewModel(RepositorioRecuperacionSupabase())
    },
) {
    val estado = viewModel.estado
    val alEnlaceEnviadoActual by rememberUpdatedState(alEnlaceEnviado)
    LaunchedEffect(estado.enlaceEnviado) {
        if (estado.enlaceEnviado) {
            alEnlaceEnviadoActual(estado.correo.trim())
            viewModel.alAvisarEnlaceEnviado()
        }
    }

    ContenidoRecuperarContrasena(
        estado = estado,
        alCambiarUsuario = viewModel::alCambiarUsuario,
        alCambiarCorreo = viewModel::alCambiarCorreo,
        alVerificar = viewModel::alPulsarVerificar,
        alVolver = alVolver,
        alContactarSoporte = alContactarSoporte,
    )
}

/** Contenido sin estado: dibuja [estado] y avisa de cada acción del usuario. Todo va de arriba hacia abajo. */
@Composable
private fun ContenidoRecuperarContrasena(
    estado: EstadoRecuperarContrasena,
    alCambiarUsuario: (String) -> Unit,
    alCambiarCorreo: (String) -> Unit,
    alVerificar: () -> Unit,
    alVolver: () -> Unit,
    alContactarSoporte: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colores.fondoLavanda)
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimensiones.margenPantallaAmplio),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Encabezado(alVolver = alVolver)
        Spacer(Modifier.height(Dimensiones.espacioEncabezadoLogoAmplio))
        LogoMarcaConPunto()
        Text(
            text = stringResource(R.string.eslogan_app).uppercase(),
            style = EstilosTexto.notaFuerte,
            color = Colores.textoApagado,
        )
        Spacer(Modifier.height(Dimensiones.espacioEsloganTarjeta))
        TarjetaFormulario(
            estado = estado,
            alCambiarUsuario = alCambiarUsuario,
            alCambiarCorreo = alCambiarCorreo,
            alVerificar = alVerificar,
            alContactarSoporte = alContactarSoporte,
        )
        Spacer(Modifier.height(Dimensiones.espacioTarjetaBoton))
        BotonDegradado(
            texto = stringResource(R.string.recuperar_boton_verificar),
            alPulsar = alVerificar,
            iconoFinal = Iconos.adelante,
            tamanoIcono = Dimensiones.tamanoIconoCompacto,
            radio = Dimensiones.radioBotonCompacto,
            cargando = estado.cargando,
            altura = Dimensiones.alturaBotonAlto,
            colorSombra = Colores.azulMarca,
        )
        Spacer(Modifier.height(Dimensiones.espacioEntreBotones))
        BotonContorno(
            texto = stringResource(R.string.recuperar_boton_volver),
            alPulsar = alVolver,
            iconoInicial = Iconos.atras,
        )
        Spacer(Modifier.height(Dimensiones.espacioBotonesPie))
        NotaSeguridad()
        Spacer(Modifier.height(Dimensiones.espacioPieInferiorAmplio))
    }
}

/** Botón para volver y el paso actual. */
@Composable
private fun Encabezado(alVolver: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Dimensiones.rellenoSuperiorEncabezadoAmplio),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BotonIcono(
            icono = Iconos.atras,
            descripcion = stringResource(R.string.accion_volver),
            alPulsar = alVolver,
            color = Colores.tinta,
            tamano = Dimensiones.tamanoBotonVolverConFondo,
            tamanoIcono = Dimensiones.tamanoIconoGrande,
            fondo = Colores.superficie,
        )
        Spacer(Modifier.weight(1f))
        Pildora(texto = stringResource(R.string.recuperar_paso), icono = Iconos.restablecerClave)
    }
}

/** Título, explicación, los dos campos y el recuadro para contactar a soporte. */
@Composable
private fun TarjetaFormulario(
    estado: EstadoRecuperarContrasena,
    alCambiarUsuario: (String) -> Unit,
    alCambiarCorreo: (String) -> Unit,
    alVerificar: () -> Unit,
    alContactarSoporte: () -> Unit,
) {
    Tarjeta {
        Text(
            text = stringResource(R.string.recuperar_titulo),
            style = EstilosTexto.titulo,
            color = Colores.tinta,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(Dimensiones.espacioTituloDescripcionTarjeta))
        Text(
            text = stringResource(R.string.recuperar_descripcion),
            style = EstilosTexto.cuerpo,
            color = Colores.textoApagado,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(Dimensiones.espacioDescripcionCampos))
        if (estado.errorGeneral != null) {
            AvisoError(mensaje = stringResource(estado.errorGeneral), origen = estado.origenError)
            Spacer(Modifier.height(Dimensiones.espacioGrande))
        }
        CampoConEtiqueta(
            etiqueta = stringResource(R.string.recuperar_etiqueta_usuario),
            valor = estado.usuario,
            alCambiar = alCambiarUsuario,
            ejemplo = stringResource(R.string.recuperar_ejemplo_usuario),
            icono = Iconos.persona,
            apariencia = AparienciaCampo.RELLENO,
            mensajeError = estado.errorUsuario?.let { stringResource(it) },
            origenError = estado.origenError,
        )
        Spacer(Modifier.height(Dimensiones.espacioEntreCamposRelleno))
        CampoConEtiqueta(
            etiqueta = stringResource(R.string.recuperar_etiqueta_correo),
            valor = estado.correo,
            alCambiar = alCambiarCorreo,
            ejemplo = stringResource(R.string.recuperar_ejemplo_correo),
            icono = Iconos.sobre,
            apariencia = AparienciaCampo.RELLENO,
            mensajeError = estado.errorCorreo?.let { stringResource(it) },
            origenError = estado.origenError,
            tipoTeclado = KeyboardType.Email,
            accionTeclado = ImeAction.Done,
            alConfirmar = alVerificar,
            extraEtiqueta = { SeguroSsl() },
        )
        Spacer(Modifier.height(Dimensiones.espacioCamposAyuda))
        AvisoAyuda(
            pregunta = stringResource(R.string.recuperar_cambiaste_correo),
            enlace = stringResource(R.string.recuperar_contactar_soporte),
            alPulsarEnlace = alContactarSoporte,
        )
    }
}

/** "Seguro SSL" con un escudo, al lado de la etiqueta del correo. */
@Composable
private fun SeguroSsl() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Iconos.escudo,
            contentDescription = null,
            tint = Colores.azulIntenso,
            modifier = Modifier.size(Dimensiones.tamanoIconoPequeno),
        )
        Spacer(Modifier.width(Dimensiones.espacioMinimo))
        Text(
            text = stringResource(R.string.recuperar_seguro_ssl),
            style = EstilosTexto.ayudaDestacada,
            color = Colores.azulIntenso,
        )
    }
}

/** Nota de seguridad al pie, con un escudo verde. */
@Composable
private fun NotaSeguridad() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Iconos.escudoVerificado,
            contentDescription = null,
            tint = Colores.verdeSeguro,
            modifier = Modifier.size(Dimensiones.tamanoIconoMediano),
        )
        Spacer(Modifier.width(Dimensiones.espacioIconoTextoPildora))
        Text(
            text = stringResource(R.string.recuperar_proteccion),
            style = EstilosTexto.ayuda,
            color = Colores.grisContorno,
        )
    }
}

@Composable
private fun VistaPrevia(estado: EstadoRecuperarContrasena) {
    TemaFinanzia {
        ContenidoRecuperarContrasena(
            estado = estado,
            alCambiarUsuario = {},
            alCambiarCorreo = {},
            alVerificar = {},
            alVolver = {},
            alContactarSoporte = {},
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun VistaPreviaRecuperarContrasena() {
    VistaPrevia(EstadoRecuperarContrasena())
}

@Preview(showSystemUi = true)
@Composable
private fun VistaPreviaRecuperarContrasenaConErrores() {
    VistaPrevia(
        EstadoRecuperarContrasena(
            errorUsuario = R.string.error_usuario_vacio,
            errorCorreo = R.string.error_correo_vacio,
            errorGeneral = R.string.error_campos_incompletos,
        ),
    )
}
