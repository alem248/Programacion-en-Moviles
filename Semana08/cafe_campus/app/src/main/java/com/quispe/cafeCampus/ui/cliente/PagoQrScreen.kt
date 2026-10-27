package com.quispe.cafeCampus.ui.cliente

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.quispe.cafeCampus.data.pago.QrEmvCo
import com.quispe.cafeCampus.ui.components.AppTopBar
import com.quispe.cafeCampus.ui.components.CafeButton
import com.quispe.cafeCampus.viewmodel.PagoViewModel

/**
 * Pago con QR interoperable (Yape/Plin): genera en tiempo real un código
 * con el monto del pedido automático, usando el payload del QR del comercio.
 */
@Composable
fun PagoQrScreen(
    vm: PagoViewModel,
    monto: Double,
    onBack: () -> Unit
) {
    val payloadBase by vm.payloadBase.collectAsState()
    // Respaldo: si la billetera rechaza el QR con monto (p. ej. un Yape personal),
    // se muestra el QR original tal cual y el cliente escribe el monto.
    var conMonto by rememberSaveable { mutableStateOf(true) }
    val payload = remember(payloadBase, monto, conMonto) {
        when {
            payloadBase.isBlank() -> null
            conMonto -> QrEmvCo.construir(payloadBase, monto)
            else -> payloadBase.trim()
        }
    }
    val nombreComercio = remember(payloadBase) { QrEmvCo.nombreComercio(payloadBase) }
    val clipboard = LocalClipboardManager.current

    Column(Modifier.fillMaxSize()) {
        AppTopBar(titulo = "Pagar con Yape / Plin", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (payload == null) {
                val sinConfigurar = payloadBase.isBlank()
                Spacer(Modifier.height(40.dp))
                Text(
                    text = if (sinConfigurar) "Pagos con QR no disponibles" else "No se pudo generar el QR",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (sinConfigurar) {
                        "El administrador aún no configura su QR de Yape/Plin. " +
                                "Para activarlo debe pegar el código de su QR en " +
                                "Perfil → Pagos QR."
                    } else {
                        "El monto del pedido (S/ ${"%.2f".format(monto)}) no es válido " +
                                "para un QR de pago. Revisa tu carrito."
                    },
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(20.dp))
                CafeButton(texto = "Volver", onClick = onBack, outlined = true)
                Spacer(Modifier.height(24.dp))
                return@Column
            }

            Spacer(Modifier.height(20.dp))
            Text(
                text = "Monto a pagar",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "S/ ${"%.2f".format(monto)}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            if (nombreComercio != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = nombreComercio,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(16.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                QrImagen(
                    payload = payload,
                    modifier = Modifier
                        .size(264.dp)
                        .padding(16.dp)
                )
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = if (conMonto) {
                    "1. Abre Yape o Plin\n2. Escanea este código\n3. El monto aparece solo: solo confirma"
                } else {
                    "1. Abre Yape o Plin\n2. Escanea este código\n3. Escribe S/ ${"%.2f".format(monto)} y confirma"
                },
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
            if (conMonto) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Si tu app no muestra el monto, escribe S/ ${"%.2f".format(monto)} a mano",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = { conMonto = !conMonto }) {
                Text(
                    text = if (conMonto) "¿Yape no acepta el código? Usar QR sin monto"
                    else "Volver al QR con monto automático",
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(16.dp))
            CafeButton(
                texto = "Copiar monto",
                onClick = { clipboard.setText(AnnotatedString("%.2f".format(monto))) },
                outlined = true
            )
            Spacer(Modifier.height(10.dp))
            CafeButton(texto = "Volver al pedido", onClick = onBack)
            Spacer(Modifier.height(24.dp))
        }
    }
}

/** Dibuja el payload como imagen QR (negro sobre blanco, sin fondos). */
@Composable
private fun QrImagen(payload: String, modifier: Modifier = Modifier) {
    val bitmap = remember(payload) {
        try {
            val hints = mapOf(
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
                EncodeHintType.CHARACTER_SET to "UTF-8",
                EncodeHintType.MARGIN to 1
            )
            val matriz = QRCodeWriter().encode(payload, BarcodeFormat.QR_CODE, 512, 512, hints)
            val ancho = matriz.width
            val alto = matriz.height
            // Se llenan todos los píxeles en un arreglo y se copian de una sola vez
            val pixeles = IntArray(ancho * alto) { i ->
                if (matriz[i % ancho, i / ancho]) android.graphics.Color.BLACK
                else android.graphics.Color.WHITE
            }
            Bitmap.createBitmap(pixeles, ancho, alto, Bitmap.Config.ARGB_8888)
        } catch (e: Exception) {
            null
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Código QR de pago con el monto automático",
            modifier = modifier
        )
    }
}
