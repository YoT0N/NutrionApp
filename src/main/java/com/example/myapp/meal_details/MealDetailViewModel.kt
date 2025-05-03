package com.example.myapp.meal_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.myapp.home.Meal

class MealDetailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<MealDetailState>(MealDetailState.Loading)
    val uiState: StateFlow<MealDetailState> = _uiState

    fun onEvent(event: MealDetailEvent) {
        when (event) {
            is MealDetailEvent.LoadMeal -> loadMeal(event.mealId)
            is MealDetailEvent.EditMeal -> editMeal()
            is MealDetailEvent.DeleteMeal -> deleteMeal()
            else -> {}
        }
    }

    private fun loadMeal(mealId: String) {
        viewModelScope.launch {
            // Имитация загрузки данных
            val meal = when (mealId) {
                "1" -> Meal("1", "Сніданок", 350, listOf("Вівсянка", "Ягоди", "Горіхи"))
                "2" -> Meal("2", "Обід", 550, listOf("Курка", "Гречка", "Овочі"))
                "3" -> Meal("3", "Вечеря", 400, listOf("Риба", "Салат"))
                else -> Meal("0", "Невідомий прийом їжі", 0, emptyList())
            }
            _uiState.value = MealDetailState.Success(meal)
        }
    }

    private fun editMeal() {
        // Логика редактирования
    }

    private fun deleteMeal() {
        // Логика удаления
    }
}

sealed class MealDetailEvent {
    data class LoadMeal(val mealId: String) : MealDetailEvent()
    object EditMeal : MealDetailEvent()
    object DeleteMeal : MealDetailEvent()
}

sealed class MealDetailState {
    object Loading : MealDetailState()
    data class Success(val meal: Meal) : MealDetailState()
    data class Error(val message: String) : MealDetailState()
}