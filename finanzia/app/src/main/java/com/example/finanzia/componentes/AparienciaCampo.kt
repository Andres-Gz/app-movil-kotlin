package com.example.finanzia.componentes

/** Cómo se ve la caja de un campo de texto con la etiqueta arriba. */
enum class AparienciaCampo {
    /** Blanca, con borde gris y sombra suave (registro). */
    TARJETA,

    /** Rellena de azul muy claro, sin borde ni sombra (recuperar contraseña). */
    RELLENO,

    /** Blanca, más alta, con sombra y sin borde; con error se rellena de rosa (nueva contraseña). */
    ELEVADO,
}
