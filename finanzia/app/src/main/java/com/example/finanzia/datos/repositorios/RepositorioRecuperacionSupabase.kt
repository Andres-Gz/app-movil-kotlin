package com.example.finanzia.datos.repositorios

import com.example.finanzia.configuracion.Constantes
import com.example.finanzia.datos.ClienteSupabase
import com.example.finanzia.utilidades.registrarAdvertencia
import com.example.finanzia.utilidades.resumen
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthErrorCode
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.exception.AuthSessionMissingException
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException

/**
 * Recupera la contraseña con Supabase Auth: manda el enlace al correo y, cuando la app se abre
 * desde ese enlace (ver ActividadPrincipal), cambia la contraseña con la sesión temporal.
 */
class RepositorioRecuperacionSupabase(
    private val cliente: SupabaseClient = ClienteSupabase.cliente,
) : RepositorioRecuperacion {

    override suspend fun enviarEnlace(correo: String): ResultadoRecuperacion = intentar {
        // Supabase no avisa si el correo no tiene cuenta: así nadie puede averiguar qué correos están registrados.
        cliente.auth.resetPasswordForEmail(correo, redirectUrl = Constantes.Enlaces.URL_RECUPERACION)
    }

    override suspend fun cambiarContrasena(contrasena: String): ResultadoRecuperacion = intentar {
        cliente.auth.updateUser { password = contrasena }
        // Se cierra la sesión temporal para que el usuario entre con su contraseña nueva.
        cliente.auth.signOut()
    }

    private suspend fun intentar(accion: suspend () -> Unit): ResultadoRecuperacion = try {
        accion()
        ResultadoRecuperacion.Exitoso
    } catch (error: AuthSessionMissingException) {
        ResultadoRecuperacion.Fallido(ErrorRecuperacion.SIN_SESION)
    } catch (error: AuthRestException) {
        val motivo = when (error.errorCode) {
            AuthErrorCode.OverEmailSendRateLimit, AuthErrorCode.OverRequestRateLimit -> ErrorRecuperacion.DEMASIADOS_INTENTOS
            AuthErrorCode.EmailAddressInvalid, AuthErrorCode.EmailAddressNotAuthorized -> ErrorRecuperacion.CORREO_RECHAZADO
            AuthErrorCode.SessionNotFound -> ErrorRecuperacion.SIN_SESION
            AuthErrorCode.SamePassword -> ErrorRecuperacion.MISMA_CONTRASENA
            AuthErrorCode.WeakPassword -> ErrorRecuperacion.CONTRASENA_DEBIL
            else -> ErrorRecuperacion.DESCONOCIDO
        }
        if (motivo == ErrorRecuperacion.DESCONOCIDO) registrarAdvertencia(Constantes.Registro.RECHAZO_RECUPERACION, error.resumen())
        ResultadoRecuperacion.Fallido(motivo)
    } catch (error: HttpRequestException) {
        registrarAdvertencia(Constantes.Registro.SIN_CONEXION, error.cause)
        ResultadoRecuperacion.Fallido(ErrorRecuperacion.SIN_CONEXION)
    } catch (error: RestException) {
        registrarAdvertencia(Constantes.Registro.ERROR_SERVIDOR, error.resumen())
        ResultadoRecuperacion.Fallido(ErrorRecuperacion.DESCONOCIDO)
    }
}
