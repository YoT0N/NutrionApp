package com.example.myapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val name: String,
    val calories: Int,
    val ingredients: List<String>,
    val dateAdded: Long = System.currentTimeMillis()
)

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val email: String,
    val passwordHash: String,
    val name: String? = null,
    val goal: String? = null,
    val targetCalories: Int? = null,
    val restrictions: List<String>? = null,
    val registrationDate: Long = System.currentTimeMillis()
)