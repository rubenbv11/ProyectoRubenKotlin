package com.example.proyectoruben.modelo

import kotlinx.serialization.Serializable

@Serializable
data class ReservaRequest(
    val clienteId: Int = 1,      // Por ahora hardcoded hasta tener login
    val servicioId: Int,
    val fecha: String,            // formato: "2026-05-12"
    val hora: String              // formato: "10:00"
)