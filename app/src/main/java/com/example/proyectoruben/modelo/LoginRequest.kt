package com.example.proyectoruben.modelo

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val contrasena: String
)