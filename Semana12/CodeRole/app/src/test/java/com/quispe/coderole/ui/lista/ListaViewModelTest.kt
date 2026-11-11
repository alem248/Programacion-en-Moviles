package com.quispe.coderole.ui.lista

import com.quispe.coderole.domain.model.Personaje
import com.quispe.coderole.domain.repository.ItemRepository
import com.quispe.coderole.ui.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ListaViewModelTest {
    @After
    fun restaurarDispatcher() = Dispatchers.resetMain()

    @Test
    fun muestraLosElementosQueEntregaElRepositorio() = runTest {
        Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
        val mentor = Personaje(1, "Mentora", "Kotlin", "Guía de programación")
        val viewModel = ListaViewModel(RepositorioFalso(listOf(mentor)))

        val resultado = viewModel.uiState.value.estado
        assertTrue(resultado is UiState.Exito)
        assertEquals(listOf(mentor), (resultado as UiState.Exito).datos)
    }

    @Test
    fun conservaLaCacheYMarcaSinConexionSiFallaLaActualizacion() = runTest {
        Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
        val mentor = Personaje(1, "Mentora", "Kotlin", "Guía de programación")
        val repositorio = RepositorioFalso(listOf(mentor), Result.failure(IllegalStateException("offline")))

        val viewModel = ListaViewModel(repositorio)

        assertTrue(viewModel.uiState.value.sinConexion)
        val resultado = viewModel.uiState.value.estado
        assertTrue(resultado is UiState.Exito)
        assertEquals(listOf(mentor), (resultado as UiState.Exito).datos)
    }

    @Test
    fun exponeErrorRecuperableCuandoNoHayCache() = runTest {
        Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
        val viewModel = ListaViewModel(RepositorioFalso(emptyList(), Result.failure(IllegalStateException("offline"))))

        assertTrue(viewModel.uiState.value.sinConexion)
        assertTrue(viewModel.uiState.value.estado is UiState.Error)
    }

    private class RepositorioFalso(
        iniciales: List<Personaje>,
        private val resultadoRefresco: Result<Int> = Result.success(iniciales.size),
    ) : ItemRepository {
        private val datos = MutableStateFlow(iniciales)
        override val personajes: Flow<List<Personaje>> = datos
        override val favoritos: Flow<List<Personaje>> = MutableStateFlow(emptyList())
        override suspend fun refrescar() = resultadoRefresco
        override suspend fun obtenerDetalle(id: Int) =
            datos.value.firstOrNull { it.id == id }?.let { Result.success(it) }
                ?: Result.failure(NoSuchElementException("No encontrado"))
        override suspend fun alternarFavorito(id: Int) {
            datos.value = datos.value.map { if (it.id == id) it.copy(esFavorito = !it.esFavorito) else it }
        }
        override suspend fun cantidadGuardada() = datos.value.size
        override suspend fun cantidadFavoritos() = datos.value.count { it.esFavorito }
        override suspend fun limpiarCache() { datos.value = emptyList() }
    }
}
