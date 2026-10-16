package com.example.finanzia.pantallas.inicio

import com.example.finanzia.R
import com.example.finanzia.datos.modelos.MetaActual
import com.example.finanzia.datos.modelos.ResumenInicio
import com.example.finanzia.datos.repositorios.ErrorDatos
import com.example.finanzia.datos.repositorios.RepositorioInicio
import com.example.finanzia.datos.repositorios.ResultadoInicio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InicioViewModelTest {

    private val repositorio = RepositorioInicioFalso()
    private lateinit var viewModel: InicioViewModel

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = InicioViewModel(repositorio)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun alCargar_guardaElResumen() {
        viewModel.cargar()

        assertEquals(RESUMEN, viewModel.estado.resumen)
        assertFalse(viewModel.estado.cargando)
        assertNull(viewModel.estado.error)
    }

    @Test
    fun siFallaLaConexion_muestraElAvisoYConservaLoQueHabia() {
        viewModel.cargar()
        repositorio.respuesta = ResultadoInicio.Fallido(ErrorDatos.SIN_CONEXION)

        viewModel.cargar()

        assertEquals(R.string.error_sin_conexion, viewModel.estado.error)
        assertEquals(RESUMEN, viewModel.estado.resumen)
    }

    @Test
    fun elOjo_ocultaYMuestraLosMontos() {
        viewModel.alAlternarMontos()
        assertFalse(viewModel.estado.montosVisibles)
    }

    @Test
    fun balance_esSaldoMasIngresosMenosGastos() {
        assertEquals(650.0, RESUMEN.balance, 0.001)
    }

    @Test
    fun calculos_ahorroYMeta() {
        assertEquals(50.0, CalculosInicio.porcentajeAhorro(RESUMEN)!!, 0.001)
        assertEquals(50.0, CalculosInicio.ahorroSugerido(RESUMEN), 0.001)
        assertNull(CalculosInicio.porcentajeAhorro(RESUMEN.copy(ingresos = 0.0)))
        val meta = MetaActual("Vacaciones", objetivo = 2500.0, ahorrado = 1800.0)
        assertEquals(0.72f, CalculosInicio.progreso(meta), 0.001f)
        assertEquals(700.0, CalculosInicio.falta(meta), 0.001)
    }

    private class RepositorioInicioFalso : RepositorioInicio {
        var respuesta: ResultadoInicio = ResultadoInicio.Exitoso(RESUMEN)
        override suspend fun obtenerResumen(): ResultadoInicio = respuesta
    }

    private companion object {
        val RESUMEN = ResumenInicio(nombre = "Ana", saldoInicial = 150.0, ingresos = 1000.0, gastos = 500.0, totalMovimientos = 2)
    }
}
