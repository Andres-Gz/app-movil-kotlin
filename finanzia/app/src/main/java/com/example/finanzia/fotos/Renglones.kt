package com.example.finanzia.fotos

import com.example.finanzia.configuracion.Constantes
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

/** Una línea que leyó el OCR y dónde está en la foto: su centro, su tamaño y su inclinación en grados. */
data class LineaLeida(
    val texto: String,
    val centroX: Double,
    val centroY: Double,
    val ancho: Double,
    val alto: Double,
    val angulo: Double,
)

/**
 * Vuelve a armar los renglones de la foto. El OCR entrega las columnas por separado: en una factura
 * devuelve "SUBTOTAL, IVA, TOTAL" y aparte "49.500, 9.400, 58.900", y así no se sabe qué valor es de cuál.
 * Primero se endereza la foto, porque casi nunca sale derecha: con dos grados, el precio de un recibo ya
 * queda un renglón más abajo que su nombre.
 */
fun armarRenglones(lineas: List<LineaLeida>): String {
    if (lineas.isEmpty()) return ""
    val giro = Math.toRadians(inclinacion(lineas))
    val renglones = mutableListOf<MutableList<Enderezada>>()
    lineas.map { Enderezada(it, giro) }.sortedBy { it.y }.forEach { linea ->
        val renglon = renglones.lastOrNull()
        if (renglon != null && linea.vaEnElRenglonDe(renglon.first())) renglon += linea else renglones += mutableListOf(linea)
    }
    return renglones.joinToString(Constantes.Fotos.SEPARADOR_RENGLONES) { renglon ->
        renglon.sortedBy { it.x }.joinToString(Constantes.Fotos.SEPARADOR_COLUMNAS) { it.linea.texto }
    }
}

/**
 * La inclinación de la foto: la mediana de las líneas, donde cada una pesa según su ancho. Las cortas, como
 * "IVA" o "9.400", suelen llegar con 0 grados aunque la foto esté torcida; las largas sí la traen bien.
 */
private fun inclinacion(lineas: List<LineaLeida>): Double {
    val mitad = lineas.sumOf { it.ancho } / 2
    var acumulado = 0.0
    return lineas.sortedBy { it.angulo }.first { linea ->
        acumulado += linea.ancho
        acumulado >= mitad
    }.angulo
}

private class Enderezada(val linea: LineaLeida, giro: Double) {
    val x = linea.centroX * cos(giro) + linea.centroY * sin(giro)
    val y = linea.centroY * cos(giro) - linea.centroX * sin(giro)

    /** Dos líneas van juntas si sus centros están a menos de medio alto de distancia. */
    fun vaEnElRenglonDe(otra: Enderezada): Boolean =
        abs(y - otra.y) <= max(linea.alto, otra.linea.alto) * Constantes.Fotos.TOLERANCIA_RENGLON
}
