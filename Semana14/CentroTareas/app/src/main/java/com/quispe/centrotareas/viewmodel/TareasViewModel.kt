package com.quispe.centrotareas.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quispe.centrotareas.data.TareasRepository
import com.quispe.centrotareas.model.CargaUi
import com.quispe.centrotareas.model.PrimosUi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TareasViewModel(private val repo: TareasRepository) : ViewModel() {

    // ----- Tarea con progreso (pasos 1 a 4)
    private val _mensaje = MutableStateFlow("Listo")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    // Paso 3: progreso de la tarea (0f a 1f)
    private val _progreso = MutableStateFlow(0f)
    val progreso: StateFlow<Float> = _progreso.asStateFlow()

    // Paso 4: trabajo (Job) y estado de ejecución
    private var trabajo: Job? = null

    private val _corriendo = MutableStateFlow(false)
    val corriendo: StateFlow<Boolean> = _corriendo.asStateFlow()

    // Pasos 3 y 4: la tarea avanza por una corrutina cancelable sin bloquear el hilo principal
    fun tareaLarga() {
        if (_corriendo.value) return // doble inicio prevenido
        trabajo = viewModelScope.launch {
            _corriendo.value = true
            _progreso.value = 0f
            _mensaje.value = "Trabajando..."
            Log.d("HILO", "Antes: ${Thread.currentThread().name}")
            try {
                withContext(Dispatchers.Default) {
                    Log.d("HILO", "Dentro: ${Thread.currentThread().name}")
                    for (i in 1..100) {
                        delay(40) // 100 vueltas x 40 ms ≈ 4 s
                        _progreso.value = i / 100f
                    }
                }
                Log.d("HILO", "Despues: ${Thread.currentThread().name}")
                _mensaje.value = "Terminó"
            } finally {
                _corriendo.value = false
            }
        }
    }

    // Paso 4: cancelar a mitad de camino; la barra se queda donde estaba
    fun cancelar() {
        trabajo?.cancel()
        _mensaje.value = "Cancelado"
    }

    // ----- Contar primos (paso 5)
    private val _primos = MutableStateFlow<PrimosUi>(PrimosUi.Inicial)
    val primos: StateFlow<PrimosUi> = _primos.asStateFlow()
    // Paso 5: contar números primos en segundo plano
    private var primosJob: Job? = null

    fun contar(hasta: Int) {
        if (primosJob?.isActive == true) return // doble inicio prevenido
        _primos.value = PrimosUi.Calculando
        val inicio = System.currentTimeMillis()
        primosJob = viewModelScope.launch {
            try {
                val cantidad = repo.contarPrimos(hasta)
                val ms = System.currentTimeMillis() - inicio
                _primos.value = PrimosUi.Listo(cantidad, ms)
            } catch (e: CancellationException) {
                _primos.value = PrimosUi.Cancelado
                throw e // se vuelve a lanzar para no romper la cancelación
            }
        }
    }

    fun cancelarPrimos() {
        primosJob?.cancel()
    }

    // ----- Dos cargas (pasos 6 y 7)
    private val _carga = MutableStateFlow<CargaUi>(CargaUi.Inicial)
    val carga: StateFlow<CargaUi> = _carga.asStateFlow()
    // Paso 6: cargar los datos una por una (secuencial, ≈ 5 s)
    fun cargarUnaPorUna() {
        if (_carga.value is CargaUi.Cargando) return // doble inicio prevenido
        _carga.value = CargaUi.Cargando
        viewModelScope.launch {
            val inicio = System.currentTimeMillis()
            val perfil = repo.cargarPerfil()
            val pedidos = repo.cargarPedidos(false)
            val ms = System.currentTimeMillis() - inicio
            _carga.value = CargaUi.Exito("Una por una", perfil, pedidos.size, ms)
        }
    }

    // Paso 6: cargar los datos a la vez con async (≈ 3 s)
    fun cargarALaVez() {
        if (_carga.value is CargaUi.Cargando) return // doble inicio prevenido
        _carga.value = CargaUi.Cargando
        viewModelScope.launch {
            val inicio = System.currentTimeMillis()
            val (perfil, pedidos) = coroutineScope {
                val perfilDeferred = async { repo.cargarPerfil() }
                val pedidosDeferred = async { repo.cargarPedidos(false) }
                perfilDeferred.await() to pedidosDeferred.await()
            }
            val ms = System.currentTimeMillis() - inicio
            _carga.value = CargaUi.Exito("A la vez", perfil, pedidos.size, ms)
        }
    }
    // TODO Paso 7: simularError y manejo de errores
}
