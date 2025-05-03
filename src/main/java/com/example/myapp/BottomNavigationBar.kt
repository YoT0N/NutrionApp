package com.example.myapp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        Screen.Main,
        Screen.MealPlan,
        Screen.Profile
    )

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
                        else -> Icon(Icons.Default.MoreVert, contentDescription = null)
                    }
                },
                label = {
                    Text(
                        when (screen) {
                            Screen.Main -> "Головна"
                            Screen.MealPlan -> "Харчування"
                            Screen.Profile -> "Профіль"
                            else -> ""
                        }
                    )
                },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}