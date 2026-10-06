# Finanzia

Tu asistente financiero con IA. App Android hecha con Kotlin y Jetpack Compose.

> **Estado:** en desarrollo. Por ahora tiene la pantalla de inicio de sesión.

## Requisitos

- [Android Studio](https://developer.android.com/studio) reciente. Trae su propio JDK, así que no hace falta instalar Java aparte.
- Android SDK 37. Android Studio lo descarga al abrir el proyecto.
- Un emulador o un teléfono con Android 7.0 (API 24) o superior.

## Cómo ejecutarla

1. Clona el repositorio en una carpeta local, **fuera de OneDrive** o de cualquier carpeta sincronizada con la nube (ver [Problemas conocidos](#problemas-conocidos)).
2. En Android Studio: **File → Open** y elige la carpeta `finanzia/`, no la raíz del repositorio.
3. Espera a que termine la sincronización de Gradle, elige un emulador y pulsa **Run ▶**.

También se puede desde la terminal, dentro de `finanzia/` (en Windows usa `gradlew.bat`):

| Para… | Comando |
|---|---|
| Compilar | `./gradlew assembleDebug` |
| Instalar en el emulador o teléfono conectado | `./gradlew installDebug` |
| Pruebas unitarias | `./gradlew testDebugUnitTest` |
| Pruebas de pantalla (necesitan un emulador abierto) | `./gradlew connectedDebugAndroidTest` |
| Revisión de código (lint) | `./gradlew lintDebug` |

Si la terminal no encuentra Java, usa el JDK que trae Android Studio. En Windows: `JAVA_HOME=C:\Program Files\Android\Android Studio\jbr`.

## Organización de los archivos

```
finanzia/                               ← proyecto Android (esta es la carpeta que se abre en Android Studio)
├── gradle/libs.versions.toml           ← versiones de todas las dependencias
└── app/src/
    ├── main/
    │   ├── java/com/example/finanzia/
    │   │   ├── ActividadPrincipal.kt   ← única actividad: aplica el tema y muestra la pantalla inicial
    │   │   ├── tema/                   ← configuración visual
    │   │   │   ├── Colores.kt          ← la paleta de colores
    │   │   │   ├── Tipografia.kt       ← la fuente, los 3 tamaños de letra y los estilos de texto
    │   │   │   ├── Dimensiones.kt      ← espacios, esquinas, alturas, tamaños y sombras
    │   │   │   ├── Iconos.kt           ← los iconos, cada uno con un nombre que dice para qué es
    │   │   │   └── TemaFinanzia.kt     ← junta todo lo anterior en el tema de la app
    │   │   ├── componentes/            ← piezas reutilizables: campos, botones, barra superior…
    │   │   └── pantallas/
    │   │       └── iniciosesion/       ← una carpeta por pantalla
    │   │           ├── PantallaInicioSesion.kt
    │   │           ├── EstadoInicioSesion.kt
    │   │           ├── InicioSesionViewModel.kt
    │   │           └── ValidadorInicioSesion.kt
    │   └── res/values/strings.xml      ← todos los textos de la app
    ├── test/                           ← pruebas unitarias (corren en el computador)
    └── androidTest/                    ← pruebas de pantalla (corren en el emulador)
```

## Cómo está construida

La app se arma por capas, como piezas de construcción:

1. **Configuración (`tema/`)**: aquí se definen todos los valores visuales (colores, letras, medidas e iconos). Para cambiar el aspecto de la app se cambia aquí, y el cambio se ve en todas partes.
2. **Componentes (`componentes/`)**: piezas reutilizables, como un campo de texto o un botón, hechas solo con la configuración. No guardan estado ni saben en qué pantalla están: reciben valores y avisan lo que pasa mediante callbacks.
3. **Pantallas (`pantallas/`)**: cada pantalla junta componentes y siempre tiene la misma forma:
   - `Estado<X>.kt`: una `data class` con todo lo que la pantalla muestra.
   - `<X>ViewModel.kt`: guarda el estado, recibe lo que hace el usuario (`alCambiarCorreo`, `alPulsarEntrar`…) y produce un estado nuevo.
   - `Pantalla<X>.kt`: dibuja el estado. La parte que dibuja no tiene estado propio, por eso su vista previa (`@Preview`) funciona sin ViewModel.
   - La lógica pura, como las validaciones, va en su propio archivo (`Validador<X>.kt`) para probarla sin emulador.

Una pantalla no navega por su cuenta: avisa con callbacks (`alIniciarSesion`, `alRegistrarse`…) y `ActividadPrincipal` decide a dónde ir.

## Reglas del proyecto

Estas reglas mantienen el código ordenado y predecible. Antes de subir un cambio, revisa que las cumpla.

### 1. Nada escrito a mano en el código

Cada tipo de valor vive en **un solo archivo**. Si necesitas uno que no existe, agrégalo ahí con un nombre que diga para qué sirve y úsalo desde ahí.

| Valor | Dónde vive | No se escribe en ningún otro lado |
|---|---|---|
| Colores | `tema/Colores.kt` | `Color(0xFF…)`, `Color.White`, `Color.Transparent`… |
| Fuente, tamaños y estilos de letra | `tema/Tipografia.kt` | `.sp`, `FontFamily`, `FontWeight`, `TextStyle(…)` |
| Medidas | `tema/Dimensiones.kt` | `.dp` |
| Iconos | `tema/Iconos.kt` | `Icons.…` |
| Textos (incluidos los de accesibilidad) | `res/values/strings.xml` | cualquier texto que vea el usuario |

```kotlin
// ✗ Así no
Text("Entrar", fontSize = 16.sp, color = Color(0xFF2563EB), modifier = Modifier.padding(16.dp))

// ✓ Así sí
Text(
    text = stringResource(R.string.inicio_sesion_boton_entrar),
    style = EstilosTexto.cuerpo,
    color = Colores.azulMarca,
    modifier = Modifier.padding(Dimensiones.espacioMediano),
)
```

### 2. Solo tres tamaños de letra

`TamanoLetra.pequeno`, `mediano` y `grande`. Para destacar un texto se cambia el peso o el color; no se inventa un cuarto tamaño. Todo texto usa un estilo de `EstilosTexto`.

### 3. Todo en español

- Textos de la app, carpetas, archivos, clases, funciones, variables, recursos, comentarios y mensajes de commit.
- Sin tildes ni ñ en nombres de código ni de archivos: `contrasena`, `TamanoLetra`, `Tipografia`. En los textos de `strings.xml` sí van.
- Los callbacks empiezan con `al` en lugar de `on`: `alPulsar`, `alCambiarCorreo`.
- La app existe solo en español: los textos van únicamente en `res/values/strings.xml`, sin carpetas de traducción (`values-en`…).
- Se exceptúan las palabras de Kotlin, Android y Compose, y los nombres que exigen Android o Gradle (`AndroidManifest.xml`, `build.gradle.kts`, `strings.xml`…).

### 4. Nombres claros

- Un concepto por archivo, y el archivo se llama como su clase o composable principal.
- Las claves de `strings.xml` llevan prefijo: el de la pantalla (`inicio_sesion_…`), `accion_…` para las descripciones de accesibilidad y `error_…` para los mensajes de error.

### 5. Simple

No agregues capas ni librerías (inyección de dependencias, navegación, red…) hasta que una pantalla real las necesite.

## Cómo agregar una pantalla nueva

1. Crea la carpeta `pantallas/<nombrepantalla>/` con `Pantalla<X>.kt`, `Estado<X>.kt` y `<X>ViewModel.kt`, siguiendo la forma de `iniciosesion/`.
2. Agrega sus textos a `strings.xml` con el prefijo de la pantalla.
3. Arma la pantalla con los componentes que ya existen. Si necesitas una pieza nueva, créala sin estado en `componentes/`.
4. Si falta un color, una medida o un icono, agrégalo primero en `tema/`.
5. Conéctala desde `ActividadPrincipal` mediante callbacks.
6. Escribe pruebas: unitarias para el ViewModel y la lógica, y de pantalla para los flujos importantes.
7. Antes de subir: compila, pasan las pruebas y `./gradlew lintDebug` no da errores.

## Commits

En español, contando qué cambia y por qué. El asunto es corto y en presente, por ejemplo `Agrega la pantalla de registro` o `Corrige la validación del correo`.

## Notas técnicas

- Las dependencias se declaran solo en `gradle/libs.versions.toml`. Las de Compose van sin versión porque la fija el BOM.
- El proyecto usa AGP 9, que ya trae soporte para Kotlin: **no** agregues el plugin `org.jetbrains.kotlin.android`.
- Las reglas de R8 para la versión de lanzamiento van en `app/src/main/keepRules/`.
- Material 3 ya no incluye iconos: usamos `material-icons-extended`, siempre a través de `tema/Iconos.kt`.

## Problemas conocidos

- **Proyecto dentro de OneDrive** u otra carpeta sincronizada: Gradle falla con `Unable to delete directory…` o `Cannot snapshot… not a regular file`. Clona el repositorio de nuevo en una carpeta local, por ejemplo `C:\dev\`. Si solo lo mueves, los archivos pueden conservar la marca de OneDrive y el error sigue.
