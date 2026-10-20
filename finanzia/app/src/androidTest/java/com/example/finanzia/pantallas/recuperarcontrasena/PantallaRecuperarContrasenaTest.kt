package com.example.finanzia.pantallas.recuperarcontrasena

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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PantallaRecuperarContrasenaTest {

    @get:Rule
    val reglaCompose = createComposeRule()

    private val contexto = InstrumentationRegistry.getInstrumentation().targetContext
    private var correoRecibido: String? = null
    private var volvio = false

    private fun mostrarPantalla() {
        reglaCompose.setContent {
            TemaFinanzia {
                PantallaRecuperarContrasena(
                    alVolver = { volvio = true },
                    alEnlaceEnviado = { correoRecibido = it },
                    alContactarSoporte = {},
                    viewModel = viewModel { RecuperarContrasenaViewModel(RepositorioRecuperacionFalso()) },
                )
            }
        }
    }

    private fun pulsar(@StringRes texto: Int) {
        reglaCompose.onNodeWithText(contexto.getString(texto)).performScrollTo().performClick()
    }

    @Test
    fun verificarConFormularioVacio_muestraErroresYNoSigue() {
        mostrarPantalla()

        pulsar(R.string.recuperar_boton_verificar)

        reglaCompose.onNodeWithText(contexto.getString(R.string.error_campos_incompletos)).assertIsDisplayed()
        reglaCompose.onNodeWithText(contexto.getString(R.string.error_usuario_vacio)).assertIsDisplayed()
        reglaCompose.runOnIdle { assertNull(correoRecibido) }
    }

    @Test
    fun verificarConDatosValidos_avisaQueSeEnvioElEnlace() {
        mostrarPantalla()
        val campos = reglaCompose.onAllNodes(hasSetTextAction())
        campos[0].performTextInput(" carlos_mendoza ")
        campos[1].performTextInput("carlos@correo.com")

        pulsar(R.string.recuperar_boton_verificar)

        reglaCompose.runOnIdle { assertEquals("carlos@correo.com", correoRecibido) }
    }

    @Test
    fun volverAIniciarSesion_avisa() {
        mostrarPantalla()

        pulsar(R.string.recuperar_boton_volver)

        reglaCompose.runOnIdle { assertTrue(volvio) }
    }

    /** Repositorio de mentira: siempre responde que salió bien, sin internet. */
    private class RepositorioRecuperacionFalso : RepositorioRecuperacion {
        override suspend fun enviarEnlace(correo: String): ResultadoRecuperacion = ResultadoRecuperacion.Exitoso
        override suspend fun cambiarContrasena(contrasena: String): ResultadoRecuperacion = ResultadoRecuperacion.Exitoso
    }
}
