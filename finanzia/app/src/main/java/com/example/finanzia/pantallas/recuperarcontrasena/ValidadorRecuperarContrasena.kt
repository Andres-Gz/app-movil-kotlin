package com.example.finanzia.pantallas.recuperarcontrasena

import androidx.annotation.StringRes
import com.example.finanzia.R

/**
 * Reglas del formulario de recuperar contraseña: devuelven el texto de error, o `null` si el valor es válido.
 * El correo se revisa con `validarCorreo` de utilidades/, que comparten todos los formularios.
 */
object ValidadorRecuperarContrasena {

    @StringRes
    fun validarUsuario(usuario: String): Int? = if (usuario.isBlank()) R.string.error_usuario_vacio else null

    /** Error para el aviso general: solo cuando falta llenar algún campo. */
    @StringRes
    fun validarFormulario(usuario: String, correo: String): Int? =
        if (usuario.isBlank() || correo.isBlank()) R.string.error_campos_incompletos else null
}
