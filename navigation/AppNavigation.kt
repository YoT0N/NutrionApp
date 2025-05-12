
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
 * Об'єкт, що містить маршрути додатка
 *//*

object Routes {
    const val HOME = "home"
    const val MEAL_PLAN = "meal_plan"
    const val NUTRITION_ANALYSIS = "nutrition_analysis"
    const val PROFILE = "profile"
}

*/
/**
 * Навігаційний граф додатка
 * @param navController Контролер навігації
 * @param startDestination Початковий екран (за замовчуванням - головний екран)
 *//*

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String = Routes.HOME
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Головний екран
        composable(Routes.HOME) {
            HomeScreen(
                onMealPlanClick = { navController.navigate(Routes.MEAL_PLAN) },
                onNutritionAnalysisClick = { navController.navigate(Routes.NUTRITION_ANALYSIS) },
                onProfileClick = { navController.navigate(Routes.PROFILE) }
            )
        }

        // План харчування
        composable(Routes.MEAL_PLAN) {
            MealPlanScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // Аналіз харчування
        composable(Routes.NUTRITION_ANALYSIS) {
            NutritionAnalysisScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // Профіль користувача
        composable(Routes.PROFILE) {
            ProfileScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}*/
