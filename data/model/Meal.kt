package com.example.lab5.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Data class representing a meal in the domain layer.
 *
 * @property id Unique identifier for the meal
 * @property name Name of the meal (e.g., "Breakfast", "Chicken Salad")
 * @property calories Total calories in kcal
 * @property protein Protein content in grams
 * @property carbs Carbohydrates content in grams
 * @property fat Fat content in grams
 * @property dateTime When the meal was consumed
 * @property userId ID of the user who logged this meal
 * @property mealType Type of meal (breakfast, lunch, etc.)
 */
data class Meal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val calories: Float,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val dateTime: LocalDateTime,
    val userId: Long? = null,
    val mealType: MealType = MealType.OTHER
) {
    /**
     * Calculates total macronutrients in percentage
     */
    fun getMacronutrientDistribution(): Map<String, Float> {
        val total = protein + carbs + fat
        return if (total > 0) {
            mapOf(
                "protein" to (protein / total * 100),
                "carbs" to (carbs / total * 100),
                "fat" to (fat / total * 100)
            )
        } else {
            emptyMap()
        }
    }
}

/**
 * Enum representing different types of meals
 */
enum class MealType {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK,
    OTHER;

    companion object {
        fun fromString(value: String): MealType {
            return try {
                valueOf(value.uppercase())
            } catch (e: IllegalArgumentException) {
                OTHER
            }
        }
    }
}