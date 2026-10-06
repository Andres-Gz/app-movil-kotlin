package com.example.finanzia.pantallas.iniciosesion

import com.example.finanzia.R
import com.example.finanzia.datos.modelos.Usuario
import com.example.finanzia.datos.repositorios.ErrorSesion
import com.example.finanzia.datos.repositorios.RepositorioSesion
import com.example.finanzia.datos.repositorios.ResultadoSesion
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
class InicioSesionViewModelTest {

    private val repositorio = RepositorioSesionFalso()
    private lateinit var viewModel: InicioSesionViewModel

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = InicioSesionViewModel(repositorio)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun alPulsarEntrarConFormularioVacio_muestraErroresSinLlamarAlRepositorio() {
        viewModel.alPulsarEntrar()

        assertEquals(R.string.error_correo_vacio, viewModel.estado.errorCorreo)
        assertEquals(R.string.error_contrasena_vacia, viewModel.estado.errorContrasena)
        assertEquals(0, repositorio.llamadas)
    }

    @Test
    fun alPulsarEntrarConDatosValidos_guardaElUsuario() {
        llenarFormulario()

        viewModel.alPulsarEntrar()

        assertEquals(USUARIO_DE_PRUEBA, viewModel.estado.usuario)
        assertFalse(viewModel.estado.cargando)
        assertNull(viewModel.estado.errorGeneral)
    }

    @Test
    fun alPulsarEntrar_enviaElCorreoSinEspacios() {
        viewModel.alCambiarCorreo("  ana@correo.com ")
        viewModel.alCambiarContrasena("secreta123")

        viewModel.alPulsarEntrar()

        assertEquals("ana@correo.com", repositorio.ultimoCorreo)
    }

    @Test
    fun credencialesIncorrectas_muestranElErrorGeneral() {
        repositorio.respuesta = CompletableDeferred(ResultadoSesion.Fallido(ErrorSesion.CREDENCIALES_INCORRECTAS))
        llenarFormulario()

        viewModel.alPulsarEntrar()

        assertEquals(R.string.error_credenciales_incorrectas, viewModel.estado.errorGeneral)
        assertNull(viewModel.estado.usuario)
    }

    @Test
    fun mientrasEsperaLaRespuesta_estaCargandoYNoRepiteLaLlamada() {
        repositorio.respuesta = CompletableDeferred()
        llenarFormulario()

        viewModel.alPulsarEntrar()
        viewModel.alPulsarEntrar()

        assertTrue(viewModel.estado.cargando)
        assertEquals(1, repositorio.llamadas)

        repositorio.respuesta.complete(ResultadoSesion.Exitoso(USUARIO_DE_PRUEBA))

        assertFalse(viewModel.estado.cargando)
    }

    @Test
    fun alCambiarCorreo_borraSoloElErrorDelCorreo() {
        viewModel.alPulsarEntrar()

        viewModel.alCambiarCorreo("a")

        assertNull(viewModel.estado.errorCorreo)
        assertEquals(R.string.error_contrasena_vacia, viewModel.estado.errorContrasena)
    }

    @Test
    fun alEditarDespuesDeUnFallo_seBorraElErrorGeneral() {
        repositorio.respuesta = CompletableDeferred(ResultadoSesion.Fallido(ErrorSesion.SIN_CONEXION))
        llenarFormulario()
        viewModel.alPulsarEntrar()

        viewModel.alCambiarContrasena("otra")

        assertNull(viewModel.estado.errorGeneral)
    }

    @Test
    fun alAlternarVisibilidad_muestraYOcultaLaContrasena() {
        viewModel.alAlternarVisibilidadContrasena()
        assertTrue(viewModel.estado.contrasenaVisible)

        viewModel.alAlternarVisibilidadContrasena()
        assertFalse(viewModel.estado.contrasenaVisible)
    }

    @Test
    fun alCambiarRecordarme_guardaLaPreferencia() {
        viewModel.alCambiarRecordarme(true)

        assertTrue(viewModel.estado.recordarme)
    }

    private fun llenarFormulario() {
        viewModel.alCambiarCorreo("ana@correo.com")
        viewModel.alCambiarContrasena("secreta123")
    }

    /** Repositorio de mentira: responde lo que la prueba le diga, sin internet. */
    private class RepositorioSesionFalso : RepositorioSesion {
        var respuesta = CompletableDeferred<ResultadoSesion>(ResultadoSesion.Exitoso(USUARIO_DE_PRUEBA))
        var llamadas = 0
        var ultimoCorreo: String? = null

        override suspend fun iniciarSesion(correo: String, contrasena: String): ResultadoSesion {
            llamadas++
            ultimoCorreo = correo
            return respuesta.await()
        }
    }

    private companion object {
        val USUARIO_DE_PRUEBA = Usuario(idUsuario = 1, nombre = "Ana", correo = "ana@correo.com")
    }
}
