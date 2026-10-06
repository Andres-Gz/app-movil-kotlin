package com.example.finanzia.pantallas.iniciosesion

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import com.example.finanzia.componentes.BarraSuperior
import com.example.finanzia.componentes.BotonDegradado
import com.example.finanzia.componentes.BotonIcono
import com.example.finanzia.componentes.CampoContrasena
import com.example.finanzia.componentes.CampoTexto
import com.example.finanzia.componentes.CasillaVerificacion
import com.example.finanzia.componentes.LogoMarca
import com.example.finanzia.componentes.SeparadorConTexto
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
    alVolver: () -> Unit,
    alIniciarSesion: (Usuario) -> Unit,
    alOlvidarContrasena: () -> Unit,
    alRegistrarse: () -> Unit,
    alCambiarTema: () -> Unit,
    alPedirAyuda: () -> Unit,
    viewModel: InicioSesionViewModel = viewModel { InicioSesionViewModel(RepositorioSesionSupabase()) },
) {
    val estado = viewModel.estado
    val alIniciarSesionActual by rememberUpdatedState(alIniciarSesion)
    LaunchedEffect(estado.usuario) {
        estado.usuario?.let(alIniciarSesionActual)
    }

    ContenidoInicioSesion(
        estado = estado,
        alCambiarCorreo = viewModel::alCambiarCorreo,
        alCambiarContrasena = viewModel::alCambiarContrasena,
        alAlternarVisibilidadContrasena = viewModel::alAlternarVisibilidadContrasena,
        alCambiarRecordarme = viewModel::alCambiarRecordarme,
        alEntrar = viewModel::alPulsarEntrar,
        alVolver = alVolver,
        alOlvidarContrasena = alOlvidarContrasena,
        alRegistrarse = alRegistrarse,
        alCambiarTema = alCambiarTema,
        alPedirAyuda = alPedirAyuda,
    )
}

/** Contenido sin estado: dibuja [estado] y avisa de cada acción del usuario. */
@Composable
private fun ContenidoInicioSesion(
    estado: EstadoInicioSesion,
    alCambiarCorreo: (String) -> Unit,
    alCambiarContrasena: (String) -> Unit,
    alAlternarVisibilidadContrasena: () -> Unit,
    alCambiarRecordarme: (Boolean) -> Unit,
    alEntrar: () -> Unit,
    alVolver: () -> Unit,
    alOlvidarContrasena: () -> Unit,
    alRegistrarse: () -> Unit,
    alCambiarTema: () -> Unit,
    alPedirAyuda: () -> Unit,
) {
    Scaffold(
        containerColor = Colores.fondo,
        topBar = {
            BarraSuperior(
                titulo = stringResource(R.string.inicio_sesion_titulo_barra),
                alVolver = alVolver,
            ) {
                BotonIcono(
                    icono = Iconos.modoOscuro,
                    descripcion = stringResource(R.string.accion_cambiar_tema),
                    alPulsar = alCambiarTema,
                )
                Spacer(Modifier.width(Dimensiones.espacioPequeno))
                BotonIcono(
                    icono = Iconos.ayuda,
                    descripcion = stringResource(R.string.accion_ayuda),
                    alPulsar = alPedirAyuda,
                    color = Colores.azulMarca,
                    conFondo = true,
                )
            }
        },
    ) { margenes ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(margenes)
                .consumeWindowInsets(margenes)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimensiones.margenPantalla),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(Dimensiones.espacioMediano))
            SeccionBienvenida()
            Spacer(Modifier.height(Dimensiones.espacioGrande))

            CampoTexto(
                etiqueta = stringResource(R.string.inicio_sesion_etiqueta_correo),
                valor = estado.correo,
                alCambiar = alCambiarCorreo,
                ejemplo = stringResource(R.string.inicio_sesion_ejemplo_correo),
                icono = Iconos.correo,
                mensajeError = estado.errorCorreo?.let { stringResource(it) },
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
                alConfirmar = alEntrar,
            )
            Spacer(Modifier.height(Dimensiones.espacioMediano))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                CasillaVerificacion(
                    texto = stringResource(R.string.inicio_sesion_recordarme),
                    marcada = estado.recordarme,
                    alCambiar = alCambiarRecordarme,
                )
                Spacer(Modifier.weight(1f))
                TextoEnlace(
                    texto = stringResource(R.string.inicio_sesion_olvide_contrasena),
                    alPulsar = alOlvidarContrasena,
                )
            }
            Spacer(Modifier.height(Dimensiones.espacioMediano))
            if (estado.errorGeneral != null) {
                Text(
                    text = stringResource(estado.errorGeneral),
                    style = EstilosTexto.nota,
                    color = Colores.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(Dimensiones.espacioPequeno))
            }
            BotonDegradado(
                texto = stringResource(R.string.inicio_sesion_boton_entrar),
                alPulsar = alEntrar,
                iconoFinal = Iconos.continuar,
                cargando = estado.cargando,
            )

            Spacer(Modifier.height(Dimensiones.espacioGrande))
            SeparadorConTexto(texto = stringResource(R.string.inicio_sesion_separador))
            Spacer(Modifier.height(Dimensiones.espacioEnorme))
            SeccionRegistro(alRegistrarse = alRegistrarse)
            Spacer(Modifier.height(Dimensiones.espacioGrande))
        }
    }
}

/** Logo, eslogan, saludo y descripción. */
@Composable
private fun SeccionBienvenida() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        LogoMarca()
        Spacer(Modifier.height(Dimensiones.espacioMinimo))
        Text(
            text = stringResource(R.string.inicio_sesion_eslogan),
            style = EstilosTexto.nota,
            color = Colores.textoSecundario,
        )
        Spacer(Modifier.height(Dimensiones.espacioMediano))
        Text(
            text = stringResource(R.string.inicio_sesion_saludo),
            style = EstilosTexto.titulo,
            color = Colores.textoPrincipal,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Dimensiones.espacioPequeno))
        Text(
            text = stringResource(R.string.inicio_sesion_descripcion),
            style = EstilosTexto.cuerpo,
            color = Colores.textoSecundario,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Dimensiones.espacioMediano),
        )
    }
}

/** Enlace para crear cuenta y aviso de seguridad. */
@Composable
private fun SeccionRegistro(alRegistrarse: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
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
        Spacer(Modifier.height(Dimensiones.espacioPequeno))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Iconos.candado,
                contentDescription = null,
                tint = Colores.textoTenue,
                modifier = Modifier.size(Dimensiones.tamanoIconoPequeno),
            )
            Spacer(Modifier.width(Dimensiones.espacioMinimo))
            Text(
                text = stringResource(R.string.inicio_sesion_datos_protegidos),
                style = EstilosTexto.nota,
                color = Colores.textoTenue,
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun VistaPreviaInicioSesion() {
    TemaFinanzia {
        ContenidoInicioSesion(
            estado = EstadoInicioSesion(),
            alCambiarCorreo = {},
            alCambiarContrasena = {},
            alAlternarVisibilidadContrasena = {},
            alCambiarRecordarme = {},
            alEntrar = {},
            alVolver = {},
            alOlvidarContrasena = {},
            alRegistrarse = {},
            alCambiarTema = {},
            alPedirAyuda = {},
        )
    }
}
