package com.example.finanzia.pantallas.primergasto

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
class PrimerGastoViewModelTest {

    private val repositorio = RepositorioGastosFalso()
    private lateinit var viewModel: PrimerGastoViewModel

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = PrimerGastoViewModel(repositorio)
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
    fun porDefectoLaCategoriaEsGastosFijos() {
        assertEquals(CategoriaGasto.GASTOS_FIJOS, viewModel.estado.categoria)
    }

    @Test
    fun guardarConDatos_enviaMontoYCategoriaYAvisa() {
        viewModel.alCambiarMonto("3,200")
        viewModel.alElegirCategoria(CategoriaGasto.COMIDA)

        viewModel.alPulsarGuardar()

        assertEquals(3200L to CategoriaGasto.COMIDA, repositorio.ultimo)
        assertTrue(viewModel.estado.guardado)
        assertFalse(viewModel.estado.cargando)
    }

    @Test
    fun siSupabaseFalla_muestraElAvisoYConservaLosDatos() {
        repositorio.respuesta = CompletableDeferred(ResultadoMovimiento.Fallido(ErrorDatos.DESCONOCIDO))
        viewModel.alCambiarMonto("3200")

        viewModel.alPulsarGuardar()

        assertEquals(R.string.error_gasto_no_guardado, viewModel.estado.errorGeneral)
        assertEquals("3200", viewModel.estado.monto)
        assertNull(viewModel.estado.errorMonto)
    }

    /** Repositorio de mentira: responde lo que la prueba le diga, sin internet. */
    private class RepositorioGastosFalso : RepositorioMovimientos {
        var respuesta = CompletableDeferred<ResultadoMovimiento>(ResultadoMovimiento.Exitoso)
        var llamadas = 0
        var ultimo: Pair<Long, CategoriaGasto>? = null

        override suspend fun registrarIngreso(monto: Long, frecuencia: Frecuencia): ResultadoMovimiento =
            ResultadoMovimiento.Exitoso

        override suspend fun registrarGasto(monto: Long, categoria: CategoriaGasto): ResultadoMovimiento {
            llamadas++
            ultimo = monto to categoria
            return respuesta.await()
        }
        override suspend fun editarMovimiento(id: Long, monto: Long, descripcion: String, categoria: CategoriaGasto?): ResultadoMovimiento =
            ResultadoMovimiento.Exitoso

        override suspend fun eliminarMovimiento(id: Long): ResultadoMovimiento = ResultadoMovimiento.Exitoso
    }
}
