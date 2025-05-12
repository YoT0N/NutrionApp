
package com.example.lab5.navigation
/*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.lab5.ui.screens.home.HomeScreen
import com.example.lab5.ui.screens.mealplan.MealPlanScreen
import com.example.lab5.ui.screens.nutrionanalysis.NutritionAnalysisScreen
import com.example.lab5.ui.screens.profile.ProfileScreen

*/
/**
 * Основний навігаційний граф додатка
 * @param navController Контролер навігації
 * @param startDestination Початковий маршрут (за замовчуванням - головний екран)
 *//*

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Головний екран
        composable(route = Screen.Home.route) {
            HomeScreen(
                onMealPlanClick = { navController.navigate(Screen.MealPlan.route) },
                onNutritionAnalysisClick = { navController.navigate(Screen.NutritionAnalysis.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) }
            )
        }

        // Екран плану харчування
        composable(route = Screen.MealPlan.route) {
            MealPlanScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // Екран аналізу харчування
        composable(route = Screen.NutritionAnalysis.route) {
            NutritionAnalysisScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // Екран профілю
        composable(route = Screen.Profile.route) {
            ProfileScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
*/
