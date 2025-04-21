package com.example.lab5.ui.screens.profile

import com.example.lab5.data.model.User

/**
 * Data class representing the state of the Profile screen.
 * Compatible with Android API 24+ (Nougat and above).
 */
data class ProfileState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val user: User? = null,

    // Editable fields
    val name: String = "",
    val email: String = "",
    val birthDate: Long? = null,
    val height: String = "",
    val weight: String = "",
    val gender: String? = null,

    // Password change fields
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isPasswordChangeSuccessful: Boolean? = null,

    // UI states
    val showDatePicker: Boolean = false,
    val showPasswordDialog: Boolean = false,
    val showDeleteConfirmation: Boolean = false,

    // Validation flags
    val isNameValid: Boolean = true,
    val isEmailValid: Boolean = true,
    val isHeightValid: Boolean = true,
    val isWeightValid: Boolean = true,
    val isCurrentPasswordValid: Boolean = true,
    val isNewPasswordValid: Boolean = true,
    val isConfirmPasswordValid: Boolean = true,

    // Avatar/photo
    val avatarUri: String? = null,
    val isAvatarUploading: Boolean = false
) {
    /**
     * Returns true if all required fields are valid for profile update
     */
    fun isProfileValid(): Boolean {
        return isNameValid && isEmailValid &&
                (height.isEmpty() || isHeightValid) &&
                (weight.isEmpty() || isWeightValid)
    }

    /**
     * Returns true if all password fields are valid for password change
     */
    fun isPasswordChangeValid(): Boolean {
        return isCurrentPasswordValid &&
                isNewPasswordValid &&
                isConfirmPasswordValid &&
                newPassword == confirmPassword
    }
}