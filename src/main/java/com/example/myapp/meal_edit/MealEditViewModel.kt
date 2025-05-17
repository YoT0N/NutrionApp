package com.example.myapp.meal_edit

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapp.database.Meal as DatabaseMeal
import com.example.myapp.database.MealRepository
import com.example.myapp.home.Meal
import com.example.myapp.home.MainEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
class MealEditViewModel(
    private val mealRepository: MealRepository? = null,
    private val userId: Long
) : ViewModel() {
    private val _uiState = MutableStateFlow<MealEditState>(MealEditState.Loading)
    val uiState: StateFlow<MealEditState> = _uiState

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun onEvent(event: MealEditEvent) {
        when (event) {
            is MealEditEvent.LoadMeal -> loadMeal(event.mealId)
            is MealEditEvent.CreateNewMeal -> createNewMeal()
            is MealEditEvent.UpdateName -> updateName(event.name)
            is MealEditEvent.UpdateCalories -> updateCalories(event.calories)
            is MealEditEvent.UpdateDate -> updateDate(event.date)
            is MealEditEvent.UpdateIngredient -> updateIngredient(event.index, event.value)
            is MealEditEvent.DeleteIngredient -> deleteIngredient(event.index)
            is MealEditEvent.AddNewIngredient -> addNewIngredient()
            is MealEditEvent.SaveMeal -> saveMeal()
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun loadMeal(mealId: String) {
        viewModelScope.launch {
            _uiState.value = MealEditState.Loading
            try {
                val mealFromRepo = mealRepository?.getMealById(mealId.toLong())

                if (mealFromRepo != null) {
                    val mealDate = LocalDate.ofInstant(
                        java.time.Instant.ofEpochMilli(mealFromRepo.dateAdded),
                        ZoneId.systemDefault()
                    )
                    _selectedDate.value = mealDate

                    val uiMeal = Meal(
                        id = mealFromRepo.id.toString(),
                        name = mealFromRepo.name,
                        calories = mealFromRepo.calories,
                        ingredients = mealFromRepo.ingredients
                    )
                    _uiState.value = MealEditState.Editing(uiMeal, mealDate)
                } else {
                    // Якщо не знайдено в репозиторії, використовуємо мок-дані
                    val meal = when (mealId) {
                        "1" -> Meal("1", "Сніданок", 350, listOf("Вівсянка", "Ягоди", "Горіхи"))
                        "2" -> Meal("2", "Обід", 550, listOf("Курка", "Гречка", "Овочі"))
                        "3" -> Meal("3", "Вечеря", 400, listOf("Риба", "Салат"))
                        else -> Meal("0", "Невідомий прийом їжі", 0, emptyList())
                    }
                    _uiState.value = MealEditState.Editing(meal, LocalDate.now())
                }
            } catch (e: Exception) {
                _uiState.value = MealEditState.Error("Помилка завантаження страви: ${e.message}")
            }
        }
    }

    private fun createNewMeal() {
        val newMeal = Meal(
            id = "0",
            name = "",
            calories = 0,
            ingredients = listOf("")
        )
        _uiState.value = MealEditState.Editing(newMeal, _selectedDate.value)
    }

    private fun updateName(name: String) {
        val currentState = _uiState.value
        if (currentState is MealEditState.Editing) {
            _uiState.value = currentState.copy(
                meal = currentState.meal.copy(name = name)
            )
        }
    }

    private fun updateCalories(caloriesStr: String) {
        val currentState = _uiState.value
        if (currentState is MealEditState.Editing) {
            try {
                val calories = caloriesStr.toIntOrNull() ?: 0
                _uiState.value = currentState.copy(
                    meal = currentState.meal.copy(calories = calories)
                )
            } catch (e: NumberFormatException) {
                // Якщо введене не число, зберігаємо попереднє значення
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateDate(date: LocalDate) {
        val currentState = _uiState.value
        if (currentState is MealEditState.Editing) {
            _selectedDate.value = date
            _uiState.value = currentState.copy(date = date)
        }
    }

    private fun updateIngredient(index: Int, value: String) {
        val currentState = _uiState.value
        if (currentState is MealEditState.Editing) {
            val ingredients = currentState.meal.ingredients.toMutableList()
            if (index >= 0 && index < ingredients.size) {
                ingredients[index] = value
                _uiState.value = currentState.copy(
                    meal = currentState.meal.copy(ingredients = ingredients)
                )
            }
        }
    }

    private fun deleteIngredient(index: Int) {
        val currentState = _uiState.value
        if (currentState is MealEditState.Editing) {
            val ingredients = currentState.meal.ingredients.toMutableList()
            if (index >= 0 && index < ingredients.size) {
                ingredients.removeAt(index)
                _uiState.value = currentState.copy(
                    meal = currentState.meal.copy(ingredients = ingredients)
                )
            }
        }
    }

    private fun addNewIngredient() {
        val currentState = _uiState.value
        if (currentState is MealEditState.Editing) {
            val ingredients = currentState.meal.ingredients + ""
            _uiState.value = currentState.copy(
                meal = currentState.meal.copy(ingredients = ingredients)
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveMeal() {
        val currentState = _uiState.value
        if (currentState is MealEditState.Editing) {
            viewModelScope.launch {
                try {
                    // Збереження в репозиторій, якщо доступний
                    mealRepository?.let { repo ->
                        // Конвертуємо дату в timestamp
                        val timeInMillis = currentState.date
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli()

                        val mealEntity = DatabaseMeal(
                            id = currentState.meal.id.toLongOrNull() ?: 0,
                            userId = userId, // Додано userId
                            name = currentState.meal.name,
                            calories = currentState.meal.calories,
                            ingredients = currentState.meal.ingredients,
                            dateAdded = timeInMillis
                        )

                        if (mealEntity.id == 0L) {
                            val newId = repo.insert(mealEntity)
                            val updatedMeal = currentState.meal.copy(id = newId.toString())
                            _uiState.value = MealEditState.Editing(updatedMeal, currentState.date)
                        } else {
                            repo.update(mealEntity)
                        }
                    }
                } catch (e: Exception) {
                    _uiState.value = MealEditState.Error("Помилка збереження: ${e.message}")
                }
            }
        }
    }
}

sealed class MealEditEvent {
    data class LoadMeal(val mealId: String) : MealEditEvent()
    object CreateNewMeal : MealEditEvent()
    data class UpdateName(val name: String) : MealEditEvent()
    data class UpdateCalories(val calories: String) : MealEditEvent()
    data class UpdateDate(val date: LocalDate) : MealEditEvent()
    data class UpdateIngredient(val index: Int, val value: String) : MealEditEvent()
    data class DeleteIngredient(val index: Int) : MealEditEvent()
    object AddNewIngredient : MealEditEvent()
    object SaveMeal : MealEditEvent()
}

sealed class MealEditState {
    object Loading : MealEditState()
    data class Editing(val meal: Meal, val date: LocalDate) : MealEditState()
    data class Error(val message: String) : MealEditState()
}