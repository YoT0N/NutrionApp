package com.example.myapp

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.myapp.auth.SharedViewModel
import com.example.myapp.ui.WindowWidthSizeClass


@Composable
fun AdaptiveNavigation(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    widthSizeClass: WindowWidthSizeClass,
    content: @Composable () -> Unit
) {
    val items = listOf(
        NavigationItem(
            route = Screen.Main.route,
            label = "Головна",
            icon = Icons.Default.Home
        ),
        NavigationItem(
            route = Screen.MealPlan.route,
            label = "Харчування",
            icon = Icons.Default.AccountBox
        ),
        NavigationItem(
            route = Screen.Profile.route,
            label = "Профіль",
            icon = Icons.Default.Person
        )
    )

    // Отримуємо email з SharedViewModel
    val email by sharedViewModel.currentUserEmail.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Функція для навігації
    val onNavigate: (Screen) -> Unit = { screen ->
        when (screen) {
            Screen.Profile -> {
                email?.let { nonNullEmail ->
                    navController.navigate(Screen.Profile.createRoute(nonNullEmail)) {
                        launchSingleTop = true
                        restoreState = true
                    }
                } ?: run {
                    navController.navigate(Screen.Auth.route)
                }
            }
            else -> {
                navController.navigate(screen.route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }

    when (widthSizeClass) {
        WindowWidthSizeClass.COMPACT -> {
            // Bottom Navigation для телефонів у портретній орієнтації
            Scaffold(
                bottomBar = {
                    BottomAppBar {
                        items.forEach { item ->
                            val selected = currentRoute?.startsWith(item.route.split("/")[0]) == true
                            NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) },
                                selected = selected,
                                onClick = { onNavigate(getScreenFromRoute(item.route)) }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    content()
                }
            }
        }
        WindowWidthSizeClass.MEDIUM -> {
            // Navigation Rail для телефонів у альбомній орієнтації
            Row {
                NavigationRail {
                    Spacer(Modifier.height(16.dp))
                    items.forEach { item ->
                        val selected = currentRoute?.startsWith(item.route.split("/")[0]) == true
                        NavigationRailItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = selected,
                            onClick = { onNavigate(getScreenFromRoute(item.route)) }
                        )
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    content()
                }
            }
        }
        WindowWidthSizeClass.EXPANDED -> {
            // Navigation Drawer для планшетів
            PermanentNavigationDrawer(
                drawerContent = {
                    PermanentDrawerSheet(Modifier.width(240.dp)) {
                        Spacer(Modifier.height(24.dp))
                        Text(
                            "Мій раціон",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        items.forEach { item ->
                            val selected = currentRoute?.startsWith(item.route.split("/")[0]) == true
                            NavigationDrawerItem(
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) },
                                selected = selected,
                                onClick = { onNavigate(getScreenFromRoute(item.route)) },
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            ) {
                content()
            }
        }
    }
}

// Допоміжна функція для отримання Screen з route
private fun getScreenFromRoute(route: String): Screen {
    return when (route.split("/")[0]) {
        "main" -> Screen.Main
        "meal_plan" -> Screen.MealPlan
        "profile" -> Screen.Profile
        else -> Screen.Main
    }
}

// Допоміжний клас для представлення елементів навігації
data class NavigationItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)