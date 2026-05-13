package com.example.proyectoruben.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoruben.datos.SessionManager
import com.example.proyectoruben.modelo.LoginRequest
import com.example.proyectoruben.modelo.RegistroRequest
import com.example.proyectoruben.red.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Cargando : AuthUiState()
    object Exito : AuthUiState()
    data class Error(val mensaje: String) : AuthUiState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    val haySession = sessionManager.haySession
    val clienteNombre = sessionManager.clienteNombre
    val clienteEmail = sessionManager.clienteEmail
    val clienteTelefono = sessionManager.clienteTelefono
    val clienteId = sessionManager.clienteId

    fun login(email: String, contrasena: String) {
        if (email.isBlank() || contrasena.isBlank()) {
            _uiState.value = AuthUiState.Error("Rellena todos los campos")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Cargando
            try {
                val response = RetrofitClient.apiService.login(
                    LoginRequest(email.trim(), contrasena)
                )
                sessionManager.guardarSesion(
                    response.id,
                    response.nombre,
                    response.email,
                    response.telefono
                )
                _uiState.value = AuthUiState.Exito
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Email o contraseña incorrectos")
            }
        }
    }

    fun registro(nombre: String, email: String, telefono: String, contrasena: String,
                 confirmar: String) {
        if (nombre.isBlank() || email.isBlank() || contrasena.isBlank()) {
            _uiState.value = AuthUiState.Error("Rellena todos los campos obligatorios")
            return
        }
        if (contrasena != confirmar) {
            _uiState.value = AuthUiState.Error("Las contraseñas no coinciden")
            return
        }
        if (contrasena.length < 6) {
            _uiState.value = AuthUiState.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Cargando
            try {
                val response = RetrofitClient.apiService.registro(
                    RegistroRequest(nombre.trim(), email.trim(), telefono.trim(), contrasena)
                )
                sessionManager.guardarSesion(
                    response.id,
                    response.nombre,
                    response.email,
                    response.telefono
                )
                _uiState.value = AuthUiState.Exito
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Error al registrarse: ${e.message}")
            }
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            sessionManager.cerrarSesion()
            _uiState.value = AuthUiState.Idle
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}