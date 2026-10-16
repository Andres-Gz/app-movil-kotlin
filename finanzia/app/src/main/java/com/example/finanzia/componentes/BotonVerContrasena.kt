package com.example.finanzia.componentes

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.example.finanzia.R
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.Iconos

/** Icono del ojo para mostrar u ocultar una contraseña. Cada diseño puede usar sus propios iconos y color. */
@Composable
fun BotonVerContrasena(
    visible: Boolean,
    alAlternar: () -> Unit,
    tamanoIcono: Dp = Dimensiones.tamanoIcono,
    color: Color = Colores.iconoCampo,
    iconoMostrar: ImageVector = Iconos.mostrar,
    iconoOcultar: ImageVector = Iconos.ocultar,
) {
    BotonIcono(
        icono = if (visible) iconoOcultar else iconoMostrar,
        descripcion = stringResource(
            if (visible) R.string.accion_ocultar_contrasena else R.string.accion_mostrar_contrasena,
        ),
        alPulsar = alAlternar,
        color = color,
        tamanoIcono = tamanoIcono,
    )
}
