package com.example.lab5.data.repository

import com.example.lab5.data.local.dao.NutritionGoalDao
import com.example.lab5.data.local.entities.NutritionGoalEntity
import com.example.lab5.data.model.NutritionGoal
import com.example.lab5.data.model.NutritionSummary

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

interface NutritionRepository {
    fun getActiveGoalByUser(userId: Long): Flow<NutritionGoal?>
    fun getAllGoalsByUser(userId: Long): Flow<List<NutritionGoal>>
    suspend fun insertOrUpdateGoal(goal: NutritionGoal): Long
    suspend fun deleteGoal(goal: NutritionGoal)
    suspend fun getDailyNutritionSummary(userId: Long, date: Date): NutritionSummary
}

class NutritionRepositoryImpl(
    private val nutritionGoalDao: NutritionGoalDao
) : NutritionRepository {

    override fun getActiveGoalByUser(userId: Long): Flow<NutritionGoal?> =
        nutritionGoalDao.getActiveGoalByUser(userId)
            .map { entity -> entity?.toModel() }

    override fun getAllGoalsByUser(userId: Long): Flow<List<NutritionGoal>> =
        nutritionGoalDao.getAllGoalsByUser(userId)
            .map { entities -> entities.map { it.toModel() } }

    override suspend fun insertOrUpdateGoal(goal: NutritionGoal): Long =
        nutritionGoalDao.insertOrUpdateGoal(goal.toEntity())

    override suspend fun deleteGoal(goal: NutritionGoal) {
        nutritionGoalDao.deleteGoal(goal.toEntity())
    }

    override suspend fun getDailyNutritionSummary(userId: Long, date: Date): NutritionSummary =
        nutritionGoalDao.getDailyNutritionSummary(userId, date).let {
            NutritionSummary(it.calories, it.protein, it.carbs, it.fat)
        }
}

// Конвертаційні функції розширення
private fun NutritionGoal.toEntity(): NutritionGoalEntity = NutritionGoalEntity(
    id = id,
    userId = userId,
    dailyCalories = dailyCalories,
    dailyProtein = dailyProtein,
    dailyCarbs = dailyCarbs,
    dailyFat = dailyFat,
    startDate = startDate,
    endDate = endDate,
    isActive = isActive
)

private fun NutritionGoalEntity.toModel(): NutritionGoal = NutritionGoal(
    id = id,
    userId = userId,
    dailyCalories = dailyCalories,
    dailyProtein = dailyProtein,
    dailyCarbs = dailyCarbs,
    dailyFat = dailyFat,
    startDate = startDate,
    endDate = endDate,
    isActive = isActive
)
