package com.quispe.lab02carritokotlin.POOIA

/**
 * ABSTRACCIÓN + POLIMORFISMO: la interfaz define el contrato "saber
 * calcular un descuento". El carrito trabaja contra la interfaz, sin
 * importarle qué estrategia concreta se use (patrón Strategy).
 */
interface CalculadoraDescuento {
    fun calcular(total: Double): Double
    fun describir(total: Double): String
}

/**
 * Implementación concreta con las mismas reglas del proyecto original:
 * más de S/ 5000 -> 10%, más de S/ 3000 -> 5%, en otro caso 0%.
 */
class DescuentoPorMonto : CalculadoraDescuento {

    override fun calcular(total: Double): Double {
        return when {
            total > 5000 -> total * 0.10
            total > 3000 -> total * 0.05
            else -> 0.0
        }
    }

    override fun describir(total: Double): String {
        val porcentaje = if (total > 5000) "10%" else "5%"
        return "Descuento aplicado: $porcentaje por compra mayor a S/ 3000"
    }
}

/**
 * Estrategia alternativa: no aplicar descuento alguno.
 * Demuestra el POLIMORFISMO por interfaz: el Recibo puede recibir
 * esta clase o DescuentoPorMonto sin cambiar ni una línea de su código
 * (por ejemplo, para campañas donde el descuento está desactivado).
 */
class SinDescuento : CalculadoraDescuento {

    override fun calcular(total: Double): Double = 0.0

    override fun describir(total: Double): String = "Sin descuento aplicable"
}
