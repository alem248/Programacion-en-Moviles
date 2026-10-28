# PROMPTS — Fase 2 (con IA): mejoras CafeCampus · Semana 08

| | |
|---|---|
| **Proyecto** | CafeCampus — `Semana08/cafe_campus` (Semana 08) |
| **Rama** | `mejora-ia` (Fase 2) |
| **Fase 1** | MVVM + Room completo → rama `main`, **sin IA** (13 commits) |
| **Fase 2** | 5 mejoras + pagos QR → rama `mejora-ia`, **con IA** (commits con prefijo `ia:`) |

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

## 2. Mejoras implementadas (5) y sus prompts

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

### M5 · Pago con QR de Yape/Plin con monto automático — `ia: agrega pago con QR…`

> Agrega el plus del sistema: al pagar con Yape/Plin, mostrar un QR con el monto
> automático. Crea `data/pago/QrEmvCo.kt` que construya payloads **EMVCo Merchant
> Presented Mode** (TLV de 2 dígitos + CRC-16/CCITT-FALSE en el tag 63) tomando
> como base el payload del QR REAL del comercio: conserva la cuenta destino
> (tags 26–51) intacta e inyecta el monto del pedido en el tag 54 (moneda 604 =
> PEN). Crea `PagoViewModel` que valide y guarde el payload en DataStore (clave
> `pagoQrPayload`) y `PagoQrScreen` que renderice el código con zxing, muestre el
> monto, instrucciones, botón de copiar y estado vacío si no hay QR configurado.
> En `ConfirmarPedidoScreen` agrega un botón "Ver QR para pagar" cuando la forma
> de pago sea Yape-Plin, y en `AjustesScreen` una sección admin "Pagos con QR"
> para pegar el payload. Ruta `pago_qr/{monto}` en `Rutas`/`NavCafe`. Agrega 7
> pruebas unitarias que validan el CRC contra el valor oficial del estándar.

**Refinamiento (segundo prompt):**

> Actúa como un desarrollador móvil experto. Necesito que al pagar con Yape se
> muestre un QR generado dinámicamente con el precio total de los productos
> seleccionados, para que al escanearlo el monto ya aparezca y el cliente no lo
> digite. Explica cómo se estructura un QR EMVCo compatible con Yape/Plin, dame
> el código en Kotlin para transformar `montoTotal` y la cuenta del comercio en el
> payload, las librerías recomendadas y, si el monto fijo exige la API oficial
> (Culqi, Niubiz, Izipay…), los pasos generales para integrarla.

Con ese prompt se endureció `QrEmvCo`: conserva **todos** los campos del QR
original (tag 62, plantillas 64/80–99 del emisor), marca el código como dinámico
(tag 01 = `12`), valida el CRC del QR pegado por el admin (detecta copias
incompletas), rechaza montos ≤ 0 o de más de 13 caracteres, agrega la referencia
como subtag 05 del tag 62 sin pisar sus datos y calcula el CRC sobre los bytes
UTF-8. `PagoQrScreen` distingue "QR no configurado" de "monto inválido", indica
escribir el monto a mano si la billetera no lo muestra y genera el bitmap en una
sola pasada. Pruebas: de 7 a 13.

**Vinculación del QR personal de Yape (tercer prompt):** al decodificar el QR
personal de la dueña con zxing resultó ser EMVCo (estático `01=11`, token de la
cuenta en el tag 39, `59=YAPERO`, CRC válido), así que `QrEmvCo` lo acepta sin
cambios. Para no subir la cuenta a Git, el payload se lee de `local.properties`
(`pago.qr.payload`) y llega a la app como `BuildConfig.QR_PAGO_DEFECTO`, usado
por `PreferenciasStore` mientras el admin no guarde otro. Como Yape no garantiza
respetar el monto en un QR personal, `PagoQrScreen` tiene el botón "Usar QR sin
monto", que muestra el QR original y pide escribir el monto. Pruebas: 14.

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
5. **QR dinámico sobre el QR estático del comercio.** Yape y Plin usan el
   mismo estándar EMVCo (QR interoperable en el Perú desde septiembre de 2023),
   así que un solo código se lee en ambas billeteras. En vez de exigir una
   cuenta de comerciante propia, la app parte del payload REAL que pega el
   admin (su QR estático) y solo le inyecta el monto y recalcula el CRC: la
   cuenta destino viaja intacta y el pago cae en la cuenta correcta.
   **Límite conocido:** solo funciona con el QR de *negocio* (EMVCo); el QR
   personal de Yape no usa este formato. Y como el código no lo emite un
   adquirente (Niubiz, Izipay, Culqi…), cada billetera decide si respeta el
   tag 54: por eso la pantalla muestra el monto y permite copiarlo. Para un
   monto garantizado y la confirmación automática del pago hace falta la API de
   QR dinámico de un adquirente, que genera el código desde un backend.
67. **Visualización de ventas por día y avatar en perfil.** El gráfico de barras en Canvas permite al administrador evaluar visualmente la tendencia semanal de ventas sin salir del panel principal. La selección de foto de perfil usa contratos modernos de Compose (`PickVisualMedia`) y persiste en DataStore por usuario.
8. **Actualización de identidad semilla.** Se cambió el nombre y correo del estudiante por defecto de "Juan Pérez" (`juan@campus.test`) a "Alexandra Pérez" (`alexandra@campus.test`), alineando las vistas del cliente y los saludos.

---

## 4. Resultado (commits en `mejora-ia`)

| Commit | Descripción |
|---|---|
| `21f0ea2` | `ia: agrega registro de estudiante con validacion de correo unico` |
| `7e7ab9a` | `ia: agrega funcion de repetir pedido desde el historial` |
| `c8d4f45` | `ia: agrega reporte del dia con producto mas vendido y total por pago` |
| `ae023fc` | `ia: agrega animacion al stepper de seguimiento` |
| `2ab90e8` | `ia: conecta registro, reporte y repetir pedido en la navegacion` |
| `748118e` | `ia: quita el cambio de perfil de Ajustes; el rol se define al iniciar sesion` |
| `e0e6ac1` | `fix(sesion): el pedido y el perfil usan el usuario logueado y no el primero del rol` |
| `05ee9de` | `ia: mejora la armonia de colores del modo oscuro del administrador` |
| `14d7d34` | `ia: agrega pago con QR de Yape/Plin con monto automatico` |
| `a8f12c3` | `ia: agrega grafico de ventas por dia en dashboard admin y seleccion de foto de perfil de estudiante` |

Además, el `fix(ui): saludo del menu muestra el nombre real del usuario…` se actualizó
para mostrar "Alexandra" por defecto.

**Verificación:** `gradlew :app:assembleDebug` compila sin errores (KSP/Room OK) y
`gradlew :app:testDebugUnitTest` pasa todas las pruebas unitarias.
