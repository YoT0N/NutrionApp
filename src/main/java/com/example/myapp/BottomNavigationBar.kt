package com.example.myapp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.myapp.auth.SharedViewModel
import com.example.myapp.settings.StringResource

@Composable
fun BottomNavigationBar(navController: NavController, sharedViewModel: SharedViewModel) {
    val items = listOf(
        Screen.Main,
        Screen.MealPlan,
        Screen.Profile
    )

    val strings = StringResource.strings

    // Отримуємо email з SharedViewModel
    val email by sharedViewModel.currentUserEmail.collectAsState()

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    when (screen) {
                        Screen.Main -> Icon(Icons.Default.Home, contentDescription = null)
                        Screen.MealPlan -> Icon(Icons.Default.AccountBox, contentDescription = null)
                        Screen.Profile -> Icon(Icons.Default.Person, contentDescription = null)
                        else -> Icon(Icons.Default.Home, contentDescription = null)
                    }
                },
                label = {
                    Text(
                        when (screen) {
                            Screen.Main -> strings.home
                            Screen.MealPlan -> strings.mealPlan
                            Screen.Profile -> strings.profile
                            else -> ""
                        }
                    )
                },
                selected = currentRoute?.startsWith(screen.route.split("/")[0]) == true,
                onClick = {
                    when (screen) {
                        Screen.Profile -> {
                            email?.let { nonNullEmail ->
                                navController.navigate(Screen.Profile.createRoute(nonNullEmail)) {
                                    // Запобігаємо створенню кількох копій одного призначення
                                    launchSingleTop = true
                                    // Відновлюємо стан при повторному виборі
                                    restoreState = true
                                }
                            } ?: run {
                                // Якщо email відсутній, перейти на екран авторизації
                                navController.navigate(Screen.Auth.route)
                            }
                        }
                        else -> {
                            navController.navigate(screen.route) {
                                // Очищаємо стек до початкового призначення графа
                                popUpTo(navController.graph.startDestinationId)
                                // Запобігаємо створенню кількох копій одного призначення
                                launchSingleTop = true
                                // Відновлюємо стан при повторному виборі
                                restoreState = true
                            }
                        }
                    }
                }
            )
        }
    }
}