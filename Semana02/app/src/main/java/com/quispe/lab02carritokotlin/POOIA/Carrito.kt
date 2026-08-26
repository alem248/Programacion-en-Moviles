package com.quispe.lab02carritokotlin.POOIA

/**
 * El corazón del sistema: agrupa los ítems y toda la lógica de cálculo
 * que en el proyecto original eran funciones sueltas (calcularSubtotal,
 * calcularIGV, calcularTotal, buscarProducto, mostrarDetalle...).
 *
 * ENCAPSULAMIENTO: la lista de ítems es privada; desde afuera solo se
 * puede consultar una copia de solo lectura mediante obtenerItems().
 */
class Carrito {

    private val items = mutableListOf<ItemCarrito>()

    fun obtenerItems(): List<ItemCarrito> = items.toList()

    fun estaVacio(): Boolean = items.isEmpty()

    fun cantidadDeProductos(): Int = items.size

    /**
     * Si el producto ya está en el carrito, acumula la cantidad;
     * si no, crea un nuevo ítem (misma regla del proyecto original).
     */
    fun agregarProducto(producto: Producto, cantidad: Int): Boolean {
        if (cantidad <= 0) return false

        val existente = buscarItem(producto.nombre)
        if (existente != null) {
            existente.agregarCantidad(cantidad)
        } else {
            items.add(ItemCarrito(producto, cantidad))
        }
        return true
    }

    fun buscarItem(nombre: String): ItemCarrito? {
        return items.find { it.producto.nombre.equals(nombre, ignoreCase = true) }
    }

    fun calcularSubtotal(): Double {
        var subtotal = 0.0
        for (item in items) {
            subtotal += item.calcularImporte()
        }
        return subtotal
    }

    fun calcularIGV(): Double = calcularSubtotal() * IGV

    fun calcularTotal(): Double = calcularSubtotal() + calcularIGV()

    /**
     * POLIMORFISMO: aunque la lista mezcla ProductoElectronico y
     * ProductoAccesorio, aquí se tratan todos como Producto.
     */
    fun productoMasCaro(): Producto? {
        return items.maxByOrNull { it.producto.precio }?.producto
    }

    companion object {
        const val IGV = 0.18
    }
}
