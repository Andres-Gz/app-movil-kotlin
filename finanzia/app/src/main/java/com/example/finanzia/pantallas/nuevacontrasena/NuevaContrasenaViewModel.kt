package com.example.finanzia.pantallas.nuevacontrasena

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanzia.R
import com.example.finanzia.datos.repositorios.RepositorioRecuperacion
import com.example.finanzia.datos.repositorios.ResultadoRecuperacion
import com.example.finanzia.utilidades.mensajeDeRecuperacion
import kotlinx.coroutines.launch

/**
 * Paso 2 de recuperar la contraseña: revisa el formulario y guarda la contraseña nueva en Supabase.
 * Funciona con la sesión temporal que deja el enlace del correo al abrir la app.
 */
class NuevaContrasenaViewModel(
    private val repositorio: RepositorioRecuperacion,
) : ViewModel() {

    var estado by mutableStateOf(EstadoNuevaContrasena())
        private set

    fun alCambiarContrasena(contrasena: String) {
        estado = estado.sinErrores().copy(contrasena = contrasena)
    }

    fun alCambiarConfirmacion(confirmacion: String) {
        estado = estado.sinErrores().copy(confirmacion = confirmacion)
    }

    fun alAlternarVisibilidadContrasena() {
        estado = estado.copy(contrasenaVisible = !estado.contrasenaVisible)
    }

    fun alAlternarVisibilidadConfirmacion() {
        estado = estado.copy(confirmacionVisible = !estado.confirmacionVisible)
    }

    /** Si alguna está oculta, muestra las dos; si ya se ven las dos, las oculta. */
    fun alAlternarAmbas() {
        val mostrar = !estado.ambasVisibles
        estado = estado.copy(contrasenaVisible = mostrar, confirmacionVisible = mostrar)
    }

    /** Revisa el formulario y, si está bien, guarda la contraseña nueva. */
    fun alPulsarActualizar() {
        if (estado.cargando) return
        val errorContrasena = ValidadorNuevaContrasena.validarContrasena(estado.contrasena)
        val errorConfirmacion = ValidadorNuevaContrasena.validarConfirmacion(estado.contrasena, estado.confirmacion)
        val aviso = ValidadorNuevaContrasena.aviso(estado.contrasena, estado.confirmacion)
        estado = estado.copy(
            errorContrasena = errorContrasena,
            errorConfirmacion = errorConfirmacion,
            avisoTitulo = aviso?.first,
            avisoMensaje = aviso?.second,
        )
        if (errorContrasena != null || errorConfirmacion != null) return

        estado = estado.copy(cargando = true)
        viewModelScope.launch {
            estado = when (val resultado = repositorio.cambiarContrasena(estado.contrasena)) {
                // El formulario queda limpio por si se vuelve a esta pantalla.
                is ResultadoRecuperacion.Exitoso -> EstadoNuevaContrasena(contrasenaActualizada = true)
                is ResultadoRecuperacion.Fallido -> estado.copy(
                    cargando = false,
                    avisoTitulo = R.string.error_no_actualizada,
                    avisoMensaje = mensajeDeRecuperacion(resultado.error),
                )
            }
        }
    }

    /** La pantalla ya avisó que la contraseña se actualizó. */
    fun alAvisarContrasenaActualizada() {
        estado = estado.copy(contrasenaActualizada = false)
    }

    /** Al editar cualquiera de los dos campos se quitan todos los errores: dependen de ambos. */
    private fun EstadoNuevaContrasena.sinErrores() =
        copy(errorContrasena = null, errorConfirmacion = null, avisoTitulo = null, avisoMensaje = null)
}
