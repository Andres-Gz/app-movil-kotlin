package com.example.finanzia.datos.modelos

import com.example.finanzia.configuracion.Constantes

/**
 * Categorías de una meta. En la base se mandan con el nombre en minúsculas (`viajes`, `emergencia`…),
 * que es lo que acepta la función `crear_meta`; ella guarda el nombre que se muestra y su emoji.
 */
enum class CategoriaMeta {
    VIAJES,
    EMERGENCIA,
    TECNOLOGIA,
    VEHICULO,
    VIVIENDA,
    EDUCACION,
    ;

    val clave: String get() = name.lowercase()

    companion object {
        /** La categoría que corresponde al nombre guardado en `metas.categoria`, o `null` si es otra. */
        fun desdeNombre(nombre: String?): CategoriaMeta? = when (nombre?.lowercase()) {
            Constantes.BaseDatos.CATEGORIA_META_VIAJES -> VIAJES
            Constantes.BaseDatos.CATEGORIA_META_EMERGENCIA -> EMERGENCIA
            Constantes.BaseDatos.CATEGORIA_META_TECNOLOGIA -> TECNOLOGIA
            Constantes.BaseDatos.CATEGORIA_META_VEHICULO -> VEHICULO
            Constantes.BaseDatos.CATEGORIA_META_VIVIENDA -> VIVIENDA
            Constantes.BaseDatos.CATEGORIA_META_EDUCACION -> EDUCACION
            else -> null
        }
    }
}

/** Lo que se manda a `crear_meta` (supabase/metas.sql) para guardar una meta nueva. */
data class MetaNueva(
    val nombre: String,
    val categoria: CategoriaMeta,
    val objetivo: Long,
    /** Último día del mes elegido, en formato `yyyy-MM-dd`. */
    val fechaLimite: String,
    val frecuencia: Frecuencia,
    /** Cuánto aportar en cada fecha de la [frecuencia] para llegar a tiempo. */
    val aporte: Long,
)
