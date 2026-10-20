package com.example.finanzia.pantallas.bienvenida

import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.platform.app.InstrumentationRegistry
import com.example.finanzia.R
import com.example.finanzia.tema.TemaFinanzia
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PantallaBienvenidaTest {

    @get:Rule
    val reglaCompose = createComposeRule()

    private val contexto = InstrumentationRegistry.getInstrumentation().targetContext
    private var comenzo = false
    private var personalizo = false

    private fun mostrarPantalla() {
        reglaCompose.setContent {
            TemaFinanzia {
                PantallaBienvenida(
                    correo = CORREO,
                    alComenzar = { comenzo = true },
                    alPersonalizarMetas = { personalizo = true },
                )
            }
        }
    }

    private fun pulsar(@StringRes texto: Int) {
        reglaCompose.onNodeWithText(contexto.getString(texto)).performScrollTo().performClick()
    }

    @Test
    fun muestraElCorreoDeLaCuentaYLosTresLogros() {
        mostrarPantalla()

        reglaCompose.onNodeWithText(CORREO, substring = true).performScrollTo().assertIsDisplayed()
        reglaCompose.onNodeWithText(contexto.getString(R.string.bienvenida_progreso, 3, 3)).assertIsDisplayed()
    }

    @Test
    fun comenzarAhora_avisa() {
        mostrarPantalla()

        pulsar(R.string.bienvenida_boton_comenzar)

        reglaCompose.runOnIdle { assertTrue(comenzo) }
    }

    @Test
    fun personalizarMetas_avisa() {
        mostrarPantalla()

        pulsar(R.string.bienvenida_boton_personalizar)

        reglaCompose.runOnIdle { assertTrue(personalizo) }
    }

    private companion object {
        const val CORREO = "ana@correo.com"
    }
}
