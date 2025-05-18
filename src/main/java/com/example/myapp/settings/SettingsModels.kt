package com.example.myapp.settings

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

data class UserSettings(
    val darkThemeEnabled: Boolean = false,
    val language: String = "ua", // "ua" or "en"
    val uiDensity: String = "comfortable" // "comfortable" or "compact"
)

sealed class SettingsEvent {
    object LoadSettings : SettingsEvent()
    data class ToggleDarkTheme(val enabled: Boolean) : SettingsEvent()
    data class ChangeLanguage(val language: String) : SettingsEvent()
    data class ChangeUiDensity(val density: String) : SettingsEvent()
}
