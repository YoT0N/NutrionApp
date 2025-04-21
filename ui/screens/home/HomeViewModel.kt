package com.example.lab5.ui.screens.home

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

class HomeViewModel(
    private val mealRepository: MealRepository,
    private val currentUserId: Long = 1L
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadMeals()
        loadDailyNutrition()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.LoadMeals -> loadMeals()
            is HomeEvent.AddMeal -> addMeal(event.meal)
            is HomeEvent.UpdateMeal -> updateMeal(event.meal)
            is HomeEvent.DeleteMeal -> deleteMeal(event.meal)
            is HomeEvent.SelectMeal -> selectMeal(event.mealId)
            is HomeEvent.ApplyFilter -> applyFilter(event.filterType)
            HomeEvent.ClearFilters -> clearFilters()
            is HomeEvent.SearchQueryChanged -> updateSearchQuery(event.query)
            HomeEvent.ExecuteSearch -> executeSearch()
            HomeEvent.LoadDailyNutrition -> loadDailyNutrition()
        }
    }

    private fun loadMeals() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                mealRepository.getAllMealsByUser(currentUserId).collect { meals ->
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
                val summary = mealRepository.getDailyNutritionSummary(currentUserId, Date())
                _state.update {
                    it.copy(
                        nutritionSummary = NutritionSummary(
                            proteinPercent = calculatePercentage(summary.protein, summary.calories),
                            carbsPercent = calculatePercentage(summary.carbs, summary.calories),
                            fatPercent = calculatePercentage(summary.fat, summary.calories),
                            totalCalories = summary.calories.toInt()
                        )
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

    private fun calculatePercentage(nutrient: Float, total: Float): Float {
        return if (total > 0) (nutrient / total) * 100 else 0f
    }

    private fun addMeal(meal: Meal) {
        viewModelScope.launch {
            try {
                mealRepository.insertMeal(meal)
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
                mealRepository.deleteMeal(meal)
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
        // Handle meal selection
    }

    private fun applyFilter(filterType: MealType) {
        _state.update { it.copy(currentFilter = filterType) }
    }

    private fun clearFilters() {
        _state.update {
            it.copy(
                currentFilter = MealType.OTHER,
                searchQuery = ""
            )
        }
    }

    private fun updateSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    private fun executeSearch() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                loadMeals()
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Search failed: ${e.message}"
                    )
                }
            }
        }
    }
}