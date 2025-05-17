package com.example.myapp.meal_edit

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapp.home.Meal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

@SuppressLint("NewApi")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealEditScreen(
    navController: NavController,
    mealId: String? = null,
    onSaveComplete: () -> Unit = {}
) {
    val viewModel: MealEditViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    LaunchedEffect(mealId) {
        if (mealId != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                viewModel.onEvent(MealEditEvent.LoadMeal(mealId))
            }
        } else {
            viewModel.onEvent(MealEditEvent.CreateNewMeal)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (mealId != null) "Редагування страви" else "Нова страва") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onEvent(MealEditEvent.SaveMeal)
                    onSaveComplete()
                    navController.popBackStack()
                }
            ) {
                Icon(Icons.Default.Check, contentDescription = "Зберегти")
            }
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is MealEditState.Editing -> {
                MealEditForm(
                    meal = state.meal,
                    date = state.date,
                    onMealNameChange = { viewModel.onEvent(MealEditEvent.UpdateName(it)) },
                    onMealCaloriesChange = { viewModel.onEvent(MealEditEvent.UpdateCalories(it)) },
                    onDateChange = { viewModel.onEvent(MealEditEvent.UpdateDate(it)) },
                    onIngredientChange = { index, value ->
                        viewModel.onEvent(MealEditEvent.UpdateIngredient(index, value))
                    },
                    onDeleteIngredient = { viewModel.onEvent(MealEditEvent.DeleteIngredient(it)) },
                    onAddIngredient = { viewModel.onEvent(MealEditEvent.AddNewIngredient) },
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp)
                )
            }
            MealEditState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is MealEditState.Error -> {
                ErrorContent(
                    message = state.message,
                    onBackClick = { navController.popBackStack() },
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp)
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MealEditForm(
    meal: Meal,
    date: LocalDate,
    onMealNameChange: (String) -> Unit,
    onMealCaloriesChange: (String) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onIngredientChange: (Int, String) -> Unit,
    onDeleteIngredient: (Int) -> Unit,
    onAddIngredient: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
    ) {
        // Назва страви
        TextField(
            value = meal.name,
            onValueChange = onMealNameChange,
            label = { Text("Назва страви") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Калорії
        TextField(
            value = meal.calories.toString(),
            onValueChange = onMealCaloriesChange,
            label = { Text("Калорії") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Дата
        DatePickerField(
            selectedDate = date,
            onDateSelected = onDateChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Інгредієнти
        Text("Інгредієнти:", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        // Список інгредієнтів
        meal.ingredients.forEachIndexed { index, ingredient ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = ingredient,
                    onValueChange = { onIngredientChange(index, it) },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { onDeleteIngredient(index) }) {
                    Icon(Icons.Default.Clear, contentDescription = "Видалити")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Кнопка додавання інгредієнта
        Button(
            onClick = onAddIngredient,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Додати інгредієнт")
        }
    }
}

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DatePickerField(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    // Конвертуємо LocalDate в формат для DatePicker
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            Icons.Default.DateRange,
            contentDescription = "Календар",
            modifier = Modifier.padding(end = 8.dp)
        )

        Text(
            "Дата: ${selectedDate.format(dateFormatter)}",
            modifier = Modifier.weight(1f)
        )

        Button(onClick = { showDatePicker = true }) {
            Text("Змінити")
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val newDate = LocalDate.ofInstant(
                                Instant.ofEpochMilli(millis),
                                ZoneId.systemDefault()
                            )
                            onDateSelected(newDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Скасувати")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onBackClick) {
            Text("Назад")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun rememberDatePickerState(
    initialSelectedDateMillis: Long? = null,
    yearRange: IntRange = IntRange(1900, 2100),
    initialDisplayedMonthMillis: Long? = initialSelectedDateMillis
): DatePickerState {
    return remember {
        DatePickerState(
            initialSelectedDateMillis = initialSelectedDateMillis,
            initialDisplayedMonthMillis = initialDisplayedMonthMillis,
            yearRange = yearRange,
            locale = TODO(),
            initialDisplayMode = TODO(),
            selectableDates = TODO()
        )
    }
}