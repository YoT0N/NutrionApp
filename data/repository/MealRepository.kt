package com.example.lab5.data.repository

import com.example.lab5.data.local.dao.MealDao
import com.example.lab5.data.local.entities.MealEntity
import com.example.lab5.data.model.Meal
import com.example.lab5.data.model.NutritionSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

/**
 * Interface defining operations for meal data management
 */
interface MealRepository {
    suspend fun insertMeal(meal: Meal): Long
    suspend fun updateMeal(meal: Meal)
    suspend fun deleteMeal(meal: Meal)
    fun getMealById(mealId: Long): Flow<Meal?>
    fun getAllMealsByUser(userId: Long): Flow<List<Meal>>
    fun getMealsByDate(userId: Long, date: Date): Flow<List<Meal>>
    suspend fun getDailyNutritionSummary(userId: Long, date: Date): NutritionSummary
}

/**
 * Implementation of MealRepository using Room database
 */
class MealRepositoryImpl(
    private val mealDao: MealDao
) : MealRepository {

    override suspend fun insertMeal(meal: Meal): Long {
        return mealDao.insertMeal(meal.toEntity())
    }

    override suspend fun updateMeal(meal: Meal) {
        mealDao.updateMeal(meal.toEntity())
    }

    override suspend fun deleteMeal(meal: Meal) {
        mealDao.deleteMeal(meal.toEntity())  // Викликаємо новий метод DAO
    }

    override fun getMealById(mealId: Long): Flow<Meal?> =
        mealDao.getMealById(mealId).map { it?.toModel() }

    override fun getAllMealsByUser(userId: Long): Flow<List<Meal>> {
        return mealDao.getAllMealsByUser(userId).map { entities ->
            entities.map { it.toModel() }
        }
    }

    override fun getMealsByDate(userId: Long, date: Date): Flow<List<Meal>> {
        return mealDao.getMealsByDate(userId, date).map { entities ->
            entities.map { it.toModel() }
        }
    }

    override suspend fun getDailyNutritionSummary(userId: Long, date: Date): NutritionSummary {
        return mealDao.getDailyNutritionSummary(userId, date).let {
            NutritionSummary(
                calories = it.calories,
                protein = it.protein,
                carbs = it.carbs,
                fat = it.fat
            )
        }
    }
}


/* Extension functions for model-entity conversion */
private fun Meal.toEntity(): MealEntity = MealEntity(
    id = id,
    name = name,
    calories = calories,
    protein = protein,
    carbs = carbs,
    fat = fat,
    dateTime = dateTime,
    userId = userId,
    mealType = mealType
)

private fun MealEntity.toModel(): Meal = Meal(
    id = id,
    name = name,
    calories = calories,
    protein = protein,
    carbs = carbs,
    fat = fat,
    dateTime = dateTime,
    userId = userId,
    mealType = mealType
)