package com.example.myapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapp.auth.SharedViewModel
import com.example.myapp.database.AppDatabase
import com.example.myapp.database.Meal
import com.example.myapp.database.MealDao
import com.example.myapp.mealplan.MealPlanEvent
import com.example.myapp.mealplan.MealPlanViewModel
import com.example.myapp.settings.StringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlanScreen(navController: NavController, sharedViewModel: SharedViewModel) {
    val userId = sharedViewModel.userId.collectAsState().value
    val strings = StringResource.strings

    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val viewModel: MealPlanViewModel = viewModel(
        factory = MealPlanViewModelFactory(database.mealDao())
    )

    var mealName by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        if (userId == null) {
            // Redirect to login screen if userId is null
            navController.navigate(Screen.Auth.route) {
                popUpTo(Screen.Main.route) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.addNewMeal) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = strings.back)
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
            OutlinedTextField(
                value = mealName,
                onValueChange = { mealName = it },
                label = { Text(strings.mealName) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = calories,
                onValueChange = { calories = it },
                label = { Text(strings.calories) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = ingredients,
                onValueChange = { ingredients = it },
                label = { Text(strings.ingredientsSeparator) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // Only proceed if userId is not null
                    userId?.let { uid ->
                        val meal = Meal(
                            name = mealName,
                            calories = calories.toIntOrNull() ?: 0,
                            ingredients = ingredients.split(",").map { it.trim() },
                            userId = uid // Adding the userId here
                        )
                        viewModel.onEvent(MealPlanEvent.SaveMeal(meal))
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = userId != null
            ) {
                Text(strings.save)
            }
        }
    }
}

class MealPlanViewModelFactory(
    private val mealDao: MealDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MealPlanViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MealPlanViewModel(mealDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}