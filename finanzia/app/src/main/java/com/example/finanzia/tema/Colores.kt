package com.example.finanzia.tema

import androidx.compose.ui.graphics.Color

/**
 * Paleta de la app, medida sobre los diseños (son colores de Tailwind).
 * Es el único lugar del código donde se escriben valores de color.
 */
object Colores {
    // Marca
    val verdeMarca = Color(0xFF10B981)
    val verdeOscuro = Color(0xFF059669)
    val verdeDestello = Color(0xFF34D399)
    val azulMarca = Color(0xFF2563EB)
    val azulProfundo = Color(0xFF0D1B3D)

    // Fondos, superficies y bordes
    val fondo = Color(0xFFFAFBFD)
    val superficie = Color(0xFFFFFFFF)
    val bordeCampo = Color(0xFFE8EDF3)

    // Textos e iconos, del más oscuro al más claro
    val textoPrincipal = Color(0xFF0F172A)
    val textoCampo = Color(0xFF1E293B)
    val textoFuerte = Color(0xFF334155)
    val textoSecundario = Color(0xFF64748B)
    val textoTenue = Color(0xFF94A3B8)
    val grisClaro = Color(0xFFCBD5E1)
    val iconoCampo = textoTenue
    val textoEjemplo = grisClaro
    val textoSobreColor = Color(0xFFFFFFFF)

    // Errores, del más claro al más oscuro
    val errorFondoCampo = Color(0xFFFCF8F9)
    val errorFondo = Color(0xFFFEF2F2)
    val errorBordeSuave = Color(0xFFFECACA)
    val errorClaro = Color(0xFFF87171)
    val error = Color(0xFFEF4444)
    val errorOscuro = Color(0xFFDC2626)
    val errorMuyOscuro = Color(0xFFB91C1C)

    // Sombras y transparencia
    val sombra = Color(0xFF0F172A)
    val transparente = Color.Transparent

    // Degradados
    val degradadoBoton = listOf(verdeMarca, azulMarca)

    // Fondo del inicio de sesión: dos brillos verdes en las esquinas de abajo y dos olas a la derecha
    val brilloEsquinaIzquierda = arrayOf(
        0f to verdeMarca.copy(alpha = 0.085f),
        0.35f to verdeMarca.copy(alpha = 0.06f),
        0.57f to verdeMarca.copy(alpha = 0.03f),
        0.9f to verdeMarca.copy(alpha = 0.004f),
        1f to verdeMarca.copy(alpha = 0f),
    )
    val brilloEsquinaDerecha = arrayOf(
        0f to verdeMarca.copy(alpha = 0.19f),
        0.3f to verdeMarca.copy(alpha = 0.125f),
        0.58f to verdeMarca.copy(alpha = 0.064f),
        0.86f to verdeMarca.copy(alpha = 0.013f),
        1f to verdeMarca.copy(alpha = 0f),
    )
    val olaClara = Color(0xFF98E6CB)
    val olaOscura = Color(0xFF8ADDC2)

    // Paleta de recuperar contraseña (ese diseño usa tonos propios, más fríos)
    val fondoLavanda = Color(0xFFF8F9FF)
    val rellenoCampo = Color(0xFFEFF4FF)
    val fondoInformativo = Color(0xFFEAF2FF)
    val tinta = Color(0xFF0B1C30)
    val textoApagado = Color(0xFF434655)
    val grisContorno = Color(0xFF737686)
    val azulIntenso = Color(0xFF004AC6)
    val verdeSeguro = Color(0xFF006C49)
    val verdePunto = Color(0xFF4EDEA3)
    val moradoAyuda = Color(0xFF5D41AB)
    val azulPildora = Color(0xFFDCE9FF)
    val azulBotonClaro = Color(0xFFE5EEFF)
    val verdeMenta = Color(0xFF6CF8BB)
    val verdeSobreMenta = Color(0xFF00714D)
    val errorContenedor = Color(0xFFFFDAD6)
    val errorIntenso = Color(0xFFBA1A1A)
    val errorProfundo = Color(0xFF93000A)
    val errorProfundoSuave = errorProfundo.copy(alpha = 0.8f)
    val degradadoBotonFrio = listOf(verdeSeguro, azulIntenso)

    // Bienvenida: la misma paleta fría, con unos tonos más para la lista y sus insignias
    val fondoLista = Color(0xFFF5F8FF)
    val fondoConfirmacion = Color(0xFFE2ECFF)
    val verdeNoche = Color(0xFF002113)
    val lavandaInsignia = Color(0xFFE8DDFF)
    val moradoNoche = Color(0xFF21005E)

    // Primeros pasos (paleta de Tailwind, como el inicio de sesión)
    val textoMedio = Color(0xFF475569)
    val verdeNieve = Color(0xFFECFDF5)
    val verdeNieveBorde = Color(0xFFD1FAE5)
    val azulSuave = Color(0xFFDBEAFE)
    val rosaIntenso = Color(0xFFF43F5E)
    val blancoTranslucido = Color(0x33FFFFFF)

    // Primer ingreso: fondos de los iconos de cada frecuencia y del consejo
    val indigoNieve = Color(0xFFEEF2FF)
    val amarilloNieve = Color(0xFFFEF9C3)
    val degradadoConsejo = listOf(rellenoCampo, verdeNieve)

    // Primer gasto: acento rosa (rose de Tailwind)
    val rosaNieve = Color(0xFFFFF1F2)
    val rosaNieveBorde = Color(0xFFFFE4E6)
    val rosaBruma = Color(0xFFFFF6F5)
    val rosaSeleccion = Color(0xFFFCF5F8)
    val rosaOscuro = Color(0xFFE11D48)
    val rosaProfundo = Color(0xFFBE123C)

    // Inicio (dashboard)
    val mentaClara = Color(0xFF97F5CE)
    val indigoClaro = Color(0xFFDBE1FF)
    val blancoVelo = Color(0x26FFFFFF)
    val blancoTenue = Color(0xB3FFFFFF)
    val degradadoBalance = listOf(azulMarca, azulIntenso, verdeSeguro)
    val degradadoConversar = listOf(verdeSeguro, azulMarca)

    // Metas: brillo menta arriba a la derecha de la tarjeta del ahorro, barras y tarjeta de la racha
    val azulBruma = Color(0xFFD3E4FE)
    val celesteMascota = Color(0xFFDEF3FE)
    val brilloMentaAhorro = arrayOf(0f to verdeMenta.copy(alpha = 0.32f), 1f to verdeMenta.copy(alpha = 0f))
    val brilloLavandaAhorro = arrayOf(0f to rellenoCampo, 1f to rellenoCampo.copy(alpha = 0f))
    val degradadoAhorroTotal = listOf(verdeSeguro, azulMarca)
    val degradadoAvanceMeta = listOf(verdeSeguro, azulIntenso)
    val degradadoRacha = listOf(azulBotonClaro, rellenoCampo)

    // Fondo del registro: brillo lavanda abajo a la izquierda y menta abajo a la derecha
    private val lavanda = Color(0xFFEEF3FD)
    private val menta = Color(0xFFC7F3E3)
    val brilloLavanda = arrayOf(
        0f to lavanda,
        0.17f to lavanda.copy(alpha = 0.82f),
        0.33f to lavanda.copy(alpha = 0.59f),
        0.49f to lavanda.copy(alpha = 0.41f),
        0.65f to lavanda.copy(alpha = 0.18f),
        1f to lavanda.copy(alpha = 0f),
    )
    val brilloMenta = arrayOf(
        0f to menta,
        0.31f to menta.copy(alpha = 0.73f),
        0.53f to menta.copy(alpha = 0.55f),
        0.62f to menta.copy(alpha = 0.46f),
        0.78f to menta.copy(alpha = 0.25f),
        0.9f to menta.copy(alpha = 0.125f),
        1f to menta.copy(alpha = 0f),
    )
}
