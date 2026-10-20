package com.example.finanzia.pantallas.nuevameta

import com.example.finanzia.R
import com.example.finanzia.datos.modelos.CategoriaMeta
import com.example.finanzia.datos.modelos.Frecuencia
import com.example.finanzia.datos.modelos.Meta
import com.example.finanzia.datos.modelos.MetaNueva
import com.example.finanzia.datos.repositorios.ErrorDatos
import com.example.finanzia.datos.repositorios.RepositorioMetas
import com.example.finanzia.datos.repositorios.ResultadoMeta
import com.example.finanzia.datos.repositorios.ResultadoMetas
import com.example.finanzia.pantallas.metas.CalculosMetas
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
class NuevaMetaViewModelTest {

    private val repositorio = RepositorioMetasFalso()
    private lateinit var viewModel: NuevaMetaViewModel

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = NuevaMetaViewModel(repositorio, hoy = { HOY })
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun conElFormularioVacio_pideElNombreYNoGuarda() {
        viewModel.alPulsarContinuar()

        assertEquals(R.string.error_meta_nombre_vacio, viewModel.estado.error)
        assertFalse(viewModel.estado.errorDelServidor)
        assertNull(repositorio.recibida)
    }

    @Test
    fun pideElMontoYLuegoElMes() {
        viewModel.alCambiarNombre("Vacaciones")
        viewModel.alPulsarContinuar()
        assertEquals(R.string.error_meta_monto_vacio, viewModel.estado.error)

        viewModel.alCambiarMonto("3.500.000")
        viewModel.alPulsarContinuar()
        assertEquals(R.string.error_meta_fecha_vacia, viewModel.estado.error)
    }

    @Test
    fun losBotonesSumanAlMontoYSeResaltan() {
        viewModel.alCambiarMonto("2500000")
        viewModel.alSumarMonto(1_000_000L)

        assertEquals("3500000", viewModel.estado.monto)
        assertEquals(1_000_000L, viewModel.estado.incrementoElegido)

        viewModel.alCambiarMonto("35000001")
        assertNull(viewModel.estado.incrementoElegido)
    }

    @Test
    fun alContinuar_guardaLaMetaConElAporteQueHaceFalta() {
        llenarFormulario()

        viewModel.alPulsarContinuar()

        // Del 10 de marzo al 30 de noviembre caen 18 quincenas: 3.500.000 / 18 = 194.444, redondeado a 195.000.
        val esperada = MetaNueva(
            nombre = "Vacaciones en San Andrés",
            categoria = CategoriaMeta.EMERGENCIA,
            objetivo = 3_500_000L,
            fechaLimite = "2026-11-30",
            frecuencia = Frecuencia.QUINCENAL,
            aporte = 195_000L,
        )
        assertEquals(esperada, repositorio.recibida)
        assertTrue(viewModel.estado.guardada)
    }

    @Test
    fun siSupabaseFalla_muestraElAvisoDelServidor() {
        repositorio.respuesta = ResultadoMeta.Fallido(ErrorDatos.DESCONOCIDO)
        llenarFormulario()

        viewModel.alPulsarContinuar()

        assertEquals(R.string.error_meta_no_guardada, viewModel.estado.error)
        assertTrue(viewModel.estado.errorDelServidor)
        assertFalse(viewModel.estado.guardada)
    }

    @Test
    fun calculos_mesesYAportesHastaElMes() {
        val noviembre = MesAno(Calendar.NOVEMBER, 2026)

        assertEquals(8, CalculosNuevaMeta.mesesRestantes(noviembre, HOY))
        assertEquals(18, CalculosNuevaMeta.aportes(noviembre, Frecuencia.QUINCENAL, HOY))
        assertEquals(9, CalculosNuevaMeta.aportes(noviembre, Frecuencia.MENSUAL, HOY))
        assertEquals(195_000L, CalculosMetas.montoAporte(3_500_000.0, 18))
    }

    @Test
    fun alEditar_llegaElFormularioLleno() {
        viewModel.alAbrir(GUARDADA)

        val estado = viewModel.estado
        assertTrue(estado.editando)
        assertEquals("Vacaciones San Andrés", estado.nombre)
        assertEquals(CategoriaMeta.VIAJES, estado.categoria)
        assertEquals("4000000", estado.monto)
        assertEquals(MesAno(Calendar.NOVEMBER, 2026), estado.mes)
        assertEquals(Frecuencia.QUINCENAL, estado.frecuencia)
    }

    @Test
    fun alEditarSoloElNombre_conservaLaRegla() {
        viewModel.alAbrir(GUARDADA)
        viewModel.alCambiarNombre("Viaje a San Andrés")

        viewModel.alPulsarContinuar()

        assertEquals(7L, repositorio.idEditada)
        assertEquals(200_000L, repositorio.recibida?.aporte)
        assertEquals("Viaje a San Andrés", repositorio.recibida?.nombre)
        assertTrue(viewModel.estado.guardada)
    }

    @Test
    fun alCambiarElMonto_recalculaConLoQueFalta() {
        viewModel.alAbrir(GUARDADA)
        viewModel.alCambiarMonto("5000000")

        viewModel.alPulsarContinuar()

        // Faltan 5.000.000 − 2.800.000 = 2.200.000 en 18 quincenas: 122.222, redondeado a 123.000.
        assertEquals(123_000L, repositorio.recibida?.aporte)
    }

    @Test
    fun alEditarUnaMetaVencida_pideOtroMes() {
        viewModel.alAbrir(GUARDADA.copy(fechaLimite = "2026-02-28T12:00:00+00:00"))

        viewModel.alPulsarContinuar()

        assertEquals(R.string.error_meta_fecha_pasada, viewModel.estado.error)
        assertNull(repositorio.recibida)
    }

    @Test
    fun soloSePuedenElegirMesesDesdeElSiguiente() {
        assertEquals(MesAno(Calendar.APRIL, 2026), CalculosNuevaMeta.primerMes(HOY))
        assertFalse(CalculosNuevaMeta.disponible(MesAno(Calendar.MARCH, 2026), HOY))
        assertTrue(CalculosNuevaMeta.disponible(MesAno(Calendar.JANUARY, 2027), HOY))
    }

    private fun llenarFormulario() {
        viewModel.alCambiarNombre("  Vacaciones en San Andrés ")
        viewModel.alElegirCategoria(CategoriaMeta.EMERGENCIA)
        viewModel.alCambiarMonto("3500000")
        viewModel.alElegirMes(MesAno(Calendar.NOVEMBER, 2026))
    }

    private class RepositorioMetasFalso : RepositorioMetas {
        var respuesta: ResultadoMeta = ResultadoMeta.Exitoso
        var recibida: MetaNueva? = null
        var idEditada: Long? = null

        override suspend fun obtenerResumen(): ResultadoMetas = ResultadoMetas.Fallido(ErrorDatos.DESCONOCIDO)

        override suspend fun crearMeta(meta: MetaNueva): ResultadoMeta {
            recibida = meta
            return respuesta
        }

        override suspend fun editarMeta(id: Long, meta: MetaNueva): ResultadoMeta {
            idEditada = id
            recibida = meta
            return respuesta
        }

        // Esta pantalla no aporta, no ajusta la regla ni elimina.
        override suspend fun aportar(id: Long, monto: Long): ResultadoMeta = ResultadoMeta.Fallido(ErrorDatos.DESCONOCIDO)
        override suspend fun ajustarRegla(id: Long, aporte: Long, frecuencia: Frecuencia): ResultadoMeta =
            ResultadoMeta.Fallido(ErrorDatos.DESCONOCIDO)
        override suspend fun eliminarMeta(id: Long): ResultadoMeta = ResultadoMeta.Fallido(ErrorDatos.DESCONOCIDO)
    }

    private companion object {
        val HOY: Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(2026, Calendar.MARCH, 10)
        }

        /** Una meta ya guardada, como llega de `resumen_metas`. */
        val GUARDADA = Meta(
            idMeta = 7,
            nombre = "Vacaciones San Andrés",
            objetivo = 4_000_000.0,
            ahorrado = 2_800_000.0,
            fechaLimite = "2026-11-30T12:00:00+00:00",
            categoria = "Viajes & Ocio",
            icono = "🏝️",
            montoAporte = 200_000.0,
            frecuenciaAporte = "quincenal",
        )
    }
}
