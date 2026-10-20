package com.example.finanzia.datos.repositorios

import com.example.finanzia.configuracion.Constantes
import com.example.finanzia.datos.ClienteSupabase
import com.example.finanzia.datos.modelos.Frecuencia
import com.example.finanzia.datos.modelos.MetaNueva
import com.example.finanzia.datos.modelos.ResumenMetas
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Lee y guarda las metas con las funciones de supabase/metas.sql: `resumen_metas`, `crear_meta`,
 * `editar_meta`, `aportar_meta`, `ajustar_regla_meta` y `eliminar_meta`. Todas toman al usuario de la sesión.
 */
class RepositorioMetasSupabase(
    private val cliente: SupabaseClient = ClienteSupabase.cliente,
) : RepositorioMetas {

    override suspend fun obtenerResumen(): ResultadoMetas =
        when (
            val llamada = llamarConSesion(cliente) {
                cliente.postgrest.rpc(Constantes.BaseDatos.FUNCION_RESUMEN_METAS).decodeAs<ResumenMetas>()
            }
        ) {
            is Llamada.Exitosa -> ResultadoMetas.Exitoso(llamada.valor)
            is Llamada.Fallida -> ResultadoMetas.Fallido(llamada.error)
        }

    override suspend fun crearMeta(meta: MetaNueva): ResultadoMeta =
        llamar(Constantes.BaseDatos.FUNCION_CREAR_META, buildJsonObject { ponerDatos(meta) })

    override suspend fun editarMeta(id: Long, meta: MetaNueva): ResultadoMeta =
        llamar(
            Constantes.BaseDatos.FUNCION_EDITAR_META,
            buildJsonObject {
                put(Constantes.BaseDatos.PARAMETRO_ID, id)
                ponerDatos(meta)
            },
        )

    override suspend fun aportar(id: Long, monto: Long): ResultadoMeta =
        llamar(
            Constantes.BaseDatos.FUNCION_APORTAR_META,
            buildJsonObject {
                put(Constantes.BaseDatos.PARAMETRO_ID, id)
                put(Constantes.BaseDatos.PARAMETRO_MONTO, monto)
            },
        )

    override suspend fun ajustarRegla(id: Long, aporte: Long, frecuencia: Frecuencia): ResultadoMeta =
        llamar(
            Constantes.BaseDatos.FUNCION_AJUSTAR_REGLA_META,
            buildJsonObject {
                put(Constantes.BaseDatos.PARAMETRO_ID, id)
                put(Constantes.BaseDatos.PARAMETRO_MONTO_APORTE, aporte)
                put(Constantes.BaseDatos.PARAMETRO_FRECUENCIA, frecuencia.clave)
            },
        )

    override suspend fun eliminarMeta(id: Long): ResultadoMeta =
        llamar(Constantes.BaseDatos.FUNCION_ELIMINAR_META, buildJsonObject { put(Constantes.BaseDatos.PARAMETRO_ID, id) })

    /** Los parámetros que comparten `crear_meta` y `editar_meta`. */
    private fun JsonObjectBuilder.ponerDatos(meta: MetaNueva) {
        put(Constantes.BaseDatos.PARAMETRO_NOMBRE, meta.nombre)
        put(Constantes.BaseDatos.PARAMETRO_CATEGORIA, meta.categoria.clave)
        put(Constantes.BaseDatos.PARAMETRO_MONTO, meta.objetivo)
        put(Constantes.BaseDatos.PARAMETRO_FECHA_LIMITE, meta.fechaLimite)
        put(Constantes.BaseDatos.PARAMETRO_FRECUENCIA, meta.frecuencia.clave)
        put(Constantes.BaseDatos.PARAMETRO_MONTO_APORTE, meta.aporte)
    }

    private suspend fun llamar(funcion: String, parametros: JsonObject): ResultadoMeta =
        when (val llamada = llamarConSesion(cliente) { cliente.postgrest.rpc(funcion, parametros) }) {
            is Llamada.Exitosa -> ResultadoMeta.Exitoso
            is Llamada.Fallida -> ResultadoMeta.Fallido(llamada.error)
        }
}
