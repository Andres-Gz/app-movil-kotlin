package com.example.finanzia.pantallas.iniciosesion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finanzia.R
import com.example.finanzia.componentes.AvisoError
import com.example.finanzia.componentes.BotonDegradado
import com.example.finanzia.componentes.CampoContrasena
import com.example.finanzia.componentes.CampoTexto
import com.example.finanzia.componentes.FondoConOlas
import com.example.finanzia.componentes.LogoMarca
import com.example.finanzia.componentes.OrigenError
import com.example.finanzia.componentes.TextoEnlace
import com.example.finanzia.datos.modelos.Usuario
import com.example.finanzia.datos.repositorios.RepositorioSesionSupabase
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos
import com.example.finanzia.tema.TemaFinanzia

/**
 * Pantalla de inicio de sesión. El estado vive en [InicioSesionViewModel];
 * la navegación se delega a quien la muestra mediante los callbacks.
 * Cuando el inicio de sesión sale bien, avisa con [alIniciarSesion] y el usuario.
 */
@Composable
fun PantallaInicioSesion(
    alIniciarSesion: (Usuario) -> Unit,
    alOlvidarContrasena: () -> Unit,
    alRegistrarse: () -> Unit,
    viewModel: InicioSesionViewModel = viewModel { InicioSesionViewModel(RepositorioSesionSupabase()) },
) {
    val estado = viewModel.estado
    val alIniciarSesionActual by rememberUpdatedState(alIniciarSesion)
    LaunchedEffect(estado.usuario) {
        estado.usuario?.let { usuario ->
            alIniciarSesionActual(usuario)
            viewModel.alAvisarSesionIniciada()
        }
    }

    ContenidoInicioSesion(
        estado = estado,
        alCambiarCorreo = viewModel::alCambiarCorreo,
        alCambiarContrasena = viewModel::alCambiarContrasena,
        alAlternarVisibilidadContrasena = viewModel::alAlternarVisibilidadContrasena,
        alEntrar = viewModel::alPulsarEntrar,
        alOlvidarContrasena = alOlvidarContrasena,
        alRegistrarse = alRegistrarse,
    )
}

/**
 * Contenido sin estado: dibuja [estado] y avisa de cada acción del usuario.
 * El formulario queda centrado entre la barra de estado y el enlace de registro, que va abajo.
 */
@Composable
private fun ContenidoInicioSesion(
    estado: EstadoInicioSesion,
    alCambiarCorreo: (String) -> Unit,
    alCambiarContrasena: (String) -> Unit,
    alAlternarVisibilidadContrasena: () -> Unit,
    alEntrar: () -> Unit,
    alOlvidarContrasena: () -> Unit,
    alRegistrarse: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        FondoConOlas()
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .heightIn(min = maxHeight)
                    .padding(horizontal = Dimensiones.margenPantalla),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Spacer(Modifier.height(Dimensiones.espacioGrande))
                Formulario(
                    estado = estado,
                    alCambiarCorreo = alCambiarCorreo,
                    alCambiarContrasena = alCambiarContrasena,
                    alAlternarVisibilidadContrasena = alAlternarVisibilidadContrasena,
                    alEntrar = alEntrar,
                    alOlvidarContrasena = alOlvidarContrasena,
                )
                SeccionRegistro(alRegistrarse = alRegistrarse)
            }
        }
    }
}

/** Bienvenida, aviso de error, campos, enlace para recuperar la contraseña y botón para entrar. */
@Composable
private fun Formulario(
    estado: EstadoInicioSesion,
    alCambiarCorreo: (String) -> Unit,
    alCambiarContrasena: (String) -> Unit,
    alAlternarVisibilidadContrasena: () -> Unit,
    alEntrar: () -> Unit,
    alOlvidarContrasena: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        SeccionBienvenida()
        Spacer(Modifier.height(Dimensiones.espacioDescripcionFormulario))
        if (estado.errorGeneral != null) {
            AvisoError(mensaje = stringResource(estado.errorGeneral), origen = estado.origenError)
            Spacer(Modifier.height(Dimensiones.espacioMediano))
        }
        CampoTexto(
            etiqueta = stringResource(R.string.inicio_sesion_etiqueta_correo),
            valor = estado.correo,
            alCambiar = alCambiarCorreo,
            ejemplo = stringResource(R.string.inicio_sesion_ejemplo_correo),
            icono = Iconos.correo,
            mensajeError = estado.errorCorreo?.let { stringResource(it) },
            origenError = estado.origenError,
            tipoTeclado = KeyboardType.Email,
        )
        Spacer(Modifier.height(Dimensiones.espacioMediano))
        CampoContrasena(
            etiqueta = stringResource(R.string.inicio_sesion_etiqueta_contrasena),
            valor = estado.contrasena,
            alCambiar = alCambiarContrasena,
            ejemplo = stringResource(R.string.inicio_sesion_ejemplo_contrasena),
            visible = estado.contrasenaVisible,
            alAlternarVisibilidad = alAlternarVisibilidadContrasena,
            mensajeError = estado.errorContrasena?.let { stringResource(it) },
            origenError = estado.origenError,
            alConfirmar = alEntrar,
        )
        Spacer(Modifier.height(Dimensiones.espacioFormularioEnlace))
        TextoEnlace(
            texto = stringResource(R.string.inicio_sesion_olvide_contrasena),
            alPulsar = alOlvidarContrasena,
        )
        Spacer(Modifier.height(Dimensiones.espacioEnlaceBoton))
        BotonDegradado(
            texto = stringResource(R.string.inicio_sesion_boton_entrar),
            alPulsar = alEntrar,
            iconoFinal = Iconos.continuar,
            cargando = estado.cargando,
        )
    }
}

/** Logo, eslogan, saludo y descripción. */
@Composable
private fun SeccionBienvenida() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        LogoMarca()
        Spacer(Modifier.height(Dimensiones.espacioLogoEslogan))
        Text(
            text = stringResource(R.string.eslogan_app),
            style = EstilosTexto.nota,
            color = Colores.verdeMarca,
        )
        Spacer(Modifier.height(Dimensiones.espacioEsloganSaludo))
        Text(
            text = stringResource(R.string.inicio_sesion_saludo),
            style = EstilosTexto.titulo,
            color = Colores.textoPrincipal,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Dimensiones.espacioSaludoDescripcion))
        Text(
            text = stringResource(R.string.inicio_sesion_descripcion),
            style = EstilosTexto.cuerpo,
            color = Colores.textoSecundario,
            textAlign = TextAlign.Center,
        )
    }
}

/** Enlace para crear una cuenta, al pie de la pantalla. */
@Composable
private fun SeccionRegistro(alRegistrarse: () -> Unit) {
    Row(
        modifier = Modifier.padding(top = Dimensiones.espacioGrande, bottom = Dimensiones.espacioPieInferior),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.inicio_sesion_sin_cuenta),
            style = EstilosTexto.cuerpo,
            color = Colores.textoSecundario,
        )
        Spacer(Modifier.width(Dimensiones.espacioMinimo))
        TextoEnlace(
            texto = stringResource(R.string.inicio_sesion_registrarse),
            alPulsar = alRegistrarse,
            estilo = EstilosTexto.cuerpoDestacado,
        )
    }
}

@Composable
private fun VistaPrevia(estado: EstadoInicioSesion) {
    TemaFinanzia {
        ContenidoInicioSesion(
            estado = estado,
            alCambiarCorreo = {},
            alCambiarContrasena = {},
            alAlternarVisibilidadContrasena = {},
            alEntrar = {},
            alOlvidarContrasena = {},
            alRegistrarse = {},
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun VistaPreviaInicioSesion() {
    VistaPrevia(EstadoInicioSesion())
}

@Preview(showSystemUi = true)
@Composable
private fun VistaPreviaCamposVacios() {
    VistaPrevia(
        EstadoInicioSesion(
            errorCorreo = R.string.error_correo_vacio,
            errorContrasena = R.string.error_contrasena_vacia,
            errorGeneral = R.string.error_campos_incompletos,
        ),
    )
}

@Preview(showSystemUi = true)
@Composable
private fun VistaPreviaCredencialesIncorrectas() {
    VistaPrevia(
        EstadoInicioSesion(
            correo = stringResource(R.string.inicio_sesion_ejemplo_correo),
            contrasena = stringResource(R.string.inicio_sesion_ejemplo_contrasena),
            errorContrasena = R.string.error_contrasena_no_coincide,
            errorGeneral = R.string.error_credenciales_incorrectas,
            origenError = OrigenError.SERVIDOR,
        ),
    )
}
