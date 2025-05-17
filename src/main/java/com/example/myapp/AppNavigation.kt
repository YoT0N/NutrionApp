package com.example.myapp

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapp.auth.AuthScreen
import com.example.myapp.auth.RegistrationScreen
import com.example.myapp.auth.SharedViewModel
import com.example.myapp.home.MainScreen
import com.example.myapp.meal_edit.MealEditScreen
import com.example.myapp.profile.ProfileScreen
import com.example.myapp.settings.SettingsViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(settingsViewModel: SettingsViewModel) {
    val navController = rememberNavController()

    val sharedViewModel: SharedViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Auth.route
    ) {
        composable(Screen.Auth.route) {
            AuthScreen(navController, sharedViewModel)
        }

        composable(Screen.Registration.route) {
            RegistrationScreen(navController)
        }

        composable(Screen.Main.route) {
            MainScreen(navController, sharedViewModel)
        }

        composable(Screen.MealPlan.route) {
            MealPlanScreen(navController, sharedViewModel)
        }

        composable(Screen.NutritionStats.route) {
            // Замість відображення Compose екрану запускаємо Activity з XML-лейаутом
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                val intent = Intent(context, NutritionStatsActivity::class.java)
                context.startActivity(intent)
                // Повертаємося назад, щоб кнопка "назад" в NutritionStatsActivity повертала до MainScreen
                navController.popBackStack()
            }
        }

        composable(
            route = Screen.MealEdit.route,
            arguments = listOf(navArgument("mealId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val mealId = backStackEntry.arguments?.getString("mealId")
            MealEditScreen(
                navController = navController,
                mealId = mealId,
                onSaveComplete = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("MEAL_UPDATED", true)
                }
            )
        }

        composable(
            route = Screen.Profile.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { _ ->
            ProfileScreen(
                navController = navController,
                sharedViewModel = sharedViewModel,
                settingsViewModel = settingsViewModel
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Registration : Screen("registration")
    object Main : Screen("main")
    object Profile : Screen("profile/{email}") {
        fun createRoute(email: String): String = "profile/$email"
    }
    object MealPlan : Screen("meal_plan")
    object NutritionStats : Screen("nutrition_stats")

    object MealEdit : Screen("meal_edit/{mealId}") {
        fun createRoute(mealId: String?) = if (mealId != null) "meal_edit/$mealId" else "meal_edit/null"
    }
}