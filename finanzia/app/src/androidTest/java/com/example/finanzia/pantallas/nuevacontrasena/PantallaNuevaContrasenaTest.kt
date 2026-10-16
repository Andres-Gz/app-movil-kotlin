package com.example.finanzia.pantallas.nuevacontrasena

import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.platform.app.InstrumentationRegistry
import com.example.finanzia.R
import com.example.finanzia.datos.repositorios.RepositorioRecuperacion
import com.example.finanzia.datos.repositorios.ResultadoRecuperacion
import com.example.finanzia.tema.TemaFinanzia
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PantallaNuevaContrasenaTest {

    @get:Rule
    val reglaCompose = createComposeRule()

    private val contexto = InstrumentationRegistry.getInstrumentation().targetContext
    private var actualizada = false
    private var cancelo = false

    private fun mostrarPantalla() {
        reglaCompose.setContent {
            TemaFinanzia {
                PantallaNuevaContrasena(
                    alVolver = {},
                    alCancelar = { cancelo = true },
                    alContrasenaActualizada = { actualizada = true },
                    viewModel = viewModel { NuevaContrasenaViewModel(RepositorioRecuperacionFalso()) },
                )
            }
        }
    }

    private fun escribir(contrasena: String, confirmacion: String) {
        val campos = reglaCompose.onAllNodes(hasSetTextAction())
        campos[0].performTextInput(contrasena)
        campos[1].performTextInput(confirmacion)
    }

    private fun pulsar(@StringRes texto: Int) {
        reglaCompose.onNodeWithText(contexto.getString(texto)).performScrollTo().performClick()
    }

    @Test
    fun contrasenasDistintas_muestranElAvisoYNoSigue() {
        mostrarPantalla()
        escribir("Finanzia2024!", "Finanzia2024?")

        pulsar(R.string.nueva_boton_actualizar)

        reglaCompose.onNodeWithText(contexto.getString(R.string.error_contrasenas_no_coinciden))
            .performScrollTo()
            .assertIsDisplayed()
        reglaCompose.onNodeWithText(contexto.getString(R.string.nueva_no_coincide)).performScrollTo().assertIsDisplayed()
        reglaCompose.runOnIdle { assertFalse(actualizada) }
    }

    @Test
    fun contrasenaValidaYConfirmada_avisaQueSeActualizo() {
        mostrarPantalla()
        escribir("Finanzia2024!", "Finanzia2024!")

        reglaCompose.onNodeWithText(contexto.getString(R.string.nueva_cumple_requisitos)).assertIsDisplayed()
        pulsar(R.string.nueva_boton_actualizar)

        reglaCompose.runOnIdle { assertTrue(actualizada) }
    }

    @Test
    fun cancelar_avisa() {
        mostrarPantalla()

        pulsar(R.string.nueva_boton_cancelar)

        reglaCompose.runOnIdle { assertTrue(cancelo) }
    }

    /** Repositorio de mentira: siempre responde que salió bien, sin internet. */
    private class RepositorioRecuperacionFalso : RepositorioRecuperacion {
        override suspend fun enviarEnlace(correo: String): ResultadoRecuperacion = ResultadoRecuperacion.Exitoso
        override suspend fun cambiarContrasena(contrasena: String): ResultadoRecuperacion = ResultadoRecuperacion.Exitoso
    }
}
