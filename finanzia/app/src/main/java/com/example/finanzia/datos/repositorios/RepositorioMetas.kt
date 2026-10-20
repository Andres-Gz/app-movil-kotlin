package com.example.finanzia.datos.repositorios

import com.example.finanzia.datos.modelos.Frecuencia
import com.example.finanzia.datos.modelos.MetaNueva
import com.example.finanzia.datos.modelos.ResumenMetas

/** Lee y guarda las metas del usuario y sus aportes. */
interface RepositorioMetas {
    suspend fun obtenerResumen(): ResultadoMetas
    suspend fun crearMeta(meta: MetaNueva): ResultadoMeta
    suspend fun editarMeta(id: Long, meta: MetaNueva): ResultadoMeta
    suspend fun aportar(id: Long, monto: Long): ResultadoMeta
    suspend fun ajustarRegla(id: Long, aporte: Long, frecuencia: Frecuencia): ResultadoMeta

    /** La quita de la lista (queda inactiva); sus aportes no se borran. */
    suspend fun eliminarMeta(id: Long): ResultadoMeta
}

/** Respuesta al pedir el resumen de metas. */
sealed interface ResultadoMetas {
    data class Exitoso(val resumen: ResumenMetas) : ResultadoMetas
    data class Fallido(val error: ErrorDatos) : ResultadoMetas
}

/** Respuesta al guardar una meta o un aporte. */
sealed interface ResultadoMeta {
    data object Exitoso : ResultadoMeta
    data class Fallido(val error: ErrorDatos) : ResultadoMeta
}
