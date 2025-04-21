package com.example.lab5.ui.screens.nutrionanalysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab5.data.model.Meal
import com.example.lab5.data.model.MealType
import com.example.lab5.data.model.NutritionSummary
import com.example.lab5.data.repository.MealRepository
import com.example.lab5.ui.components.ChartType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class NutritionAnalysisViewModel(
    private val mealRepository: MealRepository,
    private val currentUserId: Long = 1L // Default user ID
) : ViewModel() {

    private val _state = MutableStateFlow(NutritionAnalysisState())
    val state: StateFlow<NutritionAnalysisState> = _state.asStateFlow()

    init {
        loadInitialData()
    }

    fun onEvent(event: NutritionAnalysisEvent) {
        when (event) {
            NutritionAnalysisEvent.LoadNutritionData -> loadNutritionData()
            is NutritionAnalysisEvent.ChangeDateRange -> changeDateRange(event.startDate, event.endDate)
            is NutritionAnalysisEvent.ApplyMealTypeFilter -> event.mealType?.let {
                applyMealTypeFilter(
                    it
                )
            }
            NutritionAnalysisEvent.ClearFilters -> clearFilters()
            is NutritionAnalysisEvent.AnalyzeMeal -> analyzeMeal(event.meal)
            NutritionAnalysisEvent.AnalyzeAllMeals -> analyzeAllMeals()
            NutritionAnalysisEvent.ExportToPdf -> exportToPdf()
            NutritionAnalysisEvent.ExportToCsv -> exportToCsv()
            NutritionAnalysisEvent.ShowSettings -> showSettings()
            is NutritionAnalysisEvent.ChangeChartType -> changeChartType(event.chartType)
        }
    }

    private fun loadInitialData() {
        loadNutritionData()
        loadDailyNutritionSummary()
    }

    private fun loadNutritionData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // Отримуємо список дат у діапазоні
                val dateRange = getDatesBetween(_state.value.startDate, _state.value.endDate)

                // Збираємо всі прийоми їжі для кожної дати
                val allMeals = mutableListOf<Meal>()
                dateRange.forEach { date ->
                    mealRepository.getMealsByDate(currentUserId, date).collect { meals ->
                        allMeals.addAll(meals)
                    }
                }

                _state.update {
                    it.copy(
                        meals = allMeals,
                        isLoading = false,
                        error = null
                    )
                }
                analyzeAllMeals()
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

    /**
     * Допоміжна функція для отримання списку дат у діапазоні
     */
    private fun getDatesBetween(startDate: Date, endDate: Date): List<Date> {
        val dates = mutableListOf<Date>()
        val calendar = Calendar.getInstance().apply {
            time = startDate
        }

        while (calendar.time <= endDate) {
            dates.add(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return dates
    }

    private fun loadDailyNutritionSummary() {
        viewModelScope.launch {
            try {
                val summary = mealRepository.getDailyNutritionSummary(
                    userId = currentUserId,
                    date = Date() // Current date
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
                        error = "Failed to load nutrition summary: ${e.message}"
                    )
                }
            }
        }
    }

    private fun changeDateRange(startDate: Date, endDate: Date) {
        _state.update {
            it.copy(
                startDate = startDate,
                endDate = endDate
            )
        }
        loadNutritionData()
    }

    private fun applyMealTypeFilter(mealType: MealType) {
        _state.update { it.copy(selectedMealType = mealType) }
    }

    private fun clearFilters() {
        _state.update {
            it.copy(
                selectedMealType = null,
                startDate = Date(),
                endDate = Date()
            )
        }
        loadNutritionData()
    }

    private fun analyzeMeal(meal: Meal) {
        // Implement single meal analysis logic
    }

    private fun analyzeAllMeals() {
        val meals = _state.value.meals
        if (meals.isEmpty()) return

        // Calculate total nutrition
        val totalNutrition = meals.fold(NutritionSummary()) { acc, meal ->
            NutritionSummary(
                calories = acc.calories + meal.calories,
                protein = acc.protein + meal.protein,
                carbs = acc.carbs + meal.carbs,
                fat = acc.fat + meal.fat
            )
        }

        _state.update {
            it.copy(
                weeklyNutrition = totalNutrition,
                monthlyNutrition = totalNutrition // For demo, same as weekly
            )
        }
    }

    private fun exportToPdf() {
        viewModelScope.launch {
            _state.update { it.copy(isExporting = true) }
            try {
                // Implement PDF export logic
                _state.update {
                    it.copy(
                        isExporting = false,
                        exportSuccess = true
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isExporting = false,
                        exportSuccess = false,
                        error = "Export failed: ${e.message}"
                    )
                }
            }
        }
    }

    private fun exportToCsv() {
        // Similar to exportToPdf()
    }

    private fun showSettings() {
        // Handle settings display
    }

    private fun changeChartType(chartType: ChartType) {
        _state.update { it.copy(chartType = chartType) }
    }
}