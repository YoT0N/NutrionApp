package com.example.myapp.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val sharedViewModel: SharedViewModel
) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.Login -> login(event.email, event.password)
            AuthEvent.NavigateToRegistration -> _uiState.value = AuthUiState.NavigateToRegistration
            AuthEvent.Logout -> logout()
        }
    }

    private fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val userResult = authRepository.login(email, password)
                if (userResult != null) {
                    sharedViewModel.setUser(email, userResult.id)
                    _uiState.value = AuthUiState.Success(email)
                } else {
                    _uiState.value = AuthUiState.Error("Невірний email або пароль")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error("Помилка авторизації: ${e.message}")
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            sharedViewModel.clearUser()
            _uiState.value = AuthUiState.Idle
        }
    }
}

sealed class AuthEvent {
    data class Login(val email: String, val password: String) : AuthEvent()
    object NavigateToRegistration : AuthEvent()
    object Logout : AuthEvent()
}

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val email: String) : AuthUiState()
    object NavigateToRegistration : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}