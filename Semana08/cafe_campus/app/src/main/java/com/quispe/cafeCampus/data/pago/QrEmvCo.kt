package com.quispe.cafeCampus.data.pago

import java.util.Locale

/**
 * Genera payloads de pago en formato EMVCo Merchant Presented Mode,
 * el estándar que usan Yape y Plin (QR interoperable en el Perú desde
 * septiembre de 2023: un solo código se lee en ambas billeteras).
 *
 * Toma el "payload base" del QR real del comercio (donde está la cuenta
 * destino), le inyecta el monto del pedido (tag 54), lo marca como
 * dinámico (tag 01 = 12) y recalcula el CRC (tag 63) para que el código
 * sea válido. Todos los demás campos del QR original se conservan.
 *
 * Estructura TLV: ID (2 dígitos) + Longitud (2 dígitos) + Valor.
 */
object QrEmvCo {

    /** Tags que contienen la cuenta destino (26..51 = Merchant Account Information). */
    private val TAGS_CUENTA = (26..51).map { it.toString() }

    /** EMVCo limita el monto (tag 54) a 13 caracteres, p. ej. "9999999999.99". */
    private const val LARGO_MAX_MONTO = 13

    /**
     * Construye el payload final con el monto automático.
     * Devuelve null si el payload base no es válido o el monto no se puede codificar.
     */
    fun construir(base: String, monto: Double, referencia: String = ""): String? {
        val campos = validarBase(base) ?: return null
        if (monto <= 0.0 || monto.isNaN()) return null
        val montoTexto = String.format(Locale.US, "%.2f", monto)
        if (montoTexto.length > LARGO_MAX_MONTO) return null

        // Se parte del QR original completo: cuenta destino, datos adicionales
        // (62), plantillas propias del emisor (80..99), etc. viajan intactos.
        val salida = campos.toMap(LinkedHashMap())
        salida.remove("63")                                  // el CRC se recalcula
        salida["01"] = "12"                                  // QR dinámico (un solo cobro)
        salida["54"] = montoTexto                            // Monto automático
        if ("53" !in salida) salida["53"] = "604"            // 604 = PEN (soles)
        if (referencia.isNotBlank()) {
            agregarReferencia(salida["62"].orEmpty(), referencia)?.let { salida["62"] = it }
        }

        val sb = StringBuilder()
        // 00 siempre primero; el resto en orden de ID (EMVCo lo permite y queda legible)
        salida.entries
            .sortedBy { it.key.toInt() }
            .forEach { (id, valor) -> sb.append(tlv(id, valor) ?: return null) }
        sb.append("6304")                                    // Tag 63 + longitud del CRC
        sb.append(String.format(Locale.US, "%04X", crc16Ccitt(sb.toString())))
        return sb.toString()
    }

    /**
     * Valida el texto que pega el administrador. Devuelve los campos si es un
     * QR de pago EMVCo bien formado, o null con cualquier error (texto cortado,
     * CRC que no cuadra, sin cuenta destino...).
     */
    fun validarBase(base: String): List<Pair<String, String>>? {
        val limpio = base.trim()
        val campos = parsearTlv(limpio) ?: return null
        if (campos.firstOrNull() != ("00" to "01")) return null
        if (campos.none { it.first in TAGS_CUENTA }) return null

        // Si trae CRC (todo QR real lo trae), debe coincidir: detecta copias incompletas
        val crc = campos.find { it.first == "63" }?.second
        if (crc != null) {
            val cuerpo = limpio.dropLast(4)
            if (!cuerpo.endsWith("6304")) return null
            if (!crc.equals(String.format(Locale.US, "%04X", crc16Ccitt(cuerpo)), ignoreCase = true)) {
                return null
            }
        }
        return campos
    }

    /** Nombre del comercio del payload base (para mostrar en pantalla). */
    fun nombreComercio(base: String): String? =
        parsearTlv(base.trim())?.find { it.first == "59" }?.second

    /**
     * Extrae los campos TLV de nivel superior de un payload.
     * Devuelve null si la cadena no se puede recorrer completa.
     */
    private fun parsearTlv(cadena: String): List<Pair<String, String>>? {
        val resultado = mutableListOf<Pair<String, String>>()
        var i = 0
        while (i < cadena.length) {
            if (i + 4 > cadena.length) return null
            val id = cadena.substring(i, i + 2)
            if (id.toIntOrNull() == null) return null
            val largo = cadena.substring(i + 2, i + 4).toIntOrNull() ?: return null
            val fin = i + 4 + largo
            if (fin > cadena.length) return null
            resultado.add(id to cadena.substring(i + 4, fin))
            i = fin
        }
        return resultado.ifEmpty { null }
    }

    /**
     * Agrega la referencia del pedido como "Reference Label" (subtag 05) dentro
     * del tag 62, conservando los subcampos que ya tuviera el QR del comercio.
     */
    private fun agregarReferencia(datos62: String, referencia: String): String? {
        val sub = parsearTlv(datos62).orEmpty().filter { it.first != "05" }
        val nuevo = sub.joinToString("") { (id, v) -> tlv(id, v).orEmpty() } +
                (tlv("05", referencia.take(25)) ?: return null)
        return nuevo.takeIf { it.length <= 99 }
    }

    /** Arma un campo: ID (2) + Longitud (2) + Valor; null si el valor no cabe. */
    private fun tlv(id: String, valor: String): String? {
        if (valor.length > 99) return null
        return id + String.format(Locale.US, "%02d", valor.length) + valor
    }

    /**
     * CRC-16/CCITT-FALSE: polinomio 0x1021, inicial 0xFFFF, sin inversión.
     * Es el cálculo que exige EMVCo para el tag 63 (sobre los bytes UTF-8).
     */
    internal fun crc16Ccitt(texto: String): Int {
        var crc = 0xFFFF
        for (b in texto.toByteArray(Charsets.UTF_8)) {
            crc = crc xor ((b.toInt() and 0xFF) shl 8)
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
