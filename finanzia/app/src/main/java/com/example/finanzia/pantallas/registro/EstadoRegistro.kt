package com.example.finanzia.pantallas.registro

import androidx.annotation.StringRes
import com.example.finanzia.componentes.OrigenError
import com.example.finanzia.utilidades.RequisitosContrasena
import com.example.finanzia.utilidades.revisarRequisitos

/** Todo lo que muestra la pantalla de registro. */
data class EstadoRegistro(
    val nombre: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val contrasenaVisible: Boolean = false,
    val aceptaTerminos: Boolean = false,
    val cargando: Boolean = false,
    @param:StringRes val errorNombre: Int? = null,
    @param:StringRes val errorCorreo: Int? = null,
    @param:StringRes val errorContrasena: Int? = null,
    /** La casilla de términos se pinta de rojo si se intentó crear la cuenta sin aceptarlos. */
    val errorTerminos: Boolean = false,
    /** Mensaje del aviso rojo que aparece sobre los campos. */
    @param:StringRes val errorGeneral: Int? = null,
    /** De dónde vienen los errores que se muestran: cambia cómo se pintan. */
    val origenError: OrigenError = OrigenError.FORMULARIO,
    /** Se llena cuando la cuenta se creó: es el mensaje para el usuario. */
    @param:StringRes val mensajeCuentaCreada: Int? = null,
) {
    /** Qué requisitos cumple la contraseña escrita, para los puntos de colores. */
    val requisitos: RequisitosContrasena get() = revisarRequisitos(contrasena)
}
