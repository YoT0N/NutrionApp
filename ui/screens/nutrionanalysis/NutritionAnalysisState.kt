package com.example.lab5.ui.screens.nutrionanalysis

import com.example.lab5.data.model.Meal
import com.example.lab5.data.model.MealType
import com.example.lab5.data.model.NutritionSummary
import com.example.lab5.ui.components.ChartType
import java.util.Date

data class NutritionAnalysisState(
    // Основні дані
    val meals: List<Meal> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,

    // Фільтрація
    val startDate: Date = Date(),
    val endDate: Date = Date(),
    val selectedMealType: MealType? = null,

    // Результати аналізу
    val dailyNutrition: NutritionSummary = NutritionSummary(),
    val weeklyNutrition: NutritionSummary = NutritionSummary(),
    val monthlyNutrition: NutritionSummary = NutritionSummary(),

    // Налаштування відображення
    val chartType: ChartType = ChartType.PIE_CHART,
    val isExporting: Boolean = false,
    val exportSuccess: Boolean? = null
) {
    /**
     * Чи є помилка у стані
     */
    val hasError: Boolean get() = error != null

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
            (selectedMealType == null || meal.mealType == selectedMealType) /*&&
                    meal.dateTime in startDate..endDate*/
        }
}

/**
 * Типи графіків для візуалізації
 */

