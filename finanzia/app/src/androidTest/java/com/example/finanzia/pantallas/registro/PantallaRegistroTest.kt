package com.example.finanzia.pantallas.registro

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.platform.app.InstrumentationRegistry
import com.example.finanzia.R
import com.example.finanzia.datos.repositorios.ErrorRegistro
import com.example.finanzia.datos.repositorios.RepositorioRegistro
import com.example.finanzia.datos.repositorios.ResultadoRegistro
import com.example.finanzia.tema.TemaFinanzia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class PantallaRegistroTest {

    @get:Rule
    val reglaCompose = createComposeRule()

    private val contexto = InstrumentationRegistry.getInstrumentation().targetContext
    private var mensajeRecibido: String? = null

    private fun mostrarPantalla(respuesta: ResultadoRegistro = ResultadoRegistro.Exitoso(necesitaConfirmacion = false)) {
        reglaCompose.setContent {
            TemaFinanzia {
                PantallaRegistro(
                    alVolver = {},
                    alCrearCuenta = { mensajeRecibido = it },
                    alIniciarSesion = {},
                    alVerTerminos = {},
                    alVerPrivacidad = {},
                    viewModel = viewModel { RegistroViewModel(RepositorioRegistroFalso(respuesta)) },
                )
            }
        }
    }

    private fun pulsarCrearCuenta() {
        reglaCompose.onNodeWithText(contexto.getString(R.string.registro_boton_crear)).performScrollTo().performClick()
    }

    private fun llenarYCrearCuenta() {
        val campos = reglaCompose.onAllNodes(hasSetTextAction())
        campos[0].performTextInput("Paula Gómez")
        campos[1].performTextInput("paula@correo.com")
        campos[2].performTextInput("Secreta12")
        // Se usa la acción y no un toque: en el centro de la fila puede caer uno de los enlaces.
        reglaCompose.onNode(isToggleable()).performSemanticsAction(SemanticsActions.OnClick)
        pulsarCrearCuenta()
    }

    @Test
    fun crearCuentaConFormularioVacio_muestraErroresYNoAvisa() {
        mostrarPantalla()

        pulsarCrearCuenta()

        reglaCompose.onNodeWithText(contexto.getString(R.string.error_campos_incompletos)).assertIsDisplayed()
        reglaCompose.onNodeWithText(contexto.getString(R.string.error_nombre_vacio)).assertIsDisplayed()
        reglaCompose.runOnIdle { assertNull(mensajeRecibido) }
    }

    @Test
    fun crearCuentaConDatosValidos_avisaConElMensaje() {
        mostrarPantalla()

        llenarYCrearCuenta()

        reglaCompose.runOnIdle { assertEquals(contexto.getString(R.string.registro_cuenta_creada), mensajeRecibido) }
    }

    @Test
    fun correoYaRegistrado_muestraElAviso() {
        mostrarPantalla(ResultadoRegistro.Fallido(ErrorRegistro.CORREO_YA_REGISTRADO))

        llenarYCrearCuenta()

        reglaCompose.onNodeWithText(contexto.getString(R.string.error_correo_registrado)).assertIsDisplayed()
        reglaCompose.runOnIdle { assertNull(mensajeRecibido) }
    }

    /** Repositorio de mentira: siempre responde lo mismo, sin internet. */
    private class RepositorioRegistroFalso(private val respuesta: ResultadoRegistro) : RepositorioRegistro {
        override suspend fun registrarse(nombre: String, correo: String, contrasena: String): ResultadoRegistro =
            respuesta
    }
}
