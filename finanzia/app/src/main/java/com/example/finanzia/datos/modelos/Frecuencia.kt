package com.example.finanzia.datos.modelos

/**
 * Cada cuánto llega un ingreso. En la base se guarda con el nombre en minúsculas
 * (`mensual`, `quincenal`…), que es lo que acepta la función `registrar_ingreso`.
 */
enum class Frecuencia {
    MENSUAL,
    QUINCENAL,
    SEMANAL,
    DIARIO,
    ;

    val clave: String get() = name.lowercase()
}
