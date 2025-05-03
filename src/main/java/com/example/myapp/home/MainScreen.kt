package com.example.myapp.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.Add

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapp.BottomNavigationBar
import com.example.myapp.Screen
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val viewModel: MainViewModel = viewModel()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val meals by viewModel.meals.collectAsState()
    val dailyNutrition by viewModel.dailyNutrition.collectAsState()
    val caloriesConsumed = dailyNutrition.first
    val caloriesGoal = dailyNutrition.second

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мій раціон") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.NutritionStats.route) }) {
                        Icon(Icons.Default.Build, contentDescription = "Статистика")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.MealPlan.route) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Додати прийом їжі")
            }
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
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
    }
}

@Composable
fun MealCard(meal: Meal, onClick: () -> Unit) {
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
                Text("${meal.calories} ккал", style = MaterialTheme.typography.bodyMedium)
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
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(onClick = { onDateSelected(selectedDate.minusDays(1)) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Попередній день")
        }

        Text(
            text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, d MMMM")),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )

        IconButton(onClick = { onDateSelected(selectedDate.plusDays(1)) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Наступний день")
        }
    }
}

@Composable
fun DailyMealPlan(meals: List<Meal>, navController: NavController) {
    LazyColumn {
        item {
            Text("Прийоми їжі:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(meals) { meal ->
            MealCard(
                meal = meal,
                onClick = { navController.navigate("meal_detail/${meal.id}") }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun DailyNutritionSummary(caloriesConsumed: Int, caloriesGoal: Int) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Денний баланс:", style = MaterialTheme.typography.titleMedium)
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
                Text("$caloriesConsumed ккал")
                Text("Ціль: $caloriesGoal ккал")
            }
        }
    }
}

data class Meal(
    val id: String,
    val name: String,
    val calories: Int,
    val ingredients: List<String>
)