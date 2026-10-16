package com.example.finanzia.datos.repositorios

import com.example.finanzia.configuracion.Constantes
import com.example.finanzia.datos.ClienteSupabase
import com.example.finanzia.utilidades.registrarAdvertencia
import com.example.finanzia.utilidades.resumen
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthErrorCode
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Crea la cuenta en Supabase Auth y guarda el nombre como dato extra del usuario.
 * La fila de la tabla `usuarios` no se crea aquí: la debe crear la base de datos al registrarse alguien.
 */
class RepositorioRegistroSupabase(
    private val cliente: SupabaseClient = ClienteSupabase.cliente,
) : RepositorioRegistro {

    override suspend fun registrarse(nombre: String, correo: String, contrasena: String): ResultadoRegistro = try {
        val usuario = cliente.auth.signUpWith(Email) {
            email = correo
            password = contrasena
            data = buildJsonObject { put(Constantes.BaseDatos.DATO_NOMBRE, nombre) }
        }
        // Si se pide confirmar el correo, Supabase no avisa que el correo ya existe:
        // responde con un usuario sin identidades.
        if (usuario?.identities?.isEmpty() == true) {
            ResultadoRegistro.Fallido(ErrorRegistro.CORREO_YA_REGISTRADO)
        } else {
            ResultadoRegistro.Exitoso(necesitaConfirmacion = cliente.auth.currentSessionOrNull() == null)
        }
    } catch (error: AuthRestException) {
        val motivo = when (error.errorCode) {
            AuthErrorCode.UserAlreadyExists, AuthErrorCode.EmailExists -> ErrorRegistro.CORREO_YA_REGISTRADO
            AuthErrorCode.EmailAddressInvalid, AuthErrorCode.EmailAddressNotAuthorized -> ErrorRegistro.CORREO_RECHAZADO
            AuthErrorCode.WeakPassword -> ErrorRegistro.CONTRASENA_DEBIL
            AuthErrorCode.OverEmailSendRateLimit, AuthErrorCode.OverRequestRateLimit -> ErrorRegistro.DEMASIADOS_INTENTOS
            else -> ErrorRegistro.DESCONOCIDO
        }
        if (motivo == ErrorRegistro.DESCONOCIDO) registrarAdvertencia(Constantes.Registro.RECHAZO_REGISTRO, error.resumen())
        ResultadoRegistro.Fallido(motivo)
    } catch (error: HttpRequestException) {
        registrarAdvertencia(Constantes.Registro.SIN_CONEXION, error.cause)
        ResultadoRegistro.Fallido(ErrorRegistro.SIN_CONEXION)
    } catch (error: RestException) {
        registrarAdvertencia(Constantes.Registro.ERROR_SERVIDOR, error.resumen())
        ResultadoRegistro.Fallido(ErrorRegistro.DESCONOCIDO)
    }
}
