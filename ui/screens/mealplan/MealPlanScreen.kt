package com.example.lab5.ui.screens.mealplan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab5.R
import com.example.lab5.data.model.Meal
import com.example.lab5.data.model.MealType
import com.example.lab5.ui.components.MealItem
import com.example.lab5.ui.components.NutritionChart
import com.example.lab5.ui.theme.NutritionAppTheme
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlanScreen(
    modifier: Modifier = Modifier,
    viewModel: MealPlanViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onAddMealClick: () -> Unit = {},
    onMealClick: (Long) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("EEE, MMM d", Locale.getDefault()) }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.meal_plan_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddMealClick,
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
            // Date selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateFormatter.format(state.selectedDate),
                    style = MaterialTheme.typography.titleMedium
                )
                // Here you could add date picker buttons
            }

            // Nutrition chart
            NutritionChart(
                protein = state.dailyNutrition.protein,
                carbs = state.dailyNutrition.carbs,
                fat = state.dailyNutrition.fat,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(200.dp)
            )

            // Search bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { newValue ->
                    searchQuery = newValue
                    viewModel.onEvent(MealPlanEvent.SearchQueryChanged(newValue.text))
                },
                onSearch = { viewModel.onEvent(MealPlanEvent.ExecuteSearch) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // Filter chips
            MealTypeFilterChips(
                selectedType = state.selectedMealType,
                onTypeSelected = { type ->
                    viewModel.onEvent(MealPlanEvent.ApplyMealTypeFilter(type))
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
                state.showEmptyState -> {
                    EmptyMealPlanState(
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.filteredMeals) { meal ->
                            MealItem(
                                meal = meal,
                                onItemClick = { onMealClick(meal.id) },
                                onDeleteClick = { viewModel.onEvent(MealPlanEvent.DeleteMeal(meal)) }
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
private fun MealTypeFilterChips(
    selectedType: MealType?,
    onTypeSelected: (MealType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MealType.values().forEach { type ->
            FilterChip(
                selected = type == selectedType,
                onClick = { onTypeSelected(type) },
                label = { Text(type.name) },
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Composable
private fun EmptyMealPlanState(modifier: Modifier = Modifier) {
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
