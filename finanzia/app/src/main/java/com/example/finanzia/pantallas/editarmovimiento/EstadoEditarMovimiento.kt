package com.example.finanzia.pantallas.editarmovimiento

import androidx.annotation.StringRes
import com.example.finanzia.datos.modelos.CategoriaGasto

/** Cómo terminó la edición: la pantalla avisa con el mensaje que corresponde y se cierra. */
enum class ResultadoEdicion { GUARDADO, ELIMINADO }

/** Todo lo que muestra la pantalla de editar un movimiento. */
data class EstadoEditarMovimiento(
    val esIngreso: Boolean = false,
    /** Solo las cifras del monto, sin separadores. */
    val monto: String = "",
    val descripcion: String = "",
    /** Solo los gastos tienen categoría; `null` si es ingreso o si su categoría no es de las cuatro. */
    val categoria: CategoriaGasto? = null,
    val cargando: Boolean = false,
    /** Se pidió eliminar y se espera la confirmación. */
    val confirmandoEliminar: Boolean = false,
    @param:StringRes val errorMonto: Int? = null,
    @param:StringRes val errorDescripcion: Int? = null,
    @param:StringRes val errorGeneral: Int? = null,
    val resultado: ResultadoEdicion? = null,
)
