package com.example.myapp.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.Login -> login(event.email, event.password)
            AuthEvent.NavigateToRegistration -> _uiState.value = AuthUiState.NavigateToRegistration
        }
    }

    private fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val isSuccess = authRepository.login(email, password)
                _uiState.value = if (isSuccess) {
                    AuthUiState.Success
                } else {
                    AuthUiState.Error("Невірний email або пароль")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Помилка мережі: ${e.message}")
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
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    object NavigateToRegistration : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}