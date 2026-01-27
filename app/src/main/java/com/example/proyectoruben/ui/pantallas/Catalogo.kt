package com.example.proyectoruben.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Modelo mock basado en Producto.cs
data class ProductoUI(
    val nombre: String,
    val precio: Double,
    val stock: Int
)

@Composable
fun Catalogo(modifier: Modifier = Modifier) {
    val productos = listOf(
        ProductoUI("Champú Pro", 12.50, 10),
        ProductoUI("Cera Mate", 8.99, 5),
        ProductoUI("Aceite Barba", 15.00, 2),
        ProductoUI("Gel Fijador", 6.50, 0) // Sin stock
    )

    Column(modifier = modifier.padding(8.dp)) {
        Text(
            "Nuestros Productos",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(8.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(productos) { producto ->
                ProductoItem(producto)
            }
        }
    }
}

@Composable
fun ProductoItem(producto: ProductoUI) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Placeholder para ImagenUrl
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = producto.nombre, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(text = "${producto.precio}€", color = MaterialTheme.colorScheme.primary)

            Spacer(modifier = Modifier.height(8.dp))

            if (producto.stock > 0) {
                Button(
                    onClick = { /* Añadir al carrito */ },
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Añadir", fontSize = 12.sp)
                }
            } else {
                Text("Agotado", color = Color.Red, fontSize = 12.sp)
            }
        }
    }
}