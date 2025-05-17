package com.example.myapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class NutritionDayAdapter(private val nutritionDays: List<NutritionDay>) :
    RecyclerView.Adapter<NutritionDayAdapter.NutritionDayViewHolder>() {

    class NutritionDayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayNameTextView: TextView = itemView.findViewById(R.id.text_day_name)
        val dateTextView: TextView = itemView.findViewById(R.id.text_date)
        val caloriesProgressBar: ProgressBar = itemView.findViewById(R.id.progress_calories)
        val consumedTextView: TextView = itemView.findViewById(R.id.text_consumed)
        val goalTextView: TextView = itemView.findViewById(R.id.text_goal)
        val balanceTextView: TextView = itemView.findViewById(R.id.text_balance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NutritionDayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nutrition_day, parent, false)
        return NutritionDayViewHolder(view)
    }

    override fun onBindViewHolder(holder: NutritionDayViewHolder, position: Int) {
        val day = nutritionDays[position]
        val context = holder.itemView.context

        // Встановлюємо назву дня і дату
        holder.dayNameTextView.text = day.dayName
        holder.dateTextView.text = day.formattedDate

        // Встановлюємо прогрес
        val progress = (day.consumed.toFloat() / day.goal * 100).toInt()
        holder.caloriesProgressBar.progress = progress

        // Змінюємо колір прогресу в залежності від перевищення/недобору
        val progressColor = when {
            day.isPositive -> ContextCompat.getColor(context, R.color.balance_positive)
            day.isNegative -> ContextCompat.getColor(context, R.color.balance_negative)
            else -> ContextCompat.getColor(context, R.color.balance_neutral)
        }
        holder.caloriesProgressBar.progressTintList = android.content.res.ColorStateList.valueOf(progressColor)

        // Встановлюємо текстові значення
        holder.consumedTextView.text = context.getString(R.string.kcal, day.consumed)
        holder.goalTextView.text = context.getString(R.string.kcal_of, day.goal)

        // Встановлюємо баланс і його колір
        holder.balanceTextView.text = context.getString(R.string.balance_format, day.difference)
        val balanceColor = when {
            day.isPositive -> ContextCompat.getColor(context, R.color.balance_positive)
            day.isNegative -> ContextCompat.getColor(context, R.color.balance_negative)
            else -> ContextCompat.getColor(context, R.color.balance_neutral)
        }
        holder.balanceTextView.setTextColor(balanceColor)
    }

    override fun getItemCount() = nutritionDays.size
}