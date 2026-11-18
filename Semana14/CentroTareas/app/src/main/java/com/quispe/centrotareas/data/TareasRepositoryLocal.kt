package com.quispe.centrotareas.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException

class TareasRepositoryLocal : TareasRepository {

    // Mejora 1: tarea simulada con progreso como Flow; flowOn mueve la ejecución a Default
    override fun progresoTarea(): Flow<Float> = flow {
        Log.d("HILO", "Dentro: ${Thread.currentThread().name}")
        for (i in 1..100) {
            delay(40) // 100 vueltas x 40 ms ≈ 4 s
            emit(i / 100f)
        }
    }.flowOn(Dispatchers.Default)

    override suspend fun cargarPerfil(): String {
        delay(2000)
        return "Ana Torres (ficticia)"
    }

    override suspend fun cargarPedidos(simularError: Boolean): List<String> {
        delay(3000)
        if (simularError) throw IOException("Sin conexión")
        return listOf("Pedido 101", "Pedido 102", "Pedido 103")
    }

    // Paso 5: cálculo pesado en Dispatchers.Default, cancelable con ensureActive()
    override suspend fun contarPrimos(hasta: Int): Int = withContext(Dispatchers.Default) {
        var cantidad = 0
        for (n in 2..hasta) {
            ensureActive() // permite cancelar el cálculo a mitad del camino
            if (esPrimo(n)) cantidad++
        }
        cantidad
    }

    private fun esPrimo(n: Int): Boolean {
        var d = 2
        // mientras d*d <= n evita calcular la raíz y no desborda
        while (d.toLong() * d <= n) {
            if (n % d == 0) return false
            d++
        }
        return true
    }
}
