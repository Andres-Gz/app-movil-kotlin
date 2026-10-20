package com.example.finanzia.pantallas.nuevacontrasena

import androidx.annotation.StringRes
import com.example.finanzia.R
import com.example.finanzia.utilidades.RequisitosContrasena
import com.example.finanzia.utilidades.revisarRequisitos

/** Todo lo que muestra la pantalla de nueva contraseña (paso 2 de recuperar la contraseña). */
data class EstadoNuevaContrasena(
    val contrasena: String = "",
    val confirmacion: String = "",
    val contrasenaVisible: Boolean = false,
    val confirmacionVisible: Boolean = false,
    val cargando: Boolean = false,
    @param:StringRes val errorContrasena: Int? = null,
    @param:StringRes val errorConfirmacion: Int? = null,
    /** Título y explicación del aviso rojo de arriba. */
    @param:StringRes val avisoTitulo: Int? = null,
    @param:StringRes val avisoMensaje: Int? = null,
    /** Se pone en `true` cuando Supabase ya guardó la contraseña nueva. */
    val contrasenaActualizada: Boolean = false,
) {
    /** Qué requisitos cumple la contraseña nueva, para la lista con marcas. */
    val requisitos: RequisitosContrasena get() = revisarRequisitos(contrasena)

    /** La confirmación se escribió pero no es igual a la contraseña nueva. */
    val noCoinciden: Boolean get() = errorConfirmacion == R.string.error_contrasenas_distintas

    val ambasVisibles: Boolean get() = contrasenaVisible && confirmacionVisible
}
