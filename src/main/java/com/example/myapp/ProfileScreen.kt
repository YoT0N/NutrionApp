package com.example.myapp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.chromium.base.Flag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val userProfile = remember {
        UserProfile(
            name = "Олександр",
            email = "olexandr@example.com",
            goal = "Схуднення",
            targetCalories = 1800,
            restrictions = listOf("Без цукру", "Без глютену")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мій профіль") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ProfileHeader(userProfile)

            Spacer(modifier = Modifier.height(24.dp))

            NutritionGoals(userProfile)

            Spacer(modifier = Modifier.height(16.dp))

            DietaryRestrictions(userProfile)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { navController.navigate(Screen.Auth.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Вийти з акаунту")
            }
        }
    }
}

@Composable
fun ProfileHeader(profile: UserProfile) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(profile.name, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(profile.email, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun NutritionGoals(profile: UserProfile) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Мої цілі", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = "Ціль")
                Spacer(modifier = Modifier.width(8.dp))
                Text(profile.goal)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Check, contentDescription = "Калорії")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Цільові калорії: ${profile.targetCalories} ккал/день")
            }
        }
    }
}

@Composable
fun DietaryRestrictions(profile: UserProfile) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Харчові обмеження", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            profile.restrictions.forEach { restriction ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = "Обмеження")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(restriction)
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

data class UserProfile(
    val name: String,
    val email: String,
    val goal: String,
    val targetCalories: Int,
    val restrictions: List<String>
)