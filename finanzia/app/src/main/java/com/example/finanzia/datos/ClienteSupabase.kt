package com.example.finanzia.datos

import com.example.finanzia.BuildConfig
import com.example.finanzia.configuracion.Constantes
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.postgrest.Postgrest

/**
 * Conexión única con Supabase para toda la app (equivale al DataSource de Spring).
 * La URL y la clave salen de local.properties a través de BuildConfig.
 */
object ClienteSupabase {
    val cliente: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_CLAVE,
        ) {
            install(Auth) {
                // Los enlaces que manda Supabase por correo vuelven a la app por aquí (ver AndroidManifest).
                scheme = Constantes.Enlaces.ESQUEMA_APP
                host = Constantes.Enlaces.HOST_RECUPERACION
            }
            install(Postgrest)
            // Para llamar a las Edge Functions (el asistente).
            install(Functions)
        }
    }
}
