package com.example.myapp.profile

data class UserProfile(
    val name: String,
    val email: String,
    val goal: String,
    val targetCalories: Int,
    val restrictions: List<String>
)

sealed class ProfileEvent {
    data class LoadUserProfile(val email: String) : ProfileEvent()
    data class UpdateProfile(
        val name: String,
        val goal: String,
        val targetCalories: Int,
        val restrictions: List<String>
    ) : ProfileEvent()
    object Logout : ProfileEvent()
}

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val profile: UserProfile) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
    object Logout : ProfileUiState()
}