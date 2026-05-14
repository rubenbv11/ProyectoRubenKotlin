package com.example.proyectoruben.ui.pantallas

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectoruben.modelo.ServicioDto
import com.example.proyectoruben.viewmodel.ReservaUiState
import com.example.proyectoruben.viewmodel.ReservarViewModel
import com.example.proyectoruben.viewmodel.ServiciosUiState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.material3.SelectableDates

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Reservar(
    modifier: Modifier = Modifier,
    viewModel: ReservarViewModel = viewModel(),
    clienteId: Int = 1
) {
    val uiState by viewModel.uiState.collectAsState()
    val reservaState by viewModel.reservaState.collectAsState()

    var servicioSeleccionado by remember { mutableStateOf<ServicioDto?>(null) }
    var horaSeleccionada by remember { mutableStateOf("") }
    var fechaSeleccionada by remember { mutableStateOf<LocalDate?>(null) }
    var mostrarDatePicker by remember { mutableStateOf(false) }

    val horas = listOf("09:00", "10:00", "11:30", "12:00", "16:00", "17:30", "18:30", "19:00")
    val formatoMostrar = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es", "ES"))
    val formatoApi = DateTimeFormatter.ISO_LOCAL_DATE
    val hoy = remember { LocalDate.now() }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val fecha = LocalDate.ofEpochDay(utcTimeMillis / 86400000)
                return !fecha.isBefore(hoy)
            }
        }
    )

    val puedeConfirmar = servicioSeleccionado != null &&
            fechaSeleccionada != null &&
            horaSeleccionada.isNotEmpty()

    // ── Diálogo éxito ────────────────────────────────────────────────────────
    if (reservaState is ReservaUiState.Exito) {
        AlertDialog(
            onDismissRequest = { viewModel.resetReservaState() },
            icon = { Text("✅", style = MaterialTheme.typography.headlineLarge) },
            title = {
                Text("¡Reserva confirmada!", textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth())
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()) {
                    servicioSeleccionado?.let {
                        Text(it.nombre, fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    fechaSeleccionada?.let {
                        Text("📅 ${it.format(formatoMostrar).replaceFirstChar { c -> c.uppercase() }}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("🕐 $horaSeleccionada",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetReservaState()
                        servicioSeleccionado = null
                        horaSeleccionada = ""
                        fechaSeleccionada = null
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Perfecto") }
            }
        )
    }

    // ── DatePicker ───────────────────────────────────────────────────────────
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
        ) { DatePicker(state = datePickerState) }
    }

    // ── Layout principal ─────────────────────────────────────────────────────
    Column(modifier = modifier.fillMaxSize()) {

        // Cabecera fija
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Nueva Reserva",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Elige servicio, fecha y hora",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Indicador de pasos
        StepIndicator(
            paso1 = servicioSeleccionado != null,
            paso2 = fechaSeleccionada != null,
            paso3 = horaSeleccionada.isNotEmpty()
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {

            // ── PASO 1: Servicio ─────────────────────────────────────────────
            item {
                SectionHeader(
                    numero = "1",
                    titulo = "Elige un servicio",
                    completado = servicioSeleccionado != null,
                    subtitulo = servicioSeleccionado?.nombre
                )
            }

            when (val state = uiState) {
                is ServiciosUiState.Cargando -> item {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp),
                        contentAlignment = Alignment.Center) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Cargando servicios...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                is ServiciosUiState.Error -> item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(state.mensaje, color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(onClick = { viewModel.cargarServicios() }) {
                            Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reintentar")
                        }
                    }
                }
                is ServiciosUiState.Exito -> item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(state.servicios) { servicio ->
                            ServicioCardCarrusel(
                                servicio = servicio,
                                seleccionado = servicioSeleccionado?.id == servicio.id,
                                onClick = { servicioSeleccionado = servicio }
                            )
                        }
                    }
                }
            }
            // ── PASO 2: Fecha ────────────────────────────────────────────────
            item {
                SectionHeader(
                    numero = "2",
                    titulo = "Selecciona la fecha",
                    completado = fechaSeleccionada != null,
                    subtitulo = fechaSeleccionada?.format(formatoMostrar)
                        ?.replaceFirstChar { it.uppercase() }
                )
                Padding16 {
                    OutlinedCard(
                        onClick = { mostrarDatePicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(
                            if (fechaSeleccionada != null) 2.dp else 1.dp,
                            if (fechaSeleccionada != null)
                                MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (fechaSeleccionada != null)
                                                MaterialTheme.colorScheme.secondaryContainer
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.DateRange,
                                        contentDescription = null,
                                        tint = if (fechaSeleccionada != null)
                                            MaterialTheme.colorScheme.secondary
                                        else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        if (fechaSeleccionada != null) "Fecha seleccionada"
                                        else "Toca para seleccionar",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        fechaSeleccionada?.format(formatoMostrar)
                                            ?.replaceFirstChar { it.uppercase() }
                                            ?: "Seleccionar fecha",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (fechaSeleccionada != null)
                                            FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (fechaSeleccionada != null)
                                            MaterialTheme.colorScheme.onSurface
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            if (fechaSeleccionada != null) {
                                Icon(Icons.Default.Check, null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }

            // ── PASO 3: Hora ─────────────────────────────────────────────────
            item {
                SectionHeader(
                    numero = "3",
                    titulo = "Hora disponible",
                    completado = horaSeleccionada.isNotEmpty(),
                    subtitulo = if (horaSeleccionada.isNotEmpty()) horaSeleccionada else null
                )
                Padding16 {
                    // Grid 4 columnas
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        horas.chunked(4).forEach { fila ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                fila.forEach { hora ->
                                    HoraChip(
                                        hora = hora,
                                        seleccionada = horaSeleccionada == hora,
                                        onClick = { horaSeleccionada = hora },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                // Rellena espacios vacíos si la fila tiene menos de 4
                                repeat(4 - fila.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            // ── Resumen ──────────────────────────────────────────────────────
            if (puedeConfirmar) {
                item {
                    Padding16 {
                        ResumenReserva(
                            servicio = servicioSeleccionado,
                            fecha = fechaSeleccionada?.format(formatoMostrar)
                                ?.replaceFirstChar { it.uppercase() } ?: "",
                            hora = horaSeleccionada
                        )
                    }
                }
            }
        }

        // ── Botón fijo abajo ─────────────────────────────────────────────────
        Surface(
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (reservaState is ReservaUiState.Error) {
                    Text(
                        (reservaState as ReservaUiState.Error).mensaje,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Button(
                    onClick = {
                        servicioSeleccionado?.let { s ->
                            fechaSeleccionada?.let { f ->
                                viewModel.confirmarReserva(
                                    servicioId = s.id,
                                    fecha = f.format(formatoApi),
                                    hora = horaSeleccionada,
                                    clienteId = clienteId
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    enabled = puedeConfirmar && reservaState !is ReservaUiState.Enviando,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    if (reservaState is ReservaUiState.Enviando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Confirmando...", fontWeight = FontWeight.SemiBold)
                    } else {
                        Text(
                            if (puedeConfirmar) "Confirmar reserva" else "Completa los pasos",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

// ── Componentes auxiliares ───────────────────────────────────────────────────

@Composable
fun StepIndicator(paso1: Boolean, paso2: Boolean, paso3: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepDot(numero = "1", completado = paso1, label = "Servicio")
        StepLine(completado = paso1)
        StepDot(numero = "2", completado = paso2, label = "Fecha")
        StepLine(completado = paso2)
        StepDot(numero = "3", completado = paso3, label = "Hora")
    }
}

@Composable
fun StepDot(numero: String, completado: Boolean, label: String) {
    val bgColor by animateColorAsState(
        targetValue = if (completado) MaterialTheme.colorScheme.secondary
        else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(300), label = "step_color"
    )
    val textColor by animateColorAsState(
        targetValue = if (completado) MaterialTheme.colorScheme.onSecondary
        else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(300), label = "step_text"
    )
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(28.dp).clip(CircleShape).background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            if (completado) {
                Icon(Icons.Default.Check, null,
                    tint = textColor, modifier = Modifier.size(16.dp))
            } else {
                Text(numero, style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold, color = textColor)
            }
        }
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = if (completado) MaterialTheme.colorScheme.secondary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp)
    }
}

@Composable
fun RowScope.StepLine(completado: Boolean) {
    val color by animateColorAsState(
        targetValue = if (completado) MaterialTheme.colorScheme.secondary
        else MaterialTheme.colorScheme.outlineVariant,
        animationSpec = tween(300), label = "line_color"
    )
    HorizontalDivider(modifier = Modifier.weight(1f).padding(bottom = 14.dp),
        color = color, thickness = 2.dp)
}

@Composable
fun SectionHeader(numero: String, titulo: String, completado: Boolean, subtitulo: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        if (completado) MaterialTheme.colorScheme.secondary
                        else MaterialTheme.colorScheme.primaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    numero,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (completado) MaterialTheme.colorScheme.onSecondary
                    else MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(titulo, style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold)
        }
        if (subtitulo != null && completado) {
            Text(subtitulo,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false).padding(start = 8.dp),
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun HoraChip(hora: String, seleccionada: Boolean, onClick: () -> Unit, modifier: Modifier) {
    val bgColor by animateColorAsState(
        targetValue = if (seleccionada) MaterialTheme.colorScheme.secondary
        else MaterialTheme.colorScheme.surface,
        animationSpec = tween(200), label = "hora_bg"
    )
    val textColor by animateColorAsState(
        targetValue = if (seleccionada) MaterialTheme.colorScheme.onSecondary
        else MaterialTheme.colorScheme.onSurface,
        animationSpec = tween(200), label = "hora_text"
    )

    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(
            1.dp,
            if (seleccionada) MaterialTheme.colorScheme.secondary
            else MaterialTheme.colorScheme.outlineVariant
        ),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(hora,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (seleccionada) FontWeight.Bold else FontWeight.Normal,
                color = textColor)
        }
    }
}

@Composable
fun ResumenReserva(servicio: ServicioDto?, fecha: String, hora: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Resumen de tu reserva",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 10.dp))
            servicio?.let {
                ResumenRow("✂️ Servicio", it.nombre)
                Spacer(modifier = Modifier.height(4.dp))
            }
            ResumenRow("📅 Fecha", fecha)
            Spacer(modifier = Modifier.height(4.dp))
            ResumenRow("🕐 Hora", hora)
            servicio?.let {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onSecondaryContainer.copy(.2f))
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total estimado",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer)
                    Text("${"%.2f".format(it.costo)}€",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
    }
}

@Composable
fun ResumenRow(label: String, valor: String) {
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(.7f))
        Text(valor, style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            maxLines = 1, overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, false).padding(start = 8.dp),
            textAlign = TextAlign.End)
    }
}

@Composable
fun Padding16(content: @Composable () -> Unit) {
    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        content()
    }
}
    @Composable
    fun ServicioCardCarrusel(
        servicio: ServicioDto,
        seleccionado: Boolean,
        onClick: () -> Unit
    ) {
        val bgColor by animateColorAsState(
            targetValue = if (seleccionado) MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.surface,
            animationSpec = tween(200), label = "card_bg"
        )
        val borderColor by animateColorAsState(
            targetValue = if (seleccionado) MaterialTheme.colorScheme.secondary
            else MaterialTheme.colorScheme.outlineVariant,
            animationSpec = tween(200), label = "card_border"
        )

        Card(
            onClick = onClick,
            modifier = Modifier
                .width(160.dp)
                .height(160.dp),
            colors = CardDefaults.cardColors(containerColor = bgColor),
            border = BorderStroke(if (seleccionado) 2.dp else 1.dp, borderColor),
            elevation = CardDefaults.cardElevation(0.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Icono arriba
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (seleccionado)
                                MaterialTheme.colorScheme.secondary.copy(alpha = .2f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (seleccionado) {
                        Icon(Icons.Default.Check, null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp))
                    } else {
                        Icon(Icons.Default.Schedule, null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp))
                    }
                }

                // Info abajo
                Column {
                    Text(
                        servicio.nombre,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("${servicio.duracion}min",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${"%.2f".format(servicio.costo)}€",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }