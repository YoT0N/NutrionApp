package com.example.lab5.ui.screens.mealplan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab5.data.model.Meal
import com.example.lab5.data.model.MealType
import com.example.lab5.data.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

class MealPlanViewModel(
    private val mealRepository: MealRepository,
    private val currentUserId: Long = 1L // Default user ID
) : ViewModel() {

    private val _state = MutableStateFlow(MealPlanState())
    val state: StateFlow<MealPlanState> = _state.asStateFlow()

    init {
        loadMeals()
        loadDailyNutrition()
    }

    fun onEvent(event: MealPlanEvent) {
        when (event) {
            MealPlanEvent.LoadMeals -> loadMeals()
            MealPlanEvent.LoadWeeklySummary -> loadWeeklySummary()
            is MealPlanEvent.AddMeal -> addMeal(event.meal)
            is MealPlanEvent.UpdateMeal -> updateMeal(event.meal)
            is MealPlanEvent.DeleteMeal -> deleteMeal(event.meal)
            is MealPlanEvent.SelectMeal -> selectMeal(event.mealId)
            is MealPlanEvent.ChangeDate -> changeDate(event.date)
            is MealPlanEvent.ApplyMealTypeFilter -> applyMealTypeFilter(event.mealType)
            MealPlanEvent.ClearFilters -> clearFilters()
            is MealPlanEvent.SearchQueryChanged -> updateSearchQuery(event.query)
            MealPlanEvent.ExecuteSearch -> executeSearch()
            MealPlanEvent.StartEditing -> startEditing()
            MealPlanEvent.CancelEditing -> cancelEditing()
            MealPlanEvent.SaveChanges -> saveChanges()
        }
    }

    private fun loadMeals() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val meals = mealRepository.getMealsByDate(
                    userId = currentUserId,
                    date = _state.value.selectedDate
                ).collect { meals ->
                    _state.update {
                        it.copy(
                            meals = meals,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load meals: ${e.message}"
                    )
                }
            }
        }
    }

    private fun loadDailyNutrition() {
        viewModelScope.launch {
            try {
                val summary = mealRepository.getDailyNutritionSummary(
                    userId = currentUserId,
                    date = _state.value.selectedDate
                )
                _state.update {
                    it.copy(
                        dailyNutrition = summary,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Failed to load nutrition: ${e.message}"
                    )
                }
            }
        }
    }

    private fun loadWeeklySummary() {
        viewModelScope.launch {
            // Implement weekly summary logic
        }
    }

    private fun addMeal(meal: Meal) {
        viewModelScope.launch {
            try {
                mealRepository.insertMeal(meal)
                loadMeals() // Refresh the list
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Failed to add meal: ${e.message}"
                    )
                }
            }
        }
    }

    private fun updateMeal(meal: Meal) {
        viewModelScope.launch {
            try {
                mealRepository.updateMeal(meal)
                loadMeals() // Refresh the list
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Failed to update meal: ${e.message}"
                    )
                }
            }
        }
    }

    private fun deleteMeal(meal: Meal) {
        viewModelScope.launch {
            try {
                mealRepository.deleteMeal(meal) // Create temporary meal object
                loadMeals() // Refresh the list
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Failed to delete meal: ${e.message}"
                    )
                }
            }
        }
    }

    private fun selectMeal(mealId: Long) {
        _state.update {
            it.copy(
                selectedMeal = it.meals.firstOrNull { meal -> meal.id == mealId }
            )
        }
    }

    private fun changeDate(date: Date) {
        _state.update { it.copy(selectedDate = date) }
        loadMeals()
        loadDailyNutrition()
    }

    private fun applyMealTypeFilter(mealType: MealType) {
        _state.update { it.copy(selectedMealType = mealType) }
    }

    private fun clearFilters() {
        _state.update {
            it.copy(
                selectedMealType = null,
                searchQuery = ""
            )
        }
    }

    private fun updateSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    private fun executeSearch() {
        // Implement search logic if needed
        loadMeals()
    }

    private fun startEditing() {
        _state.update {
            it.copy(
                isEditing = true,
                editedMeal = it.selectedMeal?.copy() // Create a copy for editing
            )
        }
    }

    private fun cancelEditing() {
        _state.update {
            it.copy(
                isEditing = false,
                editedMeal = null
            )
        }
    }

    private fun saveChanges() {
        _state.value.editedMeal?.let { meal ->
            viewModelScope.launch {
                try {
                    mealRepository.updateMeal(meal)
                    _state.update {
                        it.copy(
                            isEditing = false,
                            editedMeal = null
                        )
                    }
                    loadMeals() // Refresh the list
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            error = "Failed to save changes: ${e.message}"
                        )
                    }
                }
            }
        }
    }
}