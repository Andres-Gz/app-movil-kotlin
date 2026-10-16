package com.example.finanzia.pantallas.editarmovimiento

import com.example.finanzia.R
import com.example.finanzia.datos.modelos.CategoriaGasto
import com.example.finanzia.datos.modelos.Frecuencia
import com.example.finanzia.datos.modelos.MovimientoReciente
import com.example.finanzia.datos.repositorios.ErrorDatos
import com.example.finanzia.datos.repositorios.RepositorioMovimientos
import com.example.finanzia.datos.repositorios.ResultadoMovimiento
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
class EditarMovimientoViewModelTest {

    private val repositorio = RepositorioFalso()

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun arrancaConLosDatosDelMovimiento() {
        val viewModel = EditarMovimientoViewModel(repositorio, GASTO)

        with(viewModel.estado) {
            assertFalse(esIngreso)
            assertEquals("64500", monto)
            assertEquals("Supermercado", descripcion)
            assertEquals(CategoriaGasto.COMIDA, categoria)
        }
    }

    @Test
    fun guardarGasto_mandaLosCambiosConLaCategoria() {
        val viewModel = EditarMovimientoViewModel(repositorio, GASTO)
        viewModel.alCambiarMonto("70,000")
        viewModel.alCambiarDescripcion("  Mercado del mes ")
        viewModel.alElegirCategoria(CategoriaGasto.GASTOS_FIJOS)

        viewModel.alPulsarGuardar()

        assertEquals(Edicion(7L, 70000L, "Mercado del mes", CategoriaGasto.GASTOS_FIJOS), repositorio.edicion)
        assertEquals(ResultadoEdicion.GUARDADO, viewModel.estado.resultado)
    }

    @Test
    fun guardarIngreso_noMandaCategoria() {
        val viewModel = EditarMovimientoViewModel(repositorio, INGRESO)

        viewModel.alPulsarGuardar()

        assertNull(repositorio.edicion?.categoria)
        assertTrue(viewModel.estado.esIngreso)
    }

    @Test
    fun sinMontoNiDescripcion_noLlamaAlRepositorio() {
        val viewModel = EditarMovimientoViewModel(repositorio, GASTO)
        viewModel.alCambiarMonto("")
        viewModel.alCambiarDescripcion("   ")

        viewModel.alPulsarGuardar()

        assertEquals(R.string.error_monto_vacio, viewModel.estado.errorMonto)
        assertEquals(R.string.error_descripcion_vacia, viewModel.estado.errorDescripcion)
        assertNull(repositorio.edicion)
    }

    @Test
    fun eliminar_pideConfirmacionAntes() {
        val viewModel = EditarMovimientoViewModel(repositorio, GASTO)

        viewModel.alPulsarEliminar()
        assertTrue(viewModel.estado.confirmandoEliminar)
        assertNull(repositorio.eliminado)

        viewModel.alConfirmarEliminar()
        assertEquals(7L, repositorio.eliminado)
        assertEquals(ResultadoEdicion.ELIMINADO, viewModel.estado.resultado)
    }

    @Test
    fun siSupabaseFalla_muestraElAvisoYNoTermina() {
        repositorio.respuesta = ResultadoMovimiento.Fallido(ErrorDatos.SIN_CONEXION)
        val viewModel = EditarMovimientoViewModel(repositorio, GASTO)

        viewModel.alPulsarGuardar()

        assertEquals(R.string.error_sin_conexion, viewModel.estado.errorGeneral)
        assertNull(viewModel.estado.resultado)
        assertFalse(viewModel.estado.cargando)
    }

    private data class Edicion(val id: Long, val monto: Long, val descripcion: String, val categoria: CategoriaGasto?)

    private class RepositorioFalso : RepositorioMovimientos {
        var respuesta: ResultadoMovimiento = ResultadoMovimiento.Exitoso
        var edicion: Edicion? = null
        var eliminado: Long? = null

        override suspend fun registrarIngreso(monto: Long, frecuencia: Frecuencia) = respuesta
        override suspend fun registrarGasto(monto: Long, categoria: CategoriaGasto) = respuesta

        override suspend fun editarMovimiento(id: Long, monto: Long, descripcion: String, categoria: CategoriaGasto?): ResultadoMovimiento {
            edicion = Edicion(id, monto, descripcion, categoria)
            return respuesta
        }

        override suspend fun eliminarMovimiento(id: Long): ResultadoMovimiento {
            eliminado = id
            return respuesta
        }
    }

    private companion object {
        val GASTO = MovimientoReciente(7, "gasto", 64500.0, "Supermercado", "2026-10-13T14:32:00+00:00", "Comida", "Cuenta principal")
        val INGRESO = MovimientoReciente(8, "ingreso", 2800000.0, "Ingreso mensual", "2026-10-12T09:00:00+00:00", null, "Cuenta principal")
    }
}
