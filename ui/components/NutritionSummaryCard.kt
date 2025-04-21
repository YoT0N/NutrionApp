package com.example.lab5.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lab5.R

/**
 * Картка для відображення зведеної інформації про харчування.
 * Підтримує API 24+ (Android 7.0+).
 *
 * @param title Заголовок картки (наприклад, "Добова", "Тижнева")
 * @param calories Кількість калорій
 * @param protein Кількість білків (г)
 * @param carbs Кількість вуглеводів (г)
 * @param fat Кількість жирів (г)
 * @param modifier Modifier для налаштування розміщення
 */
@Composable
fun NutritionSummaryCard(
    title: String,
    calories: Float,
    protein: Float,
    carbs: Float,
    fat: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            NutritionInfoRow(
                label = stringResource(R.string.calories),
                value = "${calories.toInt()} ${stringResource(R.string.kcal)}"
            )

            NutritionInfoRow(
                label = stringResource(R.string.protein),
                value = "${protein.toInt()}g"
            )

            NutritionInfoRow(
                label = stringResource(R.string.carbs),
                value = "${carbs.toInt()}g"
            )

            NutritionInfoRow(
                label = stringResource(R.string.fat),
                value = "${fat.toInt()}g"
            )
        }
    }
}

@Composable
private fun NutritionInfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NutritionSummaryCardPreview() {
    MaterialTheme {
        NutritionSummaryCard(
            title = "Добова",
            calories = 2150f,
            protein = 120f,
            carbs = 250f,
            fat = 80f,
            modifier = Modifier.padding(16.dp)
        )
    }
}