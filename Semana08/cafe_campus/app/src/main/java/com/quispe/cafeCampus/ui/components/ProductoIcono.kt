package com.quispe.cafeCampus.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.quispe.cafeCampus.R

@Composable
fun ProductoIcono(
    icono: String,
    modifier: Modifier = Modifier
) {
    val recurso = when (icono) {
        "cafe" -> R.drawable.cafe
        "plato" -> R.drawable.plato
        "menu_dia" -> R.drawable.menu_dia
        "jugo" -> R.drawable.jugo
        "pan" -> R.drawable.pan
        "botella" -> R.drawable.botella
        "dulce" -> R.drawable.dulce
        else -> null
    }

    if (recurso != null) {
        Image(
            painter = painterResource(id = recurso),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier.clip(RoundedCornerShape(12.dp))
        )
    } else {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocalCafe,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
