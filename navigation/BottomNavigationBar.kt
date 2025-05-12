package com.example.lab5.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.lab5.R

@Composable
fun BottomNavigationBar(
    navController: NavController,
    screens: List<Screen> = Screen.bottomNavScreens
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    if (screens.any { it.route == currentRoute }) {
        NavigationBar(
            modifier = Modifier.height(64.dp),
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 8.dp
        ) {
            screens.forEach { screen ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = screen.iconRes),
                            contentDescription = stringResource(id = screen.titleRes)
                        )
                    },
                    label = { Text(text = stringResource(id = screen.titleRes)) },
                    selected = currentRoute == screen.route,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }
    }
}