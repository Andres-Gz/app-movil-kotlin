package com.example.finanzia.pantallas.nuevameta

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.example.finanzia.R
import com.example.finanzia.datos.modelos.Frecuencia
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.Dimensiones
import com.example.finanzia.tema.EstilosTexto
import com.example.finanzia.tema.Iconos
import java.util.Calendar

/** Las dos frecuencias que ofrece el diseño, con su texto. */
private val frecuencias = listOf(
    Frecuencia.QUINCENAL to R.string.nueva_meta_quincenal,
    Frecuencia.MENSUAL to R.string.nueva_meta_mensual,
)

/** "Fecha estimada de logro" (abre el selector del mes) y "Frecuencia del aporte". */
@Composable
internal fun TarjetaFechaMeta(
    mes: MesAno?,
    frecuencia: Frecuencia,
    hoy: Calendar,
    alAbrirSelector: () -> Unit,
    alElegirFrecuencia: (Frecuencia) -> Unit,
) {
    val forma = RoundedCornerShape(Dimensiones.radioBoton)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(Dimensiones.elevacionCampo, forma, ambientColor = Colores.sombra, spotColor = Colores.sombra)
            .background(Colores.superficie, forma)
            .padding(Dimensiones.espacioGrande),
    ) {
        Text(text = stringResource(R.string.nueva_meta_etiqueta_fecha), style = EstilosTexto.botonSecundario, color = Colores.tinta)
        Spacer(Modifier.height(Dimensiones.espacioEtiquetaSeccionMeta))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimensiones.alturaCampoFechaMeta)
                .clip(RoundedCornerShape(Dimensiones.radioCampo))
                .background(Colores.rellenoCampo)
                .clickable(role = Role.Button, onClick = alAbrirSelector)
                .padding(horizontal = Dimensiones.rellenoHorizontalCampoFecha),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Iconos.calendarioMes, null, tint = Colores.azulIntenso, modifier = Modifier.size(Dimensiones.tamanoIcono))
            Spacer(Modifier.width(Dimensiones.espacioMediano))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = textoMes(mes), style = EstilosTexto.tituloAviso, color = Colores.tinta)
                Text(text = detalleMes(mes, frecuencia, hoy), style = EstilosTexto.ayuda, color = Colores.textoApagado)
            }
            Icon(
                imageVector = Iconos.editarFecha,
                contentDescription = stringResource(R.string.accion_elegir_fecha),
                tint = Colores.grisContorno,
                modifier = Modifier.size(Dimensiones.tamanoIcono),
            )
        }
        Spacer(Modifier.height(Dimensiones.espacioFechaFrecuencia))
        Text(text = stringResource(R.string.nueva_meta_etiqueta_frecuencia), style = EstilosTexto.botonSecundario, color = Colores.tinta)
        Spacer(Modifier.height(Dimensiones.espacioEtiquetaSeccionMeta))
        SelectorFrecuencia(frecuencia = frecuencia, alElegir = alElegirFrecuencia)
    }
}

/** "Quincenal (15 y 30)" o "Mensual (Día 30)". También lo usa "Ajustar regla" en la pantalla de metas. */
@Composable
internal fun SelectorFrecuencia(frecuencia: Frecuencia, alElegir: (Frecuencia) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimensiones.alturaSelectorFrecuencia)
            .background(Colores.azulBotonClaro, RoundedCornerShape(Dimensiones.radioCampo))
            .padding(Dimensiones.rellenoSelectorFrecuencia),
    ) {
        frecuencias.forEach { (opcion, texto) ->
            OpcionFrecuencia(
                texto = texto,
                elegida = opcion == frecuencia,
                alElegir = { alElegir(opcion) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/** Una mitad del selector de frecuencia: la elegida queda en blanco y con sombra. */
@Composable
private fun OpcionFrecuencia(@StringRes texto: Int, elegida: Boolean, alElegir: () -> Unit, modifier: Modifier = Modifier) {
    val forma = RoundedCornerShape(Dimensiones.radioBotonMeta)
    Box(
        modifier = modifier
            .fillMaxHeight()
            .shadow(if (elegida) Dimensiones.elevacionCampo else Dimensiones.elevacionNula, forma, ambientColor = Colores.sombra, spotColor = Colores.sombra)
            .background(if (elegida) Colores.superficie else Colores.transparente, forma)
            .clip(forma)
            .selectable(selected = elegida, role = Role.RadioButton, onClick = alElegir),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(texto),
            style = EstilosTexto.botonSecundario,
            color = if (elegida) Colores.tinta else Colores.textoApagado,
            maxLines = 1,
        )
    }
}

/** "Noviembre 2026", o "Elige el mes" si todavía no hay. */
@Composable
private fun textoMes(mes: MesAno?): String {
    if (mes == null) return stringResource(R.string.nueva_meta_sin_fecha)
    val meses = stringArrayResource(R.array.metas_meses)
    return stringResource(R.string.metas_fecha_limite, meses[mes.mes], mes.ano)
}

/** "8 meses restantes • 16 quincenas", con los aportes que de verdad caen hasta ese mes. */
@Composable
private fun detalleMes(mes: MesAno?, frecuencia: Frecuencia, hoy: Calendar): String {
    if (mes == null) return stringResource(R.string.nueva_meta_sin_fecha_detalle)
    val meses = CalculosNuevaMeta.mesesRestantes(mes, hoy)
    val aportes = CalculosNuevaMeta.aportes(mes, frecuencia, hoy)
    val textoAportes = if (frecuencia == Frecuencia.QUINCENAL) {
        pluralStringResource(R.plurals.nueva_meta_quincenas, aportes, aportes)
    } else {
        pluralStringResource(R.plurals.nueva_meta_aportes_mensuales, aportes, aportes)
    }
    return stringResource(
        R.string.nueva_meta_detalle_fecha,
        pluralStringResource(R.plurals.nueva_meta_meses_restantes, meses, meses),
        textoAportes,
    )
}
