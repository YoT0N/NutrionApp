package com.example.lab5.data.local.dao


import androidx.room.*
import com.example.lab5.data.local.entities.MealEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.Date

/**
 * Data Access Object (DAO) for meal operations in the database.
 */
@Dao
interface MealDao {

    // Create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMeals(meals: List<MealEntity>)

    // Read
    @Query("SELECT * FROM meals WHERE id = :mealId")
    fun getMealById(mealId: Long): Flow<MealEntity?>

    @Query("SELECT * FROM meals WHERE user_id = :userId ORDER BY date_time DESC")
    fun getAllMealsByUser(userId: Long): Flow<List<MealEntity>>

    @Query("""
        SELECT * FROM meals 
        WHERE user_id = :userId 
        AND date(date_time) = date(:date)
        ORDER BY date_time DESC
    """)
    fun getMealsByDate(userId: Long, date: Date): Flow<List<MealEntity>>

    @Query("""
        SELECT SUM(calories) as calories, 
               SUM(protein) as protein, 
               SUM(carbs) as carbs, 
               SUM(fat) as fat 
        FROM meals 
        WHERE user_id = :userId 
        AND date(date_time) = date(:date)
    """)
    suspend fun getDailyNutritionSummary(userId: Long, date: Date): DailyNutritionSummary

    // Update
    @Update
    suspend fun updateMeal(meal: MealEntity)

    // Delete
    @Delete
    suspend fun deleteMeal(meal: MealEntity)

    @Query("DELETE FROM meals WHERE id = :mealId")
    suspend fun deleteMealById(mealId: Long)

    @Query("DELETE FROM meals WHERE user_id = :userId")
    suspend fun deleteAllMealsForUser(userId: Long)
}

/**
 * Data class for daily nutrition summary.
 */
data class DailyNutritionSummary(
    val calories: Float = 0f,
    val protein: Float = 0f,
    val carbs: Float = 0f,
    val fat: Float = 0f
)