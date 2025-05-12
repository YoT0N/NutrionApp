package com.example.lab5.ui.components


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.min

/**
 * Компонент для візуалізації співвідношення макронутрієнтів.
 * Підтримує API 24+ (Android 7.0+).
 */
@Composable
fun NutritionChart(
    protein: Float,
    carbs: Float,
    fat: Float,
    chartType: ChartType = ChartType.PIE_CHART,
    modifier: Modifier = Modifier
) {
    // Отримуємо кольори в контексті композованої функції
    val colors = MaterialTheme.colorScheme

    Box(modifier = modifier) {
        when (chartType) {
            ChartType.PIE_CHART -> PieChart(protein, carbs, fat, colors)
            ChartType.BAR_CHART -> BarChart(protein, carbs, fat, colors)
            ChartType.LINE_CHART -> LineChart(protein, carbs, fat, colors)
        }
    }
}

@Composable
private fun PieChart(
    protein: Float,
    carbs: Float,
    fat: Float,
    colors: ColorScheme
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val total = protein + carbs + fat
        if (total <= 0) return@Canvas

        val center = Offset(size.width / 2, size.height / 2)
        val radius = min(size.width, size.height) * 0.4f

        val segments = listOf(
            Segment(protein / total, colors.primary),
            Segment(carbs / total, colors.secondary),
            Segment(fat / total, colors.tertiary)
        )

        drawPieChart(segments, center, radius)
    }
}

@Composable
private fun BarChart(
    protein: Float,
    carbs: Float,
    fat: Float,
    colors: ColorScheme
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val maxValue = maxOf(protein, carbs, fat)
        if (maxValue <= 0) return@Canvas

        val barWidth = size.width / 4f
        val padding = 16.dp.toPx()

        // Protein bar
        drawRect(
            color = colors.primary,
            topLeft = Offset(barWidth * 0.5f, size.height - (protein / maxValue) * size.height),
            size = Size(barWidth, (protein / maxValue) * size.height)
        )

        // Carbs bar
        drawRect(
            color = colors.secondary,
            topLeft = Offset(barWidth * 1.5f, size.height - (carbs / maxValue) * size.height),
            size = Size(barWidth, (carbs / maxValue) * size.height)
        )

        // Fat bar
        drawRect(
            color = colors.tertiary,
            topLeft = Offset(barWidth * 2.5f, size.height - (fat / maxValue) * size.height),
            size = Size(barWidth, (fat / maxValue) * size.height)
        )
    }
}

@Composable
private fun LineChart(
    protein: Float,
    carbs: Float,
    fat: Float,
    colors: ColorScheme
) {
    PieChart(protein, carbs, fat, colors) // Fallback to pie chart
}

private data class Segment(
    val ratio: Float,
    val color: Color
)

private fun DrawScope.drawPieChart(
    segments: List<Segment>,
    center: Offset,
    radius: Float
) {
    var startAngle = -90f // Start from top

    segments.forEach { segment ->
        val sweepAngle = segment.ratio * 360f
        drawArc(
            color = segment.color,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = true,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2)
        )
        startAngle += sweepAngle
    }
}

enum class ChartType {
    PIE_CHART,
    BAR_CHART,
    LINE_CHART
}

@Preview(showBackground = true)
@Composable
fun NutritionChartPreview() {
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            NutritionChart(
                protein = 30f,
                carbs = 50f,
                fat = 20f,
                chartType = ChartType.PIE_CHART,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            NutritionChart(
                protein = 30f,
                carbs = 50f,
                fat = 20f,
                chartType = ChartType.BAR_CHART,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}