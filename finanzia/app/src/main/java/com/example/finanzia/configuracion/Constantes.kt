package com.example.finanzia.configuracion

/**
 * Constantes internas de la app, agrupadas por tema. El usuario no ve ninguna:
 * los textos visibles van en strings.xml y los valores visuales en tema/.
 */
object Constantes {

    /** Nombres en la base de datos de Supabase. */
    object BaseDatos {
        const val TABLA_USUARIOS = "usuarios"

        /** Dato extra que se guarda en Supabase Auth al registrarse (queda en `raw_user_meta_data`). */
        const val DATO_NOMBRE = "nombre"

        /** Función que guarda un ingreso (supabase/ingresos.sql) y sus parámetros. */
        const val FUNCION_REGISTRAR_INGRESO = "registrar_ingreso"
        const val PARAMETRO_MONTO = "p_monto"
        const val PARAMETRO_FRECUENCIA = "p_frecuencia"

        /** Función que guarda un gasto (supabase/gastos.sql) y su parámetro de categoría. */
        const val FUNCION_REGISTRAR_GASTO = "registrar_gasto"
        const val PARAMETRO_CATEGORIA = "p_categoria"

        /** Función que junta todo lo que muestra el inicio (supabase/inicio.sql). */
        const val FUNCION_RESUMEN_INICIO = "resumen_inicio"

        /** Funciones que editan y eliminan un movimiento (supabase/editar_movimientos.sql). */
        const val FUNCION_EDITAR_MOVIMIENTO = "editar_movimiento"
        const val FUNCION_ELIMINAR_MOVIMIENTO = "eliminar_movimiento"
        const val PARAMETRO_ID = "p_id"
        const val PARAMETRO_DESCRIPCION = "p_descripcion"

        /** Cómo se llama el tipo de movimiento de los ingresos en `movimientos.tipo`. */
        const val TIPO_INGRESO = "ingreso"

        /** Nombres (en minúsculas) de las categorías que crea `registrar_gasto`, para elegir su icono. */
        const val CATEGORIA_FIJOS = "gastos fijos"
        const val CATEGORIA_COMIDA = "comida"
        const val CATEGORIA_DIVERSION = "diversión"
        const val CATEGORIA_TRANSPORTE = "transporte"
    }

    /** Mensajes para Logcat (se filtran con: adb logcat -s Finanzia). */
    object Registro {
        const val ETIQUETA = "Finanzia"
        const val RECHAZO_AUTH = "Supabase Auth rechazó el inicio de sesión"
        const val RECHAZO_REGISTRO = "Supabase Auth rechazó el registro"
        const val RECHAZO_RECUPERACION = "Supabase Auth rechazó la recuperación de contraseña"
        const val SIN_CONEXION = "No hubo conexión con Supabase"
        const val ERROR_SERVIDOR = "Supabase respondió con un error"
    }

    /** Formatos de montos y fechas. */
    object Formatos {
        const val PATRON_MONTO = "#,##0.00"
        const val PATRON_MONTO_ENTERO = "#,##0"
        const val PATRON_PORCENTAJE = "0.0"
        const val PATRON_FECHA_ISO = "yyyy-MM-dd'T'HH:mm:ss"
        const val LARGO_FECHA_ISO = 19
        const val ZONA_BASE = "UTC"
        const val PATRON_HORA = "HH:mm"

        /** Cuánto del gasto se propone recortar en el consejo del inicio. */
        const val RECORTE_SUGERIDO = 0.10
        const val CIEN = 100.0

        /** Cuántos círculos con iniciales se muestran junto al consejo. */
        const val INICIALES_VISIBLES = 3
    }

    /** Reglas de validación de formularios. */
    object Validacion {
        const val PATRON_CORREO = """^[^\s@]+@[^\s@]+\.[^\s@]+$"""
        const val LONGITUD_MINIMA_CONTRASENA = 8

        /** Cifras máximas del monto de un ingreso (hasta cientos de miles de millones). */
        const val CIFRAS_MAXIMAS_MONTO = 12
    }

    /** Enlaces: los de dentro de un texto y el que abre la app desde el correo de recuperación. */
    object Enlaces {
        const val TERMINOS = "terminos"
        const val PRIVACIDAD = "privacidad"

        // Si cambian, hay que cambiarlos también en AndroidManifest.xml y en Supabase (Redirect URLs).
        const val ESQUEMA_APP = "finanzia"
        const val HOST_RECUPERACION = "recuperar"
        const val URL_RECUPERACION = "$ESQUEMA_APP://$HOST_RECUPERACION"

        /** Lo que trae el enlace cuando ya no sirve (vencido o usado). */
        const val MARCA_ERROR = "error"
    }
}
