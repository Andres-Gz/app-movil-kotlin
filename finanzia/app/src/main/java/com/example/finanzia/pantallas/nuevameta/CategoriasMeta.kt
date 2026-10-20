package com.example.finanzia.pantallas.nuevameta

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.example.finanzia.R
import com.example.finanzia.datos.modelos.CategoriaMeta
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos

/** Las categorías van de a tres por fila, como en el diseño. */
private const val COLUMNAS = 3

/** Las seis categorías de una meta; la elegida va en verde menta. */
@Composable
internal fun CuadriculaCategoriasMeta(elegida: CategoriaMeta, alElegir: (CategoriaMeta) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimensiones.espacioEntreCategoriasMeta)) {
        CategoriaMeta.entries.chunked(COLUMNAS).forEach { fila ->
            Row(horizontalArrangement = Arrangement.spacedBy(Dimensiones.espacioEntreCategoriasMeta)) {
                fila.forEach { categoria ->
                    TarjetaCategoria(
                        categoria = categoria,
                        elegida = categoria == elegida,
                        alElegir = { alElegir(categoria) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaCategoria(categoria: CategoriaMeta, elegida: Boolean, alElegir: () -> Unit, modifier: Modifier = Modifier) {
    val apariencia = aparienciaDe(categoria)
    val forma = RoundedCornerShape(Dimensiones.radioBotonCompacto)
    Column(
        modifier = modifier
            .height(Dimensiones.alturaCategoriaMeta)
            .shadow(if (elegida) Dimensiones.elevacionNula else Dimensiones.elevacionCampo, forma, ambientColor = Colores.sombra, spotColor = Colores.sombra)
            .background(if (elegida) Colores.verdeMenta else Colores.superficie, forma)
            .clip(forma)
            .selectable(selected = elegida, role = Role.RadioButton, onClick = alElegir),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = apariencia.icono,
            contentDescription = null,
            tint = if (elegida) Colores.verdeSeguro else apariencia.color,
            modifier = Modifier.size(Dimensiones.tamanoIconoCategoriaMeta),
        )
        Spacer(Modifier.height(Dimensiones.espacioIconoTextoCategoriaMeta))
        Text(
            text = stringResource(apariencia.texto),
            style = EstilosTexto.nota,
            color = if (elegida) Colores.verdeSobreMenta else Colores.textoApagado,
            maxLines = 1,
        )
    }
}

/** Icono, color y nombre de cada categoría, tomados del diseño. */
private class AparienciaCategoria(val icono: ImageVector, val color: Color, @param:StringRes val texto: Int)

private fun aparienciaDe(categoria: CategoriaMeta): AparienciaCategoria = when (categoria) {
    CategoriaMeta.VIAJES -> AparienciaCategoria(Iconos.playa, Colores.verdeSeguro, R.string.nueva_meta_categoria_viajes)
    CategoriaMeta.EMERGENCIA -> AparienciaCategoria(Iconos.escudo, Colores.azulIntenso, R.string.nueva_meta_categoria_emergencia)
    CategoriaMeta.TECNOLOGIA -> AparienciaCategoria(Iconos.portatil, Colores.moradoAyuda, R.string.nueva_meta_categoria_tecnologia)
    CategoriaMeta.VEHICULO -> AparienciaCategoria(Iconos.moto, Colores.azulIntenso, R.string.nueva_meta_categoria_vehiculo)
    CategoriaMeta.VIVIENDA -> AparienciaCategoria(Iconos.casa, Colores.verdeSeguro, R.string.nueva_meta_categoria_vivienda)
    CategoriaMeta.EDUCACION -> AparienciaCategoria(Iconos.estudio, Colores.moradoAyuda, R.string.nueva_meta_categoria_educacion)
}
