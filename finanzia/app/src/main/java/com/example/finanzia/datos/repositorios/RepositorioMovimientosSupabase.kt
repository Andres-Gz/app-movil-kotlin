package com.example.finanzia.datos.repositorios

import com.example.finanzia.configuracion.Constantes
import com.example.finanzia.datos.ClienteSupabase
import com.example.finanzia.datos.modelos.CategoriaGasto
import com.example.finanzia.datos.modelos.Frecuencia
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Guarda ingresos y gastos con las funciones `registrar_ingreso` y `registrar_gasto`, y los edita o
 * elimina con `editar_movimiento` y `eliminar_movimiento` (ver supabase/). Todas toman al usuario de la sesión.
 */
class RepositorioMovimientosSupabase(
    private val cliente: SupabaseClient = ClienteSupabase.cliente,
) : RepositorioMovimientos {

    override suspend fun registrarIngreso(monto: Long, frecuencia: Frecuencia): ResultadoMovimiento =
        llamar(
            Constantes.BaseDatos.FUNCION_REGISTRAR_INGRESO,
            buildJsonObject {
                put(Constantes.BaseDatos.PARAMETRO_MONTO, monto)
                put(Constantes.BaseDatos.PARAMETRO_FRECUENCIA, frecuencia.clave)
            },
        )

    override suspend fun registrarGasto(monto: Long, categoria: CategoriaGasto): ResultadoMovimiento =
        llamar(
            Constantes.BaseDatos.FUNCION_REGISTRAR_GASTO,
            buildJsonObject {
                put(Constantes.BaseDatos.PARAMETRO_MONTO, monto)
                put(Constantes.BaseDatos.PARAMETRO_CATEGORIA, categoria.clave)
            },
        )

    override suspend fun editarMovimiento(
        id: Long,
        monto: Long,
        descripcion: String,
        categoria: CategoriaGasto?,
    ): ResultadoMovimiento =
        llamar(
            Constantes.BaseDatos.FUNCION_EDITAR_MOVIMIENTO,
            buildJsonObject {
                put(Constantes.BaseDatos.PARAMETRO_ID, id)
                put(Constantes.BaseDatos.PARAMETRO_MONTO, monto)
                put(Constantes.BaseDatos.PARAMETRO_DESCRIPCION, descripcion)
                put(Constantes.BaseDatos.PARAMETRO_CATEGORIA, categoria?.clave)
            },
        )

    override suspend fun eliminarMovimiento(id: Long): ResultadoMovimiento =
        llamar(
            Constantes.BaseDatos.FUNCION_ELIMINAR_MOVIMIENTO,
            buildJsonObject { put(Constantes.BaseDatos.PARAMETRO_ID, id) },
        )

    private suspend fun llamar(funcion: String, parametros: JsonObject): ResultadoMovimiento =
        when (val llamada = llamarConSesion(cliente) { cliente.postgrest.rpc(funcion, parametros) }) {
            is Llamada.Exitosa -> ResultadoMovimiento.Exitoso
            is Llamada.Fallida -> ResultadoMovimiento.Fallido(llamada.error)
        }
}
