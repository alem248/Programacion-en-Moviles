package com.quispe.cafeCampus.data.pago

import java.util.Locale

/**
 * Genera payloads de pago en formato EMVCo Merchant Presented Mode,
 * el estándar que usan Yape y Plin (QR interoperable en el Perú desde
 * septiembre de 2023: un solo código se lee en ambas billeteras).
 *
 * Toma el "payload base" del QR real del comercio (donde está la cuenta
 * destino), le inyecta el monto del pedido (tag 54) y recalcula el CRC
 * (tag 63) para que el código sea válido.
 *
 * Estructura TLV: ID (2 dígitos) + Longitud (2 dígitos) + Valor.
 */
object QrEmvCo {

    /** Tags que contienen la cuenta destino (26..51 = Merchant Account Information). */
    private val TAGS_CUENTA = (26..51).map { it.toString() }

    /**
     * Construye el payload final con el monto automático.
     * Devuelve null si el payload base no es válido.
     */
    fun construir(base: String, monto: Double, referencia: String = ""): String? {
        val campos = parsearTlv(base.trim())
        // Debe ser EMVCo (00 = "01") y tener cuenta destino + nombre de comercio
        if (campos.none { it.first == "00" && it.second == "01" }) return null
        val cuenta = campos.filter { it.first in TAGS_CUENTA }
        if (cuenta.isEmpty()) return null

        val mcc = campos.find { it.first == "52" }?.second ?: "5812"
        val moneda = campos.find { it.first == "53" }?.second ?: "604"
        val pais = campos.find { it.first == "58" }?.second ?: "PE"
        val nombre = campos.find { it.first == "59" }?.second ?: "CAFETERIA CAMPUS"
        val ciudad = campos.find { it.first == "60" }?.second ?: "LIMA"
        val inicio = campos.find { it.first == "01" }?.second ?: "12"

        val sb = StringBuilder()
        fun tlv(id: String, valor: String) {
            if (valor.isEmpty()) return
            sb.append(id).append(String.format(Locale.US, "%02d", valor.length)).append(valor)
        }

        tlv("00", "01")                       // Formato del payload
        tlv("01", inicio)                     // Estático (11) o dinámico (12)
        cuenta.forEach { (id, valor) -> tlv(id, valor) }  // Cuenta destino (intacta)
        tlv("52", mcc)                        // Categoría del comercio
        tlv("53", moneda)                     // 604 = PEN (soles)
        tlv("54", String.format(Locale.US, "%.2f", monto))  // Monto automático
        tlv("58", pais)                       // PE
        tlv("59", nombre)                     // Nombre del comercio
        tlv("60", ciudad)
        if (referencia.isNotBlank()) {
            tlv("62", subcampo("01", referencia))  // Referencia = pedido
        }
        sb.append("6304")                     // Tag 63 + longitud del CRC
        sb.append(String.format(Locale.US, "%04X", crc16Ccitt(sb.toString())))
        return sb.toString()
    }

    /** Nombre del comercio del payload base (para mostrar en pantalla). */
    fun nombreComercio(base: String): String? =
        parsearTlv(base.trim()).find { it.first == "59" }?.second

    /** Extrae los campos TLV de nivel superior de un payload. */
    private fun parsearTlv(cadena: String): List<Pair<String, String>> {
        val resultado = mutableListOf<Pair<String, String>>()
        var i = 0
        while (i + 4 <= cadena.length) {
            val id = cadena.substring(i, i + 2)
            val largo = cadena.substring(i + 2, i + 4).toIntOrNull() ?: break
            val fin = i + 4 + largo
            if (fin > cadena.length) break
            resultado.add(id to cadena.substring(i + 4, fin))
            i = fin
        }
        return resultado
    }

    /** Arma un subcampo anidado: ID (2) + Longitud (2) + Valor. */
    private fun subcampo(id: String, valor: String): String =
        id + String.format(Locale.US, "%02d", valor.length) + valor

    /**
     * CRC-16/CCITT-FALSE: polinomio 0x1021, inicial 0xFFFF, sin inversión.
     * Es el cálculo que exige EMVCo para el tag 63.
     */
    internal fun crc16Ccitt(texto: String): Int {
        var crc = 0xFFFF
        for (c in texto) {
            crc = crc xor (c.code shl 8)
            repeat(8) {
                crc = if (crc and 0x8000 != 0) {
                    ((crc shl 1) xor 0x1021) and 0xFFFF
                } else {
                    (crc shl 1) and 0xFFFF
                }
            }
        }
        return crc
    }
}
