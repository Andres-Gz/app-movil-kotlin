package com.example.finanzia.datos.facturas

/** Lee el texto de la foto de una factura (OCR), para que la IA registre el gasto. */
interface LectorFacturas {
    /** [imagen] es la dirección de la foto (`content://…`) que devuelve la cámara o la galería. */
    suspend fun leer(imagen: String): LecturaFactura
}

/** Lo que devuelve [LectorFacturas.leer]. */
sealed interface LecturaFactura {
    /** El texto de la factura, renglón por renglón. */
    data class Leida(val texto: String) : LecturaFactura

    /** La foto se abrió, pero no tiene texto que se pueda leer (borrosa, oscura o no es una factura). */
    data object SinTexto : LecturaFactura

    /** No se pudo abrir la foto o falló el lector. */
    data object Fallida : LecturaFactura
}
