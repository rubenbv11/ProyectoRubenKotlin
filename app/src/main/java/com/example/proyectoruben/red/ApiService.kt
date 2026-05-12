package com.example.proyectoruben.red

import com.example.proyectoruben.modelo.ServicioDto
import retrofit2.http.GET

interface ApiService {

    @GET("api/servicios")
    suspend fun getServicios(): List<ServicioDto>
}