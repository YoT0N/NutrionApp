package com.example.myapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

/**
 * Визначає класи розмірів екрану для адаптивного інтерфейсу
 */
enum class WindowWidthSizeClass {
    COMPACT, // Для телефонів у портретній орієнтації
    MEDIUM,  // Для телефонів у альбомній орієнтації та малих планшетів
    EXPANDED // Для планшетів
}

enum class WindowHeightSizeClass {
    COMPACT, // Для телефонів у альбомній орієнтації
    MEDIUM,  // Для телефонів у портретній орієнтації та малих планшетів
    EXPANDED // Для більших пристроїв
}

/**
 * Структура, яка містить класи розмірів для ширини та висоти
 */
data class WindowSizeClass(
    val widthSizeClass: WindowWidthSizeClass,
    val heightSizeClass: WindowHeightSizeClass
)

/**
 * Composable функція для визначення поточного класу розміру вікна
 */
@Composable
fun rememberWindowSizeClass(): WindowSizeClass {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    return remember(screenWidth, screenHeight) {
        val widthSizeClass = when {
            screenWidth < 600.dp -> WindowWidthSizeClass.COMPACT
            screenWidth < 840.dp -> WindowWidthSizeClass.MEDIUM
            else -> WindowWidthSizeClass.EXPANDED
        }

        val heightSizeClass = when {
            screenHeight < 480.dp -> WindowHeightSizeClass.COMPACT
            screenHeight < 900.dp -> WindowHeightSizeClass.MEDIUM
            else -> WindowHeightSizeClass.EXPANDED
        }

        WindowSizeClass(widthSizeClass, heightSizeClass)
    }
}