package com.example.proyectoruben.modelo

import kotlinx.serialization.Serializable

@Serializable
data class ReservaRequest(
    val clienteId: Int,
    val servicioId: Int,
    val fecha: String,
    val hora: String
)