package com.example.proyectoruben.modelo

import kotlinx.serialization.Serializable

@Serializable
data class RegistroRequest(
    val nombre: String,
    val email: String,
    val telefono: String,
    val contrasena: String
)