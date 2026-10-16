package com.example.finanzia.utilidades

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/** Cada cuántas cifras va un separador de miles. */
private const val CIFRAS_POR_GRUPO = 3

/**
 * Muestra un número escrito solo con cifras con separador de miles (18500 → 18,500),
 * sin cambiar lo que se guarda. El cursor se mueve bien aunque haya separadores.
 */
class SeparadorMiles(private val separador: Char) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val cifras = text.text
        val conSeparador = buildString {
            cifras.forEachIndexed { indice, cifra ->
                if (indice > 0 && (cifras.length - indice) % CIFRAS_POR_GRUPO == 0) append(separador)
                append(cifra)
            }
        }
        val mapeo = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = offset + separadoresAntesDe(offset, cifras.length)

            override fun transformedToOriginal(offset: Int): Int {
                var original = 0
                while (original < cifras.length && originalToTransformed(original + 1) <= offset) original++
                return original
            }
        }
        return TransformedText(AnnotatedString(conSeparador), mapeo)
    }

    /** Cuántos separadores quedan antes de la cifra número [posicion]. */
    private fun separadoresAntesDe(posicion: Int, largo: Int): Int =
        (1..posicion).count { it < largo && (largo - it) % CIFRAS_POR_GRUPO == 0 }
}
