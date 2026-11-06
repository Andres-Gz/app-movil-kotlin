package com.example.finanzia.pantallas.asistente

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanzia.R
import com.example.finanzia.configuracion.Constantes
import com.example.finanzia.datos.modelos.EstadoAccion
import com.example.finanzia.datos.modelos.MensajeAsistente
import com.example.finanzia.datos.repositorios.ErrorDatos
import com.example.finanzia.datos.repositorios.Llamada
import com.example.finanzia.datos.repositorios.RepositorioAsistente
import kotlinx.coroutines.launch

/**
 * El chat con Finanzia IA: manda las preguntas, muestra las respuestas, resuelve lo que propone la IA
 * (confirmar o cancelar) y abre, empieza o elimina conversaciones. La memoria la lleva el servidor:
 * basta con mandar el id de la conversación.
 */
class AsistenteViewModel(private val repositorio: RepositorioAsistente) : ViewModel() {

    var estado by mutableStateOf(EstadoAsistente())
        private set

    fun alCambiarTexto(texto: String) {
        estado = estado.copy(texto = texto.take(Constantes.Asistente.LARGO_MAXIMO_PREGUNTA), error = null)
    }

    /** Agrega lo dictado por voz a lo que ya se había escrito. */
    fun alDictar(dictado: String) {
        val texto = listOf(estado.texto.trim(), dictado.trim()).filter { it.isNotEmpty() }.joinToString(" ")
        alCambiarTexto(texto)
    }

    fun alEnviar() {
        if (!estado.puedeEnviar) return
        preguntar(estado.texto.trim())
    }

    /** Las sugerencias se mandan tal cual, sin pasar por el campo de texto. */
    fun alElegirSugerencia(pregunta: String) {
        if (estado.respondiendo || estado.cargandoMensajes) return
        preguntar(pregunta)
    }

    private fun preguntar(pregunta: String) {
        estado = estado.copy(preguntaEnviada = pregunta, texto = "", error = null)
        viewModelScope.launch {
            estado = when (val llamada = repositorio.preguntar(estado.idConversacion, pregunta)) {
                is Llamada.Exitosa -> estado.copy(
                    idConversacion = llamada.valor.idConversacion,
                    mensajes = estado.mensajes + llamada.valor.pregunta + llamada.valor.respuesta,
                    preguntaEnviada = null,
                )
                // La pregunta vuelve al campo para mandarla otra vez sin reescribirla.
                is Llamada.Fallida -> estado.copy(
                    preguntaEnviada = null,
                    texto = pregunta,
                    error = mensajeError(llamada.error, R.string.error_asistente_sin_respuesta),
                )
            }
        }
    }

    fun alConfirmarAccion(mensaje: MensajeAsistente) = resolverAccion(mensaje, confirmar = true)

    fun alCancelarAccion(mensaje: MensajeAsistente) = resolverAccion(mensaje, confirmar = false)

    private fun resolverAccion(mensaje: MensajeAsistente, confirmar: Boolean) {
        if (estado.accionEnCurso != null || mensaje.estadoAccion != EstadoAccion.PENDIENTE) return
        estado = estado.copy(accionEnCurso = mensaje.idMensaje, error = null)
        viewModelScope.launch {
            val llamada = if (confirmar) repositorio.confirmarAccion(mensaje.idMensaje) else repositorio.cancelarAccion(mensaje.idMensaje)
            estado = when (llamada) {
                is Llamada.Exitosa -> estado.copy(
                    accionEnCurso = null,
                    mensajes = estado.mensajes.map { if (it.idMensaje == llamada.valor.idMensaje) llamada.valor else it },
                    aviso = if (confirmar) R.string.asistente_aviso_guardado else null,
                )
                is Llamada.Fallida -> estado.copy(
                    accionEnCurso = null,
                    error = mensajeError(llamada.error, R.string.error_asistente_accion),
                )
            }
        }
    }

    fun alAvisoMostrado() {
        estado = estado.copy(aviso = null)
    }

    fun alAbrirAtajos() {
        estado = estado.copy(atajosAbiertos = true)
    }

    fun alCerrarAtajos() {
        estado = estado.copy(atajosAbiertos = false)
    }

    /** Escribe el comienzo de la frase para que el usuario la complete ("Registra un gasto de …"). */
    fun alElegirAtajo(texto: String) {
        estado = estado.copy(atajosAbiertos = false, texto = texto, error = null)
    }

    fun alAbrirMenu() {
        estado = estado.copy(menuAbierto = true)
    }

    fun alCerrarMenu() {
        estado = estado.copy(menuAbierto = false)
    }

    /** Deja el chat vacío; la conversación nueva se crea al mandar la primera pregunta. */
    fun alNuevaConversacion() {
        if (estado.respondiendo) return
        estado = EstadoAsistente(texto = estado.texto)
    }

    fun alPedirEliminar() {
        estado = estado.copy(menuAbierto = false, confirmandoEliminar = estado.idConversacion != null)
    }

    fun alCerrarEliminar() {
        estado = estado.copy(confirmandoEliminar = false)
    }

    fun alConfirmarEliminar() {
        val id = estado.idConversacion ?: return
        estado = estado.copy(confirmandoEliminar = false)
        viewModelScope.launch {
            estado = when (val llamada = repositorio.eliminarConversacion(id)) {
                is Llamada.Exitosa -> EstadoAsistente(aviso = R.string.asistente_aviso_eliminada)
                is Llamada.Fallida -> estado.copy(error = mensajeError(llamada.error, R.string.error_asistente_eliminar))
            }
        }
    }

    fun alAbrirHistorial() {
        estado = estado.copy(historialAbierto = true, cargandoHistorial = true, errorHistorial = null)
        viewModelScope.launch {
            estado = when (val llamada = repositorio.conversaciones()) {
                is Llamada.Exitosa -> estado.copy(cargandoHistorial = false, conversaciones = llamada.valor)
                is Llamada.Fallida -> estado.copy(
                    cargandoHistorial = false,
                    errorHistorial = mensajeError(llamada.error, R.string.error_asistente_historial),
                )
            }
        }
    }

    fun alCerrarHistorial() {
        estado = estado.copy(historialAbierto = false)
    }

    /** Abre una conversación del historial con todos sus mensajes. */
    fun alAbrirConversacion(idConversacion: Long) {
        if (estado.respondiendo) return
        estado = estado.copy(historialAbierto = false, cargandoMensajes = true, error = null)
        viewModelScope.launch {
            estado = when (val llamada = repositorio.mensajes(idConversacion)) {
                is Llamada.Exitosa -> EstadoAsistente(idConversacion = idConversacion, mensajes = llamada.valor)
                is Llamada.Fallida -> estado.copy(
                    cargandoMensajes = false,
                    error = mensajeError(llamada.error, R.string.error_asistente_historial),
                )
            }
        }
    }

    @StringRes
    private fun mensajeError(error: ErrorDatos, @StringRes desconocido: Int): Int = when (error) {
        ErrorDatos.SIN_SESION -> R.string.error_sesion_vencida
        ErrorDatos.SIN_CONEXION -> R.string.error_sin_conexion
        ErrorDatos.DESCONOCIDO -> desconocido
    }
}
