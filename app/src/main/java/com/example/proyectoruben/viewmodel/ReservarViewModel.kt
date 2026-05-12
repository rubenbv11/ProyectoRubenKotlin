package com.example.proyectoruben.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoruben.modelo.ServicioDto
import com.example.proyectoruben.red.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ServiciosUiState {
    object Cargando : ServiciosUiState()
    data class Exito(val servicios: List<ServicioDto>) : ServiciosUiState()
    data class Error(val mensaje: String) : ServiciosUiState()
}

class ReservarViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ServiciosUiState>(ServiciosUiState.Cargando)
    val uiState: StateFlow<ServiciosUiState> = _uiState

    init {
        cargarServicios()
    }

    fun cargarServicios() {
        viewModelScope.launch {
            _uiState.value = ServiciosUiState.Cargando
            try {
                val servicios = RetrofitClient.apiService.getServicios()
                _uiState.value = ServiciosUiState.Exito(servicios)
            } catch (e: Exception) {
                _uiState.value = ServiciosUiState.Error("Error: ${e.message}")
            }
        }
    }
}
