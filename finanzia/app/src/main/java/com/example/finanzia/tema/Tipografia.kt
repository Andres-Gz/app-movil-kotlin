package com.example.finanzia.tema

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Familia tipográfica de toda la app.
 * Para usar otra fuente: copiar sus .ttf en res/font y cambiar solo esta línea.
 */
val fuenteApp: FontFamily = FontFamily.Default

/** Los tres únicos tamaños de letra de la app. */
object TamanoLetra {
    val pequeno = 13.sp
    val mediano = 16.sp
    val grande = 24.sp
}

/** Interlineado como proporción del tamaño de letra. */
private const val FACTOR_INTERLINEADO = 1.4f

private fun estilo(tamano: TextUnit, peso: FontWeight) = TextStyle(
    fontFamily = fuenteApp,
    fontSize = tamano,
    fontWeight = peso,
    lineHeight = tamano * FACTOR_INTERLINEADO,
)

/** Estilos de texto: cada uno combina la fuente, uno de los 3 tamaños y un peso. */
object EstilosTexto {
    val logo = estilo(TamanoLetra.grande, FontWeight.Black)
    val titulo = estilo(TamanoLetra.grande, FontWeight.Bold)
    val tituloBarra = estilo(TamanoLetra.mediano, FontWeight.SemiBold)
    val cuerpo = estilo(TamanoLetra.mediano, FontWeight.Normal)
    val cuerpoDestacado = estilo(TamanoLetra.mediano, FontWeight.Bold)
    val etiqueta = estilo(TamanoLetra.pequeno, FontWeight.Medium)
    val etiquetaDestacada = estilo(TamanoLetra.pequeno, FontWeight.SemiBold)
    val nota = estilo(TamanoLetra.pequeno, FontWeight.Normal)
}

/** Roles tipográficos de Material apuntando a nuestros estilos, para sus componentes internos. */
internal val tipografiaMaterial = Typography(
    headlineMedium = EstilosTexto.titulo,
    titleMedium = EstilosTexto.tituloBarra,
    bodyLarge = EstilosTexto.cuerpo,
    bodyMedium = EstilosTexto.cuerpo,
    bodySmall = EstilosTexto.nota,
    labelLarge = EstilosTexto.cuerpoDestacado,
    labelMedium = EstilosTexto.etiqueta,
    labelSmall = EstilosTexto.nota,
)
