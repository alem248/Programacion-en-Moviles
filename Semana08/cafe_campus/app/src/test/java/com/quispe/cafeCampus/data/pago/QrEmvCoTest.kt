package com.quispe.cafeCampus.data.pago

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class QrEmvCoTest {

    // Payload de prueba con la estructura de un QR real de pago:
    // formato EMVCo + cuenta destino (tag 26) + mcc + moneda + comercio + ciudad.
    private val base = buildString {
        append("000201")                         // 00 Formato EMVCo
        append("010211")                         // 01 Estático
        append("2622")                           // 26 Cuenta destino (22 chars)
        append("0004yape01109876543210")         //   GUI + ID del comercio
        append("52045812")                       // 52 MCC restaurante
        append("5303604")                        // 53 PEN
        append("5802PE")                         // 58 País
        append("5916CAFETERIA CAMPUS")           // 59 Nombre
        append("6004LIMA")                       // 60 Ciudad
    }

    @Test
    fun `el crc16 ccitt-false coincide con el valor oficial del estandar`() {
        // Valor de verificación oficial de CRC-16/CCITT-FALSE para "123456789"
        assertEquals(0x29B1, QrEmvCo.crc16Ccitt("123456789"))
    }

    @Test
    fun `inyecta el monto automatico en el tag 54`() {
        val payload = QrEmvCo.construir(base, 25.5)
        assertNotNull(payload)
        assertTrue(payload!!.contains("540525.50"))
    }

    @Test
    fun `conserva la cuenta destino intacta`() {
        val payload = QrEmvCo.construir(base, 12.0)
        assertNotNull(payload)
        assertTrue(payload!!.contains("26220004yape01109876543210"))
        assertTrue(payload.contains("5916CAFETERIA CAMPUS"))
    }

    @Test
    fun `el crc final es valido segun emvco`() {
        val payload = QrEmvCo.construir(base, 99.99)!!
        val cuerpo = payload.dropLast(4)
        val crc = payload.takeLast(4)
        // El tag 63 debe traer el CRC calculado sobre todo el payload previo
        assertTrue(cuerpo.endsWith("6304"))
        assertEquals(
            String.format("%04X", QrEmvCo.crc16Ccitt(cuerpo)),
            crc
        )
    }

    @Test
    fun `rechaza un texto que no es un qr de pago`() {
        assertNull(QrEmvCo.construir("hola mundo", 10.0))
        assertNull(QrEmvCo.construir("", 10.0))
    }

    @Test
    fun `rechaza un payload sin cuenta destino`() {
        val sinCuenta = "0002010102115916CAFETERIA CAMPUS6004LIMA"
        assertNull(QrEmvCo.construir(sinCuenta, 10.0))
    }

    @Test
    fun `extrae el nombre del comercio`() {
        assertEquals("CAFETERIA CAMPUS", QrEmvCo.nombreComercio(base))
    }
}
