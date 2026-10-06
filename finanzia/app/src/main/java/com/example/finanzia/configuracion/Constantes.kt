package com.example.finanzia.configuracion

/**
 * Constantes internas de la app, agrupadas por tema. El usuario no ve ninguna:
 * los textos visibles van en strings.xml y los valores visuales en tema/.
 */
object Constantes {

    /** Nombres en la base de datos de Supabase. */
    object BaseDatos {
        const val TABLA_USUARIOS = "usuarios"
    }

    /** Mensajes para Logcat (se filtran con: adb logcat -s Finanzia). */
    object Registro {
        const val ETIQUETA = "Finanzia"
        const val RECHAZO_AUTH = "Supabase Auth rechazó el inicio de sesión"
        const val SIN_CONEXION = "No hubo conexión con Supabase"
        const val ERROR_SERVIDOR = "Supabase respondió con un error"
    }

    /** Reglas de validación de formularios. */
    object Validacion {
        const val PATRON_CORREO = """^[^\s@]+@[^\s@]+\.[^\s@]+$"""
    }
}
