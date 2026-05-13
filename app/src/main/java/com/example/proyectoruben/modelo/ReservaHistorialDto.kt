package com.example.proyectoruben.modelo

import kotlinx.serialization.Serializable

@Serializable
data class ReservaHistorialDto(
    val id: Int,
    val clienteId: Int,
    val servicioId: Int,
    val nombreServicio: String,
    val fecha: String,
    val hora: String,
    val estado: String,
    val observaciones: String? = null
)