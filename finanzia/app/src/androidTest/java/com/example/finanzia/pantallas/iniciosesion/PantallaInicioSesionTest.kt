package com.example.finanzia.pantallas.iniciosesion

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import com.example.finanzia.R
import com.example.finanzia.tema.TemaFinanzia
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PantallaInicioSesionTest {

    @get:Rule
    val reglaCompose = createComposeRule()

    private val contexto = InstrumentationRegistry.getInstrumentation().targetContext
    private var inicioSesionSolicitado = false

    private fun mostrarPantalla() {
        reglaCompose.setContent {
            TemaFinanzia {
                PantallaInicioSesion(
                    alVolver = {},
                    alIniciarSesion = { inicioSesionSolicitado = true },
                    alOlvidarContrasena = {},
                    alRegistrarse = {},
                    alCambiarTema = {},
                    alPedirAyuda = {},
                )
            }
        }
    }

    private fun pulsarEntrar() {
        reglaCompose.onNodeWithText(contexto.getString(R.string.inicio_sesion_boton_entrar)).performClick()
    }

    @Test
    fun entrarConFormularioVacio_muestraErroresYNoIniciaSesion() {
        mostrarPantalla()

        pulsarEntrar()

        reglaCompose.onNodeWithText(contexto.getString(R.string.error_correo_vacio)).assertIsDisplayed()
        reglaCompose.onNodeWithText(contexto.getString(R.string.error_contrasena_vacia)).assertIsDisplayed()
        assertFalse(inicioSesionSolicitado)
    }

    @Test
    fun entrarConDatosValidos_iniciaSesion() {
        mostrarPantalla()
        val campos = reglaCompose.onAllNodes(hasSetTextAction())

        campos[0].performTextInput("ana@correo.com")
        campos[1].performTextInput("secreta123")
        pulsarEntrar()

        assertTrue(inicioSesionSolicitado)
    }
}
