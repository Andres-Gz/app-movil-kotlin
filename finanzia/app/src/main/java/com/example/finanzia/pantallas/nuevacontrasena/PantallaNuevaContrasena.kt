package com.example.finanzia.pantallas.nuevacontrasena

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finanzia.R
import com.example.finanzia.componentes.AparienciaCampo
import com.example.finanzia.componentes.AvisoErrorDetallado
import com.example.finanzia.componentes.BarraSuperior
import com.example.finanzia.componentes.BotonDegradado
import com.example.finanzia.componentes.BotonSuave
import com.example.finanzia.componentes.BotonTexto
import com.example.finanzia.componentes.BotonVerContrasena
import com.example.finanzia.componentes.CampoConEtiqueta
import com.example.finanzia.componentes.Pildora
import com.example.finanzia.componentes.RequisitoMarcado
import com.example.finanzia.datos.repositorios.RepositorioRecuperacionSupabase
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos
import com.example.finanzia.tema.TemaFinanzia
import com.example.finanzia.utilidades.RequisitosContrasena

/**
 * Paso 2 de recuperar la contraseña: escribir la contraseña nueva y confirmarla. Se abre desde
 * el enlace del correo. El estado vive en [NuevaContrasenaViewModel]; la navegación se delega a
 * quien la muestra. Cuando Supabase guarda la contraseña, avisa con [alContrasenaActualizada].
 */
@Composable
fun PantallaNuevaContrasena(
    alVolver: () -> Unit,
    alCancelar: () -> Unit,
    alContrasenaActualizada: () -> Unit,
    viewModel: NuevaContrasenaViewModel = viewModel { NuevaContrasenaViewModel(RepositorioRecuperacionSupabase()) },
) {
    val estado = viewModel.estado
    val alContrasenaActualizadaActual by rememberUpdatedState(alContrasenaActualizada)
    LaunchedEffect(estado.contrasenaActualizada) {
        if (estado.contrasenaActualizada) {
            alContrasenaActualizadaActual()
            viewModel.alAvisarContrasenaActualizada()
        }
    }

    ContenidoNuevaContrasena(
        estado = estado,
        alCambiarContrasena = viewModel::alCambiarContrasena,
        alCambiarConfirmacion = viewModel::alCambiarConfirmacion,
        alAlternarContrasena = viewModel::alAlternarVisibilidadContrasena,
        alAlternarConfirmacion = viewModel::alAlternarVisibilidadConfirmacion,
        alAlternarAmbas = viewModel::alAlternarAmbas,
        alActualizar = viewModel::alPulsarActualizar,
        alVolver = alVolver,
        alCancelar = alCancelar,
    )
}

/** Contenido sin estado: la barra queda fija arriba y el resto se desplaza por debajo. */
@Composable
private fun ContenidoNuevaContrasena(
    estado: EstadoNuevaContrasena,
    alCambiarContrasena: (String) -> Unit,
    alCambiarConfirmacion: (String) -> Unit,
    alAlternarContrasena: () -> Unit,
    alAlternarConfirmacion: () -> Unit,
    alAlternarAmbas: () -> Unit,
    alActualizar: () -> Unit,
    alVolver: () -> Unit,
    alCancelar: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colores.fondoLavanda),
    ) {
        BarraSuperior(descripcionVolver = stringResource(R.string.accion_volver), alVolver = alVolver)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = Dimensiones.margenPantallaAmplio),
        ) {
            Spacer(Modifier.height(Dimensiones.espacioBarraEstado))
            FilaEstado()
            Spacer(Modifier.height(Dimensiones.espacioEstadoAviso))
            if (estado.avisoTitulo != null && estado.avisoMensaje != null) {
                AvisoErrorDetallado(
                    titulo = stringResource(estado.avisoTitulo),
                    mensaje = stringResource(estado.avisoMensaje),
                )
                Spacer(Modifier.height(Dimensiones.espacioAvisoTarjeta))
            }
            TarjetaPaso()
            Spacer(Modifier.height(Dimensiones.espacioTarjetaCampos))
            CampoConEtiqueta(
                etiqueta = stringResource(R.string.nueva_etiqueta_contrasena),
                valor = estado.contrasena,
                alCambiar = alCambiarContrasena,
                ejemplo = stringResource(R.string.nueva_ejemplo_contrasena),
                icono = Iconos.candadoMaterial,
                apariencia = AparienciaCampo.ELEVADO,
                mensajeError = estado.errorContrasena?.let { stringResource(it) },
                tipoTeclado = KeyboardType.Password,
                transformacionVisual = transformacion(estado.contrasenaVisible),
                estiloValor = if (estado.contrasenaVisible) EstilosTexto.valorCampo else EstilosTexto.contrasenaOculta,
                extraEtiqueta = if (estado.requisitos.cumpleTodos) {
                    { CumpleRequisitos() }
                } else {
                    null
                },
                botonFinal = {
                    OjoCampo(visible = estado.contrasenaVisible, conError = false, alAlternar = alAlternarContrasena)
                },
            )
            Spacer(Modifier.height(Dimensiones.espacioMediano))
            ListaRequisitos(requisitos = estado.requisitos)
            Spacer(Modifier.height(Dimensiones.espacioRequisitosConfirmacion))
            CampoConEtiqueta(
                etiqueta = stringResource(R.string.nueva_etiqueta_confirmacion),
                valor = estado.confirmacion,
                alCambiar = alCambiarConfirmacion,
                ejemplo = stringResource(R.string.nueva_ejemplo_confirmacion),
                icono = Iconos.restablecerClave,
                apariencia = AparienciaCampo.ELEVADO,
                mensajeError = estado.errorConfirmacion?.let { stringResource(it) },
                tipoTeclado = KeyboardType.Password,
                accionTeclado = ImeAction.Done,
                alConfirmar = alActualizar,
                transformacionVisual = transformacion(estado.confirmacionVisible),
                estiloValor = if (estado.confirmacionVisible) EstilosTexto.valorCampo else EstilosTexto.contrasenaOculta,
                extraEtiqueta = if (estado.noCoinciden) {
                    { NoCoincide() }
                } else {
                    null
                },
                botonFinal = {
                    OjoCampo(
                        visible = estado.confirmacionVisible,
                        conError = estado.errorConfirmacion != null,
                        alAlternar = alAlternarConfirmacion,
                    )
                },
            )
            Spacer(Modifier.height(Dimensiones.espacioMensajeMostrar))
            BotonSuave(
                texto = stringResource(if (estado.ambasVisibles) R.string.nueva_ocultar_ambas else R.string.nueva_mostrar_ambas),
                icono = if (estado.ambasVisibles) Iconos.ocultarMaterial else Iconos.mostrarMaterial,
                alPulsar = alAlternarAmbas,
                modifier = Modifier.align(Alignment.End),
            )
            Spacer(Modifier.height(Dimensiones.espacioMostrarActualizar))
            BotonDegradado(
                texto = stringResource(R.string.nueva_boton_actualizar),
                alPulsar = alActualizar,
                iconoFinal = Iconos.adelante,
                cargando = estado.cargando,
                tamanoIcono = Dimensiones.tamanoIconoCompacto,
                radio = Dimensiones.radioBotonCompacto,
                altura = Dimensiones.alturaBotonAlto,
                colorSombra = Colores.azulIntenso,
                colores = Colores.degradadoBotonFrio,
            )
            Spacer(Modifier.height(Dimensiones.espacioPequeno))
            BotonTexto(texto = stringResource(R.string.nueva_boton_cancelar), alPulsar = alCancelar)
            Spacer(Modifier.height(Dimensiones.espacioCancelarPie))
            PieCifrado()
            Spacer(Modifier.height(Dimensiones.espacioPieInferiorAmplio))
        }
    }
}

private fun transformacion(visible: Boolean): VisualTransformation =
    if (visible) VisualTransformation.None else PasswordVisualTransformation()

/** Punto verde con "Seguridad de la cuenta" a la izquierda y el paso actual a la derecha. */
@Composable
private fun FilaEstado() {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(Dimensiones.tamanoPuntoEstado)
                .background(Colores.verdeSeguro, CircleShape),
        )
        Spacer(Modifier.width(Dimensiones.espacioPuntoEstado))
        Text(
            text = stringResource(R.string.nueva_seguridad_cuenta),
            style = EstilosTexto.nota,
            color = Colores.textoApagado,
        )
        Spacer(Modifier.weight(1f))
        Pildora(
            texto = stringResource(R.string.nueva_paso),
            colorFondo = Colores.azulPildora,
            colorContenido = Colores.azulIntenso,
            rellenoVertical = Dimensiones.rellenoVerticalPildoraMediana,
        )
    }
}

/**
 * Presentación del paso: píldora, título y explicación. En el diseño, a la izquierda de los textos
 * queda un espacio vacío (parece una ilustración que no cargó); se respeta tal cual.
 */
@Composable
private fun TarjetaPaso() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Colores.rellenoCampo, RoundedCornerShape(Dimensiones.radioAvisoGrande))
            .padding(
                horizontal = Dimensiones.rellenoHorizontalTarjetaPaso,
                vertical = Dimensiones.rellenoVerticalTarjetaPaso,
            ),
    ) {
        Spacer(Modifier.width(Dimensiones.anchoEspacioIlustracion))
        Column {
            Pildora(
                texto = stringResource(R.string.nueva_paso_final),
                icono = Iconos.restablecerClave,
                colorFondo = Colores.verdeMenta,
                colorContenido = Colores.verdeSobreMenta,
                rellenoHorizontal = Dimensiones.rellenoHorizontalPildoraPequena,
                rellenoVertical = Dimensiones.rellenoVerticalPildoraPequena,
                tamanoIcono = Dimensiones.tamanoIconoPequeno,
            )
            Spacer(Modifier.height(Dimensiones.rellenoVerticalPildoraPequena))
            Text(text = stringResource(R.string.nueva_titulo), style = EstilosTexto.titulo, color = Colores.tinta)
            Text(text = stringResource(R.string.nueva_descripcion), style = EstilosTexto.cuerpo, color = Colores.textoApagado)
        }
    }
}

/** "Cumple requisitos" en verde, al lado de la etiqueta de la contraseña nueva. */
@Composable
private fun CumpleRequisitos() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Iconos.verificado,
            contentDescription = null,
            tint = Colores.verdeSeguro,
            modifier = Modifier.size(Dimensiones.tamanoIconoPequeno),
        )
        Spacer(Modifier.width(Dimensiones.espacioMinimo))
        Text(
            text = stringResource(R.string.nueva_cumple_requisitos),
            style = EstilosTexto.ayudaDestacada,
            color = Colores.verdeSeguro,
        )
    }
}

/** "No coincide" en rojo, al lado de la etiqueta de la confirmación. */
@Composable
private fun NoCoincide() {
    Text(
        text = stringResource(R.string.nueva_no_coincide),
        style = EstilosTexto.ayudaDestacada,
        color = Colores.errorIntenso,
    )
}

/** El ojo de cada campo, con los iconos de Material de este diseño; rojo si el campo tiene error. */
@Composable
private fun OjoCampo(visible: Boolean, conError: Boolean, alAlternar: () -> Unit) {
    BotonVerContrasena(
        visible = visible,
        alAlternar = alAlternar,
        color = if (conError) Colores.errorIntenso else Colores.textoApagado,
        iconoMostrar = Iconos.mostrarMaterial,
        iconoOcultar = Iconos.ocultarMaterial,
    )
}

/** Los cuatro requisitos en dos columnas, sobre un recuadro azul muy claro. */
@Composable
private fun ListaRequisitos(requisitos: RequisitosContrasena) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Colores.rellenoCampo, RoundedCornerShape(Dimensiones.radioCampo))
            .padding(Dimensiones.rellenoRequisitos),
    ) {
        Row {
            RequisitoMarcado(stringResource(R.string.nueva_requisito_longitud), requisitos.longitud, Modifier.weight(1f))
            RequisitoMarcado(stringResource(R.string.nueva_requisito_numero), requisitos.numero, Modifier.weight(1f))
        }
        Spacer(Modifier.height(Dimensiones.espacioFilasRequisitos))
        Row {
            RequisitoMarcado(stringResource(R.string.nueva_requisito_mayuscula), requisitos.mayuscula, Modifier.weight(1f))
            RequisitoMarcado(stringResource(R.string.nueva_requisito_simbolo), requisitos.simbolo, Modifier.weight(1f))
        }
    }
}

/** Nota de cifrado al pie, con un escudo. */
@Composable
private fun PieCifrado() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Iconos.escudoVerificado,
            contentDescription = null,
            tint = Colores.grisContorno,
            modifier = Modifier.size(Dimensiones.tamanoIconoMediano),
        )
        Spacer(Modifier.width(Dimensiones.espacioIconoPie))
        Text(
            text = stringResource(R.string.nueva_cifrado).uppercase(),
            style = EstilosTexto.ayudaDestacada,
            color = Colores.grisContorno,
        )
    }
}

@Composable
private fun VistaPrevia(estado: EstadoNuevaContrasena) {
    TemaFinanzia {
        ContenidoNuevaContrasena(
            estado = estado,
            alCambiarContrasena = {},
            alCambiarConfirmacion = {},
            alAlternarContrasena = {},
            alAlternarConfirmacion = {},
            alAlternarAmbas = {},
            alActualizar = {},
            alVolver = {},
            alCancelar = {},
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun VistaPreviaNuevaContrasena() {
    VistaPrevia(EstadoNuevaContrasena())
}

@Preview(showSystemUi = true)
@Composable
private fun VistaPreviaContrasenasDistintas() {
    VistaPrevia(
        EstadoNuevaContrasena(
            contrasena = "Finanzia2024!",
            confirmacion = "Finanzia2024?",
            errorConfirmacion = R.string.error_contrasenas_distintas,
            avisoTitulo = R.string.error_contrasenas_no_coinciden,
            avisoMensaje = R.string.error_contrasenas_no_coinciden_detalle,
        ),
    )
}
