package com.example.proyectoruben.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoruben.modelo.ReservaRequest
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

sealed class ReservaUiState {
    object Idle : ReservaUiState()
    object Enviando : ReservaUiState()
    data class Exito(val mensaje: String) : ReservaUiState()
    data class Error(val mensaje: String) : ReservaUiState()
}

class ReservarViewModel : ViewModel() {

    private val _serviciosState = MutableStateFlow<ServiciosUiState>(ServiciosUiState.Cargando)
    val uiState: StateFlow<ServiciosUiState> = _serviciosState

    private val _reservaState = MutableStateFlow<ReservaUiState>(ReservaUiState.Idle)
    val reservaState: StateFlow<ReservaUiState> = _reservaState

    init { cargarServicios() }

    fun cargarServicios() {
        viewModelScope.launch {
            _serviciosState.value = ServiciosUiState.Cargando
            try {
                val servicios = RetrofitClient.apiService.getServicios()
                _serviciosState.value = ServiciosUiState.Exito(servicios)
            } catch (e: Exception) {
                _serviciosState.value = ServiciosUiState.Error("Error: ${e.message}")
            }
        }
    }

    fun confirmarReserva(servicioId: Int, fecha: String, hora: String, clienteId: Int) {
        viewModelScope.launch {
            _reservaState.value = ReservaUiState.Enviando
            try {
                val response = RetrofitClient.apiService.crearReserva(
                    ReservaRequest(
                        clienteId = clienteId,
                        servicioId = servicioId,
                        fecha = fecha,
                        hora = hora
                    )
                )
                _reservaState.value = ReservaUiState.Exito(response.mensaje)
            } catch (e: Exception) {
                _reservaState.value = ReservaUiState.Error("Error al reservar: ${e.message}")
            }
        }
    }

    fun resetReservaState() {
        _reservaState.value = ReservaUiState.Idle
    }
}