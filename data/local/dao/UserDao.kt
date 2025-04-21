package com.example.lab5.data.local.dao

import androidx.room.*
import com.example.lab5.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for user operations in the database.
 * Provides methods for accessing and managing user data.
 */
@Dao
interface UserDao {

    // Create operations
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    // Read operations
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    fun getUserByEmail(email: String): Flow<UserEntity?>

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    // Update operations
    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET name = :name WHERE id = :userId")
    suspend fun updateUserName(userId: Long, name: String)

    @Query("UPDATE users SET weight = :weight WHERE id = :userId")
    suspend fun updateUserWeight(userId: Long, weight: Float)

    @Query("UPDATE users SET height = :height WHERE id = :userId")
    suspend fun updateUserHeight(userId: Long, height: Int)

    // Delete operations
    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: Long)

    // Authentication operations
    @Query("SELECT COUNT(*) FROM users WHERE email = :email AND password = :password")
    suspend fun authenticateUser(email: String, password: String): Int

    // Utility operations
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email LIMIT 1)")
    suspend fun isEmailExists(email: String): Boolean
}