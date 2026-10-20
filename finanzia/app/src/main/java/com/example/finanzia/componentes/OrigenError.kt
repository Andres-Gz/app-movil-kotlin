package com.example.finanzia.componentes

/**
 * De dónde viene un error. El diseño los pinta distinto: los del formulario (campos vacíos o
 * mal escritos) y los que responde el servidor (por ejemplo, una contraseña que no coincide).
 */
enum class OrigenError {
    FORMULARIO,
    SERVIDOR,
}
