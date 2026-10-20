package com.example.finanzia.datos.modelos

import com.example.finanzia.configuracion.Constantes

/**
 * Categorías del primer gasto. En la base se mandan con el nombre en minúsculas
 * (`gastos_fijos`, `comida`…), que es lo que acepta la función `registrar_gasto`.
 */
enum class CategoriaGasto {
    GASTOS_FIJOS,
    COMIDA,
    DIVERSION,
    TRANSPORTE,
    ;

    val clave: String get() = name.lowercase()

    companion object {
        /** La categoría que corresponde al nombre guardado en `categorias`, o `null` si es otra. */
        fun desdeNombre(nombre: String?): CategoriaGasto? = when (nombre?.lowercase()) {
            Constantes.BaseDatos.CATEGORIA_FIJOS -> GASTOS_FIJOS
            Constantes.BaseDatos.CATEGORIA_COMIDA -> COMIDA
            Constantes.BaseDatos.CATEGORIA_DIVERSION -> DIVERSION
            Constantes.BaseDatos.CATEGORIA_TRANSPORTE -> TRANSPORTE
            else -> null
        }
    }
}
