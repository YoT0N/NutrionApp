package com.example.myapp

import java.text.SimpleDateFormat
import java.util.*

data class NutritionDay(
    val dayName: String,
    val date: Date,
    val consumed: Int,
    val goal: Int,
) {
    val difference: Int get() = consumed - goal

    val isPositive: Boolean get() = difference > 0
    val isNegative: Boolean get() = difference < 0

    val formattedDate: String get() {
        val format = SimpleDateFormat("d MMMM", Locale.getDefault())
        return format.format(date)
    }

    companion object {
        fun createTestData(): List<NutritionDay> {
            val calendar = Calendar.getInstance()
            // Встановлюємо на початок тижня (понеділок)
            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                calendar.add(Calendar.DAY_OF_MONTH, -1)
            }

            val days = mutableListOf<NutritionDay>()
            val dayNames = listOf("Понеділок", "Вівторок", "Середа", "Четвер", "П'ятниця", "Субота", "Неділя")

            for (i in 0..6) {
                val date = calendar.time
                val consumed = when (i) {
                    0 -> 1800
                    1 -> 1750
                    2 -> 1950
                    3 -> 1600
                    4 -> 2100
                    5 -> 2200
                    6 -> 1850
                    else -> 0
                }
                val goal = if (i == 5) 2200 else 2000

                days.add(NutritionDay(dayNames[i], date, consumed, goal))
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }

            return days
        }
    }
}