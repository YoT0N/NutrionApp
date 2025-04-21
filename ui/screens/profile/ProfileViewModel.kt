package com.example.lab5.ui.screens.profile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab5.data.model.User
import com.example.lab5.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                // Assuming we have current user ID - in real app get from auth/session
                val userId = 1L // Replace with actual current user ID
                userRepository.getUserById(userId).collect { user ->
                    user?.let {
                        _state.update {
                            it.copy(
                                user = user,
                                name = user.name,
                                email = user.email,
                                birthDate = user.birthDate?.time,
                                height = user.height?.toString() ?: "",
                                weight = user.weight?.toString() ?: "",
                                gender = user.gender,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message ?: "Failed to load user data",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun handleEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.NameChanged -> updateName(event.name)
            is ProfileEvent.EmailChanged -> updateEmail(event.email)
            is ProfileEvent.BirthDateChanged -> updateBirthDate(event.date)
            is ProfileEvent.HeightChanged -> updateHeight(event.height)
            is ProfileEvent.WeightChanged -> updateWeight(event.weight)
            is ProfileEvent.GenderChanged -> updateGender(event.gender)
            is ProfileEvent.CurrentPasswordChanged -> updateCurrentPassword(event.password)
            is ProfileEvent.NewPasswordChanged -> updateNewPassword(event.password)
            is ProfileEvent.ConfirmPasswordChanged -> updateConfirmPassword(event.password)
            ProfileEvent.LoadUserData -> loadUserData()
            ProfileEvent.SaveProfile -> saveProfile()
            ProfileEvent.ChangePassword -> changePassword()
            ProfileEvent.UploadPhoto -> uploadPhoto()
            ProfileEvent.DeleteAccount -> deleteAccount()
            ProfileEvent.Logout -> logout()
            ProfileEvent.ShowDatePicker -> showDatePicker()
            ProfileEvent.DismissDatePicker -> dismissDatePicker()
            ProfileEvent.ShowPasswordDialog -> showPasswordDialog()
            ProfileEvent.DismissPasswordDialog -> dismissPasswordDialog()
            ProfileEvent.ShowDeleteConfirmation -> showDeleteConfirmation()
            ProfileEvent.DismissDeleteConfirmation -> dismissDeleteConfirmation()
        }
    }

    private fun updateName(name: String) {
        _state.update {
            it.copy(
                name = name,
                isNameValid = name.length >= 2
            )
        }
    }

    private fun updateEmail(email: String) {
        _state.update {
            it.copy(
                email = email,
                isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
            )
        }
    }

    private fun updateBirthDate(date: Long) {
        _state.update {
            it.copy(birthDate = date)
        }
    }

    private fun updateHeight(height: Int) {
        _state.update {
            it.copy(
                height = height.toString(),
                isHeightValid = height in 50..250
            )
        }
    }

    private fun updateWeight(weight: Float) {
        _state.update {
            it.copy(
                weight = weight.toString(),
                isWeightValid = (weight >= 20f) && (weight <= 300f)
            )
        }
    }

    private fun updateGender(gender: String) {
        _state.update {
            it.copy(gender = gender)
        }
    }

    private fun updateCurrentPassword(password: String) {
        _state.update {
            it.copy(
                currentPassword = password,
                isCurrentPasswordValid = password.length >= 6
            )
        }
    }

    private fun updateNewPassword(password: String) {
        _state.update {
            it.copy(
                newPassword = password,
                isNewPasswordValid = password.length >= 6,
                isConfirmPasswordValid = password == _state.value.confirmPassword
            )
        }
    }

    private fun updateConfirmPassword(password: String) {
        _state.update {
            it.copy(
                confirmPassword = password,
                isConfirmPasswordValid = password == _state.value.newPassword
            )
        }
    }

    private fun showDatePicker() {
        _state.update { it.copy(showDatePicker = true) }
    }

    private fun dismissDatePicker() {
        _state.update { it.copy(showDatePicker = false) }
    }

    private fun showPasswordDialog() {
        _state.update { it.copy(showPasswordDialog = true) }
    }

    private fun dismissPasswordDialog() {
        _state.update { it.copy(showPasswordDialog = false) }
    }

    private fun showDeleteConfirmation() {
        _state.update { it.copy(showDeleteConfirmation = true) }
    }

    private fun dismissDeleteConfirmation() {
        _state.update { it.copy(showDeleteConfirmation = false) }
    }

    private fun saveProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val currentState = _state.value
                val updatedUser = currentState.user?.copy(
                    name = currentState.name,
                    email = currentState.email,
                    birthDate = currentState.birthDate?.let { Date(it) },
                    height = currentState.height.toIntOrNull(),
                    weight = currentState.weight.toFloatOrNull(),
                    gender = currentState.gender
                )

                updatedUser?.let {
                    userRepository.updateUser(it)
                    _state.update { state ->
                        state.copy(
                            user = updatedUser,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message ?: "Failed to update profile",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun changePassword() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val currentState = _state.value
                val email = currentState.user?.email ?: ""
                val currentPassword = currentState.currentPassword
                val newPassword = currentState.newPassword

                val isAuthenticated = userRepository.authenticate(email, currentPassword)
                if (isAuthenticated) {
                    val updatedUser = currentState.user?.copy(password = newPassword)
                    updatedUser?.let {
                        userRepository.updateUser(it)
                        _state.update {
                            it.copy(
                                isPasswordChangeSuccessful = true,
                                isLoading = false,
                                currentPassword = "",
                                newPassword = "",
                                confirmPassword = "",
                                showPasswordDialog = false
                            )
                        }
                    }
                } else {
                    _state.update {
                        it.copy(
                            error = "Current password is incorrect",
                            isLoading = false,
                            isCurrentPasswordValid = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message ?: "Failed to change password",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun uploadPhoto() {
        // Implementation depends on your image picker and storage solution
    }

    private fun deleteAccount() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                _state.value.user?.let { user ->
                    userRepository.deleteUser(user)
                    // Navigate to login screen (handled in UI layer)
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message ?: "Failed to delete account",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun logout() {
        // Clear user session (implementation depends on your auth system)
    }
}