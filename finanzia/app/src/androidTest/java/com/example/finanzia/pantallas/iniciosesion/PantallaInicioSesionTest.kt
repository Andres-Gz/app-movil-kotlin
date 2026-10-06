package com.example.finanzia.pantallas.iniciosesion

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.platform.app.InstrumentationRegistry
import com.example.finanzia.R
import com.example.finanzia.datos.modelos.Usuario
import com.example.finanzia.datos.repositorios.ErrorSesion
import com.example.finanzia.datos.repositorios.RepositorioSesion
import com.example.finanzia.datos.repositorios.ResultadoSesion
import com.example.finanzia.tema.TemaFinanzia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class PantallaInicioSesionTest {

    @get:Rule
    val reglaCompose = createComposeRule()

    private val contexto = InstrumentationRegistry.getInstrumentation().targetContext
    private var usuarioRecibido: Usuario? = null

    private fun mostrarPantalla(respuesta: ResultadoSesion = ResultadoSesion.Exitoso(USUARIO_DE_PRUEBA)) {
        reglaCompose.setContent {
            TemaFinanzia {
                PantallaInicioSesion(
                    alVolver = {},
                    alIniciarSesion = { usuarioRecibido = it },
                    alOlvidarContrasena = {},
                    alRegistrarse = {},
                    alCambiarTema = {},
                    alPedirAyuda = {},
                    viewModel = viewModel { InicioSesionViewModel(RepositorioSesionFalso(respuesta)) },
                )
            }
        }
    }

    private fun llenarYEntrar() {
        val campos = reglaCompose.onAllNodes(hasSetTextAction())
        campos[0].performTextInput("ana@correo.com")
        campos[1].performTextInput("secreta123")
        reglaCompose.onNodeWithText(contexto.getString(R.string.inicio_sesion_boton_entrar)).performClick()
    }

    @Test
    fun entrarConFormularioVacio_muestraErroresYNoIniciaSesion() {
        mostrarPantalla()

        reglaCompose.onNodeWithText(contexto.getString(R.string.inicio_sesion_boton_entrar)).performClick()

        reglaCompose.onNodeWithText(contexto.getString(R.string.error_correo_vacio)).assertIsDisplayed()
        reglaCompose.onNodeWithText(contexto.getString(R.string.error_contrasena_vacia)).assertIsDisplayed()
        reglaCompose.runOnIdle { assertNull(usuarioRecibido) }
    }

    @Test
    fun entrarConDatosValidos_avisaConElUsuario() {
        mostrarPantalla()

        llenarYEntrar()

        reglaCompose.runOnIdle { assertEquals(USUARIO_DE_PRUEBA, usuarioRecibido) }
    }

    @Test
    fun credencialesIncorrectas_muestranElError() {
        mostrarPantalla(ResultadoSesion.Fallido(ErrorSesion.CREDENCIALES_INCORRECTAS))

        llenarYEntrar()

        reglaCompose.onNodeWithText(contexto.getString(R.string.error_credenciales_incorrectas)).assertIsDisplayed()
        reglaCompose.runOnIdle { assertNull(usuarioRecibido) }
    }

    /** Repositorio de mentira: siempre responde lo mismo, sin internet. */
    private class RepositorioSesionFalso(private val respuesta: ResultadoSesion) : RepositorioSesion {
        override suspend fun iniciarSesion(correo: String, contrasena: String): ResultadoSesion = respuesta
    }

    private companion object {
        val USUARIO_DE_PRUEBA = Usuario(idUsuario = 1, nombre = "Ana", correo = "ana@correo.com")
    }
}
