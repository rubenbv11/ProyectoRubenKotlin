package com.example.proyectoruben.ui

import androidx.annotation.StringRes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectoruben.R
import com.example.proyectoruben.modelo.Ruta
import com.example.proyectoruben.ui.pantallas.Catalogo
import com.example.proyectoruben.ui.pantallas.ListarReservar
import com.example.proyectoruben.ui.pantallas.Perfil
import com.example.proyectoruben.ui.pantallas.Reservar
import com.example.proyectoruben.viewmodel.AuthViewModel
import com.example.proyectoruben.viewmodel.HistorialViewModel

enum class Pantallas(@StringRes val titulo: Int) {
    Perfil(titulo = R.string.pantalla_perfil),
    Reservar(titulo = R.string.pantalla_reservar),
    ListarReservas(titulo = R.string.pantalla_lista),
    Catalogo(titulo = R.string.pantalla_productos)
}

val listaRutas = listOf(
    Ruta(Pantallas.Perfil.titulo, Pantallas.Perfil.name,
        Icons.Filled.Person, Icons.Outlined.Person),
    Ruta(Pantallas.Reservar.titulo, Pantallas.Reservar.name,
        Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
    Ruta(Pantallas.ListarReservas.titulo, Pantallas.ListarReservas.name,
        Icons.Filled.DateRange, Icons.Outlined.DateRange),
    Ruta(Pantallas.Catalogo.titulo, Pantallas.Catalogo.name,
        Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart)
)

@Composable
fun ProyectoRubenApp(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel
) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val clienteId by authViewModel.clienteId.collectAsState(initial = 1)

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                tonalElevation = 0.dp
            ) {
                listaRutas.forEachIndexed { indice, ruta ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (selectedItem == indice)
                                    ruta.iconoLleno else ruta.iconoVacio,
                                contentDescription = stringResource(id = ruta.nombre)
                            )
                        },
                        label = { Text(stringResource(id = ruta.nombre)) },
                        selected = selectedItem == indice,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.secondary,
                            selectedTextColor = MaterialTheme.colorScheme.secondary,
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        onClick = {
                            selectedItem = indice
                            navController.navigate(ruta.ruta) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Pantallas.Perfil.name,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it }) + fadeIn()
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
            }
        ) {
            composable(route = Pantallas.Perfil.name) {
                Perfil(
                    modifier = Modifier.fillMaxSize(),
                    authViewModel = authViewModel
                )
            }
            composable(route = Pantallas.Reservar.name) {
                Reservar(
                    modifier = Modifier.fillMaxSize(),
                    clienteId = clienteId ?: 1
                )
            }
            composable(route = Pantallas.ListarReservas.name) {
                val historialViewModel: HistorialViewModel =
                    androidx.lifecycle.viewmodel.compose.viewModel()
                ListarReservar(
                    modifier = Modifier.fillMaxSize(),
                    viewModel = historialViewModel,
                    clienteId = clienteId ?: 1
                )
            }
            composable(route = Pantallas.Catalogo.name) {
                Catalogo(modifier = Modifier.fillMaxSize())
            }
            composable(route = Pantallas.ListarReservas.name) {
                val historialViewModel: HistorialViewModel =
                    androidx.lifecycle.viewmodel.compose.viewModel()
                ListarReservar(
                    modifier = Modifier.fillMaxSize(),
                    viewModel = historialViewModel,
                    clienteId = clienteId ?: 1,
                    onHacerReserva = {
                        selectedItem = 1  // índice de Reservar en listaRutas
                        navController.navigate(Pantallas.Reservar.name) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}