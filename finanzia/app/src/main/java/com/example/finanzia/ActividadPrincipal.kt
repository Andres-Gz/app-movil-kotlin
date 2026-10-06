package com.example.finanzia

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import com.example.finanzia.pantallas.iniciosesion.PantallaInicioSesion
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.TemaFinanzia

class ActividadPrincipal : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // La app solo tiene tema claro: iconos oscuros en las barras del sistema.
        val barrasClaras = SystemBarStyle.light(Colores.transparente.toArgb(), Colores.transparente.toArgb())
        enableEdgeToEdge(statusBarStyle = barrasClaras, navigationBarStyle = barrasClaras)
        setContent {
            TemaFinanzia {
                PantallaInicioSesion(
                    alVolver = onBackPressedDispatcher::onBackPressed,
                    // Temporal: aún no existe la pantalla principal, así que solo saludamos.
                    alIniciarSesion = { usuario ->
                        val saludo = getString(R.string.inicio_sesion_bienvenida, usuario.nombre)
                        Toast.makeText(this@ActividadPrincipal, saludo, Toast.LENGTH_LONG).show()
                    },
                    // Aún no existen estas pantallas.
                    alOlvidarContrasena = {},
                    alRegistrarse = {},
                    alCambiarTema = {},
                    alPedirAyuda = {},
                )
            }
        }
    }
}
