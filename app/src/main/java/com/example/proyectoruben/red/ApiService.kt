package com.example.proyectoruben.red

import com.example.proyectoruben.modelo.ClienteResponse
import com.example.proyectoruben.modelo.LoginRequest
import com.example.proyectoruben.modelo.RegistroRequest
import com.example.proyectoruben.modelo.ReservaHistorialDto
import com.example.proyectoruben.modelo.ReservaRequest
import com.example.proyectoruben.modelo.ReservaResponse
import com.example.proyectoruben.modelo.ServicioDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @GET("api/servicios")
    suspend fun getServicios(): List<ServicioDto>

    @POST("api/reservas")
    suspend fun crearReserva(@Body reserva: ReservaRequest): ReservaResponse

    @GET("api/reservas/cliente/{clienteId}")
    suspend fun getReservasCliente(@Path("clienteId") clienteId: Int): List<ReservaHistorialDto>

    @PATCH("api/reservas/{id}/cancelar")
    suspend fun cancelarReserva(@Path("id") id: Int): Map<String, String>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): ClienteResponse

    @POST("api/auth/registro")
    suspend fun registro(@Body request: RegistroRequest): ClienteResponse
}