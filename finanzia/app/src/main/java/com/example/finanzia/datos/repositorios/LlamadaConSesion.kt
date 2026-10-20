package com.example.finanzia.datos.repositorios

import com.example.finanzia.configuracion.Constantes
import com.example.finanzia.utilidades.registrarAdvertencia
import com.example.finanzia.utilidades.resumen
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException

/** Lo que devuelve [llamarConSesion]: el valor pedido o por qué no se pudo. */
sealed interface Llamada<out T> {
    data class Exitosa<T>(val valor: T) : Llamada<T>
    data class Fallida(val error: ErrorDatos) : Llamada<Nothing>
}

/**
 * Hace una llamada a Supabase que necesita sesión iniciada y traduce los errores a [ErrorDatos].
 * La usan todos los repositorios que leen o guardan datos del usuario.
 */
internal suspend fun <T> llamarConSesion(cliente: SupabaseClient, accion: suspend () -> T): Llamada<T> {
    if (cliente.auth.currentSessionOrNull() == null) return Llamada.Fallida(ErrorDatos.SIN_SESION)
    return try {
        Llamada.Exitosa(accion())
    } catch (error: HttpRequestException) {
        registrarAdvertencia(Constantes.Registro.SIN_CONEXION, error.cause)
        Llamada.Fallida(ErrorDatos.SIN_CONEXION)
    } catch (error: RestException) {
        registrarAdvertencia(Constantes.Registro.ERROR_SERVIDOR, error.resumen())
        Llamada.Fallida(ErrorDatos.DESCONOCIDO)
    }
}
