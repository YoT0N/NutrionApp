package com.example.myapp.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapp.database.User
import com.example.myapp.database.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState

    private var currentUser: User? = null

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.LoadUserProfile -> loadUserProfile(event.email)
            is ProfileEvent.UpdateProfile -> updateProfile(
                event.name,
                event.goal,
                event.targetCalories,
                event.restrictions
            )
            is ProfileEvent.Logout -> logout()
        }
    }

    private fun loadUserProfile(email: String) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                Log.d("ProfileViewModel", "Шукаю користувача з email: $email")
                val user = userRepository.getUserByEmail(email)
                Log.d("ProfileViewModel", "Результат пошуку: ${user != null}")
                if (user != null) {
                    currentUser = user
                    _uiState.value = ProfileUiState.Success(
                        UserProfile(
                            name = user.name ?: "Користувач",
                            email = user.email,
                            goal = user.goal ?: "Не встановлено",
                            targetCalories = user.targetCalories ?: 2000,
                            restrictions = user.restrictions ?: emptyList()
                        )
                    )
                } else {
                    _uiState.value = ProfileUiState.Error("Користувача не знайдено")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("Помилка завантаження профіля: ${e.message}")
            }
        }
    }

    private fun updateProfile(
        name: String,
        goal: String,
        targetCalories: Int,
        restrictions: List<String>
    ) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                currentUser?.let { user ->
                    val updatedUser = user.copy(
                        name = name,
                        goal = goal,
                        targetCalories = targetCalories,
                        restrictions = restrictions
                    )
                    userRepository.updateUser(updatedUser)
                    _uiState.value = ProfileUiState.Success(
                        UserProfile(
                            name = updatedUser.name ?: "Користувач",
                            email = updatedUser.email,
                            goal = updatedUser.goal ?: "Не встановлено",
                            targetCalories = updatedUser.targetCalories ?: 2000,
                            restrictions = updatedUser.restrictions ?: emptyList()
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("Помилка оновлення профіля: ${e.message}")
            }
        }
    }

    private fun logout() {
        _uiState.value = ProfileUiState.Logout
    }
}

class ProfileViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}