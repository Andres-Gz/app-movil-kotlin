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

        /** Función que junta las metas y las fechas de sus aportes (supabase/metas.sql). */
        const val FUNCION_RESUMEN_METAS = "resumen_metas"

        /**
         * Funciones que guardan las metas (supabase/metas.sql). Usan también p_id, p_monto, p_categoria
         * y p_frecuencia, los mismos nombres que las de movimientos.
         */
        const val FUNCION_CREAR_META = "crear_meta"
        const val FUNCION_EDITAR_META = "editar_meta"
        const val FUNCION_APORTAR_META = "aportar_meta"
        const val FUNCION_AJUSTAR_REGLA_META = "ajustar_regla_meta"
        const val FUNCION_ELIMINAR_META = "eliminar_meta"
        const val PARAMETRO_NOMBRE = "p_nombre"
        const val PARAMETRO_FECHA_LIMITE = "p_fecha_limite"
        const val PARAMETRO_MONTO_APORTE = "p_monto_aporte"

        /** Nombres (en minúsculas) con que `crear_meta` guarda cada categoría, para volver a elegirla al editar. */
        const val CATEGORIA_META_VIAJES = "viajes & ocio"
        const val CATEGORIA_META_EMERGENCIA = "emergencia"
        const val CATEGORIA_META_TECNOLOGIA = "tecnología"
        const val CATEGORIA_META_VEHICULO = "vehículo"
        const val CATEGORIA_META_VIVIENDA = "vivienda"
        const val CATEGORIA_META_EDUCACION = "educación"

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

        /** Separador de miles de los montos de la pantalla de metas, que van en pesos (14850000 → 14.850.000). */
        const val SEPARADOR_MILES_PESOS = '.'
    }

    /** Cuentas de la pantalla de metas: cuándo toca cada aporte y cuántas quincenas seguidas se ha aportado. */
    object Metas {
        /** La primera quincena se paga el 15; la segunda, el último día del mes. */
        const val DIA_PRIMERA_QUINCENA = 15
        const val QUINCENAS_POR_MES = 2
        const val MESES_POR_ANO = 12
        const val DIAS_POR_SEMANA = 7
        const val MILISEGUNDOS_POR_DIA = 24L * 60 * 60 * 1000

        /** Botones que suman al monto objetivo de una meta nueva ("+$500.000"…). */
        val INCREMENTOS_MONTO = listOf(500_000L, 1_000_000L, 2_000_000L, 5_000_000L)
        const val LARGO_MAXIMO_NOMBRE = 60

        /** El aporte sugerido se redondea hacia arriba a miles de pesos (218.750 → 219.000). */
        const val REDONDEO_APORTE = 1_000L

        /** Cuántos años hacia adelante se puede elegir la fecha de una meta. */
        const val ANOS_HACIA_ADELANTE = 10

        /** Formato de la fecha límite que recibe `crear_meta`. */
        const val PATRON_FECHA_LIMITE = "yyyy-MM-dd"
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
