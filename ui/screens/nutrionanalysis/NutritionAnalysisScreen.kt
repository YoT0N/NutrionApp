package com.example.lab5.ui.screens.nutrionanalysis

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ShowChart

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab5.R
import com.example.lab5.data.model.MealType
import com.example.lab5.ui.components.ChartType
import com.example.lab5.ui.components.NutritionChart
import com.example.lab5.ui.components.NutritionSummaryCard
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionAnalysisScreen(
    modifier: Modifier = Modifier,
    viewModel: NutritionAnalysisViewModel = viewModel(),
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.nutrition_analysis_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    // Chart type selector
                    IconButton(
                        onClick = {
                            viewModel.onEvent(
                                NutritionAnalysisEvent.ChangeChartType(
                                    when (state.chartType) {
                                        ChartType.PIE_CHART -> ChartType.BAR_CHART
                                        ChartType.BAR_CHART -> ChartType.LINE_CHART
                                        ChartType.LINE_CHART -> ChartType.PIE_CHART
                                        ChartType.PIE_CHART -> TODO()
                                        ChartType.BAR_CHART -> TODO()
                                        ChartType.LINE_CHART -> TODO()
                                    }
                                )
                            )
                        }
                    ) {
                        Icon(
                            imageVector = when (state.chartType) {
                                ChartType.PIE_CHART -> Icons.Default.PieChart
                                ChartType.BAR_CHART -> Icons.Default.BarChart
                                ChartType.LINE_CHART -> Icons.Default.ShowChart
                            },
                            contentDescription = stringResource(R.string.change_chart_type)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Date range display
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${dateFormatter.format(state.startDate)} - ${dateFormatter.format(state.endDate)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Nutrition chart
            NutritionChart(
                protein = state.dailyNutrition.protein,
                carbs = state.dailyNutrition.carbs,
                fat = state.dailyNutrition.fat,
                chartType = state.chartType,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(horizontal = 16.dp)
            )

            // Nutrition summary cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NutritionSummaryCard(
                    title = stringResource(R.string.daily),
                    calories = state.dailyNutrition.calories,
                    protein = state.dailyNutrition.protein,
                    carbs = state.dailyNutrition.carbs,
                    fat = state.dailyNutrition.fat,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                NutritionSummaryCard(
                    title = stringResource(R.string.weekly),
                    calories = state.weeklyNutrition.calories,
                    protein = state.weeklyNutrition.protein,
                    carbs = state.weeklyNutrition.carbs,
                    fat = state.weeklyNutrition.fat,
                    modifier = Modifier.weight(1f)
                )
            }

            // Meal type filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MealType.values().forEach { mealType ->
                    FilterChip(
                        selected = state.selectedMealType == mealType,
                        onClick = {
                            val newFilter: MealType? = if (state.selectedMealType == mealType) null else mealType
                            viewModel.onEvent(NutritionAnalysisEvent.ApplyMealTypeFilter(newFilter))
                        },
                        label = { Text(mealType.name) }
                    )
                }
            }

            // Meals list
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                state.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.error ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                state.meals.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_meals_found),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.filteredMeals) { meal ->
                            // Ваш існуючий MealItem компонент
                            // Адаптуйте під ваші потреби
                        }
                    }
                }
            }
        }
    }
}