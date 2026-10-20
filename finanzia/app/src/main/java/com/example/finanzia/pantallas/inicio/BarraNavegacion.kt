package com.example.finanzia.pantallas.inicio

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.example.finanzia.R
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos

/** Barra de abajo: Inicio (la actual), Gráficos, Billetera, Metas y el botón "+". */
@Composable
internal fun BarraNavegacion(alAgregar: () -> Unit, alAbrirMetas: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimensiones.alturaNavegacion)
            .background(Colores.superficie)
            .padding(horizontal = Dimensiones.espacioPequeno),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ItemNavegacion(Iconos.navInicio, stringResource(R.string.inicio_nav_inicio), seleccionado = true)
        // Gráficos y Billetera aún no tienen pantalla.
        ItemNavegacion(Iconos.navGraficos, stringResource(R.string.inicio_nav_graficos), seleccionado = false)
        ItemNavegacion(Iconos.navBilletera, stringResource(R.string.inicio_nav_billetera), seleccionado = false)
        ItemNavegacion(Iconos.navMetas, stringResource(R.string.inicio_nav_metas), seleccionado = false, alPulsar = alAbrirMetas)
        Box(modifier = Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(Dimensiones.tamanoBotonAgregar)
                    .shadow(Dimensiones.elevacionBoton, CircleShape, ambientColor = Colores.azulIntenso, spotColor = Colores.azulIntenso)
                    .background(Brush.linearGradient(listOf(Colores.azulIntenso, Colores.verdeSeguro)), CircleShape)
                    .clip(CircleShape)
                    .clickable(role = Role.Button, onClick = alAgregar),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Iconos.agregar,
                    contentDescription = stringResource(R.string.accion_agregar_movimiento),
                    tint = Colores.textoSobreColor,
                    modifier = Modifier.size(Dimensiones.tamanoIconoGrande),
                )
            }
        }
    }
}

@Composable
private fun RowScope.ItemNavegacion(icono: ImageVector, texto: String, seleccionado: Boolean, alPulsar: (() -> Unit)? = null) {
    val pulsable = if (alPulsar != null) Modifier.clickable(role = Role.Button, onClick = alPulsar) else Modifier
    Column(modifier = Modifier.weight(1f).then(pulsable), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(Dimensiones.anchoSeleccionNavegacion, Dimensiones.altoSeleccionNavegacion)
                .background(if (seleccionado) Colores.azulPildora else Colores.transparente, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = if (seleccionado) Colores.azulIntenso else Colores.grisContorno,
                modifier = Modifier.size(Dimensiones.tamanoIcono),
            )
        }
        Text(
            text = texto,
            style = EstilosTexto.ayudaDestacada,
            color = if (seleccionado) Colores.azulIntenso else Colores.grisContorno,
        )
    }
}
