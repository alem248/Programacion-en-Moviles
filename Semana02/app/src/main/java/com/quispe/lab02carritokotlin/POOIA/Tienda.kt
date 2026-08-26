package com.quispe.lab02carritokotlin.POOIA

import java.util.Scanner

/**
 * Orquesta toda la aplicación: muestra el menú, lee las opciones del
 * usuario y coordina a las demás clases. Todo lo que en el proyecto
 * original vivía dentro de fun main() ahora está organizado aquí.
 */
class Tienda(private val nombreTienda: String = "TIENDA TECSUP") {

    private val scanner = Scanner(System.`in`)

    // POLIMORFISMO: el catálogo es una lista de Producto, pero contiene
    // objetos de dos clases hijas distintas conviviendo en la misma lista.
    private val catalogo: List<Producto> = listOf(
        ProductoElectronico("Laptop HP", 2500.0, 24),
        ProductoAccesorio("Mouse Logitech", 45.5),
        ProductoElectronico("Audifonos Sony", 120.0, 12),
        ProductoAccesorio("USB Kingston 64GB", 25.0),
        ProductoAccesorio("Teclado Mecanico", 180.0)
    )

    private val carrito = Carrito()

    fun iniciar() {
        mostrarBienvenida()

        print("Ingrese su nombre: ")
        val cliente = Cliente(scanner.nextLine())
        println()

        var opcion: Int
        do {
            mostrarCatalogo()
            print("Seleccione una opción: ")
            opcion = leerEntero()
            procesarOpcion(opcion)
        } while (opcion != 0)

        finalizarCompra(cliente)
    }

    private fun mostrarBienvenida() {
        println("=========================================")
        println("   CARRITO DE COMPRAS - $nombreTienda    ")
        println("=========================================")
    }

    private fun mostrarCatalogo() {
        println("--- PRODUCTOS DISPONIBLES ---")
        for (i in catalogo.indices) {
            // Cada producto "se describe a sí mismo" según su clase real.
            println("${i + 1}. ${catalogo[i].descripcion()}")
        }
        println("0. Finalizar compra y generar recibo")
    }

    private fun procesarOpcion(opcion: Int) {
        if (opcion in 1..catalogo.size) {
            val producto = catalogo[opcion - 1]
            print("Ingrese la cantidad para ${producto.nombre}: ")
            val cantidad = leerEntero()

            if (carrito.agregarProducto(producto, cantidad)) {
                println("-> Producto agregado al carrito.\n")
            } else {
                println("-> Cantidad no válida.\n")
            }
        } else if (opcion != 0) {
            println("-> Opción no válida. Intente de nuevo.\n")
        }
    }

    /**
     * Lee un entero validando la entrada: si el usuario escribe texto,
     * se descarta y se vuelve a pedir (el programa ya no se detiene
     * con una excepción como ocurría con scanner.nextInt() directo).
     */
    private fun leerEntero(): Int {
        while (!scanner.hasNextInt()) {
            scanner.next()
            print("-> Ingrese un número válido: ")
        }
        return scanner.nextInt()
    }

    private fun finalizarCompra(cliente: Cliente) {
        if (carrito.estaVacio()) {
            println("\nEl carrito está vacío. ¡Gracias por su visita, ${cliente.nombre}!")
            return
        }

        val recibo = Recibo(cliente, carrito, DescuentoPorMonto())
        recibo.imprimir()
    }
}
