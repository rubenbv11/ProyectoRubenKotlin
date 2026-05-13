package com.example.proyectoruben.ui.pantallas

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectoruben.modelo.ReservaHistorialDto
import com.example.proyectoruben.viewmodel.HistorialUiState
import com.example.proyectoruben.viewmodel.HistorialViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListarReservar(
    modifier: Modifier = Modifier,
    viewModel: HistorialViewModel = viewModel(),
    clienteId: Int = 1
) {
    val uiState by viewModel.uiState.collectAsState()
    val cancelando by viewModel.cancelando.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Próximas", "Historial")

    // Modal de detalle
    var reservaSeleccionada by remember { mutableStateOf<ReservaHistorialDto?>(null) }
    // Confirmación de cancelar
    var reservaACancelar by remember { mutableStateOf<ReservaHistorialDto?>(null) }
    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(clienteId) {
        viewModel.cargarReservas(clienteId)
    }

    // Diálogo de detalle
    reservaSeleccionada?.let { reserva ->
        DetalleReservaDialog(
            reserva = reserva,
            onDismiss = { reservaSeleccionada = null },
            onCancelar = {
                reservaSeleccionada = null
                reservaACancelar = reserva
            }
        )
    }

    // Diálogo de confirmación de cancelación
    reservaACancelar?.let { reserva ->
        AlertDialog(
            onDismissRequest = { reservaACancelar = null },
            icon = { Text("⚠️", style = MaterialTheme.typography.headlineMedium) },
            title = { Text("¿Cancelar reserva?") },
            text = {
                Text("¿Seguro que quieres cancelar la reserva de\n${reserva.nombreServicio}\nfechada el ${reserva.fecha.split("-").reversed().joinToString("/")}?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelarReserva(
                            id = reserva.id,
                            onExito = { reservaACancelar = null },
                            onError = { reservaACancelar = null }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Sí, cancelar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { reservaACancelar = null }) {
                    Text("No, volver")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = modifier.fillMaxSize().padding(padding)) {

            // ── Cabecera ─────────────────────────────────────────────────────
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Mis Reservas",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    when (val s = uiState) {
                        is HistorialUiState.Exito -> Text(
                            "${s.reservas.size} reservas encontradas",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        else -> Text(
                            "Cargando...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ── Tabs ─────────────────────────────────────────────────────────
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.secondary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                fontWeight = if (selectedTab == index)
                                    FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // ── Contenido ────────────────────────────────────────────────────
            when (val state = uiState) {
                is HistorialUiState.Cargando -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Cargando reservas...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                is HistorialUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.mensaje, color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(onClick = { viewModel.cargarReservas() }) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Reintentar")
                            }
                        }
                    }
                }

                is HistorialUiState.Exito -> {
                    val proximas = state.reservas
                        .filter { it.estado == "Pendiente" || it.estado == "Confirmada" }
                        .sortedByDescending { it.fecha }
                    val historial = state.reservas
                        .filter { it.estado == "Completada" || it.estado == "Cancelada" }
                        .sortedByDescending { it.fecha }
                    val listaActual = if (selectedTab == 0) proximas else historial

                    PullToRefreshBox(
                        isRefreshing = cancelando,
                        onRefresh = { viewModel.cargarReservas() },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (listaActual.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.DateRange, contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text("No hay reservas aquí",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    items = listaActual,
                                    key = { it.id }
                                ) { reserva ->
                                    SwipeToCancel(
                                        onCancel = { reservaACancelar = reserva },
                                        enabled = reserva.estado == "Pendiente" ||
                                                reserva.estado == "Confirmada"
                                    ) {
                                        ReservaCardReal(
                                            reserva = reserva,
                                            onClick = { reservaSeleccionada = reserva }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Swipe to cancel ──────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToCancel(
    onCancel: () -> Unit,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    if (!enabled) {
        content()
        return
    }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onCancel()
            }
            false // No dismisseamos — solo disparamos el callback
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            val color by animateColorAsState(
                targetValue = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart)
                    MaterialTheme.colorScheme.error
                else Color.Transparent,
                label = "swipe_color"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color, RoundedCornerShape(12.dp))
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Cancel,
                            contentDescription = "Cancelar",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            "Cancelar",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    ) {
        content()
    }
}

// ── Modal de detalle ─────────────────────────────────────────────────────────
@Composable
fun DetalleReservaDialog(
    reserva: ReservaHistorialDto,
    onDismiss: () -> Unit,
    onCancelar: () -> Unit
) {
    val fechaFormateada = reserva.fecha.split("-").reversed().joinToString("/")
    val puedeCancelar = reserva.estado == "Pendiente" || reserva.estado == "Confirmada"

    val (statusColor, statusBg) = when (reserva.estado) {
        "Pendiente"  -> Pair(Color(0xFFEF9F27), Color(0xFFFFF3E0))
        "Confirmada" -> Pair(Color(0xFF1D9E75), Color(0xFFE8F5E9))
        "Completada" -> Pair(Color(0xFF378ADD), Color(0xFFE3F2FD))
        "Cancelada"  -> Pair(Color(0xFFD63031), Color(0xFFFFEBEE))
        else         -> Pair(Color.Gray, Color(0xFFF5F5F5))
    }

    // Días restantes
    val diasRestantes = try {
        val fecha = LocalDate.parse(reserva.fecha)
        val hoy = LocalDate.now()
        ChronoUnit.DAYS.between(hoy, fecha)
    } catch (e: Exception) { null }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(24.dp)) {

                // Cabecera
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        reserva.nombreServicio,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Surface(color = statusBg, shape = RoundedCornerShape(6.dp)) {
                        Text(
                            reserva.estado,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = statusColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // Detalles
                DetalleRow("📅 Fecha", fechaFormateada)
                Spacer(modifier = Modifier.height(8.dp))
                DetalleRow("🕐 Hora", reserva.hora)

                // Días restantes
                if (diasRestantes != null && puedeCancelar) {
                    Spacer(modifier = Modifier.height(8.dp))
                    val textoFecha = when {
                        diasRestantes == 0L -> "¡Es hoy!"
                        diasRestantes == 1L -> "¡Mañana!"
                        diasRestantes > 0   -> "En $diasRestantes días"
                        else               -> "Fecha pasada"
                    }
                    DetalleRow("⏳ Cuándo", textoFecha)
                }

                // Observaciones
                reserva.observaciones?.let {
                    if (it.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        DetalleRow("📝 Notas", it)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (puedeCancelar) {
                        OutlinedButton(
                            onClick = onCancelar,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Cancelar reserva")
                        }
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}

@Composable
fun DetalleRow(label: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            valor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// ── Card de reserva ──────────────────────────────────────────────────────────
@Composable
fun ReservaCardReal(
    reserva: ReservaHistorialDto,
    onClick: () -> Unit = {}
) {
    val (statusColor, statusBg) = when (reserva.estado) {
        "Pendiente"  -> Pair(Color(0xFFEF9F27), Color(0xFFFFF3E0))
        "Confirmada" -> Pair(Color(0xFF1D9E75), Color(0xFFE8F5E9))
        "Completada" -> Pair(Color(0xFF378ADD), Color(0xFFE3F2FD))
        "Cancelada"  -> Pair(Color(0xFFD63031), Color(0xFFFFEBEE))
        else         -> Pair(Color.Gray, Color(0xFFF5F5F5))
    }

    val fechaFormateada = try {
        reserva.fecha.split("-").reversed().joinToString("/")
    } catch (e: Exception) { reserva.fecha }

    // Días restantes para próximas
    val diasRestantes = try {
        val fecha = LocalDate.parse(reserva.fecha)
        val hoy = LocalDate.now()
        val dias = ChronoUnit.DAYS.between(hoy, fecha)
        if (dias >= 0) dias else null
    } catch (e: Exception) { null }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    reserva.nombreServicio,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(color = statusBg, shape = RoundedCornerShape(6.dp)) {
                    Text(
                        reserva.estado,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = statusColor,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("📅 $fechaFormateada",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("🕐 ${reserva.hora}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    reserva.observaciones?.let {
                        if (it.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("📝 $it",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1)
                        }
                    }
                }
                // Badge de días restantes
                diasRestantes?.let { dias ->
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = when (dias) {
                                0L   -> "Hoy"
                                1L   -> "Mañana"
                                else -> "En $dias días"
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}