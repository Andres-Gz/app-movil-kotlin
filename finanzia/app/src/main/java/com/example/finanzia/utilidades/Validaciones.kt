package com.example.finanzia.utilidades

import androidx.annotation.StringRes
import com.example.finanzia.R
import com.example.finanzia.configuracion.Constantes

private val formatoCorreo = Regex(Constantes.Validacion.PATRON_CORREO)

/** Revisa un correo de cualquier formulario: devuelve el texto de error, o `null` si es válido. */
@StringRes
fun validarCorreo(correo: String): Int? = when {
    correo.isBlank() -> R.string.error_correo_vacio
    !formatoCorreo.matches(correo.trim()) -> R.string.error_correo_invalido
    else -> null
}

/** Qué requisitos de seguridad cumple una contraseña. Cada formulario decide cuáles exige. */
data class RequisitosContrasena(
    val longitud: Boolean,
    val numero: Boolean,
    val mayuscula: Boolean,
    val simbolo: Boolean,
) {
    /** Los tres que pide el registro. */
    val cumpleBasicos: Boolean get() = longitud && numero && mayuscula

    /** Los cuatro que pide la nueva contraseña. */
    val cumpleTodos: Boolean get() = cumpleBasicos && simbolo
}

fun revisarRequisitos(contrasena: String) = RequisitosContrasena(
    longitud = contrasena.length >= Constantes.Validacion.LONGITUD_MINIMA_CONTRASENA,
    numero = contrasena.any { it.isDigit() },
    mayuscula = contrasena.any { it.isUpperCase() },
    simbolo = contrasena.any { !it.isLetterOrDigit() && !it.isWhitespace() },
)
