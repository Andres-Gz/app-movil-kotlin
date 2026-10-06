package com.example.finanzia.pantallas.iniciosesion

import com.example.finanzia.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InicioSesionViewModelTest {

    private val viewModel = InicioSesionViewModel()

    @Test
    fun alPulsarEntrarConFormularioVacio_muestraErroresYNoContinua() {
        assertFalse(viewModel.alPulsarEntrar())
        assertEquals(R.string.error_correo_vacio, viewModel.estado.errorCorreo)
        assertEquals(R.string.error_contrasena_vacia, viewModel.estado.errorContrasena)
    }

    @Test
    fun alPulsarEntrarConDatosValidos_continuaSinErrores() {
        viewModel.alCambiarCorreo("ana@correo.com")
        viewModel.alCambiarContrasena("secreta123")

        assertTrue(viewModel.alPulsarEntrar())
        assertNull(viewModel.estado.errorCorreo)
        assertNull(viewModel.estado.errorContrasena)
    }

    @Test
    fun alCambiarCorreo_borraElErrorDelCorreo() {
        viewModel.alPulsarEntrar()

        viewModel.alCambiarCorreo("a")

        assertNull(viewModel.estado.errorCorreo)
        assertEquals(R.string.error_contrasena_vacia, viewModel.estado.errorContrasena)
    }

    @Test
    fun alAlternarVisibilidad_muestraYOcultaLaContrasena() {
        viewModel.alAlternarVisibilidadContrasena()
        assertTrue(viewModel.estado.contrasenaVisible)

        viewModel.alAlternarVisibilidadContrasena()
        assertFalse(viewModel.estado.contrasenaVisible)
    }

    @Test
    fun alCambiarRecordarme_guardaLaPreferencia() {
        viewModel.alCambiarRecordarme(true)

        assertTrue(viewModel.estado.recordarme)
    }
}
