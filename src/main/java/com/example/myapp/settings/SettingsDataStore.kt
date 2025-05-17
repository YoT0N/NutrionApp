package com.example.myapp.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Create a DataStore instance at the top level
private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {

    // Define preference keys
    companion object {
        val DARK_THEME_ENABLED = booleanPreferencesKey("dark_theme_enabled")
        val LANGUAGE = stringPreferencesKey("language")
        val UI_DENSITY = stringPreferencesKey("ui_density")
    }

    // Theme settings
    val darkThemeFlow: Flow<Boolean> = context.settingsDataStore.data
        .map { preferences ->
            preferences[DARK_THEME_ENABLED] ?: false
        }

    suspend fun saveDarkThemeEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[DARK_THEME_ENABLED] = enabled
        }
    }

    // Language settings
    val languageFlow: Flow<String> = context.settingsDataStore.data
        .map { preferences ->
            preferences[LANGUAGE] ?: "ua" // Default language is Ukrainian
        }

    suspend fun saveLanguage(language: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[LANGUAGE] = language
        }
    }

    // UI Density settings
    val uiDensityFlow: Flow<String> = context.settingsDataStore.data
        .map { preferences ->
            preferences[UI_DENSITY] ?: "comfortable" // Default density
        }

    suspend fun saveUiDensity(density: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[UI_DENSITY] = density
        }
    }
}