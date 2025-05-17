package com.example.myapp

import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapp.database.AppDatabase
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.myapp.settings.SettingsViewModel
import com.example.myapp.settings.SettingsViewModelFactory
import com.example.myapp.ui.theme.MyAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        Log.d(TAG, "SplashScreen installed")

        // Встановлюємо умову для утримання сплеш-екрану
        var keepSplashOnScreen = true
        splashScreen.setKeepOnScreenCondition { keepSplashOnScreen }
        Log.d(TAG, "SplashScreen keepOnScreen condition set")

        super.onCreate(savedInstanceState)

        // Ініціалізуємо базу даних
        AppDatabase.getDatabase(this)

        // Встановлюємо мінімальну затримку для показу сплеш-екрану
        lifecycleScope.launch {
            Log.d(TAG, "Starting delay for splash screen")
            delay(2000) // 2 секунди затримки
            Log.d(TAG, "Delay finished, removing splash screen")
            keepSplashOnScreen = false
        }

        // Логування життєвого циклу
        lifecycle.addObserver(LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> println("Lifecycle: ON_CREATE")
                Lifecycle.Event.ON_START -> println("Lifecycle: ON_START")
                Lifecycle.Event.ON_RESUME -> println("Lifecycle: ON_RESUME")
                Lifecycle.Event.ON_PAUSE -> println("Lifecycle: ON_PAUSE")
                Lifecycle.Event.ON_STOP -> println("Lifecycle: ON_STOP")
                Lifecycle.Event.ON_DESTROY -> println("Lifecycle: ON_DESTROY")
                else -> println("Lifecycle: UNKNOWN_EVENT")
            }
        })

        setContent {
            // Create SettingsViewModel instance at the app level
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(application)
            )

            // Apply our theme with settings
            MyAppTheme(
                settingsViewModel = settingsViewModel
            ) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    // Pass settingsViewModel to AppNavigation
                    AppNavigation(settingsViewModel = settingsViewModel)
                }
            }
        }
    }
}