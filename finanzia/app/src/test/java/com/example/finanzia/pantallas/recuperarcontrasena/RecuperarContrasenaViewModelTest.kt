package com.example.finanzia.pantallas.recuperarcontrasena

import com.example.finanzia.R
import com.example.finanzia.componentes.OrigenError
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
class RecuperarContrasenaViewModelTest {

    private val repositorio = RepositorioRecuperacionFalso()
    private lateinit var viewModel: RecuperarContrasenaViewModel

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = RecuperarContrasenaViewModel(repositorio)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun verificarConFormularioVacio_muestraErroresYNoPideElEnlace() {
        viewModel.alPulsarVerificar()

        with(viewModel.estado) {
            assertEquals(R.string.error_usuario_vacio, errorUsuario)
            assertEquals(R.string.error_correo_vacio, errorCorreo)
            assertEquals(R.string.error_campos_incompletos, errorGeneral)
            assertFalse(enlaceEnviado)
        }
        assertNull(repositorio.ultimoCorreo)
    }

    @Test
    fun correoMalEscrito_marcaSoloElCorreoSinAviso() {
        viewModel.alCambiarUsuario("carlos_mendoza")
        viewModel.alCambiarCorreo("carlos.correo.com")

        viewModel.alPulsarVerificar()

        assertEquals(R.string.error_correo_invalido, viewModel.estado.errorCorreo)
        assertNull(viewModel.estado.errorGeneral)
        assertNull(repositorio.ultimoCorreo)
    }

    @Test
    fun datosBienEscritos_pidenElEnlaceConElCorreoLimpio() {
        viewModel.alCambiarUsuario("carlos_mendoza")
        viewModel.alCambiarCorreo(" carlos@correo.com ")

        viewModel.alPulsarVerificar()

        assertEquals("carlos@correo.com", repositorio.ultimoCorreo)
        assertTrue(viewModel.estado.enlaceEnviado)
        assertFalse(viewModel.estado.cargando)
    }

    @Test
    fun siSupabaseNoMandaElCorreo_muestraElAvisoDelServidor() {
        repositorio.respuesta = ResultadoRecuperacion.Fallido(ErrorRecuperacion.DEMASIADOS_INTENTOS)
        viewModel.alCambiarUsuario("carlos_mendoza")
        viewModel.alCambiarCorreo("carlos@correo.com")

        viewModel.alPulsarVerificar()

        assertEquals(R.string.error_demasiados_intentos, viewModel.estado.errorGeneral)
        assertEquals(OrigenError.SERVIDOR, viewModel.estado.origenError)
        assertFalse(viewModel.estado.enlaceEnviado)
    }

    @Test
    fun alAvisarEnlaceEnviado_seBajaLaBandera() {
        viewModel.alCambiarUsuario("carlos_mendoza")
        viewModel.alCambiarCorreo("carlos@correo.com")
        viewModel.alPulsarVerificar()

        viewModel.alAvisarEnlaceEnviado()

        assertFalse(viewModel.estado.enlaceEnviado)
    }

    @Test
    fun alEditar_seBorranElErrorDelCampoYElAviso() {
        viewModel.alPulsarVerificar()

        viewModel.alCambiarUsuario("c")

        assertNull(viewModel.estado.errorUsuario)
        assertNull(viewModel.estado.errorGeneral)
        assertEquals(R.string.error_correo_vacio, viewModel.estado.errorCorreo)
    }

    /** Repositorio de mentira: responde lo que la prueba le diga, sin internet. */
    private class RepositorioRecuperacionFalso : RepositorioRecuperacion {
        var respuesta: ResultadoRecuperacion = ResultadoRecuperacion.Exitoso
        var ultimoCorreo: String? = null

        override suspend fun enviarEnlace(correo: String): ResultadoRecuperacion {
            ultimoCorreo = correo
            return respuesta
        }

        override suspend fun cambiarContrasena(contrasena: String): ResultadoRecuperacion = respuesta
    }
}
