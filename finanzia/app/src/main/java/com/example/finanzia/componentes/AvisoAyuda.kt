package com.example.finanzia.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos

/** Recuadro azul claro con un icono de ayuda, una pregunta y un enlace debajo (por ejemplo, contactar a soporte). */
@Composable
fun AvisoAyuda(
    pregunta: String,
    enlace: String,
    alPulsarEnlace: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Colores.fondoInformativo, RoundedCornerShape(Dimensiones.radioAviso))
            .padding(Dimensiones.rellenoAvisoAyuda),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = Iconos.soporte,
            contentDescription = null,
            tint = Colores.moradoAyuda,
            modifier = Modifier.size(Dimensiones.tamanoIconoMediano),
        )
        Spacer(Modifier.width(Dimensiones.espacioIconoTextoAyuda))
        Column {
            Text(text = pregunta, style = EstilosTexto.cuerpo, color = Colores.textoApagado)
            Spacer(Modifier.height(Dimensiones.espacioLineasAyuda))
            TextoEnlace(
                texto = enlace,
                alPulsar = alPulsarEnlace,
                estilo = EstilosTexto.notaFuerte,
                color = Colores.azulIntenso,
            )
        }
    }
}
