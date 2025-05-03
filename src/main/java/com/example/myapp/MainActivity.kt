package com.example.myapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }
}