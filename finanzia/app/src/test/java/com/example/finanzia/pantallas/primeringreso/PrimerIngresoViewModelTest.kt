package com.example.finanzia.pantallas.primeringreso

import com.example.finanzia.R
import com.example.finanzia.datos.modelos.CategoriaGasto
import com.example.finanzia.datos.modelos.Frecuencia
import com.example.finanzia.datos.repositorios.ErrorDatos
import com.example.finanzia.datos.repositorios.RepositorioMovimientos
import com.example.finanzia.datos.repositorios.ResultadoMovimiento
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
class PrimerIngresoViewModelTest {

    private val repositorio = RepositorioMovimientosFalso()
    private lateinit var viewModel: PrimerIngresoViewModel

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = PrimerIngresoViewModel(repositorio)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun guardarSinMonto_marcaElErrorYNoLlamaAlRepositorio() {
        viewModel.alPulsarGuardar()

        assertEquals(R.string.error_monto_vacio, viewModel.estado.errorMonto)
        assertEquals(0, repositorio.llamadas)
    }

    @Test
    fun alEscribirElMonto_quedanSoloLasCifras() {
        viewModel.alCambiarMonto("$ 18,500")

        assertEquals("18500", viewModel.estado.monto)
    }

    @Test
    fun porDefectoLaFrecuenciaEsMensual() {
        assertEquals(Frecuencia.MENSUAL, viewModel.estado.frecuencia)
    }

    @Test
    fun guardarConDatos_enviaMontoYFrecuenciaYAvisa() {
        viewModel.alCambiarMonto("18500")
        viewModel.alElegirFrecuencia(Frecuencia.QUINCENAL)

        viewModel.alPulsarGuardar()

        assertEquals(18500L to Frecuencia.QUINCENAL, repositorio.ultimo)
        assertTrue(viewModel.estado.guardado)
        assertFalse(viewModel.estado.cargando)
    }

    @Test
    fun siSupabaseFalla_muestraElAvisoYConservaLosDatos() {
        repositorio.respuesta = CompletableDeferred(ResultadoMovimiento.Fallido(ErrorDatos.SIN_CONEXION))
        viewModel.alCambiarMonto("18500")

        viewModel.alPulsarGuardar()

        assertEquals(R.string.error_sin_conexion, viewModel.estado.errorGeneral)
        assertEquals("18500", viewModel.estado.monto)
        assertFalse(viewModel.estado.guardado)
    }

    @Test
    fun mientrasGuarda_noRepiteLaLlamada() {
        repositorio.respuesta = CompletableDeferred()
        viewModel.alCambiarMonto("18500")

        viewModel.alPulsarGuardar()
        viewModel.alPulsarGuardar()

        assertTrue(viewModel.estado.cargando)
        assertEquals(1, repositorio.llamadas)
    }

    @Test
    fun alAvisarGuardado_quedaLimpio() {
        viewModel.alCambiarMonto("18500")
        viewModel.alPulsarGuardar()

        viewModel.alAvisarGuardado()

        assertFalse(viewModel.estado.guardado)
        assertEquals("", viewModel.estado.monto)
        assertNull(viewModel.estado.errorGeneral)
    }

    /** Repositorio de mentira: responde lo que la prueba le diga, sin internet. */
    private class RepositorioMovimientosFalso : RepositorioMovimientos {
        var respuesta = CompletableDeferred<ResultadoMovimiento>(ResultadoMovimiento.Exitoso)
        var llamadas = 0
        var ultimo: Pair<Long, Frecuencia>? = null

        override suspend fun registrarIngreso(monto: Long, frecuencia: Frecuencia): ResultadoMovimiento {
            llamadas++
            ultimo = monto to frecuencia
            return respuesta.await()
        }

        override suspend fun registrarGasto(monto: Long, categoria: CategoriaGasto): ResultadoMovimiento =
            ResultadoMovimiento.Exitoso
        override suspend fun editarMovimiento(id: Long, monto: Long, descripcion: String, categoria: CategoriaGasto?): ResultadoMovimiento =
            ResultadoMovimiento.Exitoso

        override suspend fun eliminarMovimiento(id: Long): ResultadoMovimiento = ResultadoMovimiento.Exitoso
    }
}
