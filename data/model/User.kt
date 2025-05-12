package com.example.lab5.data.model

import java.text.SimpleDateFormat
import java.util.*

/**
 * Data class representing a user in the system.
 * Compatible with Android API 24+.
 *
 * @property id Unique user identifier
 * @property email User's email address (unique)
 * @property password Hashed password (use proper hashing in production)
 * @property name User's display name
 * @property birthDate Date of birth (nullable)
 * @property height Height in centimeters (nullable)
 * @property weight Weight in kilograms (nullable)
 * @property gender User's gender (nullable)
 * @property createdAt Account creation timestamp
 */
data class User(
    val id: Long = 0,
    val email: String,
    val password: String,
    val name: String,
    val birthDate: Date? = null,
    val height: Int? = null,
    val weight: Float? = null,
    val gender: String? = null,
    val createdAt: Date = Calendar.getInstance().time
) {
    /**
     * Calculates user's age based on birth date
     */
    fun getAge(): Int? {
        return birthDate?.let {
            val now = Calendar.getInstance()
            val dob = Calendar.getInstance().apply { time = it }
            var age = now.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
            if (now.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                age--
            }
            age
        }
    }

    /**
     * Formats date to "yyyy-MM-dd" string
     */
    fun formatDate(date: Date?): String {
        return date?.let {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
        } ?: ""
    }

    /**
     * Calculates BMI (Body Mass Index)
     */
    fun calculateBMI(): Float? {
        return if (height != null && weight != null && height > 0) {
            (weight / (height * height / 10000f))
        } else {
            null
        }
    }
}

/**
 * Gender enumeration
 */
enum class Gender {
    MALE,
    FEMALE,
    OTHER,
    PREFER_NOT_TO_SAY
}

/**
 * Extension function to hash passwords (placeholder - use proper hashing in production)
 */
fun String.hashPassword(): String {
    // In production, use proper hashing like BCrypt
    return this // Replace with actual hashing
}