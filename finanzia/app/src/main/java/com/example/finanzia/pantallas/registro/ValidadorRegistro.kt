package com.example.finanzia.pantallas.registro

import androidx.annotation.StringRes
import com.example.finanzia.R
import com.example.finanzia.utilidades.revisarRequisitos

/**
 * Reglas del formulario de registro: devuelven el texto de error, o `null` si el valor es válido.
 * El correo y los requisitos de la contraseña se revisan con las funciones de utilidades/Validaciones.kt.
 * El registro pide los tres requisitos básicos (sin símbolo especial).
 */
object ValidadorRegistro {

    @StringRes
    fun validarNombre(nombre: String): Int? = if (nombre.isBlank()) R.string.error_nombre_vacio else null

    @StringRes
    fun validarContrasena(contrasena: String): Int? = when {
        contrasena.isEmpty() -> R.string.error_contrasena_vacia
        !revisarRequisitos(contrasena).cumpleBasicos -> R.string.error_contrasena_requisitos
        else -> null
    }

    /** Error para el aviso general: primero los campos vacíos y luego los términos sin aceptar. */
    @StringRes
    fun validarFormulario(nombre: String, correo: String, contrasena: String, aceptaTerminos: Boolean): Int? = when {
        nombre.isBlank() || correo.isBlank() || contrasena.isEmpty() -> R.string.error_campos_incompletos
        !aceptaTerminos -> R.string.error_terminos
        else -> null
    }
}
