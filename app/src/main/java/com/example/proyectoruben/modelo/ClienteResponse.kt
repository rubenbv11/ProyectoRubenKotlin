package com.example.proyectoruben.modelo

import kotlinx.serialization.Serializable

@Serializable
data class ClienteResponse(
    val id: Int,
    val nombre: String,
    val email: String,
    val telefono: String? = null
)