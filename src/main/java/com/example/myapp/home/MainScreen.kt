package com.example.myapp.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapp.AdaptiveNavigation
import com.example.myapp.Screen
import com.example.myapp.auth.SharedViewModel
import com.example.myapp.database.AppDatabase
import com.example.myapp.database.MealRepository
import com.example.myapp.database.UserRepository
import com.example.myapp.settings.StringResource
import com.example.myapp.ui.WindowWidthSizeClass
import com.example.myapp.ui.rememberWindowSizeClass
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val mealRepository = remember { MealRepository(database.mealDao()) }
    val userRepository = remember { UserRepository(database.userDao()) }
    val strings = StringResource.strings

    // Отримання розміру вікна
    val windowSizeClass = rememberWindowSizeClass()

    // Get userId from SharedViewModel
    val userId by sharedViewModel.userId.collectAsState()

    // Створення viewModel з використанням фабрики
    val viewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(
            mealRepository,
            userRepository,
            userId ?: -1L
        )
    )

    val selectedDate by viewModel.selectedDate.collectAsState()
    val meals by viewModel.meals.collectAsState()
    val dailyNutrition by viewModel.dailyNutrition.collectAsState()
    val caloriesConsumed = dailyNutrition.first
    val caloriesGoal = dailyNutrition.second

    AdaptiveNavigation(
        navController = navController,
        sharedViewModel = sharedViewModel,
        widthSizeClass = windowSizeClass.widthSizeClass
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(strings.myDiet) },
                    actions = {
                        IconButton(onClick = { navController.navigate(Screen.NutritionStats.route) }) {
                            Icon(Icons.Default.Build, contentDescription = strings.nutritionStatistics)
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.MealPlan.route) }
                ) {
                    Icon(Icons.Default.Add, contentDescription = strings.addMeal)
                }
            }
        ) { innerPadding ->
            // Адаптуємо макет залежно від ширини екрану
            if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.COMPACT) {
                // Вертикальне розташування для вузьких екранів
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    DatePicker(
                        selectedDate = selectedDate,
                        onDateSelected = { viewModel.onEvent(MainEvent.ChangeDate(it)) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    DailyMealPlan(meals = meals, navController = navController)

                    Spacer(modifier = Modifier.height(16.dp))

                    DailyNutritionSummary(
                        caloriesConsumed = caloriesConsumed,
                        caloriesGoal = caloriesGoal
                    )
                }
            } else {
                // Горизонтальне розташування для широких екранів
                Row(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    // Ліва колонка - дата та підсумок по харчуванню
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        DatePicker(
                            selectedDate = selectedDate,
                            onDateSelected = { viewModel.onEvent(MainEvent.ChangeDate(it)) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        DailyNutritionSummary(
                            caloriesConsumed = caloriesConsumed,
                            caloriesGoal = caloriesGoal
                        )
                    }

                    // Права колонка - список страв
                    Column(
                        modifier = Modifier
                            .weight(2f)
                            .padding(start = 8.dp)
                    ) {
                        DailyMealPlan(meals = meals, navController = navController)
                    }
                }
            }
        }
    }
}

@Composable
fun MealCard(meal: Meal, onClick: () -> Unit) {
    val strings = StringResource.strings

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(meal.name, style = MaterialTheme.typography.titleMedium)
                Text("${meal.calories} ${strings.kcal}", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = meal.ingredients.joinToString(", "),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePicker(selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit) {
    val strings = StringResource.strings

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(onClick = { onDateSelected(selectedDate.minusDays(1)) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.previousDay)
        }

        Text(
            text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, d MMMM")),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )

        IconButton(onClick = { onDateSelected(selectedDate.plusDays(1)) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = strings.nextDay)
        }
    }
}

@Composable
fun DailyMealPlan(meals: List<Meal>, navController: NavController) {
    val strings = StringResource.strings

    LazyColumn {
        item {
            Text(strings.meals, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(meals) { meal ->
            MealCard(
                meal = meal,
                onClick = { navController.navigate(Screen.MealEdit.createRoute(meal.id)) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun DailyNutritionSummary(caloriesConsumed: Int, caloriesGoal: Int) {
    val strings = StringResource.strings

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(strings.dailyBalance, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { caloriesConsumed.toFloat() / caloriesGoal },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("$caloriesConsumed ${strings.kcal}")
                Text(strings.targetKcal.format(caloriesGoal))
            }
        }
    }
}

class MainViewModelFactory(
    private val mealRepository: MealRepository,
    private val userRepository: UserRepository, // Додаємо UserRepository
    private val userId: Long
) : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(mealRepository, userRepository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class Meal(
    val id: String,
    val name: String,
    val calories: Int,
    val ingredients: List<String>
)