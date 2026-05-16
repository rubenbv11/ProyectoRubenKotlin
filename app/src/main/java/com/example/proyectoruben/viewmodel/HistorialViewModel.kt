package com.example.proyectoruben.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoruben.modelo.ReservaHistorialDto
import com.example.proyectoruben.red.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class HistorialUiState {
    object Cargando : HistorialUiState()
    data class Exito(val reservas: List<ReservaHistorialDto>) : HistorialUiState()
    data class Error(val mensaje: String) : HistorialUiState()
}

class HistorialViewModel : ViewModel() {

    private var _ultimoClienteId: Int? = null
    private val _uiState = MutableStateFlow<HistorialUiState>(HistorialUiState.Cargando)
    val uiState: StateFlow<HistorialUiState> = _uiState

    private val _cancelando = MutableStateFlow(false)
    val cancelando: StateFlow<Boolean> = _cancelando

    fun cargarReservas(clienteId: Int) {
        _ultimoClienteId = clienteId
        viewModelScope.launch {
            _uiState.value = HistorialUiState.Cargando
            try {
                val reservas = RetrofitClient.apiService.getReservasCliente(clienteId)
                _uiState.value = HistorialUiState.Exito(reservas)
            } catch (e: Exception) {
                _uiState.value = HistorialUiState.Error("Error: ${e.message}")
            }
        }
    }

    fun cancelarReserva(id: Int, onExito: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _cancelando.value = true
            try {
                RetrofitClient.apiService.cancelarReserva(id)
                _ultimoClienteId?.let { cargarReservas(it) }
                onExito()
            } catch (e: Exception) {
                onError("No se pudo cancelar: ${e.message}")
            } finally {
                _cancelando.value = false
            }
        }
    }
}