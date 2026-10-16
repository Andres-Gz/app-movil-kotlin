package com.example.finanzia.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.example.finanzia.R
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos

/**
 * Logo del inicio de sesión: el nombre de la app en mayúsculas con un destello verde arriba a la derecha.
 * El destello no ocupa espacio, así el nombre queda centrado igual que en el diseño.
 */
@Composable
fun LogoMarca(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Text(
            text = stringResource(R.string.nombre_app).uppercase(),
            style = EstilosTexto.logo,
            color = Colores.azulNoche,
        )
        Icon(
            imageVector = Iconos.destello,
            contentDescription = null,
            tint = Colores.verdeDestello,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = Dimensiones.desplazamientoDestelloX, y = Dimensiones.desplazamientoDestelloY)
                .size(Dimensiones.tamanoDestello),
        )
    }
}

/** Logo de recuperar contraseña: el nombre con letras juntas y un punto verde al lado, centrado en conjunto. */
@Composable
fun LogoMarcaConPunto(modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(R.string.nombre_app).uppercase(),
            style = EstilosTexto.logoCompacto,
            color = Colores.tinta,
        )
        Spacer(Modifier.width(Dimensiones.espacioLogoPunto))
        Box(
            modifier = Modifier
                .size(Dimensiones.tamanoPuntoLogo)
                .background(Colores.verdePunto, CircleShape),
        )
    }
}

/**
 * Logo del registro: letras más juntas, la terminación "IA" en verde y el destello al lado del nombre.
 * Aquí el destello sí ocupa espacio, así que el conjunto completo queda centrado.
 */
@Composable
fun LogoMarcaCompacto(modifier: Modifier = Modifier) {
    val nombre = stringResource(R.string.nombre_app).uppercase()
    val resaltado = stringResource(R.string.nombre_app_resaltado)
    Row(modifier = modifier, verticalAlignment = Alignment.Top) {
        Text(
            text = buildAnnotatedString {
                if (nombre.endsWith(resaltado)) {
                    append(nombre.removeSuffix(resaltado))
                    withStyle(SpanStyle(color = Colores.verdeMarca)) { append(resaltado) }
                } else {
                    append(nombre)
                }
            },
            style = EstilosTexto.logoCompacto,
            color = Colores.azulProfundo,
        )
        Spacer(Modifier.width(Dimensiones.espacioLogoDestello))
        Icon(
            imageVector = Iconos.destello,
            contentDescription = null,
            tint = Colores.verdeMarca,
            modifier = Modifier
                .padding(top = Dimensiones.rellenoSuperiorDestello)
                .size(Dimensiones.tamanoDestelloCompacto),
        )
    }
}
