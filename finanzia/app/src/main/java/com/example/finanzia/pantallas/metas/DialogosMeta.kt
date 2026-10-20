package com.example.finanzia.pantallas.metas

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.example.finanzia.R
import com.example.finanzia.componentes.ChipMonto
import com.example.finanzia.configuracion.Constantes
import com.example.finanzia.datos.modelos.Frecuencia
import com.example.finanzia.pantallas.nuevameta.SelectorFrecuencia
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.utilidades.SeparadorMiles
import com.example.finanzia.utilidades.formatearPesos
import java.util.Calendar
import kotlin.math.ceil

/** Lo que se puede hacer en las ventanas de una meta. */
internal class AccionesDialogo(
    val alCambiarMonto: (String) -> Unit,
    val alUsarMonto: (Long) -> Unit,
    val alElegirFrecuencia: (Frecuencia) -> Unit,
    val alConfirmar: () -> Unit,
    val alCerrar: () -> Unit,
)

/** Muestra la ventana abierta (aportar, ajustar la regla o confirmar que se elimina), si hay una. Ninguna está en el diseño. */
@Composable
internal fun DialogoMetaAbierto(estado: EstadoMetas, hoy: Calendar, acciones: AccionesDialogo) {
    when (val dialogo = estado.dialogo) {
        null -> Unit
        is DialogoMeta.Aportar -> DialogoAportar(dialogo, estado, acciones)
        is DialogoMeta.AjustarRegla -> DialogoAjustarRegla(dialogo, estado, hoy, acciones)
        is DialogoMeta.Eliminar -> Dialogo(
            titulo = stringResource(R.string.metas_eliminar_titulo, dialogo.meta.nombre),
            textoConfirmar = stringResource(R.string.metas_eliminar_si),
            colorConfirmar = Colores.errorOscuro,
            estado = estado,
            acciones = acciones,
        ) {
            Text(text = stringResource(R.string.metas_eliminar_mensaje), style = EstilosTexto.cuerpo, color = Colores.textoApagado)
        }
    }
}

/** "Aportar a …": cuánto lleva, el monto y atajos con la regla y con lo que falta. */
@Composable
private fun DialogoAportar(dialogo: DialogoMeta.Aportar, estado: EstadoMetas, acciones: AccionesDialogo) {
    val meta = dialogo.meta
    Dialogo(
        titulo = stringResource(R.string.metas_aportar_titulo, meta.nombre),
        textoConfirmar = stringResource(R.string.metas_aportar_boton),
        colorConfirmar = Colores.azulIntenso,
        estado = estado,
        acciones = acciones,
    ) {
        Text(
            text = stringResource(R.string.metas_aportar_llevas, formatearPesos(meta.ahorrado), formatearPesos(meta.objetivo)),
            style = EstilosTexto.cuerpo,
            color = Colores.textoApagado,
        )
        CampoMontoDialogo(monto = estado.montoDialogo, alCambiar = acciones.alCambiarMonto)
        val regla = meta.montoAporte?.toLong()?.takeIf { it > 0 }
        val falta = ceil(CalculosMetas.falta(meta)).toLong()
        if (regla != null || falta > 0) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(Dimensiones.espacioPequeno),
            ) {
                if (regla != null) {
                    ChipMonto(
                        texto = stringResource(R.string.metas_usar_regla, formatearPesos(regla.toDouble())),
                        alPulsar = { acciones.alUsarMonto(regla) },
                        elegido = estado.montoDialogo == regla.toString(),
                    )
                }
                if (falta > 0) {
                    ChipMonto(
                        texto = stringResource(R.string.metas_usar_falta, formatearPesos(falta.toDouble())),
                        alPulsar = { acciones.alUsarMonto(falta) },
                        elegido = estado.montoDialogo == falta.toString(),
                    )
                }
            }
        }
    }
}

/** "Ajustar regla": frecuencia y monto, con el aporte que haría falta para llegar a tiempo. */
@Composable
private fun DialogoAjustarRegla(dialogo: DialogoMeta.AjustarRegla, estado: EstadoMetas, hoy: Calendar, acciones: AccionesDialogo) {
    val meta = dialogo.meta
    Dialogo(
        titulo = stringResource(R.string.metas_ajustar_regla),
        textoConfirmar = stringResource(R.string.metas_regla_guardar),
        colorConfirmar = Colores.azulIntenso,
        estado = estado,
        acciones = acciones,
    ) {
        Text(text = stringResource(R.string.metas_regla_pregunta, meta.nombre), style = EstilosTexto.cuerpo, color = Colores.textoApagado)
        SelectorFrecuencia(frecuencia = estado.frecuenciaDialogo, alElegir = acciones.alElegirFrecuencia)
        CampoMontoDialogo(monto = estado.montoDialogo, alCambiar = acciones.alCambiarMonto)
        val sugerido = CalculosMetas.aporteSugerido(meta, estado.frecuenciaDialogo, hoy)
        val mesYAno = CalculosMetas.mesYAno(meta, hoy)
        if (sugerido != null && mesYAno != null) {
            val meses = stringArrayResource(R.array.metas_meses)
            Text(
                text = stringResource(
                    R.string.metas_regla_sugerencia,
                    stringResource(R.string.metas_fecha_limite, meses[mesYAno.first], mesYAno.second),
                    formatearPesos(sugerido.toDouble()),
                    stringResource(if (estado.frecuenciaDialogo == Frecuencia.MENSUAL) R.string.metas_cada_mes else R.string.metas_cada_quincena),
                ),
                style = EstilosTexto.ayuda,
                color = Colores.textoApagado,
            )
            ChipMonto(
                texto = stringResource(R.string.metas_usar_sugerido, formatearPesos(sugerido.toDouble())),
                alPulsar = { acciones.alUsarMonto(sugerido) },
                elegido = estado.montoDialogo == sugerido.toString(),
            )
        }
    }
}

/** La ventana común: título, contenido, el error si lo hay, y los botones Cancelar y confirmar. */
@Composable
private fun Dialogo(
    titulo: String,
    textoConfirmar: String,
    colorConfirmar: Color,
    estado: EstadoMetas,
    acciones: AccionesDialogo,
    contenido: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = acciones.alCerrar,
        title = { Text(text = titulo, style = EstilosTexto.tituloAviso, color = Colores.tinta) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Dimensiones.espacioMediano)) {
                contenido()
                if (estado.errorDialogo != null) {
                    Text(text = stringResource(estado.errorDialogo), style = EstilosTexto.nota, color = Colores.errorOscuro)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = acciones.alConfirmar, enabled = !estado.guardando) {
                Text(text = textoConfirmar, style = EstilosTexto.boton, color = if (estado.guardando) Colores.textoTenue else colorConfirmar)
            }
        },
        dismissButton = {
            TextButton(onClick = acciones.alCerrar, enabled = !estado.guardando) {
                Text(text = stringResource(R.string.metas_cancelar), style = EstilosTexto.botonSecundario, color = Colores.textoApagado)
            }
        },
        containerColor = Colores.superficie,
    )
}

/** Campo del monto en pesos: "$", las cifras con puntos de miles y "COP". */
@Composable
private fun CampoMontoDialogo(monto: String, alCambiar: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimensiones.alturaCampoMontoDialogo)
            .background(Colores.rellenoCampo, RoundedCornerShape(Dimensiones.radioCampo))
            .padding(horizontal = Dimensiones.rellenoHorizontalCampoMontoDialogo),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = stringResource(R.string.nueva_meta_signo), style = EstilosTexto.tituloSeccion, color = Colores.azulIntenso)
        Spacer(Modifier.width(Dimensiones.espacioPequeno))
        BasicTextField(
            value = monto,
            onValueChange = alCambiar,
            singleLine = true,
            textStyle = EstilosTexto.tituloSeccion.copy(color = Colores.tinta, textAlign = TextAlign.End),
            cursorBrush = SolidColor(Colores.azulIntenso),
            visualTransformation = SeparadorMiles(Constantes.Formatos.SEPARADOR_MILES_PESOS),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            modifier = Modifier.weight(1f),
            decorationBox = { campo ->
                Box(contentAlignment = Alignment.CenterEnd) {
                    if (monto.isEmpty()) {
                        Text(text = stringResource(R.string.monto_ejemplo), style = EstilosTexto.tituloSeccion, color = Colores.grisClaro)
                    }
                    campo()
                }
            },
        )
        Spacer(Modifier.width(Dimensiones.espacioPequeno))
        Text(text = stringResource(R.string.nueva_meta_cop), style = EstilosTexto.nota, color = Colores.grisContorno)
    }
}
