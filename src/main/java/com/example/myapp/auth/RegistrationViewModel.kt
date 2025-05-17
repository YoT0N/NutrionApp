package com.example.myapp.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<RegistrationUiState>(RegistrationUiState.Idle)
    val uiState: StateFlow<RegistrationUiState> = _uiState

    fun onEvent(event: RegistrationEvent) {
        when (event) {
            is RegistrationEvent.Register -> register(
                event.email,
                event.password,
                event.confirmPassword
            )
            RegistrationEvent.NavigateToLogin -> _uiState.value = RegistrationUiState.NavigateToLogin
        }
    }

    private fun register(email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _uiState.value = RegistrationUiState.Loading
            try {
                if (password != confirmPassword) {
                    _uiState.value = RegistrationUiState.Error("Паролі не співпадають")
                    return@launch
                }

                if (!isValidEmail(email)) {
                    _uiState.value = RegistrationUiState.Error("Невірний формат email")
                    return@launch
                }

                if (!isValidPassword(password)) {
                    _uiState.value = RegistrationUiState.Error("Пароль повинен містити мінімум 6 символів")
                    return@launch
                }

                val user = authRepository.register(email, password)
                _uiState.value = if (user != null) {
                    RegistrationUiState.Success
                } else {
                    RegistrationUiState.Error("Користувач з таким email вже існує")
                }
            } catch (e: Exception) {
                _uiState.value = RegistrationUiState.Error("Помилка реєстрації: ${e.message}")
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
}

sealed class RegistrationEvent {
    data class Register(
        val email: String,
        val password: String,
        val confirmPassword: String
    ) : RegistrationEvent()

    object NavigateToLogin : RegistrationEvent()
}

sealed class RegistrationUiState {
    object Idle : RegistrationUiState()
    object Loading : RegistrationUiState()
    object Success : RegistrationUiState()
    object NavigateToLogin : RegistrationUiState()
    data class Error(val message: String) : RegistrationUiState()
}