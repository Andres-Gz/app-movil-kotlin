package com.example.finanzia.tema

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Cottage
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EditCalendar
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.HighlightOff
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material.icons.outlined.Laptop
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.PriceCheck
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material.icons.outlined.MarkEmailRead
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material.icons.rounded.Error
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes

/** Iconos de la app con nombre semántico: para cambiar uno, se cambia solo aquí. */
object Iconos {
    // Trazos de Lucide (lucide.dev, licencia ISC), los mismos que usa el diseño.
    val correo: ImageVector = iconoDeTrazos(
        "M4 4h16a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2z",
        "m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7",
    )
    val candado: ImageVector = iconoDeTrazos(
        "M5 11h14a2 2 0 0 1 2 2v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2z",
        "M7 11V7a5 5 0 0 1 10 0v4",
    )
    val mostrar: ImageVector = iconoDeTrazos(
        "M2.062 12.348a1 1 0 0 1 0-.696 10.75 10.75 0 0 1 19.876 0 1 1 0 0 1 0 .696 10.75 10.75 0 0 1-19.876 0",
        "M9 12a3 3 0 1 0 6 0a3 3 0 1 0-6 0",
    )
    val ocultar: ImageVector = iconoDeTrazos(
        "M10.733 5.076a10.744 10.744 0 0 1 11.205 6.575 1 1 0 0 1 0 .696 10.747 10.747 0 0 1-1.444 2.49",
        "M14.084 14.158a3 3 0 0 1-4.242-4.242",
        "M17.479 17.499a10.75 10.75 0 0 1-15.417-5.151 1 1 0 0 1 0-.696 10.75 10.75 0 0 1 4.446-5.143",
        "m2 2 20 20",
    )
    val usuario: ImageVector = iconoDeTrazos(
        "M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2",
        "M8 7a4 4 0 1 0 8 0a4 4 0 1 0-8 0",
    )
    val volver: ImageVector = iconoDeTrazos("m12 19-7-7 7-7", "M19 12H5")
    val siguiente: ImageVector = iconoDeTrazos("M5 12h14", "m12 5 7 7-7 7")
    val marcaVerificacion: ImageVector = iconoDeTrazos("M20 6 9 17l-5-5")
    val sumar: ImageVector = iconoDeTrazos("M5 12h14", "M12 5v14")
    val restar: ImageVector = iconoDeTrazos("M5 12h14")
    val abrir: ImageVector = iconoDeTrazos("m9 18 6-6-6-6")
    val cerrar: ImageVector = iconoDeTrazos("m15 18-6-6 6-6")
    val entrada: ImageVector = iconoDeTrazos("M12 5v14", "m19 12-7 7-7-7")
    val salida: ImageVector = iconoDeTrazos("M12 19V5", "m5 12 7-7 7 7")
    val confirmado: ImageVector = iconoDeTrazos("M21.801 10A10 10 0 1 1 17 3.335", "m9 11 3 3L22 4")
    val rayo: ImageVector = iconoDeTrazos(
        "M4 14a1 1 0 0 1-.78-1.63l9.9-10.2a.5.5 0 0 1 .86.46l-1.92 6.02A1 1 0 0 0 13 10h7a1 1 0 0 1 .78 1.63l-9.9 10.2a.5.5 0 0 1-.86-.46l1.92-6.02A1 1 0 0 0 11 14z",
    )

    /** Flecha del botón del inicio de sesión: de punta más pequeña que [siguiente]. */
    val continuar: ImageVector = iconoDeTrazos("M5 12h14", "m14 7 5 5-5 5")

    /** Estrella de cuatro puntas junto al nombre del logo. */
    val destello: ImageVector = iconoRelleno(
        "M12 0C12.6 6.6 17.4 11.4 24 12C17.4 12.6 12.6 17.4 12 24C11.4 17.4 6.6 12.6 0 12C6.6 11.4 11.4 6.6 12 0Z",
    )

    val error: ImageVector = Icons.Rounded.Error

    // Iconos de Material, los que usa el diseño de recuperar contraseña.
    val atras: ImageVector = Icons.AutoMirrored.Rounded.ArrowBack
    val adelante: ImageVector = Icons.AutoMirrored.Rounded.ArrowForward
    val persona: ImageVector = Icons.Outlined.Person
    val sobre: ImageVector = Icons.Outlined.Email
    val restablecerClave: ImageVector = Icons.Outlined.LockReset
    val soporte: ImageVector = Icons.Filled.HelpCenter
    val escudo: ImageVector = Icons.Outlined.Shield
    val escudoVerificado: ImageVector = Icons.Outlined.VerifiedUser

    // Iconos de Material del diseño de nueva contraseña.
    val candadoMaterial: ImageVector = Icons.Outlined.Lock
    val mostrarMaterial: ImageVector = Icons.Outlined.Visibility
    val ocultarMaterial: ImageVector = Icons.Outlined.VisibilityOff
    val requisitoCumplido: ImageVector = Icons.Filled.CheckCircle
    val requisitoPendiente: ImageVector = Icons.Outlined.RadioButtonUnchecked
    val verificado: ImageVector = Icons.Filled.Verified
    val advertencia: ImageVector = Icons.Outlined.WarningAmber
    val errorContorno: ImageVector = Icons.Outlined.ErrorOutline

    // Iconos de Material del diseño de bienvenida.
    val flechaPildora: ImageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft
    val escudoListo: ImageVector = Icons.Filled.VerifiedUser
    /** Marca de verificación de los logros: más gruesa que [marcaVerificacion]. */
    val logroCumplido: ImageVector = iconoDeTrazos("M20 6 9 17l-5-5", grosor = GROSOR_TRAZO_GRUESO)
    val correoEnviado: ImageVector = Icons.Outlined.MarkEmailRead
    val sincronizar: ImageVector = Icons.Filled.Cached

    // Iconos de Material del diseño del primer gasto.
    val informacion: ImageVector = Icons.Outlined.Info
    val ajustes: ImageVector = Icons.Outlined.Settings

    // Iconos de Material del inicio (dashboard).
    val asistente: ImageVector = Icons.Filled.AutoAwesome
    val notificaciones: ImageVector = Icons.Outlined.Notifications
    val banco: ImageVector = Icons.Outlined.AccountBalance
    val robot: ImageVector = Icons.Outlined.SmartToy
    val tendencia: ImageVector = Icons.AutoMirrored.Filled.TrendingUp
    val metaAhorro: ImageVector = Icons.Outlined.PriceCheck
    val favorito: ImageVector = Icons.Outlined.StarOutline
    val conversar: ImageVector = Icons.Outlined.Forum
    val agregar: ImageVector = Icons.Filled.Add
    val cobro: ImageVector = Icons.Outlined.Payments
    val hogar: ImageVector = Icons.Outlined.Home
    val compras: ImageVector = Icons.Outlined.ShoppingCart
    val celebracion: ImageVector = Icons.Outlined.Celebration
    val carro: ImageVector = Icons.Outlined.DirectionsCar
    val recibo: ImageVector = Icons.AutoMirrored.Outlined.ReceiptLong
    val navInicio: ImageVector = Icons.Outlined.Home
    val navGraficos: ImageVector = Icons.Outlined.BarChart
    val navBilletera: ImageVector = Icons.Outlined.AccountBalanceWallet
    val navMetas: ImageVector = Icons.Outlined.TrackChanges

    // Iconos de Material del diseño de metas.
    val agregarCirculo: ImageVector = Icons.Outlined.AddCircleOutline
    val alcancia: ImageVector = Icons.Filled.Savings
    val bandera: ImageVector = Icons.Filled.Flag
    val desplegar: ImageVector = Icons.Filled.KeyboardArrowDown
    val masOpciones: ImageVector = Icons.Filled.MoreVert
    val reloj: ImageVector = Icons.Outlined.Schedule
    val calendario: ImageVector = Icons.Outlined.CalendarToday
    val ajustarRegla: ImageVector = Icons.Filled.Tune

    // Iconos de Material del diseño de nueva meta (el escudo de "Emergencia" es [escudo]).
    val ayuda: ImageVector = Icons.AutoMirrored.Outlined.HelpOutline
    val banderaContorno: ImageVector = Icons.Outlined.Flag
    val borrarTexto: ImageVector = Icons.Outlined.HighlightOff
    val playa: ImageVector = Icons.Filled.BeachAccess
    val portatil: ImageVector = Icons.Outlined.Laptop
    val moto: ImageVector = Icons.Filled.TwoWheeler
    val casa: ImageVector = Icons.Outlined.Cottage
    val estudio: ImageVector = Icons.Outlined.School
    val calendarioMes: ImageVector = Icons.Outlined.CalendarMonth
    val editarFecha: ImageVector = Icons.Outlined.EditCalendar

    // Menú de cada meta (no está en el diseño).
    val editar: ImageVector = Icons.Outlined.Edit
    val eliminar: ImageVector = Icons.Outlined.DeleteOutline

    // Asistente (Finanzia IA)
    val navAsistente: ImageVector = robot
    val insigniaVerificada: ImageVector = Icons.Outlined.Verified
    val historial: ImageVector = Icons.Outlined.History
    val idea: ImageVector = Icons.Outlined.Lightbulb
    val alcanciaContorno: ImageVector = Icons.Outlined.Savings
    val microfono: ImageVector = Icons.Outlined.Mic
    val enviar: ImageVector = salida
}

/** Lado de la cuadrícula en la que se dibujan los iconos propios. */
private const val LADO_CUADRICULA = 24f
private const val GROSOR_TRAZO = 2f
private const val GROSOR_TRAZO_GRUESO = 3.5f

/** Icono de líneas, como los de Lucide. El color lo pone quien lo muestra (`tint`). */
private fun iconoDeTrazos(vararg trazos: String, grosor: Float = GROSOR_TRAZO): ImageVector = construirIcono { constructor ->
    trazos.forEach { trazo ->
        constructor.addPath(
            pathData = addPathNodes(trazo),
            stroke = SolidColor(Colores.textoPrincipal),
            strokeLineWidth = grosor,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
    }
}

/** Icono de una figura rellena. */
private fun iconoRelleno(figura: String): ImageVector = construirIcono { constructor ->
    constructor.addPath(pathData = addPathNodes(figura), fill = SolidColor(Colores.textoPrincipal))
}

private fun construirIcono(dibujar: (ImageVector.Builder) -> Unit): ImageVector =
    ImageVector.Builder(
        defaultWidth = Dimensiones.tamanoBaseIcono,
        defaultHeight = Dimensiones.tamanoBaseIcono,
        viewportWidth = LADO_CUADRICULA,
        viewportHeight = LADO_CUADRICULA,
    ).also(dibujar).build()
