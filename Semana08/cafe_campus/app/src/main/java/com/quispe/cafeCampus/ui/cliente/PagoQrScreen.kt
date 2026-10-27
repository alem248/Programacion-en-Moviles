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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
    val payload = remember(payloadBase, monto) {
        if (payloadBase.isBlank()) null else QrEmvCo.construir(payloadBase, monto)
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
                Spacer(Modifier.height(40.dp))
                Text(
                    text = "Pagos con QR no disponibles",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "El administrador aún no configura su QR de Yape/Plin. " +
                            "Para activarlo debe pegar el código de su QR en " +
                            "Perfil → Pagos QR.",
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
                text = "1. Abre Yape o Plin\n2. Escanea este código\n3. El monto aparece solo: solo confirma",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

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
            Bitmap.createBitmap(matriz.width, matriz.height, Bitmap.Config.ARGB_8888).also { bmp ->
                for (x in 0 until matriz.width) {
                    for (y in 0 until matriz.height) {
                        bmp.setPixel(
                            x, y,
                            if (matriz[x, y]) android.graphics.Color.BLACK
                            else android.graphics.Color.WHITE
                        )
                    }
                }
            }
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
