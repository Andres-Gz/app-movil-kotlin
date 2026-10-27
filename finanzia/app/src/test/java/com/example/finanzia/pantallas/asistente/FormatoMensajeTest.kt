package com.example.finanzia.pantallas.asistente

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Date

class FormatoMensajeTest {

    @Test
    fun separaElTituloQuePusoLaIA() {
        val separada = FormatoMensaje.separarTitulo("# Diagnóstico presupuestario\n¡Hola Paula!\nTienes margen.")

        assertEquals("Diagnóstico presupuestario", separada.titulo)
        assertEquals("¡Hola Paula!\nTienes margen.", separada.texto)
    }

    @Test
    fun sinTitulo_todoEsTexto() {
        assertEquals(RespuestaSeparada(null, "Era el 70."), FormatoMensaje.separarTitulo("Era el 70."))
    }

    @Test
    fun loQueVaEntreAsteriscos_vaResaltado() {
        val trozos = FormatoMensaje.trozos("Tienes **$320.000 / día** disponible.")

        assertEquals(
            listOf(Trozo("Tienes ", false), Trozo("$320.000 / día", true), Trozo(" disponible.", false)),
            trozos,
        )
    }

    @Test
    fun unaMarcaSinCerrar_seMuestraTalCual() {
        assertEquals(listOf(Trozo("Son ", false), Trozo("**20.000", false)), FormatoMensaje.trozos("Son **20.000"))
    }

    @Test
    fun laVistaPrevia_quitaElTituloYLasMarcas() {
        assertEquals("Era el 70.", FormatoMensaje.vistaPrevia("# Tu número\nEra el **70**."))
        assertEquals("Anotado", FormatoMensaje.vistaPrevia("# Anotado"))
    }

    @Test
    fun antiguedad_enMinutosHorasYDias() {
        val ahora = Date(1_793_112_600_000) // 2026-10-27T14:50:00Z
        assertEquals(Antiguedad.Ahora, FormatoMensaje.antiguedad("2026-10-27T14:49:30+00:00", ahora))
        assertEquals(Antiguedad.Minutos(1), FormatoMensaje.antiguedad("2026-10-27T14:49:00+00:00", ahora))
        assertEquals(Antiguedad.Horas(2), FormatoMensaje.antiguedad("2026-10-27T12:40:00+00:00", ahora))
        assertEquals(Antiguedad.Dias(3), FormatoMensaje.antiguedad("2026-10-24T14:50:00+00:00", ahora))
    }
}
