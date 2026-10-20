package com.example.finanzia.pantallas.registro

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
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finanzia.R
import com.example.finanzia.componentes.AvisoError
import com.example.finanzia.componentes.BotonDegradado
import com.example.finanzia.componentes.BotonIcono
import com.example.finanzia.componentes.BotonVerContrasena
import com.example.finanzia.componentes.CampoConEtiqueta
import com.example.finanzia.componentes.CasillaVerificacion
import com.example.finanzia.componentes.FondoConBrillos
import com.example.finanzia.componentes.IndicadorRequisito
import com.example.finanzia.componentes.LogoMarcaCompacto
import com.example.finanzia.componentes.TextoEnlace
import com.example.finanzia.configuracion.Constantes
import com.example.finanzia.datos.repositorios.RepositorioRegistroSupabase
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos
import com.example.finanzia.tema.TemaFinanzia
import com.example.finanzia.utilidades.RequisitosContrasena

/**
 * Pantalla para crear una cuenta. El estado vive en [RegistroViewModel];
 * la navegación se delega a quien la muestra mediante los callbacks.
 * Cuando la cuenta se crea, avisa con [alCrearCuenta] y el mensaje para el usuario.
 */
@Composable
fun PantallaRegistro(
    alVolver: () -> Unit,
    alCrearCuenta: (mensaje: String) -> Unit,
    alIniciarSesion: () -> Unit,
    alVerTerminos: () -> Unit,
    alVerPrivacidad: () -> Unit,
    viewModel: RegistroViewModel = viewModel { RegistroViewModel(RepositorioRegistroSupabase()) },
) {
    val estado = viewModel.estado
    val mensajeCuentaCreada = estado.mensajeCuentaCreada?.let { stringResource(it) }
    val alCrearCuentaActual by rememberUpdatedState(alCrearCuenta)
    LaunchedEffect(mensajeCuentaCreada) {
        if (mensajeCuentaCreada != null) {
            alCrearCuentaActual(mensajeCuentaCreada)
            viewModel.alAvisarCuentaCreada()
        }
    }

    ContenidoRegistro(
        estado = estado,
        alCambiarNombre = viewModel::alCambiarNombre,
        alCambiarCorreo = viewModel::alCambiarCorreo,
        alCambiarContrasena = viewModel::alCambiarContrasena,
        alAlternarVisibilidadContrasena = viewModel::alAlternarVisibilidadContrasena,
        alCambiarTerminos = viewModel::alCambiarTerminos,
        alPulsarCrearCuenta = viewModel::alPulsarCrearCuenta,
        alVolver = alVolver,
        alIniciarSesion = alIniciarSesion,
        alVerTerminos = alVerTerminos,
        alVerPrivacidad = alVerPrivacidad,
    )
}

/**
 * Contenido sin estado: dibuja [estado] y avisa de cada acción del usuario.
 * El encabezado va arriba, el enlace para iniciar sesión abajo y el formulario centrado entre los dos.
 */
@Composable
private fun ContenidoRegistro(
    estado: EstadoRegistro,
    alCambiarNombre: (String) -> Unit,
    alCambiarCorreo: (String) -> Unit,
    alCambiarContrasena: (String) -> Unit,
    alAlternarVisibilidadContrasena: () -> Unit,
    alCambiarTerminos: (Boolean) -> Unit,
    alPulsarCrearCuenta: () -> Unit,
    alVolver: () -> Unit,
    alIniciarSesion: () -> Unit,
    alVerTerminos: () -> Unit,
    alVerPrivacidad: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        FondoConBrillos()
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .heightIn(min = maxHeight),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Encabezado(alVolver = alVolver)
                Formulario(
                    estado = estado,
                    alCambiarNombre = alCambiarNombre,
                    alCambiarCorreo = alCambiarCorreo,
                    alCambiarContrasena = alCambiarContrasena,
                    alAlternarVisibilidadContrasena = alAlternarVisibilidadContrasena,
                    alCambiarTerminos = alCambiarTerminos,
                    alPulsarCrearCuenta = alPulsarCrearCuenta,
                    alVerTerminos = alVerTerminos,
                    alVerPrivacidad = alVerPrivacidad,
                )
                SeccionInicioSesion(alIniciarSesion = alIniciarSesion)
            }
        }
    }
}

/** Botón para volver, indicador de paso y bienvenida (logo, eslogan, título y descripción). */
@Composable
private fun Encabezado(alVolver: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = Dimensiones.rellenoSuperiorEncabezado,
                    start = Dimensiones.rellenoInicioEncabezado,
                    end = Dimensiones.rellenoFinEncabezado,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BotonIcono(
                icono = Iconos.volver,
                descripcion = stringResource(R.string.accion_volver),
                alPulsar = alVolver,
                color = Colores.textoFuerte,
                tamano = Dimensiones.tamanoBotonVolver,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = stringResource(R.string.registro_paso),
                style = EstilosTexto.ayudaDestacada,
                color = Colores.textoTenue,
            )
        }
        Spacer(Modifier.height(Dimensiones.espacioEncabezadoLogo))
        LogoMarcaCompacto()
        Text(
            text = stringResource(R.string.eslogan_app).uppercase(),
            style = EstilosTexto.notaFuerte,
            color = Colores.verdeOscuro,
        )
        Spacer(Modifier.height(Dimensiones.espacioEsloganTitulo))
        Text(
            text = stringResource(R.string.registro_titulo),
            style = EstilosTexto.titulo,
            color = Colores.azulProfundo,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Dimensiones.espacioTituloDescripcion))
        Text(
            text = stringResource(R.string.registro_descripcion),
            style = EstilosTexto.cuerpo,
            color = Colores.textoSecundario,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Dimensiones.margenPantalla),
        )
    }
}

/** Aviso de error, campos, requisitos de la contraseña, términos y botón para crear la cuenta. */
@Composable
private fun Formulario(
    estado: EstadoRegistro,
    alCambiarNombre: (String) -> Unit,
    alCambiarCorreo: (String) -> Unit,
    alCambiarContrasena: (String) -> Unit,
    alAlternarVisibilidadContrasena: () -> Unit,
    alCambiarTerminos: (Boolean) -> Unit,
    alPulsarCrearCuenta: () -> Unit,
    alVerTerminos: () -> Unit,
    alVerPrivacidad: () -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = Dimensiones.margenPantalla)) {
        if (estado.errorGeneral != null) {
            AvisoError(mensaje = stringResource(estado.errorGeneral), origen = estado.origenError)
            Spacer(Modifier.height(Dimensiones.espacioMediano))
        }
        CampoConEtiqueta(
            etiqueta = stringResource(R.string.registro_etiqueta_nombre),
            valor = estado.nombre,
            alCambiar = alCambiarNombre,
            ejemplo = stringResource(R.string.registro_ejemplo_nombre),
            icono = Iconos.usuario,
            mensajeError = estado.errorNombre?.let { stringResource(it) },
            capitalizacion = KeyboardCapitalization.Words,
        )
        Spacer(Modifier.height(Dimensiones.espacioGrande))
        CampoConEtiqueta(
            etiqueta = stringResource(R.string.registro_etiqueta_correo),
            valor = estado.correo,
            alCambiar = alCambiarCorreo,
            ejemplo = stringResource(R.string.registro_ejemplo_correo),
            icono = Iconos.correo,
            mensajeError = estado.errorCorreo?.let { stringResource(it) },
            tipoTeclado = KeyboardType.Email,
        )
        Spacer(Modifier.height(Dimensiones.espacioGrande))
        CampoConEtiqueta(
            etiqueta = stringResource(R.string.registro_etiqueta_contrasena),
            valor = estado.contrasena,
            alCambiar = alCambiarContrasena,
            ejemplo = stringResource(R.string.registro_ejemplo_contrasena),
            icono = Iconos.candado,
            mensajeError = estado.errorContrasena?.let { stringResource(it) },
            tipoTeclado = KeyboardType.Password,
            accionTeclado = ImeAction.Done,
            transformacionVisual = if (estado.contrasenaVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            botonFinal = {
                BotonVerContrasena(
                    visible = estado.contrasenaVisible,
                    alAlternar = alAlternarVisibilidadContrasena,
                    tamanoIcono = Dimensiones.tamanoIconoCompacto,
                )
            },
        )
        Spacer(Modifier.height(Dimensiones.espacioPequeno))
        ListaRequisitos(requisitos = estado.requisitos)
        Spacer(Modifier.height(Dimensiones.espacioRequisitosTerminos))
        AceptarTerminos(
            acepta = estado.aceptaTerminos,
            conError = estado.errorTerminos,
            alCambiar = alCambiarTerminos,
            alVerTerminos = alVerTerminos,
            alVerPrivacidad = alVerPrivacidad,
        )
        Spacer(Modifier.height(Dimensiones.espacioTerminosBoton))
        BotonDegradado(
            texto = stringResource(R.string.registro_boton_crear),
            alPulsar = alPulsarCrearCuenta,
            iconoFinal = Iconos.siguiente,
            cargando = estado.cargando,
            tamanoIcono = Dimensiones.tamanoIconoCompacto,
            radio = Dimensiones.radioBotonCompacto,
        )
    }
}

/** Los tres requisitos de la contraseña, repartidos a lo ancho. */
@Composable
private fun ListaRequisitos(requisitos: RequisitosContrasena) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensiones.espacioMinimo),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IndicadorRequisito(stringResource(R.string.registro_requisito_longitud), requisitos.longitud)
        IndicadorRequisito(stringResource(R.string.registro_requisito_numero), requisitos.numero)
        IndicadorRequisito(stringResource(R.string.registro_requisito_mayuscula), requisitos.mayuscula)
    }
}

/** Casilla para aceptar los términos. Toda la fila la marca; los dos enlaces abren cada documento. */
@Composable
private fun AceptarTerminos(
    acepta: Boolean,
    conError: Boolean,
    alCambiar: (Boolean) -> Unit,
    alVerTerminos: () -> Unit,
    alVerPrivacidad: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = acepta, role = Role.Checkbox, onValueChange = alCambiar),
        verticalAlignment = Alignment.Top,
    ) {
        CasillaVerificacion(
            marcada = acepta,
            conError = conError,
            modifier = Modifier.padding(top = Dimensiones.rellenoSuperiorCasilla),
        )
        Spacer(Modifier.width(Dimensiones.espacioCasillaTexto))
        Text(
            text = textoTerminos(alVerTerminos = alVerTerminos, alVerPrivacidad = alVerPrivacidad),
            style = EstilosTexto.ayuda,
            color = Colores.textoSecundario,
        )
    }
}

/** "Acepto los Términos de Servicio y la Política de Privacidad", con los dos enlaces en azul. */
@Composable
private fun textoTerminos(alVerTerminos: () -> Unit, alVerPrivacidad: () -> Unit): AnnotatedString {
    val estiloEnlace = TextLinkStyles(style = EstilosTexto.ayudaFuerte.toSpanStyle().copy(color = Colores.azulMarca))
    val inicio = stringResource(R.string.registro_terminos_inicio)
    val terminos = stringResource(R.string.registro_terminos_servicio)
    val union = stringResource(R.string.registro_terminos_union)
    val privacidad = stringResource(R.string.registro_terminos_privacidad)
    return buildAnnotatedString {
        append(inicio)
        withLink(LinkAnnotation.Clickable(Constantes.Enlaces.TERMINOS, estiloEnlace) { alVerTerminos() }) {
            append(terminos)
        }
        append(union)
        withLink(LinkAnnotation.Clickable(Constantes.Enlaces.PRIVACIDAD, estiloEnlace) { alVerPrivacidad() }) {
            append(privacidad)
        }
    }
}

/** Enlace para volver al inicio de sesión, al pie de la pantalla. */
@Composable
private fun SeccionInicioSesion(alIniciarSesion: () -> Unit) {
    Row(
        modifier = Modifier.padding(top = Dimensiones.espacioGrande, bottom = Dimensiones.espacioPieInferior),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.registro_con_cuenta),
            style = EstilosTexto.cuerpo,
            color = Colores.textoSecundario,
        )
        Spacer(Modifier.width(Dimensiones.espacioMinimo))
        TextoEnlace(
            texto = stringResource(R.string.registro_iniciar_sesion),
            alPulsar = alIniciarSesion,
            estilo = EstilosTexto.cuerpoDestacado,
        )
    }
}

@Composable
private fun VistaPrevia(estado: EstadoRegistro) {
    TemaFinanzia {
        ContenidoRegistro(
            estado = estado,
            alCambiarNombre = {},
            alCambiarCorreo = {},
            alCambiarContrasena = {},
            alAlternarVisibilidadContrasena = {},
            alCambiarTerminos = {},
            alPulsarCrearCuenta = {},
            alVolver = {},
            alIniciarSesion = {},
            alVerTerminos = {},
            alVerPrivacidad = {},
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun VistaPreviaRegistro() {
    VistaPrevia(EstadoRegistro())
}

@Preview(showSystemUi = true)
@Composable
private fun VistaPreviaRegistroConErrores() {
    VistaPrevia(
        EstadoRegistro(
            errorNombre = R.string.error_nombre_vacio,
            errorCorreo = R.string.error_correo_vacio,
            errorContrasena = R.string.error_contrasena_vacia,
            errorTerminos = true,
            errorGeneral = R.string.error_campos_incompletos,
        ),
    )
}
