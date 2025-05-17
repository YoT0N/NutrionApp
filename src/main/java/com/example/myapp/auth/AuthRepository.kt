package com.example.myapp.auth

import android.util.Log
import com.example.myapp.database.UserDao
import com.example.myapp.database.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

open class AuthRepository(private val userDao: UserDao) {
    private val _currentUserEmail = MutableStateFlow<String?>(null)

    suspend fun login(email: String, password: String): User? {
        delay(1000)
        val user = userDao.getUserByEmail(email)
        return user?.let {
            val isValid = it.passwordHash == password.hashCode().toString()
            if (isValid) {
                _currentUserEmail.value = email
                it
            } else {
                null
            }
        }
    }

    suspend fun register(email: String, password: String): User? {
        return if (userDao.getUserByEmail(email) == null) {
            val user = User(
                email = email,
                passwordHash = password.hashCode().toString()
            )
            userDao.insert(user)
            Log.d("AuthRepository", "Пользователь зарегистрирован: $email")
            _currentUserEmail.value = email
            userDao.getUserByEmail(email)
        } else {
            null
        }
    }

    fun logout() {
        _currentUserEmail.value = null
    }
}