package com.quispe.coderole.ui.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.quispe.coderole.domain.model.PerfilUsuario

/**
 * Foto de perfil en Base64 con inicial de respaldo. Se usa en el perfil y en
 * la cabecera del chat para que el cambio de foto se refleje en tiempo real.
 */
@Composable
fun FotoPerfil(
    perfil: PerfilUsuario?,
    tamano: Dp,
    modifier: Modifier = Modifier,
) {
    val base64 = perfil?.fotoBase64
    val bitmap = remember(base64) {
        if (base64.isNullOrBlank()) null
        else runCatching {
            val bytes = Base64.decode(base64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
        }.getOrNull()
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = "Foto de perfil",
            modifier = modifier.size(tamano).clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
    } else {
        Box(
            modifier
                .size(tamano)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = perfil?.nombreVisible?.firstOrNull()?.uppercase() ?: "C",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = (tamano.value / 2.2f).sp,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}
