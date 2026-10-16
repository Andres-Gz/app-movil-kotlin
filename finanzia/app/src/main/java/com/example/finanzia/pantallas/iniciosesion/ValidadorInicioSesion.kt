package com.example.finanzia.pantallas.iniciosesion

import androidx.annotation.StringRes
import com.example.finanzia.R

/**
 * Reglas del formulario de inicio de sesión: devuelven el texto de error, o `null` si el valor es válido.
 * El correo se revisa con `validarCorreo` de utilidades/, que comparten todos los formularios.
 */
object ValidadorInicioSesion {

    @StringRes
    fun validarContrasena(contrasena: String): Int? =
        if (contrasena.isEmpty()) R.string.error_contrasena_vacia else null

    /** Error para el aviso general: solo cuando falta llenar algún campo. */
    @StringRes
    fun validarFormulario(correo: String, contrasena: String): Int? =
        if (correo.isBlank() || contrasena.isEmpty()) R.string.error_campos_incompletos else null
}
