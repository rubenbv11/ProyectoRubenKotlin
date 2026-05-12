package com.example.proyectoruben.modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServicioDto(
    val id: Int,
    val nombre: String,
    val descripcion: String? = null,
    val duracion: Int,
    @SerialName("costo")
    val costo: Double,
    @SerialName("imagenUrl")
    val imagenUrl: String? = null
)