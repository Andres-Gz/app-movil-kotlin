package com.example.finanzia.tema

import androidx.compose.ui.unit.dp

/** Todas las medidas de la app: espacios, esquinas, alturas, tamaños y sombras, tomadas de los diseños. */
object Dimensiones {
    // Espacios generales
    val espacioMinimo = 4.dp
    val espacioPequeno = 8.dp
    val espacioMediano = 12.dp
    val espacioGrande = 16.dp
    val margenPantalla = 24.dp

    // Espacios del inicio de sesión, de arriba hacia abajo
    val espacioEsloganSaludo = 13.dp
    val espacioSaludoDescripcion = 1.dp
    val espacioDescripcionFormulario = 16.dp
    val espacioFormularioEnlace = 14.dp
    val espacioEnlaceBoton = 16.dp
    val espacioPieInferior = 28.dp

    // Espacios del registro, de arriba hacia abajo
    val rellenoSuperiorEncabezado = 4.dp
    val rellenoInicioEncabezado = 20.dp
    val rellenoFinEncabezado = 22.dp
    val espacioEncabezadoLogo = 5.dp
    val espacioEsloganTitulo = 18.dp
    val espacioTituloDescripcion = 2.dp
    val espacioEtiquetaCampo = 7.dp
    val espacioRequisitosTerminos = 20.dp
    val espacioTerminosBoton = 22.dp

    // Espacios de recuperar contraseña, de arriba hacia abajo
    val margenPantallaAmplio = 32.dp
    val rellenoSuperiorEncabezadoAmplio = 8.dp
    val espacioEncabezadoLogoAmplio = 16.dp
    val espacioEsloganTarjeta = 16.dp
    val espacioTituloDescripcionTarjeta = 6.dp
    val espacioDescripcionCampos = 24.dp
    val espacioEtiquetaCampoRelleno = 6.dp
    val espacioEntreCamposRelleno = 14.dp
    val espacioCamposAyuda = 20.dp
    val espacioTarjetaBoton = 16.dp
    val espacioEntreBotones = 12.dp
    val espacioBotonesPie = 20.dp
    val espacioPieInferiorAmplio = 24.dp

    // Espacios de la nueva contraseña (paso 2), de arriba hacia abajo
    val espacioBarraEstado = 8.dp
    val espacioEstadoAviso = 16.dp
    val espacioAvisoTarjeta = 22.dp
    val espacioTarjetaCampos = 24.dp
    val espacioEtiquetaCampoElevado = 8.dp
    val espacioRequisitosConfirmacion = 16.dp
    val espacioCampoMensajeElevado = 8.dp
    val espacioMensajeMostrar = 16.dp
    val espacioMostrarActualizar = 24.dp
    val espacioCancelarPie = 32.dp

    // Espacios de la bienvenida, de arriba hacia abajo
    val espacioSuperiorBienvenida = 12.dp
    val espacioPildoraTitulo = 18.dp
    val espacioTituloDescripcionBienvenida = 8.dp
    val espacioDescripcionLista = 20.dp
    val espacioEncabezadoLista = 19.dp
    val espacioEntreLogros = 11.dp
    val espacioListaCorreo = 15.dp
    val espacioCorreoBotones = 22.dp
    val espacioEntreBotonesBienvenida = 7.dp
    val espacioBotonesPieBienvenida = 14.dp

    // Píldora "¡Cuenta creada con éxito!": cohete, texto y flecha
    val alturaPildoraBienvenida = 26.dp
    val rellenoInicioPildoraBienvenida = 16.dp
    val rellenoFinPildoraBienvenida = 10.dp
    val espacioEmojiPildora = 10.dp
    val espacioTextoFlechaPildora = 6.dp
    val tamanoFlechaPildora = 20.dp

    // Tarjeta "Todo listo para comenzar" y sus logros
    val rellenoTarjetaLista = 15.dp
    val tamanoRecuadroIcono = 30.dp
    val espacioRecuadroTitulo = 8.dp
    val rellenoVerticalLogro = 5.dp
    val rellenoInicioLogro = 7.dp
    val rellenoFinLogro = 8.dp
    val tamanoCirculoLogro = 23.dp
    val rellenoSuperiorCirculoLogro = 3.dp
    val espacioCirculoTextoLogro = 12.dp
    val rellenoHorizontalContador = 8.dp
    val rellenoHorizontalInsignia = 6.dp
    val rellenoVerticalInsignia = 0.dp

    // Aviso del correo de confirmación
    val alturaAvisoCorreo = 56.dp
    val rellenoInicioAvisoCorreo = 11.dp
    val rellenoFinAvisoCorreo = 16.dp
    val tamanoCirculoCorreo = 34.dp
    val espacioCirculoTextoCorreo = 12.dp
    val espacioTextoIconoCorreo = 24.dp

    // Botones de la bienvenida
    val espacioIconoBotonBienvenida = 11.dp
    val alturaBotonMediano = 44.dp

    // Espacios de primeros pasos, de arriba hacia abajo
    val espacioPildoraLogo = 14.dp
    val espacioLogoInsignia = 7.dp
    val espacioLogoTitulo = 13.dp
    val espacioTituloEslogan = 2.dp
    val espacioEsloganDescripcion = 10.dp
    val espacioDescripcionSugerencia = 20.dp
    val espacioEntreOpciones = 14.dp
    val espacioOpcionesEnlace = 21.dp
    val espacioEnlacePie = 18.dp

    // Tarjeta de sugerencia y botones de opción (primeros pasos)
    val rellenoHorizontalSugerencia = 14.dp
    val rellenoVerticalSugerencia = 12.dp
    val espacioRecuadroTextoSugerencia = 10.dp
    val alturaOpcion = 74.dp
    val rellenoInicioOpcion = 18.dp
    val rellenoFinOpcion = 18.dp
    val espacioIconoTextoOpcion = 12.dp
    val radioRecuadroIcono = 8.dp

    // Primer ingreso, de arriba hacia abajo
    val espacioSuperiorIngreso = 8.dp
    val tamanoBotonVolverIngreso = 35.dp
    val radioBotonVolverIngreso = 10.dp
    val espacioBarraTitulo = 22.dp
    val tamanoRecuadroTitulo = 26.dp
    val espacioTituloDescripcionIngreso = 4.dp
    val espacioDescripcionMonto = 18.dp
    val rellenoTarjetaMonto = 16.dp
    val espacioEtiquetaMonto = 10.dp
    val espacioSimboloMonto = 10.dp
    val alturaCampoMonto = 44.dp
    val espacioMontoAyuda = 8.dp
    val espacioIconoAyudaMonto = 6.dp
    val espacioMontoFrecuencias = 22.dp
    val espacioEncabezadoFrecuencias = 12.dp
    val alturaFrecuencia = 117.dp
    val espacioEntreFrecuencias = 10.dp
    val rellenoFrecuencia = 14.dp
    val radioRecuadroFrecuencia = 10.dp
    val tamanoMarcaFrecuencia = 20.dp
    val grosorMarcaVacia = 2.dp
    val espacioFrecuenciasConsejo = 16.dp
    val rellenoConsejo = 12.dp
    val rellenoVerticalBarraInferior = 12.dp

    // Primer gasto: monto subrayado y tarjetas de categoría más bajas
    val espacioMontoSubrayado = 6.dp
    val grosorSubrayadoMonto = 2.dp
    val alturaCategoria = 94.dp
    val rellenoCategoria = 12.dp
    val tamanoRecuadroCategoria = 24.dp
    val espacioIconoEnlace = 4.dp

    // Inicio (dashboard), de arriba hacia abajo
    val margenInicio = 20.dp
    val alturaBarraInicio = 56.dp
    val tamanoLogoApp = 34.dp
    val radioLogoApp = 10.dp
    val tamanoPuntoAviso = 7.dp
    val espacioBarraSaludo = 8.dp
    val espacioEntreSecciones = 16.dp
    val radioTarjetaGrande = 20.dp
    val rellenoBalance = 20.dp
    val tamanoBotonOjo = 32.dp
    val anchoCurvaBalance = 96.dp
    val altoCurvaBalance = 44.dp
    val grosorCurvaBalance = 2.5.dp
    val radioPuntoCurva = 3.5.dp
    val espacioBalanceResumen = 14.dp
    val rellenoResumenBalance = 10.dp
    val tamanoIconoResumen = 28.dp
    val tamanoRecuadroRobot = 38.dp
    val tamanoInsigniaRobot = 14.dp
    val tamanoIconoInsignia = 9.dp
    val rellenoTarjetaIa = 14.dp
    val tamanoInicial = 18.dp
    val solapeIniciales = (-5).dp
    val alturaBarraMeta = 8.dp
    val tamanoIconoMeta = 52.dp
    val tamanoIconoMovimiento = 38.dp
    val rellenoMovimiento = 12.dp
    val alturaNavegacion = 64.dp
    val tamanoBotonAgregar = 52.dp
    val anchoSeleccionNavegacion = 44.dp
    val altoSeleccionNavegacion = 28.dp

    // Metas, de arriba hacia abajo (los márgenes de las tarjetas son margenPantallaAmplio)
    val alturaEncabezadoMetas = 58.dp
    val margenEncabezadoMetas = 30.dp
    val alturaBotonNuevaMeta = 37.dp
    val espacioEncabezadoMetas = 10.dp
    val radioBrilloMentaAhorro = 150.dp
    val radioBrilloLavandaAhorro = 190.dp
    val elevacionTarjetaMetas = 3.dp
    val rellenoTarjetaAhorro = 18.dp
    val anchoRecuadroAlcancia = 24.dp
    val altoRecuadroAlcancia = 30.dp
    val rellenoHorizontalPildoraAvance = 10.dp
    val tamanoCirculoConsejo = 26.dp
    val espacioCirculoConsejo = 10.dp
    val tamanoPuntoConsejo = 5.dp
    val espacioAhorroRacha = 20.dp
    val rellenoHorizontalRacha = 15.dp
    val rellenoVerticalRacha = 10.dp
    val tamanoMascota = 44.dp
    val grosorBordeMascota = 2.dp
    val tamanoInsigniaMascota = 16.dp
    val tamanoMarcaInsignia = 10.dp
    val espacioRachaLista = 20.dp
    val espacioIconoTituloLista = 10.dp
    val espacioOrdenarFlecha = 2.dp
    val rellenoTarjetaMeta = 15.dp
    val anchoRecuadroEmoji = 34.dp
    val altoRecuadroEmoji = 40.dp
    val espacioTituloEtiquetasMeta = 2.dp
    val rellenoHorizontalEtiquetaMeta = 8.dp
    val alturaEtiquetaMeta = 18.dp
    val tamanoIconoFecha = 12.dp
    val alturaBarraAvanceMeta = 6.dp
    val espacioBarraAporte = 14.dp
    val rellenoHorizontalAporte = 12.dp
    val rellenoVerticalAporte = 7.dp
    val radioAporte = 8.dp
    val espacioAporteBotones = 18.dp
    val alturaBotonMeta = 36.dp
    val radioBotonMeta = 10.dp
    val espacioIconoTextoBotonMeta = 6.dp
    val espacioFinalMetas = 24.dp

    // Nueva meta, de arriba hacia abajo (los márgenes son margenInicio)
    val tamanoBotonEncabezadoMeta = 36.dp
    val espacioEncabezadoObjetivo = 8.dp
    val rellenoTarjetaObjetivo = 14.dp
    val alturaPildoraObjetivo = 22.dp
    val espacioEntrePildorasObjetivo = 6.dp
    val espacioPildorasTitulo = 8.dp
    val espacioTituloDescripcionObjetivo = 4.dp
    val espacioEntreSeccionesMeta = 18.dp
    val espacioEtiquetaSeccionMeta = 6.dp
    val alturaCampoNombreMeta = 56.dp
    val rellenoCampoNombreMeta = 12.dp
    val tamanoRecuadroBandera = 34.dp
    val radioRecuadroBandera = 10.dp
    val alturaCategoriaMeta = 62.dp
    val espacioEntreCategoriasMeta = 10.dp
    val tamanoIconoCategoriaMeta = 22.dp
    val espacioIconoTextoCategoriaMeta = 6.dp
    val rellenoHorizontalPildoraMoneda = 8.dp
    val rellenoVerticalPildoraMoneda = 1.dp
    val espacioEtiquetaMontoMeta = 8.dp
    val espacioSignoMonto = 8.dp
    val espacioMontoIncrementos = 14.dp
    val alturaIncremento = 26.dp
    val rellenoHorizontalIncremento = 12.dp
    val alturaCampoFechaMeta = 54.dp
    val rellenoHorizontalCampoFecha = 14.dp
    val espacioFechaFrecuencia = 14.dp
    val alturaSelectorFrecuencia = 40.dp
    val rellenoSelectorFrecuencia = 4.dp
    val alturaMesSelector = 40.dp

    // Asistente, de arriba hacia abajo (los márgenes son margenPantallaAmplio)
    val alturaEncabezadoAsistente = 56.dp
    val tamanoAvatarEncabezado = 40.dp
    val tamanoPuntoActivo = 10.dp
    val grosorBordePuntoActivo = 2.dp
    val espacioAvatarNombre = 10.dp
    val tamanoInsigniaNombre = 18.dp
    val alturaSugerencia = 36.dp
    val rellenoHorizontalChipAsistente = 14.dp
    val espacioEntreMensajes = 16.dp
    val sangriaBurbuja = 44.dp
    val rellenoHorizontalBurbuja = 16.dp
    val rellenoVerticalBurbuja = 12.dp
    val tamanoAvatarMensaje = 32.dp
    val rellenoTarjetaRespuesta = 16.dp
    val tamanoBotonEscritura = 36.dp
    val rellenoBarraEscritura = 8.dp
    val espacioBarraNavegacion = 12.dp

    // Ventanas de aportar y ajustar la regla (no están en el diseño)
    val alturaCampoMontoDialogo = 52.dp
    val rellenoHorizontalCampoMontoDialogo = 14.dp

    // Barra superior fija (nueva contraseña)
    val alturaBarraSuperior = 60.dp
    val rellenoInicioBarra = 24.dp
    val rellenoFinBarra = 32.dp
    val tamanoAvatar = 32.dp

    // Fila de estado: punto verde y texto
    val tamanoPuntoEstado = 8.dp
    val espacioPuntoEstado = 6.dp

    // Aviso de error con título (nueva contraseña)
    val rellenoAvisoDetallado = 16.dp
    val tamanoCirculoAviso = 32.dp
    val espacioIconoTextoAviso = 12.dp
    val espacioTituloMensajeAviso = 4.dp

    // Tarjeta de presentación del paso (en el diseño, a la izquierda queda un espacio vacío)
    val rellenoHorizontalTarjetaPaso = 16.dp
    val rellenoVerticalTarjetaPaso = 12.dp
    val anchoEspacioIlustracion = 89.dp

    // Requisitos con icono (nueva contraseña)
    val rellenoRequisitos = 12.dp
    val espacioFilasRequisitos = 6.dp
    val espacioIconoRequisito = 8.dp

    // Botón suave (fondo azul claro) y pie con escudo
    val alturaBotonSuave = 34.dp
    val rellenoHorizontalBotonSuave = 12.dp
    val espacioIconoPie = 10.dp

    // Tarjeta del formulario
    val radioTarjeta = 12.dp
    val rellenoHorizontalTarjeta = 20.dp
    val rellenoSuperiorTarjeta = 16.dp
    val rellenoInferiorTarjeta = 20.dp

    // Píldora del paso actual
    val rellenoHorizontalPildora = 12.dp
    val rellenoVerticalPildora = 5.dp
    val rellenoVerticalPildoraMediana = 4.dp
    val rellenoVerticalPildoraPequena = 2.dp
    val rellenoHorizontalPildoraPequena = 10.dp
    val espacioIconoTextoPildora = 6.dp

    // Campos de texto con la etiqueta adentro (inicio de sesión)
    val alturaCampo = 58.dp
    val rellenoCampo = 16.dp
    val rellenoSuperiorTextosCampo = 8.dp
    val espacioEtiquetaValor = 2.dp
    val rellenoFinalCampoConBoton = 10.dp
    val espacioCampoMensajeFormulario = 4.dp
    val espacioCampoMensajeServidor = 6.dp

    // Campos de texto con la etiqueta arriba y caja blanca (registro)
    val alturaCampoCompacto = 44.dp
    val rellenoCampoCompacto = 12.dp
    val espacioIconoTextoCompacto = 10.dp
    val rellenoFinalCampoCompactoConBoton = 5.dp

    // Campos de texto con la etiqueta arriba y caja rellena, sin borde (recuperar contraseña)
    val alturaCampoRelleno = 48.dp
    val espacioIconoTextoRelleno = 11.dp

    // Campos de texto con la etiqueta arriba y caja blanca con sombra, sin borde (nueva contraseña)
    val alturaCampoElevado = 54.dp
    val espacioIconoTextoElevado = 12.dp
    val rellenoFinalCampoElevado = 20.dp
    val espacioOjoInsignia = 6.dp
    val rellenoFinalInsignia = 3.dp
    val tamanoInsigniaError = 28.dp

    // Aviso de error
    val rellenoHorizontalAviso = 12.dp
    val rellenoVerticalAviso = 11.dp

    // Recuadro de ayuda (por ejemplo, contactar a soporte)
    val rellenoAvisoAyuda = 12.dp
    val espacioIconoTextoAyuda = 12.dp
    val espacioLineasAyuda = 2.dp

    // Requisitos de la contraseña y casilla de términos
    val tamanoPuntoRequisito = 6.dp
    val tamanoCasilla = 16.dp
    val tamanoMarcaCasilla = 12.dp
    val grosorBordeCasilla = 1.5.dp
    val rellenoSuperiorCasilla = 2.dp
    val espacioCasillaTexto = 10.dp

    // Esquinas redondeadas
    val radioCasilla = 4.dp
    val radioInsignia = 4.dp
    val radioRecuadroPequeno = 6.dp
    val radioCampo = 12.dp
    val radioBotonPequeno = 12.dp
    val radioAviso = 12.dp
    val radioAvisoGrande = 16.dp
    val radioBotonCompacto = 14.dp
    val radioBoton = 16.dp

    // Alturas
    val alturaBoton = 48.dp
    val alturaBotonAlto = 52.dp

    // Tamaños de iconos y botones de icono
    val tamanoBaseIcono = 24.dp
    val tamanoIconoGrande = 24.dp
    val tamanoIcono = 20.dp
    val tamanoIconoCompacto = 18.dp
    val tamanoIconoMediano = 16.dp
    val tamanoIconoPequeno = 14.dp
    val tamanoBotonIcono = 32.dp
    val tamanoBotonVolver = 40.dp
    val tamanoBotonVolverConFondo = 44.dp

    // Logo del inicio de sesión: la imagen (trae el nombre y el eslogan)
    val anchoLogoInicioSesion = 220.dp

    // Logo compacto (registro): el destello va al lado del nombre
    val tamanoDestelloCompacto = 10.dp
    val espacioLogoDestello = 7.dp
    val rellenoSuperiorDestello = 3.dp

    // Logo con punto (recuperar contraseña)
    val tamanoPuntoLogo = 9.dp
    val espacioLogoPunto = 6.dp

    // Fondos decorativos
    val radioBrilloIzquierdo = 113.dp
    val radioBrilloDerecho = 180.dp
    val anchoOlaClara = 103.dp
    val altoOlaClara = 52.dp
    val anchoOlaOscura = 62.dp
    val altoOlaOscura = 30.dp
    val radioBrilloLavanda = 266.dp
    val radioBrilloMenta = 320.dp

    // Bordes y sombras
    val grosorBorde = 1.dp
    val grosorIndicadorCarga = 2.dp
    val elevacionNula = 0.dp
    val elevacionCampo = 1.dp
    val elevacionBoton = 6.dp
}
