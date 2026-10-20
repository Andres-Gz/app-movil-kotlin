package com.example.finanzia.pantallas.nuevameta

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import com.example.finanzia.R
import com.example.finanzia.componentes.TextoEnlace
import com.example.finanzia.configuracion.Constantes
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos
import java.util.Calendar

/** Los meses van de a tres por fila. */
private const val MESES_POR_FILA = 3

/**
 * Ventana para elegir el mes en que se quiere lograr la meta: el año con flechas y los doce meses.
 * Los meses que ya pasaron (y el actual) no se pueden elegir. El diseño no la muestra.
 */
@Composable
internal fun SelectorMes(elegido: MesAno?, hoy: Calendar, alElegir: (MesAno) -> Unit, alCerrar: () -> Unit) {
    val primerMes = CalculosNuevaMeta.primerMes(hoy)
    val ultimoAno = primerMes.ano + Constantes.Metas.ANOS_HACIA_ADELANTE
    var ano by rememberSaveable { mutableIntStateOf(elegido?.ano ?: primerMes.ano) }
    val nombres = stringArrayResource(R.array.inicio_meses)
    AlertDialog(
        onDismissRequest = alCerrar,
        containerColor = Colores.superficie,
        title = { Text(text = stringResource(R.string.nueva_meta_selector_titulo), style = EstilosTexto.tituloAviso, color = Colores.tinta) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Dimensiones.espacioPequeno)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FlechaAno(Iconos.cerrar, stringResource(R.string.accion_ano_anterior), habilitada = ano > primerMes.ano) { ano-- }
                    Text(
                        text = ano.toString(),
                        style = EstilosTexto.tituloSeccion,
                        color = Colores.tinta,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                    )
                    FlechaAno(Iconos.abrir, stringResource(R.string.accion_ano_siguiente), habilitada = ano < ultimoAno) { ano++ }
                }
                (0 until Constantes.Metas.MESES_POR_ANO).chunked(MESES_POR_FILA).forEach { fila ->
                    Row(horizontalArrangement = Arrangement.spacedBy(Dimensiones.espacioPequeno)) {
                        fila.forEach { numero ->
                            val mes = MesAno(numero, ano)
                            val disponible = CalculosNuevaMeta.disponible(mes, hoy)
                            val esElegido = mes == elegido
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(Dimensiones.alturaMesSelector)
                                    .clip(RoundedCornerShape(Dimensiones.radioCampo))
                                    .background(if (esElegido) Colores.verdeMenta else Colores.rellenoCampo)
                                    .clickable(enabled = disponible, role = Role.Button, onClick = { alElegir(mes) }),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = nombres[numero],
                                    style = EstilosTexto.botonSecundario,
                                    color = when {
                                        esElegido -> Colores.verdeSobreMenta
                                        disponible -> Colores.tinta
                                        else -> Colores.textoTenue
                                    },
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextoEnlace(
                texto = stringResource(R.string.nueva_meta_selector_cancelar),
                alPulsar = alCerrar,
                estilo = EstilosTexto.boton,
                color = Colores.azulIntenso,
            )
        },
    )
}

@Composable
private fun FlechaAno(icono: ImageVector, descripcion: String, habilitada: Boolean, alPulsar: () -> Unit) {
    Box(
        modifier = Modifier
            .size(Dimensiones.tamanoBotonEncabezadoMeta)
            .clip(CircleShape)
            .clickable(enabled = habilitada, role = Role.Button, onClick = alPulsar),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icono,
            contentDescription = descripcion,
            tint = if (habilitada) Colores.azulIntenso else Colores.grisClaro,
            modifier = Modifier.size(Dimensiones.tamanoIcono),
        )
    }
}
