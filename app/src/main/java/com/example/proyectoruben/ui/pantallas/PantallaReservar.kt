package com.example.proyectoruben.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Reservar(modifier: Modifier = Modifier) {
    val servicios = listOf("Corte Pelo (30m)", "Tinte (90m)", "Afeitado (20m)", "Peinado (40m)")
    var servicioSeleccionado by remember { mutableStateOf("") }

    val horas = listOf("10:00", "11:30", "16:00", "17:30")
    var horaSeleccionada by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Nueva Reserva", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(24.dp))

        Text("1. Elige un servicio:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(servicios) { servicio ->
                FilterChip(
                    selected = servicioSeleccionado == servicio,
                    onClick = { servicioSeleccionado = servicio },
                    label = { Text(servicio) },
                    leadingIcon = if (servicioSeleccionado == servicio) {
                        { Icon(Icons.Default.Check, contentDescription = null) }
                    } else null
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("2. Fecha:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { /* Abrir DatePicker */ }
        ) {
            Icon(Icons.Default.DateRange, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("27 Octubre 2023")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("3. Horas disponibles:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            horas.forEach { hora ->
                val colors = if (horaSeleccionada == hora) {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    ButtonDefaults.outlinedButtonColors()
                }

                OutlinedButton(
                    onClick = { horaSeleccionada = hora },
                    colors = colors,
                    border = if (horaSeleccionada == hora) null else ButtonDefaults.outlinedButtonBorder
                ) {
                    Text(hora)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { /* Enviar POST al backend */ },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = servicioSeleccionado.isNotEmpty() && horaSeleccionada.isNotEmpty()
        ) {
            Text("Confirmar Reserva")
        }
    }
}