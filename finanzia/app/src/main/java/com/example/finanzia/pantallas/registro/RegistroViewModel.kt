package com.example.finanzia.pantallas.registro

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanzia.R
import com.example.finanzia.componentes.OrigenError
import com.example.finanzia.datos.repositorios.ErrorRegistro
import com.example.finanzia.datos.repositorios.RepositorioRegistro
import com.example.finanzia.datos.repositorios.ResultadoRegistro
import com.example.finanzia.utilidades.validarCorreo
import kotlinx.coroutines.launch

/** Mantiene el estado del formulario de registro y reacciona a lo que hace el usuario. */
class RegistroViewModel(
    private val repositorio: RepositorioRegistro,
) : ViewModel() {

    var estado by mutableStateOf(EstadoRegistro())
        private set

    fun alCambiarNombre(nombre: String) {
        estado = estadoAlEditar().copy(nombre = nombre, errorNombre = null)
    }

    fun alCambiarCorreo(correo: String) {
        estado = estadoAlEditar().copy(correo = correo, errorCorreo = null)
    }

    fun alCambiarContrasena(contrasena: String) {
        estado = estadoAlEditar().copy(contrasena = contrasena, errorContrasena = null)
    }

    fun alAlternarVisibilidadContrasena() {
        estado = estado.copy(contrasenaVisible = !estado.contrasenaVisible)
    }

    fun alCambiarTerminos(acepta: Boolean) {
        estado = estadoAlEditar().copy(aceptaTerminos = acepta, errorTerminos = false)
    }

    /** Valida el formulario y, si está bien, crea la cuenta. */
    fun alPulsarCrearCuenta() {
        if (estado.cargando) return
        val errorNombre = ValidadorRegistro.validarNombre(estado.nombre)
        val errorCorreo = validarCorreo(estado.correo)
        val errorContrasena = ValidadorRegistro.validarContrasena(estado.contrasena)
        estado = estado.copy(
            errorNombre = errorNombre,
            errorCorreo = errorCorreo,
            errorContrasena = errorContrasena,
            errorTerminos = !estado.aceptaTerminos,
            errorGeneral = ValidadorRegistro.validarFormulario(
                estado.nombre, estado.correo, estado.contrasena, estado.aceptaTerminos,
            ),
            origenError = OrigenError.FORMULARIO,
        )
        if (errorNombre != null || errorCorreo != null || errorContrasena != null || !estado.aceptaTerminos) return

        estado = estado.copy(cargando = true)
        viewModelScope.launch {
            val resultado = repositorio.registrarse(estado.nombre.trim(), estado.correo.trim(), estado.contrasena)
            estado = when (resultado) {
                // El formulario queda limpio por si el usuario vuelve a esta pantalla.
                is ResultadoRegistro.Exitoso -> EstadoRegistro(
                    mensajeCuentaCreada = if (resultado.necesitaConfirmacion) {
                        R.string.registro_confirma_correo
                    } else {
                        R.string.registro_cuenta_creada
                    },
                )
                is ResultadoRegistro.Fallido -> estado.copy(
                    cargando = false,
                    errorGeneral = mensajeDe(resultado.error),
                    origenError = OrigenError.SERVIDOR,
                )
            }
        }
    }

    /** La pantalla ya avisó que la cuenta se creó. */
    fun alAvisarCuentaCreada() {
        estado = estado.copy(mensajeCuentaCreada = null)
    }

    /**
     * Al editar se quita el aviso. Si los errores los dio el servidor, ya no aplican
     * a los datos nuevos y se quitan todos.
     */
    private fun estadoAlEditar(): EstadoRegistro = when (estado.origenError) {
        OrigenError.FORMULARIO -> estado.copy(errorGeneral = null)
        OrigenError.SERVIDOR -> EstadoRegistro(
            nombre = estado.nombre,
            correo = estado.correo,
            contrasena = estado.contrasena,
            contrasenaVisible = estado.contrasenaVisible,
            aceptaTerminos = estado.aceptaTerminos,
        )
    }

    @StringRes
    private fun mensajeDe(error: ErrorRegistro): Int = when (error) {
        ErrorRegistro.CORREO_YA_REGISTRADO -> R.string.error_correo_registrado
        ErrorRegistro.CORREO_RECHAZADO -> R.string.error_correo_rechazado
        ErrorRegistro.CONTRASENA_DEBIL -> R.string.error_contrasena_debil
        ErrorRegistro.DEMASIADOS_INTENTOS -> R.string.error_demasiados_registros
        ErrorRegistro.SIN_CONEXION -> R.string.error_sin_conexion
        ErrorRegistro.DESCONOCIDO -> R.string.error_desconocido
    }
}
