package com.example.myapp.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    // Стан для UI
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Empty)
    val uiState: StateFlow<AuthUiState> = _uiState

    // Події від UI
    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.Login -> login(event.email, event.password)
            is AuthEvent.NavigateToRegistration -> {
                // Обробка навігації (в реальному додатку це буде через NavController)
                _uiState.value = AuthUiState.NavigateToRegistration
            }
        }
    }

    private fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            // Hard-coded логіка авторизації
            if (email == "test@example.com" && password == "password") {
                _uiState.value = AuthUiState.Success
            } else {
                _uiState.value = AuthUiState.Error("Невірний email або пароль")
            }
        }
    }
}

// Події від UI
sealed class AuthEvent {
    data class Login(val email: String, val password: String) : AuthEvent()
    object NavigateToRegistration : AuthEvent()
}

// Стан UI
sealed class AuthUiState {
    object Empty : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    object NavigateToRegistration : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}