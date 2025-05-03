package com.example.myapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionStatsScreen(navController: NavController) {
    val weeklyStats = remember {
        listOf(
            NutritionDay("Понеділок", 1800, 2000),
            NutritionDay("Вівторок", 1750, 2000),
            NutritionDay("Середа", 1950, 2000),
            NutritionDay("Четвер", 1600, 2000),
            NutritionDay("П'ятниця", 2100, 2000),
            NutritionDay("Субота", 2200, 2200),
            NutritionDay("Неділя", 1850, 2000)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Статистика харчування") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            WeeklyCaloriesChart(weeklyStats)

            Spacer(modifier = Modifier.height(16.dp))

            AverageNutritionStats(weeklyStats)
        }
    }
}

@Composable
fun WeeklyCaloriesChart(stats: List<NutritionDay>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Калорії за тиждень", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                stats.forEach { day ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val heightRatio = day.consumed.toFloat() / day.goal
                        Box(
                            modifier = Modifier
                                .width(30.dp)
                                .height(150.dp * heightRatio)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(day.dayName.take(3), style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
fun AverageNutritionStats(stats: List<NutritionDay>) {
    val averageConsumed = stats.map { it.consumed }.average().toInt()
    val averageGoal = stats.map { it.goal }.average().toInt()

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Середні показники", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Спожито:")
                Text("$averageConsumed ккал/день")
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Рекомендовано:")
                Text("$averageGoal ккал/день")
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Баланс:")
                Text("${averageConsumed - averageGoal} ккал/день")
            }
        }
    }
}

data class NutritionDay(
    val dayName: String,
    val consumed: Int,
    val goal: Int
)