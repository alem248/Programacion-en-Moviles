data class Producto(
    val nombre: String,
    val precio: Double,
    var cantidad: Int
)

fun calcularSubtotal(productos: List<Producto>): Double {
    return productos.sumOf { it.precio * it.cantidad }
}

fun calcularIGV(subtotal: Double): Double {
    return subtotal * 0.18
}

fun calcularTotal(subtotal: Double, igv: Double): Double {
    return subtotal + igv
}

fun mostrarDetalle(productos: List<Producto>) {
    println("\nDETALLE DEL CARRITO")
    productos.forEachIndexed { i, p ->
        val importe = p.precio * p.cantidad
        println(String.format("%d. %-20s x%d S/ %8.2f", i + 1, p.nombre, p.cantidad, importe))
    }
}

fun calcularDescuento(subtotal: Double): Double {
    return when {
        subtotal > 5000 -> subtotal * 0.10
        subtotal > 3000 -> subtotal * 0.05
        else -> 0.0
    }
}

fun main() {
    println("=========================================")
    println("   CARRITO DE COMPRAS - TIENDA TECSUP    ")
    println("=========================================")

    print("Ingrese su nombre: ")
    val nombreCliente = readln().ifBlank { "Cliente Tecsup" }
    println()

    val productosDisponibles = listOf(
        Producto("Laptop HP", 2500.0, 1),
        Producto("Mouse Logitech", 45.5, 1),
        Producto("Audifonos Sony", 120.0, 1),
        Producto("USB Kingston 64GB", 25.0, 1),
        Producto("Teclado Mecanico", 180.0, 1)
    )

    val carrito = mutableListOf<Producto>()
    var opcion: Int?

    do {
        println("--- PRODUCTOS DISPONIBLES ---")
        productosDisponibles.forEachIndexed { i, prod ->
            println("${i + 1}. ${prod.nombre} - S/ ${prod.precio}")
        }
        println("0. Finalizar compra y generar recibo")
        print("Seleccione una opción: ")

        opcion = readlnOrNull()?.toIntOrNull()

        if (opcion != null && opcion in 1..productosDisponibles.size) {
            val prodSeleccionado = productosDisponibles[opcion - 1]

            print("Ingrese la cantidad para ${prodSeleccionado.nombre}: ")
            val cantidad = readlnOrNull()?.toIntOrNull() ?: 0

            if (cantidad > 0) {
                val existe = carrito.find { it.nombre == prodSeleccionado.nombre }
                if (existe != null) {
                    existe.cantidad += cantidad
                } else {
                    carrito.add(Producto(prodSeleccionado.nombre, prodSeleccionado.precio, cantidad))
                }
                println("-> Producto agregado al carrito.\n")
            } else {
                println("-> Cantidad no válida.\n")
            }
        } else if (opcion != 0) {
            println("-> Opción no válida. Intente de nuevo.\n")
        }

    } while (opcion != 0)

    if (carrito.isEmpty()) {
        println("\nEl carrito está vacío. ¡Gracias por su visita, $nombreCliente!")
        return
    }

    println("\n=========================================")
    println("             RECIBO DE COMPRA            ")
    println("=========================================")
    println("Cliente: $nombreCliente")

    mostrarDetalle(carrito)

    val subtotalBruto = calcularSubtotal(carrito)
    val descuento = calcularDescuento(subtotalBruto)
    val subtotalNeto = subtotalBruto - descuento
    val igv = calcularIGV(subtotalNeto)
    val total = calcularTotal(subtotalNeto, igv)

    val totalItems = carrito.sumOf { it.cantidad }
    println(String.format("%-22s : %d", "Cantidad total ítems", totalItems))
    println(String.format("%-22s : S/ %7.2f", "Subtotal", subtotalBruto))

    if (descuento > 0.0) {
        val porcentaje = if (subtotalBruto > 5000) "10%" else "5%"
        println(String.format("%-22s : S/ %7.2f (%s)", "Descuento aplicado", descuento, porcentaje))
        println(String.format("%-22s : S/ %7.2f", "Subtotal con desc.", subtotalNeto))
    }

    println(String.format("%-22s : S/ %7.2f", "IGV (18%)", igv))
    println(String.format("%-22s : S/ %7.2f", "TOTAL A PAGAR", total))

    val masCaro = carrito.maxByOrNull { it.precio }
    if (masCaro != null) {
        println("\nProducto más caro elegido: ${masCaro.nombre} " + String.format("(S/ %.2f)", masCaro.precio))
    }

    println("\n¡Gracias por su compra, $nombreCliente!")
}