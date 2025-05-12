package com.example.lab5.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

/**
 * Data class representing user's daily nutrition goals.
 * Compatible with Android API 24+.
 */
@Entity(tableName = "nutrition_goals")
data class NutritionGoal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val userId: Long,

    val dailyCalories: Float,
    val dailyProtein: Float,
    val dailyCarbs: Float,
    val dailyFat: Float,

    val startDate: Date = Calendar.getInstance().time,
    val endDate: Date? = null,
    val isActive: Boolean = true
) {
    /**
     * Checks if goal is currently active based on date range
     */
    fun isCurrentlyActive(): Boolean {
        val now = Calendar.getInstance()
        val startCal = Calendar.getInstance().apply { time = startDate }
        val endCal = endDate?.let {
            Calendar.getInstance().apply { time = it }
        }

        return isActive &&
                !now.before(startCal) &&
                (endCal == null || !now.after(endCal))
    }

    /**
     * Formats date to "yyyy-MM-dd" string
     */
    fun formatDate(date: Date?): String {
        return date?.let {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
        } ?: ""
    }

    /**
     * Calculates remaining calories based on consumed amount
     */
    fun getRemainingCalories(consumed: Float): Float {
        return (dailyCalories - consumed).coerceAtLeast(0f)
    }
}

/**
 * Extension function to convert grams to percentage of daily value
 */
fun Float.toDailyPercentage(dailyValue: Float): Float {
    return if (dailyValue > 0) (this / dailyValue * 100) else 0f
}