package com.example.myapp.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapp.database.MealRepository
import com.example.myapp.database.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
class MainViewModel(
    private val mealRepository: MealRepository,
    private val userRepository: UserRepository, // Додаємо UserRepository
    private val userId: Long
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val _meals = MutableStateFlow<List<Meal>>(emptyList())
    val meals: StateFlow<List<Meal>> = _meals

    private val _dailyNutrition = MutableStateFlow(Pair(0, 2000))
    val dailyNutrition: StateFlow<Pair<Int, Int>> = _dailyNutrition

    @RequiresApi(Build.VERSION_CODES.O)
    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.ChangeDate -> changeDate(event.date)
            MainEvent.LoadData -> loadData()
            is MainEvent.RefreshMeals -> loadData()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun changeDate(date: LocalDate) {
        _selectedDate.value = date
        loadData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadData() {
        viewModelScope.launch {
            try {
                // Отримуємо дані користувача для визначення цільових калорій
                val user = userRepository.getUserById(userId)
                val targetCalories = user?.targetCalories ?: 2000 // Використовуємо 2000 як значення за замовчуванням

                // Отримуємо блюда для вибраної дати
                val startOfDay = selectedDate.value.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                val endOfDay = selectedDate.value.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1

                mealRepository.getMealsByDate(userId, startOfDay, endOfDay).collectLatest { mealEntities ->
                    // Конвертуємо Entity в UI модель
                    val uiMeals = mealEntities.map { entity ->
                        Meal(
                            id = entity.id.toString(),
                            name = entity.name,
                            calories = entity.calories,
                            ingredients = entity.ingredients
                        )
                    }

                    _meals.value = uiMeals

                    _dailyNutrition.value = Pair(
                        uiMeals.sumOf { it.calories },
                        targetCalories
                    )
                }
            } catch (e: Exception) {
                _meals.value = emptyList()
                _dailyNutrition.value = Pair(0, 2000)
            }
        }
    }

    init {
        loadData()
    }
}

sealed class MainEvent {
    data class ChangeDate(val date: LocalDate) : MainEvent()
    object LoadData : MainEvent()
    object RefreshMeals : MainEvent()
}