package com.example.aplicaciondam2.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.aplicaciondam2.AppDatabase
import com.example.aplicaciondam2.Coche
import com.example.aplicaciondam2.CocheRepository
import kotlinx.coroutines.launch

/**
 * Estados posibles de la UI para el catálogo.
 */
sealed class CatalogoUiState {
    object Cargando : CatalogoUiState()
    data class Exito(val coches: List<Coche>, val esModoOffline: Boolean = false) : CatalogoUiState()
    data class Error(val mensaje: String) : CatalogoUiState()
}

/**
 * ViewModel que desacopla la lógica de negocio y llamadas al repositorio del ciclo de vida de la UI.
 */
class CatalogoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CocheRepository
    private val _uiState = MutableLiveData<CatalogoUiState>()
    val uiState: LiveData<CatalogoUiState> = _uiState

    init {
        val cocheDao = AppDatabase.obtener(application).cocheDao()
        repository = CocheRepository(cocheDao)
    }

    fun cargarCatalogoLocal() {
        _uiState.value = CatalogoUiState.Cargando
        viewModelScope.launch {
            try {
                val lista = repository.obtenerCochesLocales()
                _uiState.value = CatalogoUiState.Exito(lista, esModoOffline = false)
            } catch (e: Exception) {
                _uiState.value = CatalogoUiState.Error("Error al cargar catálogo local: ${e.localizedMessage}")
            }
        }
    }

    fun cargarCatalogoOnline(marca: String) {
        _uiState.value = CatalogoUiState.Cargando
        viewModelScope.launch {
            val resultado = repository.obtenerModelosOnlineOEnCache(marca)
            resultado.onSuccess { lista ->
                _uiState.value = CatalogoUiState.Exito(lista, esModoOffline = false)
            }.onFailure { error ->
                _uiState.value = CatalogoUiState.Error(
                    "Error de conexión con la API: ${error.localizedMessage ?: "Sin acceso a Internet. Muestra de caché si existe."}"
                )
            }
        }
    }

    fun borrarCoche(coche: Coche, esModoOnline: Boolean, marcaActual: String) {
        viewModelScope.launch {
            repository.borrarCoche(coche)
            if (esModoOnline) {
                cargarCatalogoOnline(marcaActual)
            } else {
                cargarCatalogoLocal()
            }
        }
    }
}
