package com.example.finanzia.tema

import androidx.compose.ui.graphics.Color

/**
 * Paleta de la app, tomada del diseño de inicio de sesión.
 * Es el único lugar del código donde se escriben valores de color.
 */
object Colores {
    // Marca
    val verdeMarca = Color(0xFF006D4A)
    val verdeAcento = Color(0xFF4EDCA2)
    val azulMarca = Color(0xFF0A47B8)
    val azulBoton = Color(0xFF2563EB)

    // Fondos y superficies
    val fondo = Color(0xFFF8F9FE)
    val superficie = Color(0xFFFFFFFF)
    val divisor = Color(0xFFE7EDF9)

    // Textos e iconos
    val textoPrincipal = Color(0xFF171E2B)
    val textoSecundario = Color(0xFF52535C)
    val textoTenue = Color(0xFF76767C)
    val textoSobreColor = Color(0xFFFFFFFF)

    // Estados (el diseño no muestra errores: es el rojo estándar de Material 3)
    val error = Color(0xFFBA1A1A)

    // Sombras y transparencia
    val sombra = Color(0xFF0F172A)
    val sombraBoton = azulBoton
    val transparente = Color.Transparent

    // Degradados
    val degradadoBoton = listOf(verdeMarca, azulBoton)
}
