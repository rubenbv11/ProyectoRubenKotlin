package com.example.proyectoruben.modelo

import kotlinx.serialization.Serializable

@Serializable
data class ReservaResponse(
    val mensaje: String,
    val servicioId: Int? = null,
    val fecha: String? = null,
    val hora: String? = null
)