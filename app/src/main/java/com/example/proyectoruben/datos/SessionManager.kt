package com.example.proyectoruben.datos

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensión para crear el DataStore una sola vez
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sesion")

/**
 * Gestiona la sesión del usuario guardando y leyendo
 * los datos del cliente logueado en DataStore.
 */
class SessionManager(private val context: Context) {

    companion object {
        val KEY_ID      = intPreferencesKey("cliente_id")
        val KEY_NOMBRE  = stringPreferencesKey("cliente_nombre")
        val KEY_EMAIL   = stringPreferencesKey("cliente_email")
        val KEY_TELEFONO = stringPreferencesKey("cliente_telefono")
    }

    // Guarda la sesión tras login exitoso
    suspend fun guardarSesion(id: Int, nombre: String, email: String, telefono: String?) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ID]       = id
            prefs[KEY_NOMBRE]   = nombre
            prefs[KEY_EMAIL]    = email
            prefs[KEY_TELEFONO] = telefono ?: ""
        }
    }

    // Cierra la sesión borrando todos los datos
    suspend fun cerrarSesion() {
        context.dataStore.edit { it.clear() }
    }

    // Flujo que emite true si hay sesión activa
    val haySession: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[KEY_ID] != null }

    // Flujo con el ID del cliente
    val clienteId: Flow<Int?> = context.dataStore.data
        .map { prefs -> prefs[KEY_ID] }

    // Flujo con el nombre del cliente
    val clienteNombre: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[KEY_NOMBRE] ?: "" }

    val clienteEmail: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[KEY_EMAIL] ?: "" }

    val clienteTelefono: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[KEY_TELEFONO] ?: "" }
}