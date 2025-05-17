package com.example.myapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.core.view.WindowCompat
import com.example.myapp.settings.EnglishStrings
import com.example.myapp.settings.LocalStringResources
import com.example.myapp.settings.UkrainianStrings
import com.example.myapp.settings.SettingsViewModel
import com.example.myapp.settings.UserSettings

private val DarkColorScheme = darkColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun MyAppTheme(
    settingsViewModel: SettingsViewModel? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // Collect settings state if available
    val currentSettings = if (settingsViewModel != null) {
        settingsViewModel.settingsState.collectAsState().value
    } else {
        UserSettings()
    }

    // Determine theme based on settings or default
    val useDarkTheme = currentSettings.darkThemeEnabled ?: darkTheme

    // Determine language based on settings
    val languageStrings = when (currentSettings.language) {
        "en" -> EnglishStrings
        else -> UkrainianStrings
    }

    // Determine UI density based on settings
    val fontScale = when (currentSettings.uiDensity) {
        "compact" -> 0.9f
        else -> 1.0f
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        useDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkTheme
        }
    }

    // Apply settings to the application
    CompositionLocalProvider(
        // Provide language strings
        LocalStringResources provides languageStrings,
        // Provide density settings
        LocalDensity provides Density(
            density = LocalDensity.current.density,
            fontScale = LocalDensity.current.fontScale * fontScale
        )
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}