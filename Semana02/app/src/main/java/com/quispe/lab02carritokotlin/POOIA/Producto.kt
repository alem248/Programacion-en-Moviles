package com.quispe.lab02carritokotlin.POOIA

/**
 * ABSTRACCIÓN: La clase abstracta Producto define el "modelo" general
 * de cualquier producto de la tienda, sin ser un producto concreto.
 * ENCAPSULAMIENTO: precio es privado en escritura (private set) y solo
 * se puede modificar mediante el método actualizarPrecio(), que valida el dato.
 */
abstract class Producto(
    val nombre: String,
    precio: Double
) {
    var precio: Double = precio
        private set

    /**
     * Cada subclase está obligada a indicar su categoría
     * (método abstracto -> POLIMORFISMO por sobrescritura).
     */
    abstract fun categoria(): String

    /**
     * Método abierto (open) que las subclases pueden personalizar.
     */
    open fun descripcion(): String {
        return "$nombre - S/ $precio [${categoria()}]"
    }

    fun actualizarPrecio(nuevoPrecio: Double) {
        if (nuevoPrecio > 0) {
            precio = nuevoPrecio
        }
    }
}

/**
 * HERENCIA: ProductoElectronico "es un" Producto y agrega
 * un atributo propio (meses de garantía).
 */
class ProductoElectronico(
    nombre: String,
    precio: Double,
    val garantiaMeses: Int
) : Producto(nombre, precio) {

    override fun categoria(): String = "Electrónico"

    // POLIMORFISMO: sobrescribe la descripción del padre y la extiende
    override fun descripcion(): String {
        return super.descripcion() + " - Garantía: $garantiaMeses meses"
    }
}

/**
 * HERENCIA: ProductoAccesorio también "es un" Producto,
 * pero con su propio comportamiento.
 */
class ProductoAccesorio(
    nombre: String,
    precio: Double
) : Producto(nombre, precio) {

    override fun categoria(): String = "Accesorio"
}
