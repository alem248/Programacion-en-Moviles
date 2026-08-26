# Carrito de Compras — Versión Orientada a Objetos (POOIA)

Traslado del proyecto original `carrito.main.kt` (enfoque procedural) a un
sistema Orientado a Objetos con las mismas reglas de negocio: IGV del 18%,
descuento del 10% si el total supera S/ 5000 y del 5% si supera S/ 3000,
acumulación de cantidades y recibo de compra.

## Diseño de clases

| Archivo | Clase / tipo | Responsabilidad |
|---|---|---|
| `Producto.kt` | `Producto` (abstracta), `ProductoElectronico`, `ProductoAccesorio` | Modelo base con precio protegido; herencia y polimorfismo en `categoria()` y `descripcion()` |
| `ItemCarrito.kt` | `ItemCarrito` | Línea de compra (producto + cantidad) que calcula su propio importe |
| `Cliente.kt` | `Cliente` | Datos del cliente; nombre en blanco se normaliza a "Cliente Tecsup" |
| `Carrito.kt` | `Carrito` | Lista privada de ítems; agregar, quitar, vaciar y cálculos de subtotal, IGV y total |
| `Descuento.kt` | `CalculadoraDescuento` (interfaz), `DescuentoPorMonto`, `SinDescuento` | Estrategias de descuento intercambiables (patrón Strategy) |
| `Recibo.kt` | `Recibo` | Impresión del recibo de compra completo |
| `Tienda.kt` | `Tienda` | Orquesta el menú, valida entradas y coordina a las demás clases |
| `mainCarrito.kt` | `fun main()` | Punto de entrada: crea la `Tienda` y delega en `iniciar()` |

## Pilares de la POO aplicados

- **Abstracción**: `Producto` es abstracta y `CalculadoraDescuento` es una interfaz.
- **Encapsulamiento**: `precio` y `cantidad` con `private set`; lista del carrito privada.
- **Herencia**: `ProductoElectronico` (con garantía) y `ProductoAccesorio` extienden `Producto`.
- **Polimorfismo**: el catálogo es `List<Producto>` con hijas mezcladas; el descuento se inyecta por interfaz.

## Ejecución

Abrir `mainCarrito.kt` en Android Studio y ejecutar el botón ▶ junto a `fun main()`.
