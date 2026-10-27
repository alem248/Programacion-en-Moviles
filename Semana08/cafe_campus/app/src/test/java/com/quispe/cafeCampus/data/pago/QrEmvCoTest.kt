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

    /** QR "real": la base con su propio CRC, datos adicionales (62) y plantilla del emisor (80). */
    private val baseReal: String = (base + "62070703A01" + "8008" + "0004ABCD" + "6304").let {
        it + String.format("%04X", QrEmvCo.crc16Ccitt(it))
    }

    @Test
    fun `acepta un qr real con crc valido y conserva sus campos extra`() {
        assertNotNull(QrEmvCo.validarBase(baseReal))
        val payload = QrEmvCo.construir(baseReal, 8.5)!!
        assertTrue(payload.contains("62070703A01"))   // datos adicionales del comercio
        assertTrue(payload.contains("80080004ABCD"))  // plantilla propia del emisor
        assertEquals(1, Regex("6304").findAll(payload).count())  // un solo CRC, al final
        assertTrue(payload.dropLast(4).endsWith("6304"))
    }

    @Test
    fun `rechaza un qr copiado incompleto o con crc alterado`() {
        assertNull(QrEmvCo.validarBase(baseReal.dropLast(3)))
        val crcMalo = baseReal.dropLast(4) + "0000"
        assertNull(QrEmvCo.validarBase(crcMalo))
    }

    @Test
    fun `marca el qr como dinamico al llevar monto`() {
        val payload = QrEmvCo.construir(baseReal, 5.0)!!
        assertTrue(payload.startsWith("000201010212"))
    }

    @Test
    fun `reemplaza el monto si la base ya traia uno`() {
        val conMonto = base.replace("5303604", "5303604540510.00")
        val payload = QrEmvCo.construir(conMonto, 3.2)!!
        assertTrue(payload.contains("54043.20"))
        assertTrue(!payload.contains("10.00"))
    }

    @Test
    fun `rechaza montos invalidos`() {
        assertNull(QrEmvCo.construir(base, 0.0))
        assertNull(QrEmvCo.construir(base, -4.0))
        assertNull(QrEmvCo.construir(base, 1e15))  // excede los 13 caracteres del tag 54
    }

    @Test
    fun `acepta la estructura del qr personal de yape con token en el tag 39`() {
        // Misma forma que un Yape personal: estático, token de 32 hex en el tag 39, sin GUI.
        // El token es ficticio.
        val personal = ("000201" + "010211" + "3932" + "0123456789abcdef0123456789abcdef" +
                "52045611" + "5303604" + "5802PE" + "5906YAPERO" + "6004Lima" + "6304").let {
            it + String.format("%04X", QrEmvCo.crc16Ccitt(it))
        }
        assertNotNull(QrEmvCo.validarBase(personal))
        val payload = QrEmvCo.construir(personal, 4.5)!!
        assertTrue(payload.contains("39320123456789abcdef0123456789abcdef"))  // token intacto
        assertTrue(payload.contains("54044.50"))
    }

    @Test
    fun `agrega la referencia del pedido sin perder los datos del tag 62`() {
        val payload = QrEmvCo.construir(baseReal, 7.0, referencia = "PED42")!!
        // 62 = subtag 07 (terminal) del comercio + subtag 05 (referencia) del pedido
        assertTrue(payload.contains("62160703A010505PED42"))
    }
}
