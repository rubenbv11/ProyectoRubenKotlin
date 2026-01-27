package com.example.proyectoruben.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class ReservaUI(
    val id: Int,
    val servicio: String,
    val fecha: String,
    val hora: String,
    val precio: String,
    val estado: String
)

@Composable
fun ListarReservar(modifier: Modifier = Modifier) {
    val reservas = listOf(
        ReservaUI(1, "Corte Caballero", "27/10/2023", "16:30", "15.00€", "Pendiente"),
        ReservaUI(2, "Tinte y Peinado", "20/10/2023", "10:00", "45.00€", "Completada"),
        ReservaUI(3, "Afeitado", "15/09/2023", "18:00", "10.00€", "Cancelada")
    )

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Próximas", "Historial")

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(reservas) { reserva ->
                ReservaCard(reserva)
            }
        }
    }
}

@Composable
fun ReservaCard(reserva: ReservaUI) {
    val statusColor = when (reserva.estado) {
        "Pendiente" -> Color(0xFFFFA500) // Naranja
        "Confirmada" -> Color(0xFF4CAF50) // Verde
        "Cancelada" -> Color.Red
        else -> Color.Gray
    }

    Card(elevation = CardDefaults.cardElevation(4.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(text = reserva.hora, fontWeight = FontWeight.Bold)
            }

            Divider(modifier = Modifier.height(40.dp).width(1.dp))

            // Detalles
            Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                Text(text = reserva.servicio, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "Fecha: ${reserva.fecha}", style = MaterialTheme.typography.bodyMedium)
                Text(text = reserva.precio, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
            }

            AssistChip(
                onClick = {},
                label = { Text(reserva.estado) },
                colors = AssistChipDefaults.assistChipColors(leadingIconContentColor = statusColor)
            )
        }
    }
}