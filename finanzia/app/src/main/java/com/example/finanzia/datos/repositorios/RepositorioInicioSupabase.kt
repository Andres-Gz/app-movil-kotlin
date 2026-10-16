package com.example.finanzia.datos.repositorios

import com.example.finanzia.configuracion.Constantes
import com.example.finanzia.datos.ClienteSupabase
import com.example.finanzia.datos.modelos.ResumenInicio
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

/**
 * Pide el resumen con la función `resumen_inicio` de Supabase (supabase/inicio.sql), que junta en una
 * sola llamada el nombre, los totales, los últimos movimientos y la meta del usuario de la sesión.
 */
class RepositorioInicioSupabase(
    private val cliente: SupabaseClient = ClienteSupabase.cliente,
) : RepositorioInicio {

    override suspend fun obtenerResumen(): ResultadoInicio =
        when (
            val llamada = llamarConSesion(cliente) {
                cliente.postgrest.rpc(Constantes.BaseDatos.FUNCION_RESUMEN_INICIO).decodeAs<ResumenInicio>()
            }
        ) {
            is Llamada.Exitosa -> ResultadoInicio.Exitoso(llamada.valor)
            is Llamada.Fallida -> ResultadoInicio.Fallido(llamada.error)
        }
}
