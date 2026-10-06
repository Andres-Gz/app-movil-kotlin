# Finanzia

Tu asistente financiero con IA. App Android hecha con Kotlin y Jetpack Compose.

> **Estado:** en desarrollo. Por ahora tiene la pantalla de inicio de sesión, conectada a Supabase.

## Requisitos

- [Android Studio](https://developer.android.com/studio) reciente. Trae su propio JDK, así que no hace falta instalar Java aparte.
- Android SDK 36. Android Studio lo descarga al abrir el proyecto.
- Un emulador o un teléfono con Android 7.0 (API 24) o superior.

## Configuración

La app se conecta a [Supabase](https://supabase.com). Los datos de conexión no van en el código ni en el repositorio: cada quien los pone en `finanzia/local.properties`, un archivo que git ignora.

```properties
supabase.url=https://<tu-proyecto>.supabase.co
supabase.clave=sb_publishable_...
```

Pide los valores al equipo. Usa solo la clave **publicable** (`sb_publishable_…`): la secreta nunca va en la app. Si falta alguna, Gradle no compila y te dice cuál.

## Cómo ejecutarla

1. Clona el repositorio en una carpeta local, **fuera de OneDrive** o de cualquier carpeta sincronizada con la nube (ver [Problemas conocidos](#problemas-conocidos)).
2. Crea o edita `finanzia/local.properties` con los datos de conexión (ver [Configuración](#configuración)).
3. En Android Studio: **File → Open** y elige la carpeta `finanzia/`, no la raíz del repositorio.
4. Espera a que termine la sincronización de Gradle, elige un emulador y pulsa **Run ▶**.

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
    │   │   ├── configuracion/
    │   │   │   └── Constantes.kt       ← constantes internas: tablas, mensajes de Logcat, patrones
    │   │   ├── tema/                   ← configuración visual
    │   │   │   ├── Colores.kt          ← la paleta de colores
    │   │   │   ├── Tipografia.kt       ← la fuente, los 3 tamaños de letra y los estilos de texto
    │   │   │   ├── Dimensiones.kt      ← espacios, esquinas, alturas, tamaños y sombras
    │   │   │   ├── Iconos.kt           ← los iconos, cada uno con un nombre que dice para qué es
    │   │   │   └── TemaFinanzia.kt     ← junta todo lo anterior en el tema de la app
    │   │   ├── componentes/            ← piezas reutilizables: campos, botones, barra superior…
    │   │   ├── datos/                  ← todo lo que habla con Supabase
    │   │   │   ├── ClienteSupabase.kt  ← la conexión única de la app
    │   │   │   ├── modelos/            ← las tablas mapeadas a data classes (p. ej. Usuario)
    │   │   │   └── repositorios/       ← acceso a los datos: una interfaz y su implementación
    │   │   ├── pantallas/
    │   │   │   └── iniciosesion/       ← una carpeta por pantalla
    │   │   │       ├── PantallaInicioSesion.kt
    │   │   │       ├── EstadoInicioSesion.kt
    │   │   │       ├── InicioSesionViewModel.kt
    │   │   │       └── ValidadorInicioSesion.kt
    │   │   └── utilidades/             ← funciones que se usan en varios lugares (p. ej. RegistroErrores.kt)
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
4. **Datos (`datos/`)**: el ViewModel nunca habla directo con Supabase. Le pide los datos a un **repositorio**, que es una interfaz (`RepositorioSesion`) con su implementación (`RepositorioSesionSupabase`), y lo recibe por su constructor. Las tablas se mapean a `data class` en `modelos/`. En las pruebas se usa un repositorio falso, así no hace falta internet.

Una pantalla no navega por su cuenta: avisa con callbacks (`alIniciarSesion`, `alRegistrarse`…) y `ActividadPrincipal` decide a dónde ir.

Si vienes de Spring Boot, la equivalencia es:

| Spring Boot | Aquí |
|---|---|
| Vista | `Pantalla<X>.kt` |
| Controller | `<X>ViewModel.kt` |
| Service | Todavía no hace falta; si la lógica crece, va entre el ViewModel y el repositorio |
| Repository | `datos/repositorios/`: una interfaz y su implementación con Supabase |
| Entity | `datos/modelos/`: una `@Serializable data class` por tabla |
| DataSource y `application.properties` | `ClienteSupabase` y `local.properties` |

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
| Constantes internas (nombres de tablas, mensajes de Logcat, patrones de validación) | `configuracion/Constantes.kt`, agrupadas por tema (`Constantes.BaseDatos`, `Constantes.Registro`…) | cualquier texto o valor suelto en el código |
| Datos de conexión (URL y clave de Supabase) | `local.properties`, que no se sube | el código y cualquier archivo del repositorio |

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

### 6. Nada repetido

Si una función hace falta en más de un lugar, va en `utilidades/` y se reutiliza desde ahí; nunca se copia y pega. Por ejemplo, cada repositorio registra sus errores con `registrarAdvertencia(...)` de `utilidades/RegistroErrores.kt`.

## Cómo agregar una pantalla nueva

1. Crea la carpeta `pantallas/<nombrepantalla>/` con `Pantalla<X>.kt`, `Estado<X>.kt` y `<X>ViewModel.kt`, siguiendo la forma de `iniciosesion/`.
2. Agrega sus textos a `strings.xml` con el prefijo de la pantalla.
3. Arma la pantalla con los componentes que ya existen. Si necesitas una pieza nueva, créala sin estado en `componentes/`.
4. Si falta un color, una medida o un icono, agrégalo primero en `tema/`.
5. Si la pantalla necesita datos, crea su modelo en `datos/modelos/` y su repositorio en `datos/repositorios/` (interfaz + implementación). El ViewModel recibe la interfaz por su constructor.
6. Conéctala desde `ActividadPrincipal` mediante callbacks.
7. Escribe pruebas: unitarias para el ViewModel (con un repositorio falso) y la lógica, y de pantalla para los flujos importantes.
8. Antes de subir: compila, pasan las pruebas y `./gradlew lintDebug` no da errores.

## Commits

En español, contando qué cambia y por qué. El asunto es corto y en presente, por ejemplo `Agrega la pantalla de registro` o `Corrige la validación del correo`.

## Notas técnicas

- Las dependencias se declaran solo en `gradle/libs.versions.toml`. Las de Compose van sin versión porque la fija el BOM.
- El proyecto usa AGP 8.13 con Gradle 8.14 para que abra también en las versiones de Android Studio de la universidad. Con AGP 8 el plugin `org.jetbrains.kotlin.android` sí es necesario.
- La versión de lanzamiento todavía no se minifica (`isMinifyEnabled = false`).
- Material 3 ya no incluye iconos: usamos `material-icons-extended`, siempre a través de `tema/Iconos.kt`.
- Supabase se usa con [supabase-kt](https://github.com/supabase-community/supabase-kt) 3.2.6, la última versión compatible con nuestro Kotlin 2.2 (desde la 3.7 pide Kotlin 2.4).
- El inicio de sesión lo hace Supabase Auth. La tabla `usuarios` solo aporta el perfil (se busca por correo) y la app nunca lee su columna `contrasena`.

## Problemas conocidos

- **Proyecto dentro de OneDrive** u otra carpeta sincronizada: Gradle falla con `Unable to delete directory…` o `Cannot snapshot… not a regular file`. Clona el repositorio de nuevo en una carpeta local, por ejemplo `C:\dev\`. Si solo lo mueves, los archivos pueden conservar la marca de OneDrive y el error sigue.
