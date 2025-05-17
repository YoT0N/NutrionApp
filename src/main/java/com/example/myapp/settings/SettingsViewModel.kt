package com.example.myapp.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsDataStore = SettingsDataStore(application)

    private val _settingsState = MutableStateFlow(UserSettings())
    val settingsState: StateFlow<UserSettings> = _settingsState

    init {
        loadSettings()
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.ToggleDarkTheme -> setDarkTheme(event.enabled)
            is SettingsEvent.ChangeLanguage -> setLanguage(event.language)
            is SettingsEvent.ChangeUiDensity -> setUiDensity(event.density)
            is SettingsEvent.LoadSettings -> loadSettings()
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            combine(
                settingsDataStore.darkThemeFlow,
                settingsDataStore.languageFlow,
                settingsDataStore.uiDensityFlow
            ) { darkThemeEnabled, language, uiDensity ->
                UserSettings(
                    darkThemeEnabled = darkThemeEnabled,
                    language = language,
                    uiDensity = uiDensity
                )
            }.collect { settings ->
                _settingsState.value = settings
            }
        }
    }

    private fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.saveDarkThemeEnabled(enabled)
        }
    }

    private fun setLanguage(language: String) {
        viewModelScope.launch {
            settingsDataStore.saveLanguage(language)
        }
    }

    private fun setUiDensity(density: String) {
        viewModelScope.launch {
            settingsDataStore.saveUiDensity(density)
        }
    }
}

class SettingsViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}