package com.example.proyectoruben.ui

import androidx.annotation.StringRes
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
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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

enum class Pantallas(@StringRes val titulo: Int){
    Perfil(titulo = R.string.pantalla_perfil),
    Reservar(titulo = R.string.pantalla_reservar),
    ListarReservas(titulo = R.string.pantalla_lista),

    Catalogo(titulo =  R.string.pantalla_productos)
}


val listaRutas = listOf(
    Ruta(Pantallas.Perfil.titulo, Pantallas.Perfil.name, Icons.Filled.Person, Icons.Outlined.Person),
    Ruta(Pantallas.Reservar.titulo, Pantallas.Reservar.name, Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
    Ruta(Pantallas.ListarReservas.titulo, Pantallas.ListarReservas.name, Icons.Filled.DateRange, Icons.Outlined.DateRange),
    Ruta(Pantallas.Catalogo.titulo, Pantallas.Catalogo.name, Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart)
)


@Composable
fun ProyectoRubenApp(
    navController: NavHostController = rememberNavController()
) {
    var selectedItem by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                listaRutas.forEachIndexed { indice, ruta ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (selectedItem == indice) ruta.iconoLleno else ruta.iconoVacio,
                                contentDescription = stringResource(id = ruta.nombre)
                            )
                        },
                        label = { Text(stringResource(id = ruta.nombre)) },
                        selected = selectedItem == indice,
                        onClick = {
                            selectedItem = indice
                            // Navegación simple
                            navController.navigate(ruta.ruta) {
                                // Opcional: evita que se apilen muchas pantallas al volver atrás
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
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
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Pantallas.Perfil.name) {
                Perfil(
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            composable(route = Pantallas.Reservar.name) {
                Reservar(
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            composable(route = Pantallas.ListarReservas.name) {
                ListarReservar(
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            composable (route = Pantallas.Catalogo.name){
                Catalogo(
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
    }
}