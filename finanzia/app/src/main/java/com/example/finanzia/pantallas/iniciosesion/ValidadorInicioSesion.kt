package com.example.finanzia.pantallas.iniciosesion

import androidx.annotation.StringRes
import com.example.finanzia.configuracion.Constantes
import com.example.finanzia.R

/** Reglas del formulario de inicio de sesión: devuelven el texto de error, o `null` si el valor es válido. */
object ValidadorInicioSesion {

    private val formatoCorreo = Regex(Constantes.Validacion.PATRON_CORREO)

    @StringRes
    fun validarCorreo(correo: String): Int? = when {
        correo.isBlank() -> R.string.error_correo_vacio
        !formatoCorreo.matches(correo.trim()) -> R.string.error_correo_invalido
        else -> null
    }

    @StringRes
    fun validarContrasena(contrasena: String): Int? =
        if (contrasena.isEmpty()) R.string.error_contrasena_vacia else null
}
