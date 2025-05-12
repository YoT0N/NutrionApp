package com.example.lab5


import android.app.Application
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lab5.data.local.database.NutritionDatabase
import com.example.lab5.data.repository.MealRepositoryImpl
import com.example.lab5.data.repository.UserRepositoryImpl
import com.example.lab5.ui.screens.home.HomeScreen
import com.example.lab5.ui.screens.home.HomeViewModel
import com.example.lab5.ui.screens.nutrionanalysis.NutritionAnalysisScreen
import com.example.lab5.ui.screens.nutrionanalysis.NutritionAnalysisViewModel
import com.example.lab5.ui.screens.profile.ProfileScreen
import com.example.lab5.ui.screens.profile.ProfileViewModel
import com.example.lab5.ui.theme.NutritionAppTheme
import androidx.lifecycle.viewmodel.compose.viewModel

class NutritionApp : Application() {
    val database by lazy { NutritionDatabase.getDatabase(this) }
}

@Composable
fun NutritionApplication() {
    NutritionAppTheme {
        val navController = rememberNavController()
        val context = LocalContext.current
        val app = context.applicationContext as NutritionApp

        // Ініціалізація репозиторіїв
        val userRepository = remember { UserRepositoryImpl(app.database.userDao()) }
        val mealRepository = remember { MealRepositoryImpl(app.database.mealDao()) }

        Scaffold(
            bottomBar = {
                BottomNavigationBar(navController)
            }
        ) { paddingValues ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                color = MaterialTheme.colorScheme.background
            ) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    composable(Screen.Home.route) {
                        val homeViewModel = viewModel<HomeViewModel>(
                            factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                                override fun <T : androidx.lifecycle.ViewModel> create(
                                    modelClass: Class<T>
                                ): T {
                                    return HomeViewModel(mealRepository) as T
                                }
                            }
                        )
                        HomeScreen(
                            viewModel = homeViewModel,
                            onAddMeal = { /* Логіка додавання їжі */ },
                            onMealClick = { /* Логіка переходу до деталей їжі */ }
                        )
                    }
                    composable(Screen.Nutrition.route) {
                        val nutritionViewModel = viewModel<NutritionAnalysisViewModel>(
                            factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                                override fun <T : androidx.lifecycle.ViewModel> create(
                                    modelClass: Class<T>
                                ): T {
                                    return NutritionAnalysisViewModel(mealRepository) as T
                                }
                            }
                        )
                        NutritionAnalysisScreen(viewModel = nutritionViewModel)
                    }
                    composable(Screen.Profile.route) {
                        val profileViewModel = viewModel<ProfileViewModel>(
                            factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                                override fun <T : androidx.lifecycle.ViewModel> create(
                                    modelClass: Class<T>
                                ): T {
                                    return ProfileViewModel(userRepository) as T
                                }
                            }
                        )
                        ProfileScreen(
                            viewModel = profileViewModel,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: androidx.navigation.NavHostController) {
    val screens = listOf(
        Screen.Home,
        Screen.Nutrition,
        Screen.Profile
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        screens.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = null) },
                label = { Text(stringResource(screen.resourceId)) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

sealed class Screen(
    val route: String,
    val resourceId: Int,
    val icon: ImageVector
) {
    object Home : Screen("home", R.string.home_title, Icons.Filled.Home)
    object Nutrition : Screen("nutrition", R.string.nutrition_title, Icons.Default.ShowChart)
    object Profile : Screen("profile", R.string.profile_title, Icons.Default.Person)
}