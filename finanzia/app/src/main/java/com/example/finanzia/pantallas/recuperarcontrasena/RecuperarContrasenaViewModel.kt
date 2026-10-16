package com.example.finanzia.pantallas.recuperarcontrasena

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanzia.componentes.OrigenError
import com.example.finanzia.datos.repositorios.RepositorioRecuperacion
import com.example.finanzia.datos.repositorios.ResultadoRecuperacion
import com.example.finanzia.utilidades.mensajeDeRecuperacion
import com.example.finanzia.utilidades.validarCorreo
import kotlinx.coroutines.launch

/**
 * Paso 1 de recuperar la contraseña: revisa el formulario y le pide a Supabase que mande
 * el enlace al correo. El paso 2 se abre desde ese enlace.
 */
class RecuperarContrasenaViewModel(
    private val repositorio: RepositorioRecuperacion,
) : ViewModel() {

    var estado by mutableStateOf(EstadoRecuperarContrasena())
        private set

    fun alCambiarUsuario(usuario: String) {
        estado = estadoAlEditar().copy(usuario = usuario, errorUsuario = null)
    }

    fun alCambiarCorreo(correo: String) {
        estado = estadoAlEditar().copy(correo = correo, errorCorreo = null)
    }

    /** Revisa el formulario y, si está bien, pide el enlace para crear la contraseña nueva. */
    fun alPulsarVerificar() {
        if (estado.cargando) return
        val errorUsuario = ValidadorRecuperarContrasena.validarUsuario(estado.usuario)
        val errorCorreo = validarCorreo(estado.correo)
        estado = estado.copy(
            errorUsuario = errorUsuario,
            errorCorreo = errorCorreo,
            errorGeneral = ValidadorRecuperarContrasena.validarFormulario(estado.usuario, estado.correo),
            origenError = OrigenError.FORMULARIO,
        )
        if (errorUsuario != null || errorCorreo != null) return

        estado = estado.copy(cargando = true)
        viewModelScope.launch {
            estado = when (val resultado = repositorio.enviarEnlace(estado.correo.trim())) {
                is ResultadoRecuperacion.Exitoso -> estado.copy(cargando = false, enlaceEnviado = true)
                is ResultadoRecuperacion.Fallido -> estado.copy(
                    cargando = false,
                    errorGeneral = mensajeDeRecuperacion(resultado.error),
                    origenError = OrigenError.SERVIDOR,
                )
            }
        }
    }

    /** La pantalla ya avisó que el enlace se envió. */
    fun alAvisarEnlaceEnviado() {
        estado = estado.copy(enlaceEnviado = false)
    }

    /** Al editar se quita el aviso; si lo dio el servidor, se quitan también los errores de los campos. */
    private fun estadoAlEditar(): EstadoRecuperarContrasena = when (estado.origenError) {
        OrigenError.FORMULARIO -> estado.copy(errorGeneral = null)
        OrigenError.SERVIDOR -> estado.copy(
            errorGeneral = null,
            errorUsuario = null,
            errorCorreo = null,
            origenError = OrigenError.FORMULARIO,
        )
    }
}
