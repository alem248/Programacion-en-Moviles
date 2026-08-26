import java.util.Scanner

data class Producto(
    val nombre: String,
    val precio: Double,
    var cantidad: Int
)

fun calcularSubtotal(productos: List<Producto>): Double {
    var subtotal = 0.0
    for (p in productos) {
        subtotal += p.precio * p.cantidad
    }
    return subtotal
}

fun calcularIGV(subtotal: Double): Double {
    return subtotal * 0.18
}

fun calcularTotal(subtotal: Double, igv: Double): Double {
    return subtotal + igv
}

fun mostrarDetalle(productos: List<Producto>) {
    println("DETALLE DEL CARRITO")
    var i = 1
    for (p in productos) {
        val importe = p.precio * p.cantidad
        println(String.format("%d. %-20s x%d S/ %8.2f", i, p.nombre, p.cantidad, importe))
        i++
    }
}


fun calcularDescuento(total: Double): Double {
    return when {
        total > 5000 -> total * 0.10
        total > 3000 -> total * 0.05
        else -> 0.0
    }
}

fun main() {
    val scanner = Scanner(System.`in`)

    println("=========================================")
    println("   CARRITO DE COMPRAS - TIENDA TECSUP    ")
    println("=========================================")

    print("Ingrese su nombre: ")
    val nombreCliente = scanner.nextLine().ifBlank { "Cliente Tecsup" }
    println()

    val productosDisponibles = listOf(
        Producto("Laptop HP", 2500.0, 1),
        Producto("Mouse Logitech", 45.5, 1),
        Producto("Audifonos Sony", 120.0, 1),
        Producto("USB Kingston 64GB", 25.0, 1),
        Producto("Teclado Mecanico", 180.0, 1)
    )

    val carrito = mutableListOf<Producto>()
    var opcion: Int

    do {
        println("--- PRODUCTOS DISPONIBLES ---")
        for (i in productosDisponibles.indices) {
            val prod = productosDisponibles[i]
            println("${i + 1}. ${prod.nombre} - S/ ${prod.precio}")
        }
        println("0. Finalizar compra y generar recibo")
        print("Seleccione una opción: ")

        opcion = scanner.nextInt()

        if (opcion in 1..productosDisponibles.size) {
            val prodSeleccionado = productosDisponibles[opcion - 1]

            print("Ingrese la cantidad para ${prodSeleccionado.nombre}: ")
            val cantidad = scanner.nextInt()

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

    val subtotal = calcularSubtotal(carrito)
    val igv = calcularIGV(subtotal)
    val total = calcularTotal(subtotal, igv)

    println(String.format("%-22s : %d", "Cantidad de productos", carrito.size))
    println(String.format("%-22s : S/ %7.2f", "Subtotal", subtotal))
    println(String.format("%-22s : S/ %7.2f", "IGV (18%)", igv))
    println(String.format("%-22s : S/ %7.2f", "TOTAL A PAGAR", total))

    val masCaro = carrito.maxByOrNull { it.precio }
    if (masCaro != null) {
        println("\nProducto mas caro: ${masCaro.nombre} " + String.format("(S/%.2f)", masCaro.precio))
    }

    val descuento = calcularDescuento(total)
    val totalConDescuento = total - descuento

    if (descuento > 0.0) {
        val porcentaje = if (total > 5000) "10%" else "5%"
        println("Descuento aplicado: $porcentaje por compra mayor a S/ 3000")
        println(String.format("%-22s : S/ %7.2f", "TOTAL CON DESCUENTO", totalConDescuento))
    }

    println("\n¡Gracias por su compra, $nombreCliente!")
}