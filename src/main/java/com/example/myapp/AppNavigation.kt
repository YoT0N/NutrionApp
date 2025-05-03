package com.example.myapp

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapp.auth.AuthScreen
import androidx.navigation.navArgument

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Auth.route
    ) {
        composable(Screen.Auth.route) { AuthScreen(navController) }
        composable(Screen.Registration.route) { RegistrationScreen(navController) }
        composable(Screen.Main.route) { MainScreen(navController) }
        composable(Screen.Profile.route) { ProfileScreen(navController) }
        composable(Screen.MealPlan.route) { MealPlanScreen(navController) }
        composable(Screen.NutritionStats.route) { NutritionStatsScreen(navController) }
        composable(
            route = Screen.MealDetail.route,
            arguments = listOf(navArgument("mealId") { type = NavType.StringType })
        ) { backStackEntry ->
            val mealId = backStackEntry.arguments?.getString("mealId") ?: ""
            MealDetailScreen(navController, mealId)
        }
    }
}

// Hard-coded перевірка авторизації
fun isUserLoggedIn(): Boolean = false

// Екрани додатку
sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Registration : Screen("registration")
    object Main : Screen("main")
    object Profile : Screen("profile")
    object MealPlan : Screen("meal_plan")  // Новий екран для плану харчування
    object NutritionStats : Screen("nutrition_stats")  // Екран статистики
    object MealDetail : Screen("meal_detail/{mealId}") {
        fun createRoute(mealId: String) = "meal_detail/$mealId"
    }}