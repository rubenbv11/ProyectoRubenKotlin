package com.example.proyectoruben.modelo

import kotlinx.serialization.Serializable

@Serializable
data class ProductoDto(
    val id: Int,
    val nombre: String,
    val categoria: String? = null,
    val descripcion: String? = null,
    val precio: Double,
    val disponible: Boolean
)