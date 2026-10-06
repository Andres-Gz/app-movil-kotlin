package com.example.finanzia.datos

import com.example.finanzia.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
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
            install(Auth)
            install(Postgrest)
        }
    }
}
