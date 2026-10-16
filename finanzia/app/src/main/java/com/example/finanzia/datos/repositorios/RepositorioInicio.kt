package com.example.finanzia.datos.repositorios

import com.example.finanzia.datos.modelos.ResumenInicio

/** Lee el resumen que muestra la pantalla de inicio. */
interface RepositorioInicio {
    suspend fun obtenerResumen(): ResultadoInicio
}

/** Respuesta al pedir el resumen del inicio. */
sealed interface ResultadoInicio {
    data class Exitoso(val resumen: ResumenInicio) : ResultadoInicio
    data class Fallido(val error: ErrorDatos) : ResultadoInicio
}
