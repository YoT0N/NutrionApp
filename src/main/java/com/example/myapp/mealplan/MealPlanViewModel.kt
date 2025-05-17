// MealPlanViewModel.kt
package com.example.myapp.mealplan

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapp.database.Meal
import com.example.myapp.database.MealDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MealPlanViewModel(
    private val mealDao: MealDao
) : ViewModel() {
    private val _uiState = MutableStateFlow<MealPlanUiState>(MealPlanUiState.Idle)
    val uiState: StateFlow<MealPlanUiState> = _uiState

    fun onEvent(event: MealPlanEvent) {
        when (event) {
            is MealPlanEvent.SaveMeal -> saveMeal(event.meal)
        }
    }

    private fun saveMeal(meal: Meal) {
        viewModelScope.launch {
            _uiState.value = MealPlanUiState.Loading
            try {
                mealDao.insert(meal)
                _uiState.value = MealPlanUiState.Success("Прийом їжі збережено!")
                Log.d("MealPlan","Прийом їжі збережено!")
            } catch (e: Exception) {
                _uiState.value = MealPlanUiState.Error("Помилка: ${e.message}")
            }
        }
    }
}

sealed class MealPlanEvent {
    data class SaveMeal(val meal: Meal) : MealPlanEvent()
}

sealed class MealPlanUiState {
    object Idle : MealPlanUiState()
    object Loading : MealPlanUiState()
    data class Success(val message: String) : MealPlanUiState()
    data class Error(val error: String) : MealPlanUiState()
}