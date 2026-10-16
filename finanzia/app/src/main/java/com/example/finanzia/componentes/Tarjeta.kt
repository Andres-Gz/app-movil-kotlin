package com.example.finanzia.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones

/** Tarjeta blanca de esquinas redondeadas y sombra suave, para agrupar un formulario o una lista. */
@Composable
fun Tarjeta(
    modifier: Modifier = Modifier,
    relleno: PaddingValues = PaddingValues(
        start = Dimensiones.rellenoHorizontalTarjeta,
        end = Dimensiones.rellenoHorizontalTarjeta,
        top = Dimensiones.rellenoSuperiorTarjeta,
        bottom = Dimensiones.rellenoInferiorTarjeta,
    ),
    contenido: @Composable ColumnScope.() -> Unit,
) {
    val forma = RoundedCornerShape(Dimensiones.radioTarjeta)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(Dimensiones.elevacionCampo, forma, ambientColor = Colores.sombra, spotColor = Colores.sombra)
            .background(Colores.superficie, forma)
            .padding(relleno),
        content = contenido,
    )
}
