package com.example.myapp

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NutritionStatsActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var weeklyChartLayout: LinearLayout
    private lateinit var avgConsumedTextView: TextView
    private lateinit var avgGoalTextView: TextView
    private lateinit var avgBalanceTextView: TextView

    private val nutritionDays = NutritionDay.createTestData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutrition_stats)

        // Ініціалізація Toolbar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = getString(R.string.nutrition_stats_title)
        }

        // Ініціалізація RecyclerView
        recyclerView = findViewById(R.id.nutrition_days_recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = NutritionDayAdapter(nutritionDays)

        // Ініціалізація статичних елементів
        weeklyChartLayout = findViewById(R.id.weekly_chart)
        avgConsumedTextView = findViewById(R.id.avg_consumed)
        avgGoalTextView = findViewById(R.id.avg_goal)
        avgBalanceTextView = findViewById(R.id.avg_balance)

        // Налаштування графіка та статистики
        setupWeeklyChart()
        updateAverageStats()
    }

    private fun setupWeeklyChart() {
        weeklyChartLayout.removeAllViews() // Очищаємо попередні стовпці

        val maxCalories = nutritionDays.maxOf { maxOf(it.consumed, it.goal) }

        for (day in nutritionDays) {
            val view = layoutInflater.inflate(R.layout.chart_column, weeklyChartLayout, false)
            val column = view.findViewById<View>(R.id.chart_column)
            val label = view.findViewById<TextView>(R.id.chart_label)

            // Встановлюємо висоту стовпця
            val params = column.layoutParams
            val heightPercentage = day.consumed.toFloat() / maxCalories
            params.height = (150 * heightPercentage).toInt() // 150dp - максимальна висота стовпця
            column.layoutParams = params

            // Встановлюємо колір стовпця
            val columnColor = when {
                day.isPositive -> ContextCompat.getColor(this, R.color.balance_positive)
                day.isNegative -> ContextCompat.getColor(this, R.color.balance_negative)
                else -> ContextCompat.getColor(this, R.color.balance_neutral)
            }
            column.backgroundTintList = android.content.res.ColorStateList.valueOf(columnColor)

            // Встановлюємо мітку (перші 3 літери назви дня)
            label.text = day.dayName.take(3)

            // Додаємо стовпець до графіка
            weeklyChartLayout.addView(view)
        }
    }

    private fun updateAverageStats() {
        val avgConsumed = nutritionDays.map { it.consumed }.average().toInt()
        val avgGoal = nutritionDays.map { it.goal }.average().toInt()
        val avgBalance = avgConsumed - avgGoal

        avgConsumedTextView.text = getString(R.string.kcal_per_day, avgConsumed)
        avgGoalTextView.text = getString(R.string.kcal_per_day, avgGoal)
        avgBalanceTextView.text = getString(R.string.kcal_per_day, avgBalance)

        // Встановлюємо колір балансу
        val balanceColor = when {
            avgBalance > 0 -> ContextCompat.getColor(this, R.color.balance_positive)
            avgBalance < 0 -> ContextCompat.getColor(this, R.color.balance_negative)
            else -> ContextCompat.getColor(this, R.color.balance_neutral)
        }
        avgBalanceTextView.setTextColor(balanceColor)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}