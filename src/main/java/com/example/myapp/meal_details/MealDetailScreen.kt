package com.example.myapp.meal_details

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapp.home.Meal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailScreen(navController: NavController, mealId: String) {
    val viewModel: MealDetailViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    // Загрузка данных при инициализации
    LaunchedEffect(mealId) {
        viewModel.onEvent(MealDetailEvent.LoadMeal(mealId))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (uiState) {
                            is MealDetailState.Success -> (uiState as MealDetailState.Success).meal.name
                            else -> "Завантаження..."
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        when (uiState) {
            is MealDetailState.Success -> {
                val meal = (uiState as MealDetailState.Success).meal
                MealDetailContent(
                    meal = meal,
                    onEditClick = { viewModel.onEvent(MealDetailEvent.EditMeal) },
                    onDeleteClick = { viewModel.onEvent(MealDetailEvent.DeleteMeal) },
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp)
                )
            }
            MealDetailState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is MealDetailState.Error -> {
                // Обработка ошибки
            }
        }
    }
}

@Composable
private fun MealDetailContent(
    meal: Meal,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text("Калорії: ${meal.calories}", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Інгредієнти:", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Column {
            meal.ingredients.forEach { ingredient ->
                Text("• $ingredient")
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onEditClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Редагувати")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onDeleteClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Видалити")
        }
    }
}