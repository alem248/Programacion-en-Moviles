# PROMPTS — Fase 2 (con IA): buscador en tiempo real · MiBodega

| | |
|---|---|
| **Proyecto** | MiBodega — `Semana06/MiBodega-main` (Semana 06) |
| **Rama** | `mejora-ia` (Fase 2) |
| **Función pedida** | El campo de búsqueda de **Inicio** filtra la lista de productos en tiempo real |
| **Fase 1** | Estructura + navegación + 7 pantallas → rama `main`, **sin IA** (9 commits) |
| **Fase 2** | Mejora del buscador → rama `mejora-ia`, **con IA** (3 commits, éste) |

---

## 1. Contexto que se le entregó a la IA

- La app ya estaba completa en `main`: `Rutas.kt`, `AppNavegacion.kt` (NavHost con
  Login → Inicio → Detalle → Carrito → Datos de entrega → Confirmación),
  `PantallaInicio` con `LazyRow` de categorías + `LazyColumn` de productos y
  `NavigationBar` con 4 destinos.
- Se pidió **solo** tocar `PantallaInicio.kt`, sin romper la navegación, el filtro
  por categorías ni el bottomBar, y sin agregar dependencias nuevas.

---

## 2. Prompts utilizados

### Prompt 1 — Implementar el buscador (commit `feat(buscador): filtrar productos en tiempo real…`)

> En `ui/cliente/screens/inicio/PantallaInicio.kt` agrega un campo de búsqueda
> debajo de la topBar y encima del título "Productos destacados". Debe filtrar la
> lista de productos **en tiempo real**, mientras el usuario escribe, **sin botón
> de "buscar"** y sin `LaunchedEffect`. Usa `OutlinedTextField` con `leadingIcon`
> de lupa, placeholder "Buscar productos...", `singleLine = true`, esquinas de
> 12 dp y los colores de la app (fondo `GrisClaro`, borde transparente no enfocado,
> borde `VerdeBodega` al enfocar).
>
> El filtro debe **combinarse** con el chip de categoría seleccionado: si el chip
> es "Todos" solo aplica el texto; si no, debe coincidir texto **y** categoría.
> Ignora mayúsculas/minúsculas. No cambies `LazyColumn`, `chunked(2)`, la
> `NavigationBar` ni las firmas de los callbacks.

### Prompt 2 — Estado vacío, botón limpiar y ajuste de altura (commit `feat(buscador): agregar estado sin resultados…`)

> Mejora el buscador de `PantallaInicio.kt`:
> 1. Agrega un ícono **X** (`Icons.Default.Clear`) dentro del campo que borre el
>    texto al tocarlo; solo se muestra si hay texto.
> 2. Cuando no haya coincidencias, en lugar de una lista en blanco muestra un
>    estado vacío con ícono `SearchOff`, el mensaje
>    `No encontramos productos para "..."` (o "No hay productos en esta
>    categoría" si no hay texto), y un botón secundario **"Limpiar búsqueda"**
>    que ponga el texto en `""` y vuelva el chip a "Todos".
> 3. Revisa el alto: la lista y el estado vacío deben usar
>    `Modifier.weight(1f)` dentro del `Column` para que no se dibujen debajo de
>    la `NavigationBar` ni se desborde el contenido.

### Prompt 3 — Documentar la fase (commit `docs(prompts): …`)

> Crea `PROMPTS.md` en la raíz del repositorio documentando la Fase 2: contexto
> del trabajo, los prompts exactos que se usaron para implementar el buscador,
> las correcciones que hubo que aplicar al código generado por la IA y los
> commits resultantes en la rama `mejora-ia`. Debe ser coherente con el historial
> real de git y con el trabajo entregado en `main`.

---

## 3. Correcciones que apliqué al código generado por la IA

1. **El filtro ignoraba el chip de categoría.** La primera versión filtraba solo
   por `textoBusqueda`, así que al escribir "co" se perdía la categoría elegida.
   Corregido a `coincideCategoria && coincideBusqueda`, de modo que el chip y el
   texto se apliquen a la vez.
2. **No ignoraba mayúsculas ni espacios.** Usaba
   `producto.nombre.contains(textoBusqueda)`: "coca" no encontraba
   "Coca-Cola Original" y un espacio al final vaciaba la lista. Corregido a
   `contains(textoBusqueda.trim(), ignoreCase = true)`.
3. **Faltaba el estado sin resultados.** Con "zzz" la pantalla quedaba vacía y
   parecía un bug (además no había forma de reiniciar la búsqueda). Se agregó el
   composable `SinResultados` con el botón "Limpiar búsqueda".
4. **Altura de la lista.** El `LazyColumn` usaba `fillMaxSize()` después de
   varios hermanos dentro de un `Column`, por lo que se medía con toda la altura
   de la pantalla y el final de la lista quedaba bajo la `NavigationBar`. Cambiado
   a `Modifier.weight(1f)`.
5. **Colores y forma del campo.** Se ajustaron `shape` y `colors` a la paleta de
   la app (`GrisClaro` / `VerdeBodega` / borde transparente) para que no quedara
   el azul por defecto de Material.

---

## 4. Resultado (commits en `mejora-ia`)

| Commit | Descripción |
|---|---|
| `e957af4` | `feat(buscador): filtrar productos en tiempo real desde el campo de Inicio (fase 2 con IA)` |
| `4d85606` | `feat(buscador): agregar estado sin resultados, boton limpiar y icono de borrar en el campo` |
| — | `docs(prompts): agregar PROMPTS.md con los prompts de la fase 2` |

**Comportamiento final:** al escribir en el campo la lista se recalcula en cada
recomposición (no hay botón "Buscar"), el texto se combina con el chip de
categoría, la **X** limpia el campo, y si no hay coincidencias aparece el mensaje
con el botón que restaura "Todos".
