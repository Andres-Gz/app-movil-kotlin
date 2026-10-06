package com.example.finanzia.tema

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/** Nuestra paleta en los roles de Material, para que sus componentes la usen por defecto. */
private val esquemaColores = lightColorScheme(
    primary = Colores.verdeMarca,
    onPrimary = Colores.textoSobreColor,
    secondary = Colores.azulMarca,
    onSecondary = Colores.textoSobreColor,
    background = Colores.fondo,
    onBackground = Colores.textoPrincipal,
    surface = Colores.superficie,
    onSurface = Colores.textoPrincipal,
    onSurfaceVariant = Colores.textoSecundario,
    outline = Colores.divisor,
    outlineVariant = Colores.divisor,
    error = Colores.error,
)

/** Tema de la app: solo nuestra paleta y tipografía (sin color dinámico ni modo oscuro por ahora). */
@Composable
fun TemaFinanzia(contenido: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = esquemaColores,
        typography = tipografiaMaterial,
        content = contenido,
    )
}
