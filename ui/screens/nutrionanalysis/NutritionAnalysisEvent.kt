package com.example.lab5.ui.screens.nutrionanalysis

import com.example.lab5.data.model.Meal
import com.example.lab5.data.model.MealType
import java.util.Date
import com.example.lab5.ui.components.ChartType

/**
 * Події екрану аналізу харчування.
 * Підтримує API 24+ (Android 7.0+).
 */
sealed class NutritionAnalysisEvent {
    // Події завантаження даних
    object LoadNutritionData : NutritionAnalysisEvent()

    // Події фільтрації
    data class ChangeDateRange(val startDate: Date, val endDate: Date) : NutritionAnalysisEvent()
    data class ApplyMealTypeFilter(val mealType: MealType?) : NutritionAnalysisEvent()
    object ClearFilters : NutritionAnalysisEvent()

    // Події аналізу
    data class AnalyzeMeal(val meal: Meal) : NutritionAnalysisEvent()
    object AnalyzeAllMeals : NutritionAnalysisEvent()

    // Події експорту
    object ExportToPdf : NutritionAnalysisEvent()
    object ExportToCsv : NutritionAnalysisEvent()

    // Події налаштувань
    object ShowSettings : NutritionAnalysisEvent()
    data class ChangeChartType(val chartType: ChartType) : NutritionAnalysisEvent()
}

/**
 * Типи графіків для аналізу харчування
 */
