package com.example.finanzia.tema

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/**
 * Familia tipográfica de toda la app.
 * Para usar otra fuente: copiar sus .ttf en res/font y cambiar solo esta línea.
 */
val fuenteApp: FontFamily = FontFamily.Default

/**
 * Los seis tamaños de letra de la app, de menor a mayor. Cada tipo de texto usa siempre
 * el mismo tamaño en todas las pantallas; no se agregan otros.
 */
object TamanoLetra {
    val pequeno = 11.sp
    val reducido = 12.sp
    val mediano = 13.sp
    val normal = 14.sp
    val grande = 20.sp
    val enorme = 24.sp
}

/** Alto de línea de cada tamaño: va siempre con su tamaño. */
private object Interlineado {
    val pequeno = 16.sp
    val reducido = 16.sp
    val mediano = 20.sp
    val normal = 20.sp
    val grande = 28.sp
    val enorme = 32.sp
}

/** Separación extra (o menos separación) entre letras. */
private object Espaciado {
    val logoCompacto = (-0.025).em
    val etiqueta = 0.05.em
    val etiquetaExterna = 0.07.em
    val contrasenaOculta = 0.27.em
}

/** El texto queda centrado en el alto de su línea, igual que en los diseños (que son web). */
private val alineacionLinea = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.None,
)

private fun estilo(
    tamano: TextUnit,
    interlineado: TextUnit,
    peso: FontWeight,
    espaciado: TextUnit = TextUnit.Unspecified,
) = TextStyle(
    fontFamily = fuenteApp,
    fontSize = tamano,
    fontWeight = peso,
    lineHeight = interlineado,
    letterSpacing = espaciado,
    lineHeightStyle = alineacionLinea,
)

private fun enorme(peso: FontWeight, espaciado: TextUnit) =
    estilo(TamanoLetra.enorme, Interlineado.enorme, peso, espaciado)
private fun grande(peso: FontWeight) = estilo(TamanoLetra.grande, Interlineado.grande, peso)
private fun normal(peso: FontWeight) = estilo(TamanoLetra.normal, Interlineado.normal, peso)
private fun mediano(peso: FontWeight) = estilo(TamanoLetra.mediano, Interlineado.mediano, peso)
private fun reducido(peso: FontWeight) = estilo(TamanoLetra.reducido, Interlineado.reducido, peso)
private fun pequeno(peso: FontWeight, espaciado: TextUnit = TextUnit.Unspecified) =
    estilo(TamanoLetra.pequeno, Interlineado.pequeno, peso, espaciado)

/**
 * Estilos de texto por función. Un mismo tipo de texto usa el mismo estilo en todas las pantallas
 * (por ejemplo, el pie "¿No tienes cuenta?" y "¿Ya tienes una cuenta?" usan [cuerpo] y [cuerpoDestacado]).
 */
object EstilosTexto {
    // 24: logo de texto (el inicio de sesión usa la imagen del logo)
    val logoCompacto = enorme(FontWeight.ExtraBold, Espaciado.logoCompacto)

    // 24: montos de dinero que se escriben en grande
    val monto = enorme(FontWeight.ExtraBold, TextUnit.Unspecified)

    // 20: título de la pantalla
    val titulo = grande(FontWeight.Bold)
    /** Títulos dentro de la pantalla, más suaves que el principal (p. ej. "Metas Activas" y el nombre de cada meta). */
    val tituloSeccion = grande(FontWeight.SemiBold)

    // 14: botones, lo que se escribe en los campos y títulos de avisos
    val boton = normal(FontWeight.Bold)
    val botonSecundario = normal(FontWeight.Medium)
    val tituloAviso = normal(FontWeight.Bold)
    val valorCampo = normal(FontWeight.Medium)
    val ejemploCampo = normal(FontWeight.Normal)
    /** Lo que se dice en el chat con Finanzia IA; las cifras que resalta la IA van en [mensajeDestacado]. */
    val mensaje = normal(FontWeight.Normal)
    val mensajeDestacado = normal(FontWeight.Bold)
    val contrasenaOculta = valorCampo.copy(letterSpacing = Espaciado.contrasenaOculta)

    // 13: descripción bajo el título y pie de la pantalla
    val cuerpo = mediano(FontWeight.Normal)
    val cuerpoDestacado = mediano(FontWeight.Bold)

    // 12: eslogan, enlaces dentro del formulario y avisos de error
    val nota = reducido(FontWeight.Medium)
    val notaFuerte = reducido(FontWeight.SemiBold)

    // 11: etiquetas de los campos, mensajes bajo los campos, requisitos, términos y paso
    val etiqueta = pequeno(FontWeight.Medium, Espaciado.etiqueta)
    val etiquetaExterna = pequeno(FontWeight.Bold, Espaciado.etiquetaExterna)
    val ayuda = pequeno(FontWeight.Normal)
    val ayudaDestacada = pequeno(FontWeight.Medium)
    val ayudaFuerte = pequeno(FontWeight.SemiBold)
}

/** Roles tipográficos de Material apuntando a nuestros estilos, para sus componentes internos. */
internal val tipografiaMaterial = Typography(
    headlineMedium = EstilosTexto.titulo,
    bodyLarge = EstilosTexto.cuerpo,
    bodyMedium = EstilosTexto.cuerpo,
    bodySmall = EstilosTexto.nota,
    labelLarge = EstilosTexto.boton,
    labelMedium = EstilosTexto.etiqueta,
    labelSmall = EstilosTexto.ayuda,
)
