package com.example.proyectoruben.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectoruben.modelo.ProductoDto
import com.example.proyectoruben.red.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class CatalogoUiState {
    object Cargando : CatalogoUiState()
    data class Exito(val productos: List<ProductoDto>) : CatalogoUiState()
    data class Error(val mensaje: String) : CatalogoUiState()
}

class CatalogoViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<CatalogoUiState>(CatalogoUiState.Cargando)
    val uiState: StateFlow<CatalogoUiState> = _uiState

    init { cargarProductos() }

    fun cargarProductos() {
        viewModelScope.launch {
            _uiState.value = CatalogoUiState.Cargando
            try {
                val productos = RetrofitClient.apiService.getProductos()
                _uiState.value = CatalogoUiState.Exito(productos)
            } catch (e: Exception) {
                _uiState.value = CatalogoUiState.Error("Error: ${e.message}")
            }
        }
    }
}

@Composable
fun Catalogo(
    modifier: Modifier = Modifier,
    viewModel: CatalogoViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var productoSeleccionado by remember { mutableStateOf<ProductoDto?>(null) }
    var categoriaSeleccionada by remember { mutableStateOf("Todos") }

    productoSeleccionado?.let { producto ->
        ProductoDetalleDialog(
            producto = producto,
            onDismiss = { productoSeleccionado = null }
        )
    }

    Column(modifier = modifier.fillMaxSize()) {

        // ── Cabecera ─────────────────────────────────────────────────────────
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Catálogo",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                when (val s = uiState) {
                    is CatalogoUiState.Exito -> Text(
                        "${s.productos.size} productos disponibles",
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

        when (val state = uiState) {
            is CatalogoUiState.Cargando -> {
                Box(modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Cargando catálogo...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            is CatalogoUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.mensaje,
                            color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(onClick = { viewModel.cargarProductos() }) {
                            Icon(Icons.Default.Refresh, null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reintentar")
                        }
                    }
                }
            }
            is CatalogoUiState.Exito -> {
                // Categorías únicas
                val categorias = listOf("Todos") +
                        state.productos.mapNotNull { it.categoria }.distinct()

                val productosFiltrados = if (categoriaSeleccionada == "Todos")
                    state.productos
                else
                    state.productos.filter { it.categoria == categoriaSeleccionada }

                // ── Filtro de categorías ──────────────────────────────────────
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categorias) { categoria ->
                        FilterChip(
                            selected = categoriaSeleccionada == categoria,
                            onClick = { categoriaSeleccionada = categoria },
                            label = { Text(categoria) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondary
                            )
                        )
                    }
                }

                // ── Grid ─────────────────────────────────────────────────────
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(
                        start = 12.dp, end = 12.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(productosFiltrados) { producto ->
                        ProductoCard(
                            producto = producto,
                            onClick = { productoSeleccionado = producto }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductoCard(producto: ProductoDto, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            // Imagen con color de fondo dorado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    producto.nombre.first().uppercaseChar().toString(),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                // Badge agotado
                if (!producto.disponible) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp),
                        color = MaterialTheme.colorScheme.error,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            "Agotado",
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onError,
                            fontSize = 9.sp
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                producto.categoria?.let {
                    Text(
                        it.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 9.sp
                    )
                }
                Text(
                    producto.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "${"%.2f".format(producto.precio)}€",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun ProductoDetalleDialog(producto: ProductoDto, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column {
                // Cabecera con color
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        producto.nombre.first().uppercaseChar().toString(),
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    // Badge disponibilidad
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp),
                        color = if (producto.disponible)
                            MaterialTheme.colorScheme.secondary
                        else MaterialTheme.colorScheme.error,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            if (producto.disponible) "Disponible" else "Agotado",
                            modifier = Modifier.padding(
                                horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (producto.disponible)
                                MaterialTheme.colorScheme.onSecondary
                            else MaterialTheme.colorScheme.onError
                        )
                    }
                }

                Column(modifier = Modifier.padding(20.dp)) {
                    producto.categoria?.let {
                        Text(
                            it.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        producto.nombre,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    producto.descripcion?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Precio",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "${"%.2f".format(producto.precio)}€",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cerrar", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}