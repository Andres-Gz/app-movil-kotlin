package com.example.finanzia.pantallas.nuevacontrasena

import androidx.annotation.StringRes
import com.example.finanzia.R
import com.example.finanzia.utilidades.revisarRequisitos

/**
 * Reglas de la nueva contraseña: devuelven el texto de error, o `null` si el valor es válido.
 * Pide los cuatro requisitos de utilidades/Validaciones.kt (también el símbolo especial).
 */
object ValidadorNuevaContrasena {

    @StringRes
    fun validarContrasena(contrasena: String): Int? = when {
        contrasena.isEmpty() -> R.string.error_contrasena_vacia
        !revisarRequisitos(contrasena).cumpleTodos -> R.string.error_contrasena_requisitos
        else -> null
    }

    @StringRes
    fun validarConfirmacion(contrasena: String, confirmacion: String): Int? = when {
        confirmacion.isEmpty() -> R.string.error_confirmacion_vacia
        confirmacion != contrasena -> R.string.error_contrasenas_distintas
        else -> null
    }

    /**
     * Aviso de arriba (título y explicación), en orden: campos vacíos, contraseña poco segura
     * y contraseñas distintas. `null` si todo está bien.
     */
    fun aviso(contrasena: String, confirmacion: String): Pair<Int, Int>? = when {
        contrasena.isEmpty() || confirmacion.isEmpty() ->
            R.string.error_faltan_datos to R.string.error_faltan_datos_detalle
        !revisarRequisitos(contrasena).cumpleTodos ->
            R.string.error_contrasena_insegura to R.string.error_contrasena_insegura_detalle
        confirmacion != contrasena ->
            R.string.error_contrasenas_no_coinciden to R.string.error_contrasenas_no_coinciden_detalle
        else -> null
    }
}
