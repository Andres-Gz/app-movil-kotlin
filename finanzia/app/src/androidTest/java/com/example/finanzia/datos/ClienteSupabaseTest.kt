package com.example.finanzia.datos

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.postgrest
import org.junit.Test

/**
 * El cliente debe traer instalado cada plugin que usan los repositorios. Si falta uno, la app se cierra
 * al usarlo (pasó con Functions, que usa el asistente) y las pruebas con repositorios falsos no lo ven.
 * Va en el emulador porque Auth necesita Android para guardar la sesión.
 */
class ClienteSupabaseTest {

    @Test
    fun traeLosPluginsQueUsaLaApp() {
        val cliente = ClienteSupabase.cliente

        // Cada acceso lanza IllegalStateException si el plugin no está instalado.
        cliente.auth
        cliente.postgrest
        cliente.functions
    }
}
