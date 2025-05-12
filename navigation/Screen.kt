package com.example.lab5.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.lab5.R

/**
 * Клас, що визначає всі екрани додатка та їх властивості
 */
sealed class Screen(
    val route: String,
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int
) {
    object Home : Screen(
        route = "home",
        titleRes = R.string.home_title,
        iconRes = R.drawable.ic_home
    )

    object MealPlan : Screen(
        route = "meal_plan",
        titleRes = R.string.meal_plan_title,
        iconRes = R.drawable.ic_meal_plan
    )

    object NutritionAnalysis : Screen(
        route = "nutrition_analysis",
        titleRes = R.string.nutrition_analysis_title,
        iconRes = R.drawable.ic_analysis
    )

    object Profile : Screen(
        route = "profile",
        titleRes = R.string.profile_title,
        iconRes = R.drawable.ic_profile
    )

    companion object {
        /**
         * Список екранів для нижньої навігаційної панелі
         */
        val bottomNavScreens = listOf(
            Home,
            MealPlan,
            NutritionAnalysis,
            Profile
        )

        /**
         * Отримати екран за маршрутом
         */
        fun fromRoute(route: String?): Screen? {
            return bottomNavScreens.find { it.route == route }
        }
    }
}