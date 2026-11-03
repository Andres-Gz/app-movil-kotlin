package com.example.finanzia.datos.facturas

/** Un pedazo de texto que leyó el OCR y dónde quedó en la foto, en píxeles. */
data class TrozoLeido(val texto: String, val izquierda: Int, val arriba: Int, val abajo: Int) {
    val centro: Int get() = (arriba + abajo) / MITAD
}

private const val MITAD = 2

/**
 * Arma el texto de la factura renglón por renglón, como se ve en el papel. El OCR lee por bloques, y en
 * una factura el nombre de cada valor y el valor suelen quedar en bloques distintos ("TOTAL" en uno y
 * "45.900" en otro); juntando los trozos que están a la misma altura, de izquierda a derecha, vuelven a
 * quedar en la misma línea: "TOTAL 45.900". Así la IA sabe a qué corresponde cada valor.
 */
fun unirRenglones(trozos: List<TrozoLeido>): String {
    val renglones = mutableListOf<MutableList<TrozoLeido>>()
    for (trozo in trozos.sortedBy { it.centro }) {
        val renglon = renglones.lastOrNull()
        // Va en el mismo renglón si su centro no queda más abajo que el borde de abajo del primer trozo.
        if (renglon != null && trozo.centro <= renglon.first().abajo) {
            renglon += trozo
        } else {
            renglones += mutableListOf(trozo)
        }
    }
    return renglones.joinToString("\n") { renglon -> renglon.sortedBy { it.izquierda }.joinToString(" ") { it.texto } }
}
