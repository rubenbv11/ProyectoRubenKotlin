package com.example.proyectoruben.ui.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectoruben.R
import com.example.proyectoruben.viewmodel.AuthUiState
import com.example.proyectoruben.viewmodel.AuthViewModel

@Composable
fun PantallaLogin(
    viewModel: AuthViewModel,
    onLoginExitoso: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var modoRegistro by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var verContrasena by remember { mutableStateOf(false) }

    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var verConfirmar by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Exito) {
            onLoginExitoso()
            viewModel.resetState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // ── Logo ──────────────────────────────────────────────────────────
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Peluquería Charo",
                modifier = Modifier
                    .width(240.dp)
                    .height(110.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                if (modoRegistro) "Crear cuenta" else "Bienvenida",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                if (modoRegistro) "Rellena tus datos para registrarte"
                else "Inicia sesión para continuar",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── Formulario ────────────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    if (modoRegistro) {
                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre completo *") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, null,
                                    tint = MaterialTheme.colorScheme.secondary)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email *") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, null,
                                tint = MaterialTheme.colorScheme.secondary)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (modoRegistro) {
                        OutlinedTextField(
                            value = telefono,
                            onValueChange = { telefono = it },
                            label = { Text("Teléfono") },
                            leadingIcon = {
                                Icon(Icons.Default.Phone, null,
                                    tint = MaterialTheme.colorScheme.secondary)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    OutlinedTextField(
                        value = contrasena,
                        onValueChange = { contrasena = it },
                        label = { Text("Contraseña *") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, null,
                                tint = MaterialTheme.colorScheme.secondary)
                        },
                        trailingIcon = {
                            IconButton(onClick = { verContrasena = !verContrasena }) {
                                Icon(
                                    if (verContrasena) Icons.Default.VisibilityOff
                                    else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (verContrasena)
                            VisualTransformation.None
                        else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    if (modoRegistro) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = confirmar,
                            onValueChange = { confirmar = it },
                            label = { Text("Confirmar contraseña *") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, null,
                                    tint = MaterialTheme.colorScheme.secondary)
                            },
                            trailingIcon = {
                                IconButton(onClick = { verConfirmar = !verConfirmar }) {
                                    Icon(
                                        if (verConfirmar) Icons.Default.VisibilityOff
                                        else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            visualTransformation = if (verConfirmar)
                                VisualTransformation.None
                            else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    // Error
                    if (uiState is AuthUiState.Error) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.errorContainer,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                (uiState as AuthUiState.Error).mensaje,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botón principal
                    Button(
                        onClick = {
                            if (modoRegistro) {
                                viewModel.registro(nombre, email, telefono, contrasena, confirmar)
                            } else {
                                viewModel.login(email, contrasena)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = uiState !is AuthUiState.Cargando,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (uiState is AuthUiState.Cargando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (modoRegistro) "Creando cuenta..." else "Iniciando sesión...")
                        } else {
                            Text(
                                if (modoRegistro) "Crear cuenta" else "Iniciar sesión",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cambiar entre login y registro
            TextButton(onClick = {
                modoRegistro = !modoRegistro
                viewModel.resetState()
                email = ""; contrasena = ""; nombre = ""
                telefono = ""; confirmar = ""
            }) {
                Text(
                    if (modoRegistro)
                        "¿Ya tienes cuenta? Inicia sesión"
                    else
                        "¿No tienes cuenta? Regístrate",
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}