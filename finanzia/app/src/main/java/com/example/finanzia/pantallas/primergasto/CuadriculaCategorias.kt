package com.example.finanzia.pantallas.primergasto

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.finanzia.R
import com.example.finanzia.componentes.ColoresOpcion
import com.example.finanzia.componentes.TarjetaOpcion
import com.example.finanzia.datos.modelos.CategoriaGasto
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones

/** Cómo se ve cada categoría: nombre, detalle, emoji y fondo del emoji. */
private class OpcionCategoria(
    val categoria: CategoriaGasto,
    @param:StringRes val titulo: Int,
    @param:StringRes val detalle: Int,
    @param:StringRes val emoji: Int,
    val fondoEmoji: Color,
)

private val opciones = listOf(
    OpcionCategoria(CategoriaGasto.GASTOS_FIJOS, R.string.gasto_fijos, R.string.gasto_fijos_detalle, R.string.gasto_emoji_fijos, Colores.rosaNieveBorde),
    OpcionCategoria(CategoriaGasto.COMIDA, R.string.gasto_comida, R.string.gasto_comida_detalle, R.string.gasto_emoji_comida, Colores.amarilloNieve),
    OpcionCategoria(CategoriaGasto.DIVERSION, R.string.gasto_diversion, R.string.gasto_diversion_detalle, R.string.gasto_emoji_diversion, Colores.indigoNieve),
    OpcionCategoria(CategoriaGasto.TRANSPORTE, R.string.gasto_transporte, R.string.gasto_transporte_detalle, R.string.gasto_emoji_transporte, Colores.rellenoCampo),
)

/** La categoría elegida se marca en rosa. */
private val coloresCategoria = ColoresOpcion(
    acento = Colores.rosaIntenso,
    fondoElegida = Colores.rosaSeleccion,
    fondoEmojiElegida = Colores.rosaNieveBorde,
)

/**
 * Las cuatro categorías de gasto en dos columnas, con [elegida] marcada. La usan el primer gasto
 * y la edición de un movimiento.
 */
@Composable
fun CuadriculaCategorias(elegida: CategoriaGasto?, alElegir: (CategoriaGasto) -> Unit) {
    Column {
        opciones.chunked(2).forEachIndexed { fila, par ->
            if (fila > 0) Spacer(Modifier.height(Dimensiones.espacioEntreFrecuencias))
            Row(
                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(Dimensiones.espacioEntreFrecuencias),
            ) {
                par.forEach { opcion ->
                    TarjetaOpcion(
                        emoji = stringResource(opcion.emoji),
                        titulo = stringResource(opcion.titulo),
                        detalle = stringResource(opcion.detalle),
                        elegida = opcion.categoria == elegida,
                        alElegir = { alElegir(opcion.categoria) },
                        fondoEmoji = opcion.fondoEmoji,
                        colores = coloresCategoria,
                        alturaMinima = Dimensiones.alturaCategoria,
                        relleno = Dimensiones.rellenoCategoria,
                        tamanoRecuadro = Dimensiones.tamanoRecuadroCategoria,
                        radioRecuadro = Dimensiones.radioRecuadroPequeno,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                    )
                }
            }
        }
    }
}
