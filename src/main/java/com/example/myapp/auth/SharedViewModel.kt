// SharedViewModel.kt
package com.example.myapp.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

open class SharedViewModel : ViewModel() {
    private val _userId = MutableStateFlow<Long?>(null)
    val userId: StateFlow<Long?> = _userId

    private val _currentUserEmail = MutableStateFlow<String?>(null)
    val currentUserEmail: StateFlow<String?> = _currentUserEmail

    fun setUser(email: String, id: Long) {
        _currentUserEmail.value = email
        _userId.value = id
    }

    fun clearUser() {
        _currentUserEmail.value = null
        _userId.value = null
    }
}