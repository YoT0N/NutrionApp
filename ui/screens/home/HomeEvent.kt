package com.example.lab5.ui.screens.home

import com.example.lab5.data.model.MealType
import com.example.lab5.data.model.Meal

sealed class HomeEvent {
    object LoadMeals : HomeEvent()
    data class AddMeal(val meal: Meal) : HomeEvent()
    data class UpdateMeal(val meal: Meal) : HomeEvent()
    data class DeleteMeal(val meal: Meal) : HomeEvent()
    data class SelectMeal(val mealId: Long) : HomeEvent()
    data class ApplyFilter(val filterType: MealType) : HomeEvent()
    object ClearFilters : HomeEvent()
    data class SearchQueryChanged(val query: String) : HomeEvent()
    object ExecuteSearch : HomeEvent()
    object LoadDailyNutrition : HomeEvent()
}
/**
 * Часові періоди для статистики
 */
enum class StatisticsTimeRange {
    DAY, WEEK, MONTH
}