package com.example.lab5.data.repository

import com.example.lab5.data.local.dao.UserDao
import com.example.lab5.data.local.entities.UserEntity
import com.example.lab5.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

interface UserRepository {
    suspend fun createUser(user: User): Long
    suspend fun updateUser(user: User)
    suspend fun deleteUser(user: User)
    fun getUserById(userId: Long): Flow<User?>
    fun getUserByEmail(email: String): Flow<User?>
    suspend fun authenticate(email: String, password: String): Boolean
    suspend fun isEmailAvailable(email: String): Boolean
}

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {

    // Create
    override suspend fun createUser(user: User): Long {
        return userDao.insertUser(user.toEntity())
    }

    // Read
    override fun getUserById(userId: Long): Flow<User?> {
        return userDao.getUserById(userId).map { it?.toModel() }
    }

    override fun getUserByEmail(email: String): Flow<User?> {
        return userDao.getUserByEmail(email).map { it?.toModel() }
    }

    // Update
    override suspend fun updateUser(user: User) {
        userDao.updateUser(user.toEntity())
    }

    // Delete
    override suspend fun deleteUser(user: User) {
        userDao.deleteUser(user.toEntity())
    }

    // Authentication
    override suspend fun authenticate(email: String, password: String): Boolean {
        // Note: In production, compare hashed passwords
        return userDao.authenticateUser(email, password) > 0
    }

    // Validation
    override suspend fun isEmailAvailable(email: String): Boolean {
        return !userDao.isEmailExists(email)
    }
}

// Extension functions for conversion
private fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    email = email,
    password = password, // Remember to hash password before saving in production
    name = name,
    birthDate = birthDate,
    height = height,
    weight = weight,
    gender = gender.toString(),
    createdAt = createdAt
)

private fun UserEntity.toModel(): User = User(
    id = id,
    email = email,
    password = password,
    name = name,
    birthDate = birthDate,
    height = height,
    weight = weight,
    gender = gender,
    createdAt = createdAt
)

// Helper class for date formatting
object DateFormatter {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun format(date: Date?): String = date?.let { dateFormat.format(it) } ?: ""
    fun parse(dateString: String?): Date? = dateString?.let { dateFormat.parse(it) }
}