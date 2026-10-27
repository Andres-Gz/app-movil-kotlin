package com.example.finanzia

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import com.example.finanzia.configuracion.Constantes
import com.example.finanzia.datos.ClienteSupabase
import com.example.finanzia.pantallas.asistente.PantallaAsistente
import com.example.finanzia.pantallas.bienvenida.PantallaBienvenida
import com.example.finanzia.datos.modelos.Meta
import com.example.finanzia.datos.modelos.MovimientoReciente
import com.example.finanzia.pantallas.editarmovimiento.PantallaEditarMovimiento
import com.example.finanzia.pantallas.editarmovimiento.ResultadoEdicion
import com.example.finanzia.pantallas.inicio.PantallaInicio
import com.example.finanzia.pantallas.iniciosesion.PantallaInicioSesion
import com.example.finanzia.pantallas.metas.PantallaMetas
import com.example.finanzia.pantallas.nuevameta.PantallaNuevaMeta
import com.example.finanzia.pantallas.nuevacontrasena.PantallaNuevaContrasena
import com.example.finanzia.pantallas.primergasto.PantallaPrimerGasto
import com.example.finanzia.pantallas.primeringreso.PantallaPrimerIngreso
import com.example.finanzia.pantallas.primerospasos.PantallaPrimerosPasos
import com.example.finanzia.pantallas.recuperarcontrasena.PantallaRecuperarContrasena
import com.example.finanzia.pantallas.registro.PantallaRegistro
import com.example.finanzia.tema.Colores
import com.example.finanzia.tema.TemaFinanzia
import io.github.jan.supabase.auth.handleDeeplinks
import kotlinx.serialization.json.Json

/** Las pantallas a las que se puede ir. */
private enum class Destino { INICIO_SESION, REGISTRO, RECUPERAR_CONTRASENA, NUEVA_CONTRASENA, BIENVENIDA, PRIMEROS_PASOS, PRIMER_INGRESO, PRIMER_GASTO, INICIO, EDITAR_MOVIMIENTO, METAS, NUEVA_META, EDITAR_META, ASISTENTE }

/** Lo que trajo el enlace del correo de recuperación. */
private enum class EnlaceRecuperacion { VALIDO, VENCIDO }

class ActividadPrincipal : ComponentActivity() {

    /** Último enlace de recuperación recibido; la interfaz lo atiende y lo vuelve a `null`. */
    private var enlaceRecibido by mutableStateOf<EnlaceRecuperacion?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // La app solo tiene tema claro: iconos oscuros en las barras del sistema.
        val barrasClaras = SystemBarStyle.light(Colores.transparente.toArgb(), Colores.transparente.toArgb())
        enableEdgeToEdge(statusBarStyle = barrasClaras, navigationBarStyle = barrasClaras)
        // Al girar la pantalla la actividad se recrea con el mismo intent: el enlace se atiende una sola vez.
        if (savedInstanceState == null) atenderEnlace(intent)
        setContent {
            TemaFinanzia {
                // Con pocas pantallas basta guardar cuál se ve; una librería de navegación sería de más.
                var destino by rememberSaveable { mutableStateOf(Destino.INICIO_SESION) }
                // Correo de la cuenta que inició sesión, para la bienvenida.
                var correoSesion by rememberSaveable { mutableStateOf("") }
                // Movimiento que se está editando, en JSON para que sobreviva al girar la pantalla.
                var movimientoEditado by rememberSaveable { mutableStateOf<String?>(null) }
                // Meta que se está editando, en JSON igual que el movimiento.
                var metaEditada by rememberSaveable { mutableStateOf<String?>(null) }
                // Desde dónde se abrieron las metas (la bienvenida o el inicio), para volver ahí.
                var origenMetas by rememberSaveable { mutableStateOf(Destino.INICIO) }
                val abrirMetasDesde = { origen: Destino ->
                    origenMetas = origen
                    destino = Destino.METAS
                }
                val volverAlInicio = { destino = Destino.INICIO_SESION }
                val enlace = enlaceRecibido
                LaunchedEffect(enlace) {
                    when (enlace) {
                        EnlaceRecuperacion.VALIDO -> destino = Destino.NUEVA_CONTRASENA
                        EnlaceRecuperacion.VENCIDO -> {
                            mostrarMensaje(getString(R.string.recuperar_enlace_vencido))
                            destino = Destino.RECUPERAR_CONTRASENA
                        }
                        null -> Unit
                    }
                    enlaceRecibido = null
                }
                // El botón Atrás del teléfono: del paso 2 vuelve al paso 1, de primeros pasos a la bienvenida,
                // de las metas a donde se abrieron, de crear o editar una meta a las metas, del asistente al inicio
                // y de las demás al inicio de sesión.
                // En el inicio (dashboard), Atrás cierra la app como siempre.
                BackHandler(enabled = destino != Destino.INICIO_SESION && destino != Destino.INICIO) {
                    destino = when (destino) {
                        Destino.NUEVA_CONTRASENA -> Destino.RECUPERAR_CONTRASENA
                        Destino.PRIMEROS_PASOS -> Destino.BIENVENIDA
                        Destino.PRIMER_INGRESO, Destino.PRIMER_GASTO -> Destino.PRIMEROS_PASOS
                        Destino.EDITAR_MOVIMIENTO, Destino.ASISTENTE -> Destino.INICIO
                        Destino.METAS -> origenMetas
                        Destino.NUEVA_META, Destino.EDITAR_META -> Destino.METAS
                        else -> Destino.INICIO_SESION
                    }
                }
                when (destino) {
                    Destino.INICIO_SESION -> PantallaInicioSesion(
                        alIniciarSesion = { usuario ->
                            correoSesion = usuario.correo
                            destino = Destino.BIENVENIDA
                        },
                        alOlvidarContrasena = { destino = Destino.RECUPERAR_CONTRASENA },
                        alRegistrarse = { destino = Destino.REGISTRO },
                    )
                    Destino.REGISTRO -> PantallaRegistro(
                        alVolver = volverAlInicio,
                        alCrearCuenta = { mensaje ->
                            mostrarMensaje(mensaje)
                            volverAlInicio()
                        },
                        alIniciarSesion = volverAlInicio,
                        // Aún no existen los textos de términos y privacidad.
                        alVerTerminos = {},
                        alVerPrivacidad = {},
                    )
                    Destino.RECUPERAR_CONTRASENA -> PantallaRecuperarContrasena(
                        alVolver = volverAlInicio,
                        alEnlaceEnviado = { correo ->
                            mostrarMensaje(getString(R.string.recuperar_enlace_enviado, correo))
                            volverAlInicio()
                        },
                        // Aún no existe el canal de soporte.
                        alContactarSoporte = {},
                    )
                    Destino.NUEVA_CONTRASENA -> PantallaNuevaContrasena(
                        alVolver = { destino = Destino.RECUPERAR_CONTRASENA },
                        alCancelar = volverAlInicio,
                        alContrasenaActualizada = {
                            mostrarMensaje(getString(R.string.nueva_contrasena_actualizada))
                            volverAlInicio()
                        },
                    )
                    Destino.BIENVENIDA -> PantallaBienvenida(
                        correo = correoSesion,
                        alComenzar = { destino = Destino.PRIMEROS_PASOS },
                        alPersonalizarMetas = { abrirMetasDesde(Destino.BIENVENIDA) },
                    )
                    Destino.PRIMEROS_PASOS -> PantallaPrimerosPasos(
                        alRegistrarIngreso = { destino = Destino.PRIMER_INGRESO },
                        alRegistrarGasto = { destino = Destino.PRIMER_GASTO },
                        alExplorarDespues = { destino = Destino.INICIO },
                    )
                    Destino.PRIMER_INGRESO -> PantallaPrimerIngreso(
                        alVolver = { destino = Destino.PRIMEROS_PASOS },
                        alIngresoGuardado = {
                            mostrarMensaje(getString(R.string.ingreso_guardado))
                            destino = Destino.PRIMER_GASTO
                        },
                    )
                    Destino.PRIMER_GASTO -> PantallaPrimerGasto(
                        alVolver = { destino = Destino.PRIMEROS_PASOS },
                        alGastoGuardado = {
                            mostrarMensaje(getString(R.string.gasto_guardado))
                            destino = Destino.INICIO
                        },
                        alOmitir = { destino = Destino.INICIO },
                        // Aún no existe la pantalla de categorías.
                        alAdministrarCategorias = {},
                    )
                    // El "+" lleva a elegir entre registrar un ingreso o un gasto.
                    Destino.INICIO -> PantallaInicio(
                        alAgregar = { destino = Destino.PRIMEROS_PASOS },
                        alEditarMovimiento = { movimiento ->
                            movimientoEditado = Json.encodeToString(movimiento)
                            destino = Destino.EDITAR_MOVIMIENTO
                        },
                        alAbrirMetas = { abrirMetasDesde(Destino.INICIO) },
                        alAbrirAsistente = { destino = Destino.ASISTENTE },
                    )
                    Destino.ASISTENTE -> PantallaAsistente(
                        alVolver = { destino = Destino.INICIO },
                        alAbrirInicio = { destino = Destino.INICIO },
                        alAbrirMetas = { abrirMetasDesde(Destino.ASISTENTE) },
                        alAvisar = { mensaje -> mostrarMensaje(getString(mensaje)) },
                    )
                    Destino.METAS -> PantallaMetas(
                        alVolver = { destino = origenMetas },
                        alNuevaMeta = { destino = Destino.NUEVA_META },
                        alEditarMeta = { meta ->
                            metaEditada = Json.encodeToString(meta)
                            destino = Destino.EDITAR_META
                        },
                        alAvisar = { mensaje -> mostrarMensaje(getString(mensaje)) },
                    )
                    // Mientras no exista el paso 2, la meta se guarda al pulsar "Continuar" en el paso 1.
                    Destino.NUEVA_META -> PantallaNuevaMeta(
                        meta = null,
                        alVolver = { destino = Destino.METAS },
                        alMetaGuardada = {
                            mostrarMensaje(getString(R.string.nueva_meta_guardada))
                            destino = Destino.METAS
                        },
                    )
                    Destino.EDITAR_META -> {
                        val meta = metaEditada?.let { Json.decodeFromString<Meta>(it) }
                        if (meta == null) {
                            LaunchedEffect(Unit) { destino = Destino.METAS }
                        } else {
                            PantallaNuevaMeta(
                                meta = meta,
                                alVolver = { destino = Destino.METAS },
                                alMetaGuardada = {
                                    mostrarMensaje(getString(R.string.metas_aviso_editada))
                                    metaEditada = null
                                    destino = Destino.METAS
                                },
                            )
                        }
                    }
                    Destino.EDITAR_MOVIMIENTO -> {
                        val movimiento = movimientoEditado?.let { Json.decodeFromString<MovimientoReciente>(it) }
                        if (movimiento == null) {
                            LaunchedEffect(Unit) { destino = Destino.INICIO }
                        } else {
                            PantallaEditarMovimiento(
                                movimiento = movimiento,
                                alVolver = { destino = Destino.INICIO },
                                alTerminar = { resultado ->
                                    mostrarMensaje(
                                        getString(
                                            if (resultado == ResultadoEdicion.GUARDADO) R.string.editar_guardado else R.string.editar_eliminado,
                                        ),
                                    )
                                    movimientoEditado = null
                                    destino = Destino.INICIO
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    // Con launchMode="singleTask", si la app ya estaba abierta el enlace llega por aquí.
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        atenderEnlace(intent)
    }

    /**
     * Si la app se abrió desde el enlace de recuperación, Supabase toma la sesión temporal que trae
     * y se pasa al paso 2. Si el enlace ya no sirve (vencido o usado), trae un error en vez de sesión.
     */
    private fun atenderEnlace(intent: Intent) {
        val datos = intent.data ?: return
        if (datos.scheme != Constantes.Enlaces.ESQUEMA_APP || datos.host != Constantes.Enlaces.HOST_RECUPERACION) return
        val parametros = datos.fragment.orEmpty().split('&') + datos.query.orEmpty().split('&')
        if (parametros.any { it.substringBefore('=') == Constantes.Enlaces.MARCA_ERROR }) {
            enlaceRecibido = EnlaceRecuperacion.VENCIDO
            return
        }
        ClienteSupabase.cliente.handleDeeplinks(intent) { enlaceRecibido = EnlaceRecuperacion.VALIDO }
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}
