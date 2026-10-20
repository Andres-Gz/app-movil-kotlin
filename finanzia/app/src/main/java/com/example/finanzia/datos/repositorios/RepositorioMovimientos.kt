package com.example.finanzia.datos.repositorios

import com.example.finanzia.datos.modelos.CategoriaGasto
import com.example.finanzia.datos.modelos.Frecuencia

/** Guarda, edita y elimina los ingresos y gastos del usuario con sesión. */
interface RepositorioMovimientos {
    suspend fun registrarIngreso(monto: Long, frecuencia: Frecuencia): ResultadoMovimiento
    suspend fun registrarGasto(monto: Long, categoria: CategoriaGasto): ResultadoMovimiento

    /** Cambia monto y descripción; la categoría solo si no es `null` (los ingresos no tienen). */
    suspend fun editarMovimiento(id: Long, monto: Long, descripcion: String, categoria: CategoriaGasto?): ResultadoMovimiento

    suspend fun eliminarMovimiento(id: Long): ResultadoMovimiento
}

/** Respuesta al guardar un ingreso o un gasto. */
sealed interface ResultadoMovimiento {
    data object Exitoso : ResultadoMovimiento
    data class Fallido(val error: ErrorDatos) : ResultadoMovimiento
}

/** Por qué no se pudo leer o guardar datos en Supabase. */
enum class ErrorDatos {
    SIN_SESION,
    SIN_CONEXION,
    DESCONOCIDO,
}
