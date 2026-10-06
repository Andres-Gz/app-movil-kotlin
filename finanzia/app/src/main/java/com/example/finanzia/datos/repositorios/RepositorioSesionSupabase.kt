package com.example.finanzia.datos.repositorios

import com.example.finanzia.configuracion.Constantes
import com.example.finanzia.datos.ClienteSupabase
import com.example.finanzia.datos.modelos.Usuario
import com.example.finanzia.utilidades.registrarAdvertencia
import com.example.finanzia.utilidades.resumen
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthErrorCode
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

/** Inicia sesión con Supabase Auth y carga el perfil del usuario desde la tabla `usuarios`. */
class RepositorioSesionSupabase(
    private val cliente: SupabaseClient = ClienteSupabase.cliente,
) : RepositorioSesion {

    override suspend fun iniciarSesion(correo: String, contrasena: String): ResultadoSesion = try {
        cliente.auth.signInWith(Email) {
            email = correo
            password = contrasena
        }
        // La política RLS ya limita la consulta a la fila del usuario con sesión. No filtramos por correo
        // en la URL: supabase-kt 3.2.6 pone comillas a los valores con punto y PostgREST las toma literales.
        val correoSesion = cliente.auth.currentUserOrNull()?.email ?: correo
        val usuario = cliente.from(Constantes.BaseDatos.TABLA_USUARIOS)
            .select(Columns.type<Usuario>())
            .decodeList<Usuario>()
            .firstOrNull { it.correo.equals(correoSesion, ignoreCase = true) }
        if (usuario != null) ResultadoSesion.Exitoso(usuario) else ResultadoSesion.Fallido(ErrorSesion.PERFIL_NO_ENCONTRADO)
    } catch (error: AuthRestException) {
        val motivo = when (error.errorCode) {
            AuthErrorCode.InvalidCredentials -> ErrorSesion.CREDENCIALES_INCORRECTAS
            AuthErrorCode.EmailNotConfirmed -> ErrorSesion.CORREO_SIN_CONFIRMAR
            else -> ErrorSesion.DESCONOCIDO
        }
        if (motivo == ErrorSesion.DESCONOCIDO) registrarAdvertencia(Constantes.Registro.RECHAZO_AUTH, error.resumen())
        ResultadoSesion.Fallido(motivo)
    } catch (error: HttpRequestException) {
        registrarAdvertencia(Constantes.Registro.SIN_CONEXION, error.cause)
        ResultadoSesion.Fallido(ErrorSesion.SIN_CONEXION)
    } catch (error: RestException) {
        registrarAdvertencia(Constantes.Registro.ERROR_SERVIDOR, error.resumen())
        ResultadoSesion.Fallido(ErrorSesion.DESCONOCIDO)
    }
}
