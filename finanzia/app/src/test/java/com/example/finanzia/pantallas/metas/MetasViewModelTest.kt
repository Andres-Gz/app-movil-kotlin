package com.example.finanzia.pantallas.metas

import com.example.finanzia.R
import com.example.finanzia.datos.modelos.Frecuencia
import com.example.finanzia.datos.modelos.Meta
import com.example.finanzia.datos.modelos.MetaNueva
import com.example.finanzia.datos.modelos.ResumenMetas
import com.example.finanzia.datos.repositorios.ErrorDatos
import com.example.finanzia.datos.repositorios.RepositorioMetas
import com.example.finanzia.datos.repositorios.ResultadoMeta
import com.example.finanzia.datos.repositorios.ResultadoMetas
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
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalCoroutinesApi::class)
class MetasViewModelTest {

    private val repositorio = RepositorioMetasFalso()
    private lateinit var viewModel: MetasViewModel

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = MetasViewModel(repositorio, hoy = { fecha(2025, Calendar.MARCH, 10) })
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
    fun siFallaElServidor_muestraElAvisoYConservaLoQueHabia() {
        viewModel.cargar()
        repositorio.respuesta = ResultadoMetas.Fallido(ErrorDatos.DESCONOCIDO)

        viewModel.cargar()

        assertEquals(R.string.error_metas_no_cargadas, viewModel.estado.error)
        assertEquals(RESUMEN, viewModel.estado.resumen)
    }

    @Test
    fun alElegirOrden_loGuardaYCierraElMenu() {
        viewModel.alAbrirOrden()
        assertTrue(viewModel.estado.ordenAbierto)

        viewModel.alElegirOrden(OrdenMetas.NOMBRE)

        assertEquals(OrdenMetas.NOMBRE, viewModel.estado.orden)
        assertFalse(viewModel.estado.ordenAbierto)
    }

    @Test
    fun totales_yPorcentajeSinRedondearHaciaArriba() {
        assertEquals(14_850_000.0, CalculosMetas.totalAhorrado(RESUMEN.metas), 0.001)
        assertEquals(22_000_000.0, CalculosMetas.totalObjetivo(RESUMEN.metas), 0.001)
        assertEquals(67, CalculosMetas.porcentaje(14_850_000.0, 22_000_000.0))
        assertEquals(70, CalculosMetas.porcentaje(2_800_000.0, 4_000_000.0))
        assertEquals(100, CalculosMetas.porcentaje(5.0, 4.0))
        assertEquals(0, CalculosMetas.porcentaje(5.0, 0.0))
        assertEquals(1_200_000.0, CalculosMetas.falta(VACACIONES), 0.001)
    }

    @Test
    fun ordenar_porAvanceYPorNombre() {
        assertEquals(listOf(VACACIONES, EMERGENCIA), CalculosMetas.ordenar(RESUMEN.metas, OrdenMetas.MAYOR_AVANCE))
        assertEquals(listOf(EMERGENCIA, VACACIONES), CalculosMetas.ordenar(RESUMEN.metas, OrdenMetas.NOMBRE))
    }

    @Test
    fun consejo_conAportesQuincenales_llegaDiasAntes() {
        // Del 10 de marzo: 15 y 31 de marzo, 15 y 30 de abril, 15 y 31 de mayo. La fecha límite es el 18 de junio.
        val consejo = CalculosMetas.consejo(listOf(VACACIONES), fecha(2025, Calendar.MARCH, 10))

        assertEquals(ConsejoMetas.Proyeccion(VACACIONES, 18, Frecuencia.QUINCENAL), consejo)
    }

    @Test
    fun consejo_siNoAlcanza_daLosDiasDeRetraso() {
        val apurada = VACACIONES.copy(fechaLimite = "2025-05-20T00:00:00+00:00")

        val consejo = CalculosMetas.consejo(listOf(apurada), fecha(2025, Calendar.MARCH, 10)) as ConsejoMetas.Proyeccion

        assertEquals(-11, consejo.dias)
    }

    @Test
    fun consejo_casosSinProyeccion() {
        val hoy = fecha(2025, Calendar.MARCH, 10)
        assertEquals(ConsejoMetas.SinMetas, CalculosMetas.consejo(emptyList(), hoy))
        assertEquals(ConsejoMetas.TodasLogradas, CalculosMetas.consejo(listOf(VACACIONES.copy(ahorrado = 4_000_000.0)), hoy))
        assertEquals(ConsejoMetas.SinRegla, CalculosMetas.consejo(listOf(EMERGENCIA), hoy))
    }

    @Test
    fun fechaProyectada_mensual_caeElUltimoDiaDeCadaMes() {
        val logro = CalculosMetas.fechaProyectada(300.0, 100.0, Frecuencia.MENSUAL, fecha(2025, Calendar.JANUARY, 31))

        assertEquals(Calendar.APRIL, logro.get(Calendar.MONTH))
        assertEquals(30, logro.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun quincenasSeguidas_noCortaLaRachaSiLaQuincenaActualAunNoTieneAporte() {
        val hoy = fecha(2025, Calendar.MARCH, 20)
        val fechas = listOf(
            "2025-03-05T12:00:00+00:00",
            "2025-02-28T12:00:00+00:00",
            "2025-02-15T12:00:00+00:00",
            // Falta la segunda quincena de enero: aquí se corta la racha.
            "2025-01-10T12:00:00+00:00",
        )

        assertEquals(3, CalculosMetas.quincenasSeguidas(fechas, hoy))
        assertEquals(4, CalculosMetas.quincenasSeguidas(fechas + "2025-03-18T12:00:00+00:00", hoy))
        assertEquals(0, CalculosMetas.quincenasSeguidas(emptyList(), hoy))
    }

    @Test
    fun aportar_abreConElMontoDeLaReglaYAlGuardarAvisaYRecarga() {
        viewModel.alPedirAporte(VACACIONES)
        assertEquals("200000", viewModel.estado.montoDialogo)

        viewModel.alCambiarMontoDialogo("300.000")
        viewModel.alConfirmarDialogo()

        assertEquals("aportar 1 300000", repositorio.ultimaLlamada)
        assertNull(viewModel.estado.dialogo)
        assertEquals(R.string.metas_aviso_aporte, viewModel.estado.aviso)
        assertEquals(RESUMEN, viewModel.estado.resumen)
    }

    @Test
    fun aportar_loQueFalta_avisaQueSeLogro() {
        viewModel.alPedirAporte(VACACIONES)
        viewModel.alUsarMonto(1_200_000L)
        viewModel.alConfirmarDialogo()

        assertEquals(R.string.metas_aviso_lograda, viewModel.estado.aviso)
    }

    @Test
    fun aportar_sinMonto_noLlamaASupabase() {
        viewModel.alPedirAporte(EMERGENCIA)
        viewModel.alConfirmarDialogo()

        assertEquals(R.string.error_meta_aporte_vacio, viewModel.estado.errorDialogo)
        assertNull(repositorio.ultimaLlamada)
    }

    @Test
    fun ajustarRegla_sinRegla_proponeElAporteQueHaceFalta() {
        // Fondo de emergencia: faltan 5.950.000 hasta el 31 de diciembre; del 10 de marzo caen 20 quincenas
        // (297.500 cada una, redondeado a 298.000).
        val conFecha = EMERGENCIA.copy(fechaLimite = "2025-12-31T12:00:00+00:00")

        viewModel.alPedirAjusteRegla(conFecha)
        assertEquals(Frecuencia.QUINCENAL, viewModel.estado.frecuenciaDialogo)
        assertEquals("298000", viewModel.estado.montoDialogo)

        viewModel.alElegirFrecuenciaDialogo(Frecuencia.MENSUAL)
        viewModel.alConfirmarDialogo()

        assertEquals("regla 2 298000 MENSUAL", repositorio.ultimaLlamada)
        assertEquals(R.string.metas_aviso_regla, viewModel.estado.aviso)
    }

    @Test
    fun eliminar_siFalla_dejaLaVentanaAbiertaConElError() {
        repositorio.respuestaCambio = ResultadoMeta.Fallido(ErrorDatos.SIN_CONEXION)
        viewModel.alAbrirMenu(VACACIONES)
        viewModel.alPedirEliminar(VACACIONES)
        assertNull(viewModel.estado.menuDe)

        viewModel.alConfirmarDialogo()

        assertEquals("eliminar 1", repositorio.ultimaLlamada)
        assertEquals(DialogoMeta.Eliminar(VACACIONES), viewModel.estado.dialogo)
        assertEquals(R.string.error_sin_conexion, viewModel.estado.errorDialogo)
    }

    private fun fecha(ano: Int, mes: Int, dia: Int): Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        clear()
        set(ano, mes, dia)
    }

    private class RepositorioMetasFalso : RepositorioMetas {
        var respuesta: ResultadoMetas = ResultadoMetas.Exitoso(RESUMEN)
        var respuestaCambio: ResultadoMeta = ResultadoMeta.Exitoso
        var ultimaLlamada: String? = null

        override suspend fun obtenerResumen(): ResultadoMetas = respuesta
        override suspend fun crearMeta(meta: MetaNueva): ResultadoMeta = cambio("crear")
        override suspend fun editarMeta(id: Long, meta: MetaNueva): ResultadoMeta = cambio("editar $id")
        override suspend fun aportar(id: Long, monto: Long): ResultadoMeta = cambio("aportar $id $monto")
        override suspend fun ajustarRegla(id: Long, aporte: Long, frecuencia: Frecuencia): ResultadoMeta =
            cambio("regla $id $aporte $frecuencia")
        override suspend fun eliminarMeta(id: Long): ResultadoMeta = cambio("eliminar $id")

        private fun cambio(llamada: String): ResultadoMeta {
            ultimaLlamada = llamada
            return respuestaCambio
        }
    }

    private companion object {
        val VACACIONES = Meta(
            idMeta = 1,
            nombre = "Vacaciones San Andrés",
            objetivo = 4_000_000.0,
            ahorrado = 2_800_000.0,
            fechaLimite = "2025-06-18T00:00:00+00:00",
            categoria = "Viajes & Ocio",
            icono = "🏝️",
            montoAporte = 200_000.0,
            frecuenciaAporte = "quincenal",
        )
        val EMERGENCIA = Meta(idMeta = 2, nombre = "Fondo de Emergencia", objetivo = 18_000_000.0, ahorrado = 12_050_000.0)
        val RESUMEN = ResumenMetas(metas = listOf(VACACIONES, EMERGENCIA))
    }
}
