package com.example.myapp.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class MainViewModel : ViewModel() {
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    @RequiresApi(Build.VERSION_CODES.O)
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val _meals = MutableStateFlow<List<Meal>>(emptyList())
    val meals: StateFlow<List<Meal>> = _meals

    private val _dailyNutrition = MutableStateFlow(Pair(1300, 2000))
    val dailyNutrition: StateFlow<Pair<Int, Int>> = _dailyNutrition

    @RequiresApi(Build.VERSION_CODES.O)
    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.ChangeDate -> changeDate(event.date)
            MainEvent.LoadData -> loadData()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun changeDate(date: LocalDate) {
        _selectedDate.value = date
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _meals.value = listOf(
                Meal("1", "Сніданок", 350, listOf("Вівсянка", "Ягоди", "Горіхи")),
                Meal("2", "Обід", 550, listOf("Курка", "Гречка", "Овочі")),
                Meal("3", "Вечеря", 400, listOf("Риба", "Салат"))
            )

            _dailyNutrition.value = Pair(
                _meals.value.sumOf { it.calories },
                2000
            )
        }
    }

    init {
        loadData()
    }
}

sealed class MainEvent {
    data class ChangeDate(val date: LocalDate) : MainEvent()
    object LoadData : MainEvent()
}