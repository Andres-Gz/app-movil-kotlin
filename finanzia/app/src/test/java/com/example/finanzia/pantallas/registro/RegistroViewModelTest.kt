package com.example.finanzia.pantallas.registro

import com.example.finanzia.R
import com.example.finanzia.componentes.OrigenError
import com.example.finanzia.datos.repositorios.ErrorRegistro
import com.example.finanzia.datos.repositorios.RepositorioRegistro
import com.example.finanzia.datos.repositorios.ResultadoRegistro
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
class RegistroViewModelTest {

    private val repositorio = RepositorioRegistroFalso()
    private lateinit var viewModel: RegistroViewModel

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = RegistroViewModel(repositorio)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun crearCuentaConFormularioVacio_muestraErroresSinLlamarAlRepositorio() {
        viewModel.alPulsarCrearCuenta()

        with(viewModel.estado) {
            assertEquals(R.string.error_nombre_vacio, errorNombre)
            assertEquals(R.string.error_correo_vacio, errorCorreo)
            assertEquals(R.string.error_contrasena_vacia, errorContrasena)
            assertTrue(errorTerminos)
            assertEquals(R.string.error_campos_incompletos, errorGeneral)
            assertEquals(OrigenError.FORMULARIO, origenError)
        }
        assertEquals(0, repositorio.llamadas)
    }

    @Test
    fun crearCuentaSinAceptarTerminos_avisaYNoLlamaAlRepositorio() {
        llenarFormulario(aceptaTerminos = false)

        viewModel.alPulsarCrearCuenta()

        assertEquals(R.string.error_terminos, viewModel.estado.errorGeneral)
        assertTrue(viewModel.estado.errorTerminos)
        assertEquals(0, repositorio.llamadas)
    }

    @Test
    fun contrasenaSinRequisitos_marcaElCampo() {
        llenarFormulario(contrasena = "secreta")

        viewModel.alPulsarCrearCuenta()

        assertEquals(R.string.error_contrasena_requisitos, viewModel.estado.errorContrasena)
        assertEquals(0, repositorio.llamadas)
    }

    @Test
    fun requisitos_siguenLoQueSeEscribe() {
        viewModel.alCambiarContrasena("Secreta1")

        assertTrue(viewModel.estado.requisitos.cumpleBasicos)
    }

    @Test
    fun crearCuentaConDatosValidos_enviaLosDatosLimpiosYLimpiaElFormulario() {
        llenarFormulario(nombre = "  Paula Gómez ", correo = " paula@correo.com ")

        viewModel.alPulsarCrearCuenta()

        assertEquals(Triple("Paula Gómez", "paula@correo.com", "Secreta12"), repositorio.ultimosDatos)
        assertEquals(R.string.registro_cuenta_creada, viewModel.estado.mensajeCuentaCreada)
        assertEquals("", viewModel.estado.nombre)
        assertFalse(viewModel.estado.cargando)
    }

    @Test
    fun siHayQueConfirmarElCorreo_elMensajeLoDice() {
        repositorio.respuesta = CompletableDeferred(ResultadoRegistro.Exitoso(necesitaConfirmacion = true))
        llenarFormulario()

        viewModel.alPulsarCrearCuenta()

        assertEquals(R.string.registro_confirma_correo, viewModel.estado.mensajeCuentaCreada)
    }

    @Test
    fun alAvisarCuentaCreada_seBorraElMensaje() {
        llenarFormulario()
        viewModel.alPulsarCrearCuenta()

        viewModel.alAvisarCuentaCreada()

        assertNull(viewModel.estado.mensajeCuentaCreada)
    }

    @Test
    fun correoYaRegistrado_muestraElAvisoDelServidor() {
        repositorio.respuesta = CompletableDeferred(ResultadoRegistro.Fallido(ErrorRegistro.CORREO_YA_REGISTRADO))
        llenarFormulario()

        viewModel.alPulsarCrearCuenta()

        assertEquals(R.string.error_correo_registrado, viewModel.estado.errorGeneral)
        assertEquals(OrigenError.SERVIDOR, viewModel.estado.origenError)
        assertFalse(viewModel.estado.cargando)
        assertNull(viewModel.estado.mensajeCuentaCreada)
    }

    @Test
    fun siSupabaseLimitaLosCorreos_loDiceEnVezDeUnErrorGenerico() {
        repositorio.respuesta = CompletableDeferred(ResultadoRegistro.Fallido(ErrorRegistro.DEMASIADOS_INTENTOS))
        llenarFormulario()

        viewModel.alPulsarCrearCuenta()

        assertEquals(R.string.error_demasiados_registros, viewModel.estado.errorGeneral)
    }

    @Test
    fun alEditarDespuesDeUnErrorDelServidor_seBorraElAvisoYSeConservanLosDatos() {
        repositorio.respuesta = CompletableDeferred(ResultadoRegistro.Fallido(ErrorRegistro.SIN_CONEXION))
        llenarFormulario()
        viewModel.alPulsarCrearCuenta()

        viewModel.alCambiarCorreo("otra@correo.com")

        assertNull(viewModel.estado.errorGeneral)
        assertEquals("Paula", viewModel.estado.nombre)
        assertTrue(viewModel.estado.aceptaTerminos)
    }

    @Test
    fun mientrasEsperaLaRespuesta_estaCargandoYNoRepiteLaLlamada() {
        repositorio.respuesta = CompletableDeferred()
        llenarFormulario()

        viewModel.alPulsarCrearCuenta()
        viewModel.alPulsarCrearCuenta()

        assertTrue(viewModel.estado.cargando)
        assertEquals(1, repositorio.llamadas)
    }

    @Test
    fun alMarcarLosTerminos_seQuitaElErrorDeLaCasilla() {
        viewModel.alPulsarCrearCuenta()

        viewModel.alCambiarTerminos(true)

        assertFalse(viewModel.estado.errorTerminos)
        assertTrue(viewModel.estado.aceptaTerminos)
    }

    private fun llenarFormulario(
        nombre: String = "Paula",
        correo: String = "paula@correo.com",
        contrasena: String = "Secreta12",
        aceptaTerminos: Boolean = true,
    ) {
        viewModel.alCambiarNombre(nombre)
        viewModel.alCambiarCorreo(correo)
        viewModel.alCambiarContrasena(contrasena)
        viewModel.alCambiarTerminos(aceptaTerminos)
    }

    /** Repositorio de mentira: responde lo que la prueba le diga, sin internet. */
    private class RepositorioRegistroFalso : RepositorioRegistro {
        var respuesta = CompletableDeferred<ResultadoRegistro>(ResultadoRegistro.Exitoso(necesitaConfirmacion = false))
        var llamadas = 0
        var ultimosDatos: Triple<String, String, String>? = null

        override suspend fun registrarse(nombre: String, correo: String, contrasena: String): ResultadoRegistro {
            llamadas++
            ultimosDatos = Triple(nombre, correo, contrasena)
            return respuesta.await()
        }
    }
}
