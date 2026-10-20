package com.example.finanzia.pantallas.nuevacontrasena

import com.example.finanzia.R
import com.example.finanzia.datos.repositorios.ErrorRecuperacion
import com.example.finanzia.datos.repositorios.RepositorioRecuperacion
import com.example.finanzia.datos.repositorios.ResultadoRecuperacion
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
class NuevaContrasenaViewModelTest {

    private val repositorio = RepositorioRecuperacionFalso()
    private lateinit var viewModel: NuevaContrasenaViewModel

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = NuevaContrasenaViewModel(repositorio)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun contrasenasDistintas_muestranElAvisoYNoLlamanASupabase() {
        viewModel.alCambiarContrasena("Finanzia2024!")
        viewModel.alCambiarConfirmacion("Finanzia2024?")

        viewModel.alPulsarActualizar()

        with(viewModel.estado) {
            assertEquals(R.string.error_contrasenas_no_coinciden, avisoTitulo)
            assertEquals(R.string.error_contrasenas_no_coinciden_detalle, avisoMensaje)
            assertEquals(R.string.error_contrasenas_distintas, errorConfirmacion)
            assertNull(errorContrasena)
            assertTrue(noCoinciden)
            assertFalse(contrasenaActualizada)
        }
        assertNull(repositorio.ultimaContrasena)
    }

    @Test
    fun formularioVacio_avisaQueFaltanDatos() {
        viewModel.alPulsarActualizar()

        assertEquals(R.string.error_faltan_datos, viewModel.estado.avisoTitulo)
        assertEquals(R.string.error_contrasena_vacia, viewModel.estado.errorContrasena)
        assertEquals(R.string.error_confirmacion_vacia, viewModel.estado.errorConfirmacion)
        assertFalse(viewModel.estado.noCoinciden)
    }

    @Test
    fun contrasenaQueCumpleYCoincide_seGuardaYLimpiaElFormulario() {
        viewModel.alCambiarContrasena("Finanzia2024!")
        viewModel.alCambiarConfirmacion("Finanzia2024!")

        viewModel.alPulsarActualizar()

        assertEquals("Finanzia2024!", repositorio.ultimaContrasena)
        assertTrue(viewModel.estado.contrasenaActualizada)
        assertEquals("", viewModel.estado.contrasena)
        assertNull(viewModel.estado.avisoTitulo)
    }

    @Test
    fun siSupabaseNoLaGuarda_muestraElAvisoConElMotivo() {
        repositorio.respuesta = ResultadoRecuperacion.Fallido(ErrorRecuperacion.SIN_SESION)
        viewModel.alCambiarContrasena("Finanzia2024!")
        viewModel.alCambiarConfirmacion("Finanzia2024!")

        viewModel.alPulsarActualizar()

        assertEquals(R.string.error_no_actualizada, viewModel.estado.avisoTitulo)
        assertEquals(R.string.error_enlace_necesario, viewModel.estado.avisoMensaje)
        assertFalse(viewModel.estado.contrasenaActualizada)
        assertFalse(viewModel.estado.cargando)
    }

    @Test
    fun requisitos_siguenLoQueSeEscribe() {
        viewModel.alCambiarContrasena("Finanzia2024!")

        assertTrue(viewModel.estado.requisitos.cumpleTodos)
    }

    @Test
    fun alEditarCualquierCampo_seQuitanTodosLosErrores() {
        viewModel.alCambiarContrasena("Finanzia2024!")
        viewModel.alCambiarConfirmacion("otra")
        viewModel.alPulsarActualizar()

        viewModel.alCambiarContrasena("Finanzia2024?")

        assertNull(viewModel.estado.avisoTitulo)
        assertNull(viewModel.estado.errorConfirmacion)
    }

    @Test
    fun mostrarAmbas_muestraLasDosYLuegoLasOculta() {
        viewModel.alAlternarVisibilidadContrasena()

        viewModel.alAlternarAmbas()
        assertTrue(viewModel.estado.ambasVisibles)

        viewModel.alAlternarAmbas()
        assertFalse(viewModel.estado.contrasenaVisible)
        assertFalse(viewModel.estado.confirmacionVisible)
    }

    /** Repositorio de mentira: responde lo que la prueba le diga, sin internet. */
    private class RepositorioRecuperacionFalso : RepositorioRecuperacion {
        var respuesta: ResultadoRecuperacion = ResultadoRecuperacion.Exitoso
        var ultimaContrasena: String? = null

        override suspend fun enviarEnlace(correo: String): ResultadoRecuperacion = respuesta

        override suspend fun cambiarContrasena(contrasena: String): ResultadoRecuperacion {
            ultimaContrasena = contrasena
            return respuesta
        }
    }
}
