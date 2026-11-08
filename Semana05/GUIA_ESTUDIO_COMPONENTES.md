# Guía de estudio — Componentes Compose de Semana05 (`main` vs `mejora-ia`)

> **Alcance**: los 4 proyectos de Semana05: **TECSUPFit**, **ClinicaSalud**, **Navegation**, **controldetareas**.
> **Ramas**: `main` = implementación base; `mejora-ia` = refactor (carpeta `ui/components/`, animaciones, dark mode).
> **Objetivo**: que puedas *reescribir* y *borrar* partes del código en el examen.
> **Verificación**: todo fue leído de las 2 ramas; las firmas y rutas son literales de los archivos.

---

## 0. Comandos para estudiar por tu cuenta

```powershell
# Ver cómo quedó un archivo en main (sin salir de mejora-ia)
git show "main:Semana05/TECSUPFit/app/src/main/java/com/quispe/tecsupfit/ui/screens/InicioScreen.kt"

# Ver exactamente qué cambió en un proyecto
git diff main mejora-ia -- Semana05/TECSUPFit

# Archivos nuevos en mejora-ia
git diff --diff-filter=A --name-only main mejora-ia
```

**Lo que cambia entre ramas (resumen numérico):**

| Proyecto | Archivos nuevos | Pantallas modificadas | ¿Cambian modelos/navegación? |
|---|---|---|---|
| TECSUPFit | 6 (`ui/components/*`) + README | 7 de 7 | **No** (idénticos) |
| ClinicaSalud | 5 (`ui/components/*`) | 5 de 8 | **No** (idénticos) |
| Navegation | 2 (`StudentRepository`, `LoginScreen`) + README | 4 de 4 | **Sí** (nuevo repo + `Screen.Login` + startDestination) |
| controldetareas | 1 (`MainActivity.kt` completo) | — (no existe en main) | — |

---

## 1. El patrón común (apréndete ESTO primero)

Los 3 proyectos Compose grandes de Semana05 tienen **la misma arquitectura**:

```text
MainActivity  →  Theme (MaterialTheme)  →  pantalla shell (MainScreen)
                                             │  crea rememberNavController()
                                             │  dibuja bottom bar / drawer
                                             ▼
                                      AppNavigation (NavHost + object Rutas)
                                             │  composable(ruta) { Pantalla(...) }
                                             ▼
                                      ui/screens/*   (contenedores + estado)
                                             │  import ui.components.*
                                             ▼
                                      ui/components/* (presentacionales, SIN estado de negocio)
                                             │
                                             ▼
                                      modelos/Modelos.kt (data classes + listas fake + estado global)
                                             │
                                             ▼
                                      ui/theme/* (Color.kt → Theme.kt → Type.kt)
```

### 1.1 Reglas de estado (sin ViewModel ni StateFlow)

1. **Estado global reactivo** → en `modelos/Modelos.kt`:
   `val citasAgendadas = mutableStateListOf<Cita>(...)` / `val clasesDisponibles = mutableStateListOf(...)`.
   Al hacer `.add(...)` las pantallas que lo leen **se recomponen solas**.
2. **Estado local de pantalla** → `var x by remember { mutableStateOf(...) }` (filtro, texto, selección).
3. **Estado bajado a componentes (state hoisting)** → el componente **no** decide; recibe
   `seleccionada: Boolean` + `onSelect: () -> Unit`. El estado vive en la pantalla.
4. **La navegación pasa callbacks** (`onBack`, `onReservar`) o `navController` según el proyecto:
   - TECSUPFit / ClinicaSalud → reciben **callbacks `() -> Unit`** (la navegación vive en `AppNavigation`).
   - Navegation → las pantallas reciben **`navController` directamente**.
5. **Entre pantallas solo viaja un `Int`** (id) por la URL; el objeto se re-consulta con
   `clasePorId(id)` / `doctorPorId(id)` / `getStudentById(id)`. **Nunca se serializan objetos.**

### 1.2 Recetas básicas (las 4 que más piden en examen)

**A) Extraer un composable de una pantalla a `ui/components/` (lo que hizo `mejora-ia`):**
1. Crear `ui/components/MiComponente.kt` con `package <paquete>.ui.components`.
2. Mover el `@Composable private fun X(...)` **sin cambiar su lógica**, hacerlo público y añadir `modifier: Modifier = Modifier` como **último parámetro**.
3. En la pantalla: `import <paquete>.ui.components.MiComponente` y reemplazar el bloque por la llamada.
4. **El estado NO se mueve**: sigue en la pantalla y baja como parámetro/callback.

**B) Borrar un componente (volver a modo `main`):**
1. Borrar el archivo → error `unresolved reference` **solo** en las pantallas que lo importan.
2. Borrar el `import` en esas pantallas.
3. Reponer la implementación inline (era `private fun` dentro de la pantalla, o un `Card { }` dentro del `items { }`).

**C) Registrar una ruta con argumento:**
```kotlin
// 1. constante
const val DETALLE = "detalle/{claseId}"
fun detalle(claseId: Int) = "detalle/$claseId"

// 2. registro
composable(route = DETALLE,
    arguments = listOf(navArgument("claseId") { type = NavType.IntType })) { entry ->
    val claseId = entry.arguments?.getInt("claseId") ?: 1
    DetalleClaseScreen(claseId = claseId, ...)
}

// 3. navegar
navController.navigate(Rutas.detalle(claseId))
```
Si borras el `navArgument`, la ruta **compila pero siempre cae al default** (fallo silencioso).

**D) Cambiar el tema:** agregar un slot en `LightColorScheme`/`DarkColorScheme` de `Theme.kt`
(o un `val` en `Color.kt`); luego usarlo con `MaterialTheme.colorScheme.xxx`.
En `mejora-ia` ya **no se usan** `Color(0x...)` dentro de pantallas (se reemplazan por slots del esquema).

---

## 2. TECSUPFit (gimnasio)

Ruta base: `Semana05/TECSUPFit/app/src/main/java/com/quispe/tecsupfit/`

### 2.1 Modelos — `modelos/Modelos.kt` (IDÉNTICO en ambas ramas)

```kotlin
data class ClaseFit(id: Int, nombre: String, dia: String, horario: String,
                    sala: String, duracion: String, descripcion: String, cupos: Int)
enum class EstadoReserva { CONFIRMADA, COMPLETADA }
data class Reserva(val clase: ClaseFit, val estado: EstadoReserva = EstadoReserva.CONFIRMADA)

val clasesDisponibles = mutableStateListOf<ClaseFit>(   // 4 clases: id 1..4
    // Yoga funcional / Cross Training / Spinning / HIIT Express
)
fun clasePorId(id: Int) = clasesDisponibles.firstOrNull { it.id == id } ?: clasesDisponibles.first()
fun descontarCupo(id: Int) { /* .copy(cupos = cupos - 1) sobre el índice encontrado */ }
```
> Patrón clave: **lista mutable + data class inmutable** → `copy()` para mutar un elemento.

Datos privados fuera del modelo: `Pestana`/`pestanas` (MainScreen.kt), `filtros` (InicioScreen.kt),
`Rutina`/`listaRutinas` (RutinasScreen.kt), semilla de reservas en `MainScreen.kt:52-57`.

### 2.2 Navegación — `navegacion/AppNavigation.kt`

```kotlin
object Rutas {
    const val INICIO = "inicio"; const val RESERVAS = "reservas"
    const val RUTINAS = "rutinas"; const val PERFIL  = "perfil"
    const val DETALLE = "detalle/{claseId}"; const val CONFIRMACION = "confirmacion/{claseId}"
    fun detalle(claseId: Int) = "detalle/$claseId"
    fun confirmacion(claseId: Int) = "confirmacion/$claseId"
    val conBarraInferior = listOf(INICIO, RESERVAS, RUTINAS, PERFIL)
}

@Composable
fun AppNavigation(navController: NavHostController,
                  reservas: SnapshotStateList<Reserva>,
                  modifier: Modifier = Modifier)
```
- `NavHost(startDestination = Rutas.INICIO)` + **4 transiciones** (solo en `mejora-ia`):
  `enterTransition = slideInHorizontally + fadeIn(tween(300))`, y análogos exit/pop (main no los tenía).
- 6 `composable{}`: INICIO, RESERVAS, RUTINAS, PERFIL, DETALLE(arg Int), CONFIRMACION(arg Int).
- Flujos de navegación (memorizar):
  1. **Tabs**: `navigate(ruta) { popUpTo(INICIO) { saveState = true }; launchSingleTop = true; restoreState = true }` (MainScreen.kt:89-95).
  2. **Detalle → Confirmación** (`onReservar`):
     `descontarCupo(claseId)` + `reservas.add(0, Reserva(...))` +
     `navigate(confirmacion(claseId)) { popUpTo(INICIO) }` (sin save/restore: limpia el stack).
  3. **Confirmación → Reservas**: `navigate(RESERVAS) { popUpTo(INICIO){ saveState=true }; launchSingleTop=true; restoreState=true }`.
  4. **Atrás**: `onBack = { navController.popBackStack() }`.
- La bottom bar se oculta en DETALLE/CONFIRMACION porque no están en `conBarraInferior`.

### 2.3 Componentes — `ui/components/` (LOS 6 SON NUEVOS EN `mejora-ia`)

| Archivo | Firma exacta | Estado interno | Quién lo usa |
|---|---|---|---|
| `ClaseCard.kt` | `ClaseCard(clase: ClaseFit, onClick: () -> Unit, modifier: Modifier = Modifier)` | `MutableInteractionSource` + `isPressed` → `animateFloatAsState` (escala 0.97f) | `InicioScreen.kt:112` |
| `HeaderSeccion.kt` | `HeaderSeccion(titulo: String, modifier: Modifier = Modifier, subtitulo: String? = null)` | ninguno | `InicioScreen.kt:42` |
| `FilaInfoClase.kt` | `FilaInfoClase(icono: ImageVector, texto: String, modifier: Modifier = Modifier)` | ninguno | `DetalleClaseScreen.kt:128-131` (×4) |
| `ReservaItem.kt` | `ReservaItem(reserva: Reserva, modifier: Modifier = Modifier)` | 3 × `animateColorAsState` (borde/fondo/texto según estado) | `MisReservasScreen.kt:49` |
| `EstadisticaCard.kt` | `EstadisticaCard(valor: String, etiqueta: String, modifier: Modifier = Modifier)` | ninguno | `PerfilScreen.kt:92, 97` (con `Modifier.weight(1f)`) |
| `RutinaCard.kt` | `RutinaCard(nombre: String, dias: String, detalle: String, modifier: Modifier = Modifier)` | ninguno | `RutinasScreen.kt:44` |

Detalles que caen en examen:
- `RutinaCard` **no** recibe el objeto `Rutina`: la pantalla hace `rutina.nombre / rutina.dias / rutina.detalle`.
- `EstadisticaCard` **debe** aceptar `modifier` (los call sites usan `Modifier.weight(1f)`).
- `HeaderSeccion` tiene `modifier` **en el medio** (`titulo, modifier, subtitulo`) — por eso los llamados usan named args.
- Iconos `Icons.Outlined.Schedule/LocationOn/EventAvailable/MeetingRoom/AccessTime/Group` → requieren la dependencia `material-icons-extended`.

### 2.4 Pantallas

| Pantalla | Firma | Estado propio | Componentes que usa |
|---|---|---|---|
| `MainScreen` | `MainScreen()` | `navController`, `reservas` (sembradas: clase 2 CONFIRMADA, clase 3 COMPLETADA), `rutaActual`, `isDarkTheme` + `SideEffect` de status bar | NavigationBar de 4 tabs |
| `InicioScreen` | `InicioScreen(onClaseClick: (Int) -> Unit, modifier)` | `filtroActual` (chips **cosméticos**: no filtran la lista) | `HeaderSeccion`, `ClaseCard` |
| `DetalleClaseScreen` | `DetalleClaseScreen(claseId, onBack, onReservar, modifier)` | `clase = clasePorId(claseId)`, escala del botón | `FilaInfoClase` ×4 |
| `ConfirmacionScreen` | `ConfirmacionScreen(claseId, onVerReservas, modifier)` | `animarIcono` + `LaunchedEffect(Unit){ animarIcono = true }` → `animateFloatAsState(spring)` | **ninguno**: `FilaResumen` sigue `private` acá (caso a remarcar) |
| `MisReservasScreen` | `MisReservasScreen(reservas: List<Reserva>, modifier)` | ninguno (reactivo por la `SnapshotStateList`) + estado vacío | `ReservaItem` |
| `PerfilScreen` | `PerfilScreen(modifier)` | ninguno (datos fake inline: "AQ", "Alexandra Quispe", stats 14/3) | `EstadisticaCard` ×2 |
| `RutinasScreen` | `RutinasScreen(modifier)` | ninguno (`listaRutinas` privada) | `RutinaCard` |

### 2.5 Diff `main` → `mejora-ia` (qué se extrajo)

| En `main` vivía dentro de… | Ahora es… |
|---|---|
| `InicioScreen`: `private fun TarjetaClase(clase, onClick)` + header `Column` con `background(VerdeOscuro)` | `ui/components/ClaseCard.kt` + `ui/components/HeaderSeccion.kt` |
| `InicioScreen`: chips con `if (seleccionado) VerdeOscuro else Color.White` | mismos chips pero con 3 `animateColorAsState(tween(200))` |
| `DetalleClaseScreen`: `private fun FilaInformacion(icono, texto)` (sin `modifier`) | `ui/components/FilaInfoClase.kt` (+ botón con `collectIsPressedAsState`) |
| `MisReservasScreen`: `private fun TarjetaReserva(reserva)` (colores estáticos) | `ui/components/ReservaItem.kt` (colores animados) |
| `PerfilScreen`: `private fun TarjetaEstadistica(valor, etiqueta, modifier)` | `ui/components/EstadisticaCard.kt` |
| `RutinasScreen`: `Card { Row { ... } }` **dentro** del `items { }` | `ui/components/RutinaCard.kt` |
| Colores `VerdeOscuro/VerdeClaro/TextoPrincipal/TextoSecundario` directos | `MaterialTheme.colorScheme.*` |
| `Theme.kt`: `darkTheme = false` fijo + `dynamicColor` + `Purple*` | `darkTheme = isSystemInDarkTheme()`, sin `dynamicColor`, paleta verde clara/oscura (16 slots c/u) |

`ConfirmacionScreen` es la única que **no** extrajo su auxiliar (`FilaResumen` es `private` en el archivo).

### 2.6 ¿Qué se rompe si borro X?

| Borrado | Error en | Cómo revertirlo (modo main) |
|---|---|---|
| `ClaseCard.kt` | `InicioScreen.kt` import + línea 112 | reponer `private fun TarjetaClase(clase, onClick)` dentro de InicioScreen |
| `HeaderSeccion.kt` | `InicioScreen.kt:42` | reponer el `Column` con `background(VerdeOscuro)` inline |
| `FilaInfoClase.kt` | 4 llamadas en `DetalleClaseScreen` | reponer `private fun FilaInformacion(icono, texto)` y renombrar llamadas |
| `ReservaItem.kt` | `MisReservasScreen.kt:49` | reponer `private fun TarjetaReserva(reserva)` |
| `EstadisticaCard.kt` | `PerfilScreen.kt:92,97` | reponer `private fun TarjetaEstadistica(...)` inline |
| `RutinaCard.kt` | `RutinasScreen.kt:44` | reponer el `Card { }` dentro del `items { }` |
| `modelos/Modelos.kt` | 8 archivos (todo lo usa) | — |
| Las 4 transiciones del NavHost | **no** falla: compila sin animación (= main) |
| `navArgument("claseId")` | **no** falla compilación: `claseId` siempre = 1 (fallo silencioso) |
| `LaunchedEffect` de Confirmación | compila; el icono queda mini (escala 0.2f) |
| dep. `material-icons-extended` | `ClaseCard` y `DetalleClaseScreen` (iconos Outlined) |

### 2.7 Tema

- `Color.kt`: paleta clara (`VerdeOscuro 0xFF00604B`, `VerdeClaro 0xFFD7EFE7`, `GrisClaro`, `FondoApp`, `TextoPrincipal`, `TextoSecundario`) + **6 colores oscuros nuevos**; se borraron los `Purple*`/`Pink*`.
- `Theme.kt`: `LightColorScheme` y `DarkColorScheme` con 16 slots (primary/onPrimary/primaryContainer/secondary*/background/surface/surfaceVariant/outline...); `val colorScheme = if (darkTheme) Dark else Light` (if simple, **no** `when`; sin `dynamicColor`).
- `Type.kt`: solo define `bodyLarge`; el resto usa defaults de Material 3.

---

## 3. ClinicaSalud (clínica de citas)

Ruta base: `Semana05/ClinicaSalud/app/src/main/java/com/quispe/clinicasalud/`

### 3.1 Modelos — `modelos/Modelos.kt` (IDÉNTICO en ambas ramas)

```kotlin
data class Doctor(id, nombre, especialidad, biografia, imagenRes: Int, calificacion: Double, resenas: Int)
data class Especialidad(nombre: String, imagenRes: Int)
enum class EstadoCita { CONFIRMADA, COMPLETADA }
data class Cita(doctor: Doctor, fecha: String, hora: String, estado: EstadoCita = EstadoCita.CONFIRMADA)

val listaEspecialidades  // 4: Cardiología, Pediatría, Dermatología, General
val listaDoctores        // 6 doctores (ids 1..6); el 6 = "Medicina General"
val listaFechas          // ["Vie 26", "Sáb 27", "Dom 28", "Lun 30"]
val listaHorarios        // 6 horarios ("9:00 am"...)
fun doctorPorId(id: Int) = listaDoctores.firstOrNull { it.id == id } ?: listaDoctores.first()
val citasAgendadas = mutableStateListOf<Cita>(/* 3 citas semilla: 2 COMPLETADA, 1 CONFIRMADA */)
```

### 3.2 Navegación — `navegacion/AppNavigation.kt` (IDÉNTICO entre ramas)

```kotlin
object Rutas {
    const val INICIO = "inicio"; const val PERFIL = "perfil/{doctorId}"
    const val AGENDAR = "agendar/{doctorId}"; const val CONFIRMACION = "confirmacion"
    const val MIS_CITAS = "mis_citas"; const val HISTORIAL = "historial"; const val PERFIL_USUARIO = "perfil_usuario"
    fun perfil(doctorId: Int) = "perfil/$doctorId"
    fun agendar(doctorId: Int) = "agendar/$doctorId"
}
@Composable fun AppNavigation(navController: NavHostController, onMenuClick: () -> Unit)
```
- `NavHost(startDestination = Rutas.INICIO)` → **7 `composable{}`** (INICIO, PERFIL, AGENDAR, CONFIRMACION, MIS_CITAS, HISTORIAL, PERFIL_USUARIO).
- Args: `navArgument("doctorId") { type = NavType.IntType }` + `entry.arguments?.getInt("doctorId") ?: 0` (cae al 1er doctor porque `doctorPorId` tiene fallback `.first()`).
- Flujo: Inicio → Perfil(médico) → Agendar → **Confirmación** `navigate(CONFIRMACION){ popUpTo(INICIO) }` → desde ahí `popBackStack(INICIO, inclusive=false)` o `navigate(MIS_CITAS)`.
- El `rememberNavController` vive en **MainScreen** (junto al `ModalNavigationDrawer`), no en AppNavigation.

### 3.3 Componentes — `ui/components/` (LOS 5 SON NUEVOS EN `mejora-ia`)

| Archivo | Firma exacta | Estado interno | Quién lo usa |
|---|---|---|---|
| `ChipEstado.kt` | `ChipEstado(estado: EstadoCita, modifier: Modifier = Modifier)` | 2 `animateColorAsState(tween(250))` con `when(estado)` | **`CitaItem.kt:85`** (uso transitivo) |
| `CitaItem.kt` | `CitaItem(cita: Cita, modifier: Modifier = Modifier)` | `colorAcento = when(cita.estado)` (no es estado, es derivado) | `MisCitasScreen.kt:72` |
| `DoctorCard.kt` | `DoctorCard(doctor: Doctor, onClick: () -> Unit, modifier: Modifier = Modifier)` | `var expandido by remember { mutableStateOf(false) }` → **⚠ declarado y nunca usado** (estado muerto) | `InicioScreen.kt:128` |
| `EspecialidadChip.kt` | `EspecialidadChip(especialidad: Especialidad, seleccionada: Boolean, onSelect: () -> Unit, modifier)` | escala + 2 colores animados (`FilterChip`) | `InicioScreen.kt:101` |
| `HorarioFilterChip.kt` | `HorarioFilterChip(texto: String, seleccionado: Boolean, onSelect: () -> Unit, modifier)` | escala + 2 colores animados | `AgendarCitaScreen.kt:143` (fechas) y `:163` (horas) |

> `ChipEstado` es el **único componente transitivo**: `MisCitasScreen → CitaItem → ChipEstado`.
> En `main` vivía como `private fun` dentro de `MisCitasScreen.kt` con colores hardcodeados
> (`0xFF1B7B4A`, radio 6.dp); ahora usa `primaryContainer/secondaryContainer` + animación, radio 8.dp, y **gana parámetro `modifier`**.

### 3.4 Pantallas

| Pantalla | Firma | Estado propio | Componentes |
|---|---|---|---|
| `MainScreen` | `MainScreen()` (idéntica en ramas) | `navController`, `drawerState`, `scope`, `rutaActual`, 4 `OpcionMenu` | `ModalNavigationDrawer` |
| `InicioScreen` | `InicioScreen(onDoctorSelected: (Int) -> Unit, onMenuClick: () -> Unit)` | `especialidadSeleccionada: String?` (toggle a `null` deselecciona) + filtro de doctores (con caso especial "General" ↔ "Medicina General") | `EspecialidadChip`, `DoctorCard` |
| `PerfilMedicoScreen` | `PerfilMedicoScreen(doctorId, onBack, onAgendar)` | solo `doctorPorId` | — (Cards inline) |
| `AgendarCitaScreen` | `AgendarCitaScreen(doctorId, onBack, onConfirmada)` | `fechaSeleccionada` (NO se deselecciona) y `horaSeleccionada: String?` (SÍ se deselecciona); botón `enabled = hora != null`; al confirmar: `citasAgendadas.add(Cita(...))` | `HorarioFilterChip` ×2 |
| `ConfirmacionScreen` | `ConfirmacionScreen(onInicio, onMisCitas)` | `cita = citasAgendadas.lastOrNull()` (lee el último) | — |
| `MisCitasScreen` | `MisCitasScreen(onBack)` | lee `citasAgendadas` + estado vacío | `CitaItem` |
| `HistorialMedicoScreen` | `HistorialMedicoScreen(onBack)` (idéntica en ramas) | `registrosHistorial` privado (3 fake) | — |
| `PerfilScreen` | `PerfilScreen(onBack)` (idéntica en ramas) | datos inline + `private fun DatoFila(etiqueta, valor, ultima)` | — |

### 3.5 Diff `main` → `mejora-ia`

| Bloque en `main` | Destino en `mejora-ia` |
|---|---|
| `InicioScreen`: `SuggestionChip` de especialidades con colores `if(seleccionada){...}else{...}` | `EspecialidadChip` (cambió `SuggestionChip` → `FilterChip` + escala animada) |
| `InicioScreen`: `Card(onClick) { Row { imagen, nombre, Star(0xFFFFC107), calificación } }` | `DoctorCard` (Star ahora `colorScheme.tertiary`; se agregó `"${resenas} reseñas"`) |
| `MisCitasScreen`: `private fun ChipEstado` (colores fijos) | `ui/components/ChipEstado.kt` con animación y colores de tema |
| `MisCitasScreen`: card de cita con barra lateral 6.dp | `CitaItem` (el `colorAcento` ahora se calcula **dentro** del componente) |
| `AgendarCitaScreen`: 2 `FilterChip` inline + resumen en `Text` plano | 2 `HorarioFilterChip` + resumen en `Card` |
| `ConfirmacionScreen`: `ColorExito = Color(0xFF2E9E5B)` | `colorScheme.tertiary` (ya responde a dark mode) |
| Colores sueltos `Color(0x...)` en 4 pantallas | eliminados: todo `MaterialTheme.colorScheme.*` |
| `Theme.kt`: 3 slots `Purple*` | 19 slots `MdLight*`/`MdDark*` (azul `0xFF00668A` / turquesa `0xFF006A6A`) |
| `Type.kt`: solo `bodyLarge` | **9 estilos** (headline, title, body, label) aplicados explícitamente en cada `Text` |

**Sin cambios entre ramas**: `MainActivity`, `Modelos.kt`, `AppNavigation.kt`, `MainScreen`, `HistorialMedicoScreen`, `PerfilScreen`.

### 3.6 ¿Qué se rompe si borro X?

| Borrado | Error en | Revertir (modo main) |
|---|---|---|
| `CitaItem.kt` | `MisCitasScreen.kt` import + línea 72 | reponer la card con barra lateral dentro de `MisCitasScreen` |
| `ChipEstado.kt` | `CitaItem.kt:85` (¡y hay que importarlo si se devuelve a `MisCitasScreen`!) | mover la `private fun ChipEstado` de vuelta a `MisCitasScreen.kt` (sin `modifier` en la versión vieja) |
| `DoctorCard.kt` | `InicioScreen.kt:128` | reponer el `Card(onClick) {...}` inline |
| `EspecialidadChip.kt` | `InicioScreen.kt:101` | reponer `SuggestionChip(...)` inline |
| `HorarioFilterChip.kt` | **2** llamadas en `AgendarCitaScreen` (143 y 163) | reponer `FilterChip(selected, onClick, label = { Text(...) })` |
| `citasAgendadas` del modelo | `AgendarCitaScreen` (add), `ConfirmacionScreen` (lastOrNull), `MisCitasScreen` (items) | — |

---

## 4. Navegation (portal académico)

Ruta base: `Semana05/Navegation/app/src/main/java/com/quispe/appnavegation/`

### 4.1 Capa de datos — `data/StudentRepository.kt` (NUEVO)

```kotlin
data class Student(id: Int, name: String, career: String, cycle: String,
                   email: String, phone: String, bio: String, initials: String)

object StudentRepository {                       // singleton: se usa como StudentRepository.students
    val students = listOf<Student>(/* 6, ids 1..6 */)
    fun getStudentById(id: Int): Student =
        students.find { it.id == id } ?: students.first()   // nunca null → coherente con defaultValue = 1
}
```
- Consumidores: `ListScreen.kt:27` (`students`) y `DetailScreen.kt:30` (`getStudentById(itemId)`).
- **`ProfileScreen` NO lo usa** → los datos del perfil siguen hardcodeados (extracción parcial).
- En `main` los datos estaban generados inline: `val items = (1..8).map { "Elemento número $it" }`.

### 4.2 Navegación — `navigation/`

```kotlin
sealed class Screen(val route: String) {
    object Login   : Screen("login")                       // NUEVO en mejora-ia
    object Home    : Screen("home")
    object List    : Screen("list")
    object Profile : Screen("profile")
    object Detail  : Screen("detail/{itemId}") {
        fun createRoute(itemId: Int) = "detail/$itemId"
    }
}
@Composable fun AppNavigation()   // sin parámetros; rememberNavController() adentro (línea 17)
```

```kotlin
NavHost(navController, startDestination = Screen.Login.route) {   // main: Screen.Home.route
    composable(Screen.Login.route)   { LoginScreen(navController) }        // NUEVO
    composable(Screen.Home.route)    { HomeScreen(navController) }
    composable(Screen.List.route)    { ListScreen(navController) }
    composable(Screen.Profile.route) { ProfileScreen(navController) }
    composable(Screen.Detail.route,
        arguments = listOf(navArgument("itemId") {
            type = NavType.IntType; defaultValue = 1        // main: 0
        })) { entry ->
        val itemId = entry.arguments?.getInt("itemId") ?: 1 // main: ?: 0
        DetailScreen(navController, itemId)
    }
}
```

Mapa de movimientos (memorizar los 3 `popUpTo`):

| Movimiento | Código | Pila resultante |
|---|---|---|
| Login → Home | `navigate(Home) { popUpTo(Login) { inclusive = true } }` | `[home]` (atrás ya NO vuelve al login) |
| Home → List / Profile | `navigate(Screen.List.route)` | `[home, list]` |
| List → Detail | `navigate(Screen.Detail.createRoute(student.id))` | `[home, list, detail/3]` |
| Atrás (flecha) | `navController.popBackStack()` | quita el tope |
| Logout (Home y Profile) | `navigate(Login) { popUpTo(0) { inclusive = true } }` | `[login]` (pila completa) |

### 4.3 Pantallas (todas reciben `navController`, no callbacks)

| Pantalla | Estado | Notas clave |
|---|---|---|
| `LoginScreen(navController)` **NUEVO** | `email`, `password`, `passwordVisible` — 3 × `var ... by remember { mutableStateOf(...) }` | no valida credenciales; `OutlinedTextField` con `visualTransformation = PasswordVisualTransformation()` condicional; requiere import `androidx.compose.runtime.*` (los `by` necesitan `getValue/setValue`) |
| `HomeScreen(navController)` | ninguno | `Box` con `Brush.verticalGradient`, 2 `Card(onClick={navigate(...)})`, logout con `popUpTo(0){inclusive=true}` |
| `ListScreen(navController)` | `students = StudentRepository.students` | `LazyColumn` + `items(students, key = { it.id })` (requiere import `foundation.lazy.items`) + `ListItem` |
| `DetailScreen(navController, itemId)` | `student = getStudentById(itemId)` | `verticalScroll`, avatar con iniciales, Card Biografía + Card Contacto |
| `ProfileScreen(navController)` | ninguno | 5 `ListItem` con literales ("Alexandra Ximena Quispe Mallqui", "IV Ciclo"...) — **no usa repository** |

### 4.4 Diff `main` → `mejora-ia`

- **Nuevos**: `data/StudentRepository.kt` (+81), `screens/LoginScreen.kt` (+158), `README.md`.
- **Reescritos**: Detail (+186/−26), Home (+206/−31), List (+75/−21), Profile (+226/−49).
- **`MainActivity.kt` (0/+12, solo borrados)**: se eliminó el bloque
  `@Preview @Composable fun AppPreview() { AppNavegationTheme { HomeScreen(rememberNavController()) } }` + 4 imports.
  → En `mejora-ia` **ya no hay ningún `@Preview`** en todo el proyecto.
- **`Screen.kt`**: +`object Login`.
- **`AppNavigation`**: `startDestination` Home→Login, imports explícitos (antes `screens.*`), `defaultValue` 0→1.
- **Tema**: `dynamicColor = true` → **`false`**; Color.kt pasa de 6 `Purple*` de plantilla a **12 colores propios**
  (`PurplePrimary 0xFF673AB7`, `PurpleDark`, `PurpleLight`, `PurpleAccent`, gradientes, `LogoutRed`,
  `PageBackground`, `CardBackground`, `TextDark`; `PurpleGradientSoft` y `TextWhite` **sin uso**).
  `Type.kt` sin cambios.

### 4.5 ¿Qué se rompe si borro X?

| Borrado | Error en | Nota |
|---|---|---|
| `StudentRepository.kt` | `ListScreen.kt:19,27,60` y `DetailScreen.kt:22,30` | si borras solo `getStudentById` → solo Detail; si solo `students` → List |
| `LoginScreen.kt` | `AppNavigation.kt:12,24` | reimportar + registrar `composable(Screen.Login.route)` |
| `object Login` de `Screen.kt` | **4 archivos**: AppNavigation (`startDestination` + registro), LoginScreen, HomeScreen (logout), ProfileScreen (logout) | |
| El bloque `arguments` de Detail | **sin error**: todas las fichas muestran al estudiante 1 | fallo silencioso |
| `createRoute` | `ListScreen.kt:101` | alternativa válida: `navigate("detail/${student.id}")` |
| `androidx.compose.runtime.*` en LoginScreen | `by`, `remember`, `mutableStateOf` dejan de compilar (5 errores) | |
| `foundation.lazy.items` en ListScreen | `unresolved reference: items` en `:60` (el overload con `key=` viene de ahí) | |
| Color cualquiera de `Color.kt` | `Theme.kt` + 1-6 pantallas según el color (ver tabla del informe: `PurplePrimary` lo usan 6 archivos) | |
| `dynamicColor=false` → `true` | no rompe compilación; en Android 12+ el sistema pisa la paleta morada | |

---

## 5. controldetareas (archivo único)

**Situación especial**: `Semana05/controldetareas/app/src/main/java/com/quispe/controldetareas/MainActivity.kt`
es **byte a byte idéntico** al de `Semana04/controldetareas` (mismo hash git) y **solo existe en `mejora-ia`**
(221 líneas añadidas, 0 borradas). Además, **en Semana05 no hay proyecto Gradle ni `ui/theme/`** —
solo está ese `.kt` (es una copia de referencia, no compila aislado).

> ⚠ Para que compile en cualquier proyecto hace falta definir en `ui/theme/Color.kt`:
> `NavyBlue`, `LightBackground`, `CardBackground` (el `Color.kt` actual de Semana04 solo tiene los `Purple*`
> de plantilla → `unresolved reference`).

### 5.1 Código (todo en un archivo)

```kotlin
data class Tarea(val id: Int, val nombre: String, val completada: Boolean = false)

class MainActivity : ComponentActivity() {   // setContent { ControlDeTareasTheme { Scaffold { PantallaTareas() } } } }

@Composable fun ItemTarea(tarea: Tarea, onEliminar: () -> Unit, onCambiarEstado: (Boolean) -> Unit)
@Composable fun PantallaTareas(modifier: Modifier = Modifier)
@Preview @Composable fun PreviewPantallaTareas()
```

- **Estado de `PantallaTareas`** (todo hoisted en una sola pantalla):
  ```kotlin
  var textoTarea by remember { mutableStateOf("") }
  var contadorId by remember { mutableStateOf(1) }          // id autoincremental
  val listaTareas = remember { mutableStateListOf<Tarea>() } // ¡reactiva!
  ```
- **Agregar**: `if (textoTarea.isNotBlank()) { listaTareas.add(Tarea(contadorId, textoTarea)); contadorId++; textoTarea = "" }`.
- **Total**: `Text("Total de tareas: ${listaTareas.size}")` → se actualiza solo por la `SnapshotStateList`.
- **Eliminar**: `onEliminar = { listaTareas.remove(tarea) }`.
- **Marcar**: `onCambiarEstado = { completada -> val i = indexOf(tarea); if (i != -1) listaTareas[i] = listaTareas[i].copy(completada = completada) }`
  → patrón **índice + copy** (la `Tarea` es inmutable, la lista no).
- **`ItemTarea`** no tiene estado propio: solo dibuja `Card` → `Row` → `Checkbox(checked, onCheckedChange = onCambiarEstado)` +
  `Text` (gris si completada, `NavyBlue` si no) + `IconButton(onClick = onEliminar)` con `Icons.Default.Delete`.
- **LazyColumn**: `items(listaTareas, key = { it.id })` — clave por id para que Compose identifique cada fila.

---

## 6. Tabla maestra "agregar / quitar" para el examen

### 6.1 Cómo se agrega un componente nuevo (plantilla estándar del repo)

```kotlin
// ui/components/MiComponente.kt
package com.quispe.<proyecto>.ui.components

@Composable
fun MiComponente(
    dato: TipoDelModelo,          // 1) datos en crudo
    onAccion: () -> Unit = {},    // 2) callbacks (si hace falta)
    modifier: Modifier = Modifier // 3) SIEMPRE el último
) {
    // estado visual propio SOLO si es local (animate*AsState, remember de pressed)
    // colores: MaterialTheme.colorScheme.*  (NUNCA Color(0x...))
    // texto: MaterialTheme.typography.<estilo> + FontWeight
}
```
Luego: `import com.quispe.<proyecto>.ui.components.MiComponente` en la pantalla que lo use.

### 6.2 Checklist de "quito esto... ¿qué pasa?"

| Quitas | Consecuencia |
|---|---|
| Un archivo de `ui/components/` | `unresolved reference` solo en sus usuarios → borrar su `import` y re-inlinear |
| Un `import` sin borrar el uso | `unresolved reference` en el uso |
| Un uso sin borrar el componente | compila; warning de código no usado |
| Un `composable{}` del NavHost | la ruta deja de existir → `IllegalArgumentException` al navegar |
| `navArgument` | compila; argumento siempre = default (fallo **silencioso**) |
| Transiciones del NavHost | compila; sin animación |
| Estado (`remember`) | compila; pero `val` que se esperaba `var` (o datos estáticos) |
| `key = {}` en `items` | compila; posible bug de recomposición/animación |
| Un color de `Color.kt` | `unresolved reference` en `Theme.kt` y en cada pantalla que lo importe |
| `object Rutas`/`Screen` completo | rompe TODA la navegación |

### 6.3 Errores de compilación típicos y su significado

| Error | Causa |
|---|---|
| `Unresolved reference: X` | falta el archivo/import, o se quitó una definición |
| `@Composable invocable...` | falta `@Composable` en la función, o se llamó dentro de lambda no-composable |
| `Parameter type mismatch` | cambiaste el orden/tipos de los parámetros (recuerda: `modifier` al final) |
| `This material API is experimental` | falta `@OptIn(ExperimentalMaterial3Api::class)` (TopAppBar/FilterChip/FlowRow) |
| No aparece el botón de back en previews | en `mejora-ia` se borraron todos los `@Preview` de Navegation |

---

## 7. Preguntas de examen probables (autoevalúa)

**TECSUPFit**
1. Escribe la firma de los 6 componentes nuevos y di qué pantalla consume cada uno.
2. ¿Qué pasa al pulsar "Reservar cupo"? (3 pasos: descontarCupo, add a `reservas`, navigate con `popUpTo(INICIO)`).
3. ¿Por qué la bottom bar desaparece en Detalle/Confirmación?
4. ¿Por qué `ConfirmacionScreen` es "la que falta refactorizar"? (`FilaResumen` privada).
5. Restaura el `darkTheme = false` de `main`… ¿qué líneas tocas en `Theme.kt` y `MainScreen.kt`?

**ClinicaSalud**
1. Dibuja el flujo de datos de Agendar → Confirmación → Mis Citas (todo por `citasAgendadas`).
2. ¿Por qué `HorarioFilterChip` se usa para fechas Y horas? Escribe sus 2 llamadas con sus closures de selección/deselección.
3. ¿Qué estado hoisted recibe `EspecialidadChip` y dónde vive?
4. Encuentra el "estado muerto" del proyecto (`expandido` en `DoctorCard`).
5. ¿Por qué `ChipEstado` es "transitivo"? ¿Qué pasa si borras `CitaItem` pero dejas `ChipEstado`?

**Navegation**
1. Escribe `sealed class Screen` completo con `createRoute`.
2. ¿Diferencia entre `popUpTo(Login){inclusive=true}` y `popUpTo(0){inclusive=true}`?
3. ¿Por qué `getStudentById` nunca devuelve null y por qué `defaultValue = 1`?
4. ¿Qué datos NO se movieron al repository? (ProfileScreen).
5. Restaura un `@Preview` de `HomeScreen` (di los 3 imports necesarios).

**controldetareas**
1. ¿Cómo se agrega, elimina y marca una tarea? (3 fragmentos de código).
2. ¿Por qué `listaTareas` es `mutableStateListOf` y no `listOf`?
3. ¿Por qué se usa `.copy(completada = ...)` en vez de `tarea.completada = ...`?

**Transversal**
1. ¿Cuál es la diferencia entre la navegación de TECSUPFit/ClinicaSalud (callbacks) y la de Navegation (`navController` directo)?
2. Regla de oro del repo: *el estado vive en la pantalla/modelo, el componente solo dibuja*. ¿Por qué?
3. ¿Qué dependencia necesitan los iconos `Icons.Outlined.*`? (`material-icons-extended`).
