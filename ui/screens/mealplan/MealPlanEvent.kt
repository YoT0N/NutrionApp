package com.example.lab5.ui.screens.mealplan

import com.example.lab5.data.model.Meal
import com.example.lab5.data.model.MealType
import java.util.Date

/**
 * Події екрану плану харчування.
 * Підтримує API 24+ (Android 7.0+).
 */
sealed class MealPlanEvent {
    // Події завантаження даних
    object LoadMeals : MealPlanEvent()
    object LoadWeeklySummary : MealPlanEvent()

    // Події керування прийомами їжі
    data class AddMeal(val meal: Meal) : MealPlanEvent()
    data class UpdateMeal(val meal: Meal) : MealPlanEvent()
    data class DeleteMeal(val meal: Meal) : MealPlanEvent()
    data class SelectMeal(val mealId: Long) : MealPlanEvent()

    // Події фільтрації
    data class ChangeDate(val date: Date) : MealPlanEvent()
    data class ApplyMealTypeFilter(val mealType: MealType) : MealPlanEvent()
    object ClearFilters : MealPlanEvent()

    // Події пошуку
    data class SearchQueryChanged(val query: String) : MealPlanEvent()
    object ExecuteSearch : MealPlanEvent()

    // Події редагування
    object StartEditing : MealPlanEvent()
    object CancelEditing : MealPlanEvent()
    object SaveChanges : MealPlanEvent()
}