package com.example.lab5.ui.screens.profile

sealed class ProfileEvent {
    // User data events
    data class NameChanged(val name: String) : ProfileEvent()
    data class EmailChanged(val email: String) : ProfileEvent()
    data class BirthDateChanged(val date: Long) : ProfileEvent()
    data class HeightChanged(val height: Int) : ProfileEvent()
    data class WeightChanged(val weight: Float) : ProfileEvent()
    data class GenderChanged(val gender: String) : ProfileEvent()

    // Password change events
    data class CurrentPasswordChanged(val password: String) : ProfileEvent()
    data class NewPasswordChanged(val password: String) : ProfileEvent()
    data class ConfirmPasswordChanged(val password: String) : ProfileEvent()

    // Action events
    object LoadUserData : ProfileEvent()
    object SaveProfile : ProfileEvent()
    object ChangePassword : ProfileEvent()
    object UploadPhoto : ProfileEvent()
    object DeleteAccount : ProfileEvent()
    object Logout : ProfileEvent()

    // UI events
    object ShowDatePicker : ProfileEvent()
    object DismissDatePicker : ProfileEvent()
    object ShowPasswordDialog : ProfileEvent()
    object DismissPasswordDialog : ProfileEvent()
    object ShowDeleteConfirmation : ProfileEvent()
    object DismissDeleteConfirmation : ProfileEvent()
}