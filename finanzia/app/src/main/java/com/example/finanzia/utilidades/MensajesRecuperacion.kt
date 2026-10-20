package com.example.finanzia.utilidades

import androidx.annotation.StringRes
import com.example.finanzia.R
import com.example.finanzia.datos.repositorios.ErrorRecuperacion

/** El texto para el usuario de cada error de la recuperación. Lo usan los dos pasos. */
@StringRes
fun mensajeDeRecuperacion(error: ErrorRecuperacion): Int = when (error) {
    ErrorRecuperacion.DEMASIADOS_INTENTOS -> R.string.error_demasiados_intentos
    ErrorRecuperacion.CORREO_RECHAZADO -> R.string.error_correo_rechazado
    ErrorRecuperacion.SIN_SESION -> R.string.error_enlace_necesario
    ErrorRecuperacion.MISMA_CONTRASENA -> R.string.error_misma_contrasena
    ErrorRecuperacion.CONTRASENA_DEBIL -> R.string.error_contrasena_debil
    ErrorRecuperacion.SIN_CONEXION -> R.string.error_sin_conexion
    ErrorRecuperacion.DESCONOCIDO -> R.string.error_desconocido
}
