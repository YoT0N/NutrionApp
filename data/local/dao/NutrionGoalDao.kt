package com.example.lab5.data.local.dao

import androidx.room.*
import com.example.lab5.data.local.entities.DailyNutritionSummaryEntity
import com.example.lab5.data.local.entities.NutritionGoalEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Data Access Object (DAO) for nutrition goals operations in the database.
 */
@Dao
interface NutritionGoalDao {

    // Create/Update
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateGoal(goal: NutritionGoalEntity): Long

    // Read
    @Query("SELECT * FROM nutrition_goals WHERE user_id = :userId AND is_active = 1 LIMIT 1")
    fun getActiveGoalByUser(userId: Long): Flow<NutritionGoalEntity?>

    @Query("SELECT * FROM nutrition_goals WHERE user_id = :userId ORDER BY id DESC")
    fun getAllGoalsByUser(userId: Long): Flow<List<NutritionGoalEntity>>

    @Query("SELECT * FROM nutrition_goals WHERE id = :goalId")
    fun getGoalById(goalId: Long): Flow<NutritionGoalEntity?>


    // Update
    @Update
    suspend fun updateGoal(goal: NutritionGoalEntity)

    @Query("UPDATE nutrition_goals SET is_active = 0 WHERE user_id = :userId")
    suspend fun deactivateAllUserGoals(userId: Long)

    // Delete
    @Delete
    suspend fun deleteGoal(goal: NutritionGoalEntity)

    @Query("DELETE FROM nutrition_goals WHERE id = :goalId")
    suspend fun deleteGoalById(goalId: Long)

    @Query("""
    SELECT 
        SUM(calories) as calories,
        SUM(protein) as protein,
        SUM(carbs) as carbs,
        SUM(fat) as fat
    FROM meals
    WHERE user_id = :userId 
    AND date(date_time/1000, 'unixepoch') = date(:date/1000, 'unixepoch')
""")
    suspend fun getDailyNutritionSummary(userId: Long, date: Date): DailyNutritionSummaryEntity
}