package com.example.finanzia.pantallas.asistente

import com.example.finanzia.R
import com.example.finanzia.datos.modelos.AccionAsistente
import com.example.finanzia.datos.modelos.ConversacionAsistente
import com.example.finanzia.datos.modelos.EstadoAccion
import com.example.finanzia.datos.modelos.IntercambioAsistente
import com.example.finanzia.datos.modelos.MensajeAsistente
import com.example.finanzia.datos.modelos.RolMensaje
import com.example.finanzia.datos.modelos.TipoAccion
import com.example.finanzia.datos.repositorios.ErrorDatos
import com.example.finanzia.datos.repositorios.Llamada
import com.example.finanzia.datos.repositorios.RepositorioAsistente
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AsistenteViewModelTest {

    private val repositorio = RepositorioAsistenteFalso()
    private lateinit var viewModel: AsistenteViewModel

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = AsistenteViewModel(repositorio)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun alEnviar_guardaLaConversacionYMuestraPreguntaYRespuesta() {
        viewModel.alCambiarTexto("  Recuerda el 70  ")

        viewModel.alEnviar()

        assertEquals(listOf(null to "Recuerda el 70"), repositorio.preguntas)
        assertEquals(7L, viewModel.estado.idConversacion)
        assertEquals(listOf("Recuerda el 70", "# Anotado\nEl **70**."), viewModel.estado.mensajes.map { it.contenido })
        assertEquals("", viewModel.estado.texto)
        assertFalse(viewModel.estado.respondiendo)
    }

    @Test
    fun laSegundaPregunta_vaALaMismaConversacion_paraQueLaIARecuerde() {
        viewModel.alCambiarTexto("Recuerda el 70")
        viewModel.alEnviar()

        viewModel.alCambiarTexto("¿Cuál era el número?")
        viewModel.alEnviar()

        assertEquals(7L, repositorio.preguntas.last().first)
        assertEquals(4, viewModel.estado.mensajes.size)
    }

    @Test
    fun mientrasLaIAResponde_muestraLaPreguntaYNoDejaMandarOtra() {
        val pendiente = CompletableDeferred<Llamada<IntercambioAsistente>>()
        repositorio.respuestaPregunta = { pendiente.await() }
        viewModel.alCambiarTexto("Hola")
        viewModel.alEnviar()

        assertEquals("Hola", viewModel.estado.preguntaEnviada)
        viewModel.alCambiarTexto("Otra")
        viewModel.alEnviar()
        assertEquals(1, repositorio.preguntas.size)

        pendiente.complete(Llamada.Exitosa(intercambio(7, "Hola")))
        assertNull(viewModel.estado.preguntaEnviada)
    }

    @Test
    fun siFalla_devuelveLaPreguntaAlCampoYMuestraElError() {
        repositorio.respuestaPregunta = { Llamada.Fallida(ErrorDatos.DESCONOCIDO) }
        viewModel.alCambiarTexto("¿Cuánto gasté?")

        viewModel.alEnviar()

        assertEquals("¿Cuánto gasté?", viewModel.estado.texto)
        assertEquals(R.string.error_asistente_sin_respuesta, viewModel.estado.error)
        assertTrue(viewModel.estado.mensajes.isEmpty())
    }

    @Test
    fun sinConexion_avisaQueRevisenElInternet() {
        repositorio.respuestaPregunta = { Llamada.Fallida(ErrorDatos.SIN_CONEXION) }

        viewModel.alElegirSugerencia("¿Puedo cenar fuera hoy?")

        assertEquals(R.string.error_sin_conexion, viewModel.estado.error)
    }

    @Test
    fun textoVacio_noSeManda() {
        viewModel.alCambiarTexto("   ")

        viewModel.alEnviar()

        assertTrue(repositorio.preguntas.isEmpty())
    }

    @Test
    fun alConfirmarLaPropuesta_actualizaElMensajeYAvisa() {
        repositorio.respuestaPregunta = { Llamada.Exitosa(intercambio(7, "Gasté 20 mil", ACCION_GASTO)) }
        viewModel.alElegirSugerencia("Gasté 20 mil")
        val respuesta = viewModel.estado.mensajes.last()

        viewModel.alConfirmarAccion(respuesta)

        assertEquals(listOf(respuesta.idMensaje), repositorio.confirmadas)
        assertEquals(EstadoAccion.CONFIRMADA, viewModel.estado.mensajes.last().estadoAccion)
        assertEquals(R.string.asistente_aviso_guardado, viewModel.estado.aviso)
        assertNull(viewModel.estado.accionEnCurso)
    }

    @Test
    fun unaPropuestaYaResuelta_noSeVuelveAMandar() {
        repositorio.respuestaPregunta = { Llamada.Exitosa(intercambio(7, "Gasté 20 mil", ACCION_GASTO)) }
        viewModel.alElegirSugerencia("Gasté 20 mil")
        viewModel.alCancelarAccion(viewModel.estado.mensajes.last())

        viewModel.alConfirmarAccion(viewModel.estado.mensajes.last())

        assertEquals(EstadoAccion.CANCELADA, viewModel.estado.mensajes.last().estadoAccion)
        assertTrue(repositorio.confirmadas.isEmpty())
    }

    @Test
    fun alAbrirUnaConversacionDelHistorial_cargaSusMensajes() {
        viewModel.alAbrirHistorial()
        assertEquals(listOf(CONVERSACION), viewModel.estado.conversaciones)

        viewModel.alAbrirConversacion(3)

        assertEquals(3L, viewModel.estado.idConversacion)
        assertEquals(2, viewModel.estado.mensajes.size)
        assertFalse(viewModel.estado.historialAbierto)
    }

    @Test
    fun nuevaConversacion_dejaElChatVacio() {
        viewModel.alElegirSugerencia("Hola")

        viewModel.alNuevaConversacion()

        assertNull(viewModel.estado.idConversacion)
        assertTrue(viewModel.estado.mensajes.isEmpty())
    }

    @Test
    fun eliminar_borraLaConversacionAbiertaYAvisa() {
        viewModel.alElegirSugerencia("Hola")
        viewModel.alPedirEliminar()
        assertTrue(viewModel.estado.confirmandoEliminar)

        viewModel.alConfirmarEliminar()

        assertEquals(listOf(7L), repositorio.eliminadas)
        assertNull(viewModel.estado.idConversacion)
        assertEquals(R.string.asistente_aviso_eliminada, viewModel.estado.aviso)
    }

    @Test
    fun unaConversacionNueva_noSePuedeEliminar() {
        viewModel.alPedirEliminar()

        assertFalse(viewModel.estado.confirmandoEliminar)
    }

    @Test
    fun alDictar_agregaLoDictadoALoEscrito() {
        viewModel.alCambiarTexto("Registra un gasto de")

        viewModel.alDictar("veinte mil en almuerzo")

        assertEquals("Registra un gasto de veinte mil en almuerzo", viewModel.estado.texto)
    }

    @Test
    fun unAtajo_escribeElComienzoDeLaFrase() {
        viewModel.alAbrirAtajos()

        viewModel.alElegirAtajo("Registra un gasto de ")

        assertEquals("Registra un gasto de ", viewModel.estado.texto)
        assertFalse(viewModel.estado.atajosAbiertos)
    }

    private companion object {
        val ACCION_GASTO = AccionAsistente(TipoAccion.GASTO, 20000.0, categoria = "comida", descripcion = "Almuerzo")
        val CONVERSACION = ConversacionAsistente(3, "Recuerda el 70", "2026-10-27T14:00:00+00:00", "El 70.")

        fun intercambio(id: Long, pregunta: String, accion: AccionAsistente? = null) = IntercambioAsistente(
            idConversacion = id,
            pregunta = MensajeAsistente(id * 10, RolMensaje.USUARIO, pregunta, "2026-10-27T14:00:00+00:00"),
            respuesta = MensajeAsistente(
                id * 10 + 1,
                RolMensaje.ASISTENTE,
                "# Anotado\nEl **70**.",
                "2026-10-27T14:00:05+00:00",
                accion = accion,
                estadoAccion = accion?.let { EstadoAccion.PENDIENTE },
            ),
        )
    }

    /** Un repositorio sin internet: responde lo que diga cada prueba y anota lo que se le pidió. */
    private class RepositorioAsistenteFalso : RepositorioAsistente {
        val preguntas = mutableListOf<Pair<Long?, String>>()
        val confirmadas = mutableListOf<Long>()
        val eliminadas = mutableListOf<Long>()
        var respuestaPregunta: suspend (String) -> Llamada<IntercambioAsistente> = { Llamada.Exitosa(intercambio(7, it)) }

        override suspend fun preguntar(idConversacion: Long?, pregunta: String): Llamada<IntercambioAsistente> {
            preguntas += idConversacion to pregunta
            return respuestaPregunta(pregunta)
        }

        override suspend fun conversaciones(): Llamada<List<ConversacionAsistente>> = Llamada.Exitosa(listOf(CONVERSACION))

        override suspend fun mensajes(idConversacion: Long): Llamada<List<MensajeAsistente>> =
            Llamada.Exitosa(intercambio(idConversacion, "Recuerda el 70").let { listOf(it.pregunta, it.respuesta) })

        override suspend fun confirmarAccion(idMensaje: Long): Llamada<MensajeAsistente> {
            confirmadas += idMensaje
            return Llamada.Exitosa(resuelto(idMensaje, EstadoAccion.CONFIRMADA))
        }

        override suspend fun cancelarAccion(idMensaje: Long): Llamada<MensajeAsistente> =
            Llamada.Exitosa(resuelto(idMensaje, EstadoAccion.CANCELADA))

        override suspend fun eliminarConversacion(idConversacion: Long): Llamada<Unit> {
            eliminadas += idConversacion
            return Llamada.Exitosa(Unit)
        }

        private fun resuelto(idMensaje: Long, estado: EstadoAccion) =
            intercambio(7, "Gasté 20 mil", ACCION_GASTO).respuesta.copy(idMensaje = idMensaje, estadoAccion = estado)
    }
}
