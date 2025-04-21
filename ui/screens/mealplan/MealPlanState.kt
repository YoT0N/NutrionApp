package com.example.lab5.ui.screens.mealplan

import com.example.lab5.data.model.Meal
import com.example.lab5.data.model.MealType
import com.example.lab5.data.model.NutritionSummary

import java.util.Date

/**
 * Стан екрану плану харчування.
 * Підтримує API 24+ (Android 7.0+).
 */
data class MealPlanState(
    // Дані про прийоми їжі
    val meals: List<Meal> = emptyList(),
    val selectedMeal: Meal? = null,

    // Стан завантаження
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,

    // Помилки
    val error: String? = null,

    // Фільтрація та пошук
    val selectedDate: Date = Date(),
    val selectedMealType: MealType? = null,
    val searchQuery: String = "",

    // Режим редагування
    val isEditing: Boolean = false,
    val editedMeal: Meal? = null,

    // Нутрієнтна статистика
    val dailyNutrition: NutritionSummary = NutritionSummary(),
    val weeklyNutrition: NutritionSummary = NutritionSummary()
) {
    /**
     * Чи є помилка у стані
     */
    val hasError: Boolean
        get() = error != null

    /**
     * Чи відображати порожній стан
     */
    val showEmptyState: Boolean
        get() = meals.isEmpty() && !isLoading && error == null

    /**
     * Відфільтровані прийоми їжі
     */
    val filteredMeals: List<Meal>
        get() = meals.filter { meal ->
            (selectedMealType == null || meal.mealType == selectedMealType) &&
                    (searchQuery.isEmpty() || meal.name.contains(searchQuery, ignoreCase = true))
        }
}

/**
 * Додаткові класи для MealPlanState (якщо потрібно)
 */
sealed class MealPlanUiState {
    object Loading : MealPlanUiState()
    data class Success(val meals: List<Meal>) : MealPlanUiState()
    data class Error(val message: String) : MealPlanUiState()
}