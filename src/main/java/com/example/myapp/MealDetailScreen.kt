package com.example.myapp

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailScreen(navController: NavController, mealId: String) {
    // Hard-coded дані про прийом їжі
    val meal = remember(mealId) {
        when (mealId) {
            "1" -> Meal("1", "Сніданок", 350, listOf("Вівсянка", "Ягоди", "Горіхи"))
            "2" -> Meal("2", "Обід", 550, listOf("Курка", "Гречка", "Овочі"))
            "3" -> Meal("3", "Вечеря", 400, listOf("Риба", "Салат"))
            else -> Meal("0", "Невідомий прийом їжі", 0, emptyList())
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(meal.name) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
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
                onClick = { /* Логіка редагування */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Редагувати")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { /* Логіка видалення */ },
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
}