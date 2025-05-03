package com.example.myapp.auth

// Hard-coded репозиторій
class AuthRepository {
    suspend fun login(email: String, password: String): Boolean {
        // Імітація мережевого запиту
        kotlinx.coroutines.delay(1000)
        return email == "test@example.com" && password == "password"
    }
}