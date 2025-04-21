package com.example.lab5.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab5.R
import com.example.lab5.data.model.Meal
import com.example.lab5.data.model.MealType
import com.example.lab5.ui.components.MealItem
import com.example.lab5.ui.components.NutritionChart
import com.example.lab5.ui.theme.NutritionAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
    onAddMeal: () -> Unit = {},
    onMealClick: (Long) -> Unit = {} // Changed to Long to match Meal.id type
) {
    // Use remember to maintain state across recompositions
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddMeal,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_meal)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Search bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { newValue ->
                    searchQuery = newValue
                    viewModel.onEvent(HomeEvent.SearchQueryChanged(newValue.text))
                },
                onSearch = { viewModel.onEvent(HomeEvent.ExecuteSearch) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // Nutrition chart
            NutritionChart(
                protein = state.nutritionSummary.proteinPercent,
                carbs = state.nutritionSummary.carbsPercent,
                fat = state.nutritionSummary.fatPercent,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(200.dp)
            )

            // Filter chips
            MealFilterChips(
                selectedFilter = state.currentFilter,
                onFilterSelected = { filter ->
                    viewModel.onEvent(HomeEvent.ApplyFilter(filter))
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

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
                state.meals.isEmpty() -> {
                    EmptyState(
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.meals) { meal ->
                            MealItem(
                                meal = meal,
                                onItemClick = { onMealClick(meal.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },
        placeholder = {
            Text(stringResource(R.string.search_meals))
        },
        singleLine = true,
        shape = MaterialTheme.shapes.extraLarge,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        modifier = modifier,
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch()
                defaultKeyboardAction(ImeAction.Search)
            }
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
    )
}

@Composable
private fun MealFilterChips(
    selectedFilter: MealType,
    onFilterSelected: (MealType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MealType.values().forEach { filter ->
            FilterChip(
                selected = filter == selectedFilter,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter.name) },
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.no_meals_found),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


