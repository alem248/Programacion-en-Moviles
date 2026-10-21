# PROMPTS — Fase 2 (con IA): mejoras CafeCampus · Semana 08

| | |
|---|---|
| **Proyecto** | CafeCampus — `Semana08/cafe_campus` (Semana 08) |
| **Rama** | `mejora-ia` (Fase 2) |
| **Fase 1** | MVVM + Room completo → rama `main`, **sin IA** (13 commits) |
| **Fase 2** | 4 mejoras → rama `mejora-ia`, **con IA** (commits con prefijo `ia:`) |

> Nota: el `PROMPTS.md` de la raíz documenta la Fase 2 de **Semana 06 (MiBodega)**.
> Éste documenta la de Semana 08.

---

## 1. Contexto entregado a la IA

- La Fase 1 ya estaba completa en `main`: Room con 4 tablas (usuarios, productos,
  pedidos, detalle_pedido), interface `CafeRepository` + `CafeRepositoryRoom`,
  DataStore (`PreferenciasStore`), ViewModels y todas las pantallas de cliente y admin.
- Se pidieron mejoras **sin romper la arquitectura MVVM + Repository**.
- Reglas dadas: la View solo conoce a su ViewModel; el ViewModel solo conoce la
  interface `CafeRepository`; solo `CafeRepositoryRoom` toca los DAOs. Lectura
  reactiva con `Flow`, escritura con `suspend`.

> **Importante — mejoras que YA existían y se descartaron:** el menú ya tenía
> búsqueda en tiempo real (`MenuViewModel.busqueda` + `MenuScreen`) y ya había
> estados vacíos en varias pantallas. No se reimplementaron para evitar duplicar.

---

## 2. Mejoras implementadas (4) y sus prompts

### M1 · Registro de estudiante — `ia: agrega registro de estudiante…`

> Agrega el registro de estudiantes. En `UsuarioDao` crea `existeCorreo(correo)` y
> `insertarNuevo(u)` (con `OnConflictStrategy.ABORT`). En `CafeRepository` y su
> implementación Room, crea `registrar(nombre, correo, clave)` que devuelva un
> `ResultadoRegistro` (sealed: `Exito` | `Error(mensaje)`) validando: campos
> obligatorios, formato de correo, clave ≥ 4 y correo único. Crea
> `RegistroViewModel` (expone `error`, `cargando`, `exito`) y `RegistroScreen`
> con `CafeTextField`/`CafeButton`. Agrega un botón "Crear cuenta" en `LoginScreen`
> (solo para el rol CLIENTE) y registra la ruta en `Rutas` y `NavCafe`.

### M2 · Repetir pedido — `ia: agrega funcion de repetir pedido…`

> Agrega "Repetir pedido" desde el historial. El carrito vive en `MenuViewModel`,
> así que crea un `CarritoHolder` (clase sencilla con `MutableStateFlow` inyectada
> como singleton en `CafeApp` y pasada por `CafeViewModelFactory`) para compartirlo
> con `PedidosClienteViewModel` sin acoplar ambos ViewModels. Refactoriza
> `MenuViewModel` para delegar el carrito al holder manteniendo su API pública. En
> `PedidosClienteViewModel` crea `repetirPedido(pedido)` que reconstruye
> `List<ItemCarrito>` usando los productos VIGENTES (precio actual) y omite los que
> ya no existen. En `MisPedidosScreen` agrega el botón en el historial; al pulsarlo
> navega al Menú con el carrito cargado.

### M3 · Reporte del día — `ia: agrega reporte del dia…`

> Crea el reporte del día para el admin. En `PedidoDao` agrega dos consultas como
> `Flow`: `masVendido(inicio)` (JOIN de `detalle_pedido` con `pedidos`, `GROUP BY
> nombreProducto`, `ORDER BY SUM(cantidad) DESC`, `LIMIT 1`) y `totalesPorPago(inicio)`
> (`GROUP BY pago`), ambas excluyendo los pedidos `CANCELADO`. Expone
> `observarMasVendido` y `observarTotalesPorPago` en el repositorio. Crea
> `ReporteViewModel` y `ReporteScreen` (ventas del día, producto más vendido y
> barras por CAJA/YAPE). Accesible desde un botón en `PanelScreen`.

### M4 · Animación del stepper — `ia: agrega animacion al stepper…`

> En `StepperEstado.kt` anima la transición entre estados: el círculo del paso
> actual crece suavemente con `animateFloatAsState` y los colores del círculo y del
> conector cambian con `animateColorAsState` (tween 350 ms). Solo presentación, sin
> tocar la lógica de estados.

---

## 3. Decisiones técnicas

1. **`CarritoHolder` como singleton.** Para repetir un pedido hace falta llenar el
   carrito desde `PedidosClienteViewModel`, pero el carrito estaba dentro de
   `MenuViewModel`. Pasar un ViewModel a otro los acoplaría. La solución fue
   extraer el estado del carrito a un `CarritoHolder` compartido, inyectado como
   singleton en `CafeApp` y provisto por `CafeViewModelFactory`. Así ambos
   ViewModels usan la misma fuente de verdad sin conocerse entre sí.
2. **Reconstruir con productos vigentes.** `repetirPedido` usa los productos
   actuales (por `productoId`) en vez de los datos congelados del pedido, para que
   el precio y la disponibilidad reflejen el menú de hoy. Los productos eliminados
   se omiten y se avisa al usuario.
3. **Reporte con Flow, no datos fijos.** Las consultas del panel se calculan en
   Room (JOIN + GROUP BY) y se exponen como `Flow`, de modo que el reporte se
   actualiza solo cuando cambian los pedidos, cumpliendo la regla
   "lectura → Flow".
4. **Excluir cancelados.** `masVendido` y `totalesPorPago` filtran
   `estado != 'CANCELADO'`, igual que `ventasDesde`, para que el reporte sea
   consistente con las ventas del panel.

---

## 4. Resultado (commits en `mejora-ia`, prefijo `ia:`)

| Commit | Descripción |
|---|---|
| `21f0ea2` | `ia: agrega registro de estudiante con validacion de correo unico` |
| `7e7ab9a` | `ia: agrega funcion de repetir pedido desde el historial` |
| `c8d4f45` | `ia: agrega reporte del dia con producto mas vendido y total por pago` |
| `ae023fc` | `ia: agrega animacion al stepper de seguimiento` |
| `2ab90e8` | `ia: conecta registro, reporte y repetir pedido en la navegacion` |

Además, el `fix(ui): saludo del menu muestra el nombre real del usuario…` se hizo en
`main` (saludo "¡Hola, Juan!" hardcodeado → nombre real del usuario en sesión) y se
integró a `mejora-ia`.

**Verificación:** `gradlew :app:assembleDebug` compila sin errores (KSP/Room OK).
