package com.example.proyectoruben.ui.pantallas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectoruben.viewmodel.ReservaUiState
import com.example.proyectoruben.viewmodel.ReservarViewModel
import com.example.proyectoruben.viewmodel.ServiciosUiState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Reservar(
    modifier: Modifier = Modifier,
    viewModel: ReservarViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val reservaState by viewModel.reservaState.collectAsState()

    var servicioSeleccionadoId by remember { mutableStateOf<Int?>(null) }
    var horaSeleccionada by remember { mutableStateOf("") }
    var fechaSeleccionada by remember { mutableStateOf<LocalDate?>(null) }
    var mostrarDatePicker by remember { mutableStateOf(false) }

    val horas = listOf("09:00", "10:00", "11:30", "16:00", "17:30", "19:00")

    // Formateador para mostrar la fecha en pantalla
    val formatoMostrar = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("es", "ES"))
    // Formateador para enviar a la API
    val formatoApi = DateTimeFormatter.ISO_LOCAL_DATE

    // DatePicker state
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    // Diálogo de confirmación de reserva exitosa
    if (reservaState is ReservaUiState.Exito) {
        AlertDialog(
            onDismissRequest = { viewModel.resetReservaState() },
            icon = { Text("✅", style = MaterialTheme.typography.headlineMedium) },
            title = { Text("¡Reserva confirmada!") },
            text = {
                Text(
                    "Tu reserva ha sido registrada correctamente.\n\n" +
                            "Fecha: ${fechaSeleccionada?.format(formatoMostrar)}\n" +
                            "Hora: $horaSeleccionada"
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.resetReservaState()
                    servicioSeleccionadoId = null
                    horaSeleccionada = ""
                    fechaSeleccionada = null
                }) {
                    Text("Aceptar")
                }
            }
        )
    }

    // DatePicker Dialog
    if (mostrarDatePicker) {
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        fechaSeleccionada = LocalDate.ofEpochDay(millis / 86400000)
                    }
                    mostrarDatePicker = false
                }) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Nueva Reserva", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(20.dp))

        // ── PASO 1: Servicios ────────────────────────────────────────────────
        Text("1. Elige un servicio:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        when (val state = uiState) {
            is ServiciosUiState.Cargando -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cargando servicios...", style = MaterialTheme.typography.bodyMedium)
                }
            }
            is ServiciosUiState.Error -> {
                Column {
                    Text(state.mensaje, color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedButton(onClick = { viewModel.cargarServicios() }) {
                        Icon(Icons.Default.Refresh, contentDescription = null,
                            modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reintentar")
                    }
                }
            }
            is ServiciosUiState.Exito -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.heightIn(max = 320.dp)
                ) {
                    items(state.servicios) { servicio ->
                        Card(
                            onClick = { servicioSeleccionadoId = servicio.id },
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (servicioSeleccionadoId == servicio.id)
                                    MaterialTheme.colorScheme.secondaryContainer
                                else MaterialTheme.colorScheme.surface
                            ),
                            border = if (servicioSeleccionadoId == servicio.id)
                                BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)
                            else null
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(servicio.nombre,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold)
                                    Text("${servicio.duracion} min · ${servicio.costo}€",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (servicioSeleccionadoId == servicio.id) {
                                    Icon(Icons.Default.Check, contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── PASO 2: Fecha ────────────────────────────────────────────────────
        Text("2. Fecha:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = { mostrarDatePicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.DateRange, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = fechaSeleccionada?.format(formatoMostrar) ?: "Seleccionar fecha",
                color = if (fechaSeleccionada != null)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── PASO 3: Hora ─────────────────────────────────────────────────────
        Text("3. Hora disponible:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            horas.forEach { hora ->
                OutlinedButton(
                    onClick = { horaSeleccionada = hora },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
                    colors = if (horaSeleccionada == hora)
                        ButtonDefaults.buttonColors()
                    else ButtonDefaults.outlinedButtonColors()
                ) {
                    Text(hora, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // ── BOTÓN CONFIRMAR ──────────────────────────────────────────────────
        val puedeConfirmar = servicioSeleccionadoId != null &&
                fechaSeleccionada != null &&
                horaSeleccionada.isNotEmpty()

        if (reservaState is ReservaUiState.Enviando) {
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = false
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Enviando reserva...")
            }
        } else {
            Button(
                onClick = {
                    servicioSeleccionadoId?.let { id ->
                        fechaSeleccionada?.let { fecha ->
                            viewModel.confirmarReserva(
                                servicioId = id,
                                fecha = fecha.format(formatoApi),
                                hora = horaSeleccionada
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = puedeConfirmar
            ) {
                Text("Confirmar Reserva")
            }
        }

        // Error del POST
        if (reservaState is ReservaUiState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = (reservaState as ReservaUiState.Error).mensaje,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}