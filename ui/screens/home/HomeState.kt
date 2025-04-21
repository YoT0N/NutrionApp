package com.example.lab5.ui.screens.home

import com.example.lab5.data.model.Meal
import com.example.lab5.data.model.MealType

data class HomeState(
    val meals: List<Meal> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentFilter: MealType = MealType.OTHER,
    val searchQuery: String = "",
    val nutritionSummary: NutritionSummary = NutritionSummary()
) {
    /**
     * Перевіряє, чи є помилка у стані
     */
    val hasError: Boolean
        get() = error != null
}

/**
 * Модель зведеної інформації про харчування
 */
data class NutritionSummary(
    val proteinPercent: Float = 0f,
    val carbsPercent: Float = 0f,
    val fatPercent: Float = 0f,
    val totalCalories: Int = 0
) {
    /**
     * Перевіряє, чи є доступні дані
     */
    val hasData: Boolean
        get() = proteinPercent > 0 || carbsPercent > 0 || fatPercent > 0
}

