package com.example.lab5.repository

// app/src/main/java/com/example/lab5/data/repository/Repository.kt

import androidx.annotation.WorkerThread
import com.example.lab5.data.local.dao.MealDao
import com.example.lab5.data.local.dao.NutritionGoalDao
import com.example.lab5.data.local.dao.UserDao
import com.example.lab5.data.local.entities.MealEntity
import com.example.lab5.data.local.entities.NutritionGoalEntity
import com.example.lab5.data.local.entities.UserEntity
import com.example.lab5.data.model.Meal
import com.example.lab5.data.model.NutritionGoal
import com.example.lab5.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.firstOrNull
import java.util.*

/**
 * Базовий інтерфейс для всіх репозиторіїв
 */
interface BaseRepository<T> {
    suspend fun insert(item: T): Long
    suspend fun update(item: T)
    suspend fun delete(item: T)
    fun getById(id: Long): Flow<T?>
}

/**
 * Репозиторій для роботи з користувачами
 */
interface UserRepository : BaseRepository<User> {
    suspend fun getUserByEmail(email: String): User?
    suspend fun authenticate(email: String, password: String): Boolean
}

/**
 * Репозиторій для роботи з прийомами їжі
 */
interface MealRepository {
    suspend fun insertMeal(meal: Meal): Long
    suspend fun updateMeal(meal: Meal)
    suspend fun deleteMeal(meal: Meal)
    fun getMealById(mealId: Long): Flow<Meal?>
    fun getAllMealsByUser(userId: Long): Flow<List<Meal>>
    fun getMealsByDate(userId: Long, date: Date): Flow<List<Meal>>
}

/**
 * Репозиторій для роботи з цілями харчування
 */
interface NutritionRepository : BaseRepository<NutritionGoal> {
    fun getActiveGoal(userId: Long): Flow<NutritionGoal?>
}

/**
 * Реалізація UserRepository
 */
class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {

    @WorkerThread
    override suspend fun insert(user: User): Long {
        return userDao.insertUser(user.toEntity())
    }

    @WorkerThread
    override suspend fun update(user: User) {
        userDao.updateUser(user.toEntity())
    }

    @WorkerThread
    override suspend fun delete(user: User) {
        userDao.deleteUser(user.toEntity())
    }

    override fun getById(id: Long): Flow<User?> {
        return userDao.getUserById(id).map { it?.toModel() }
    }

    @WorkerThread
    override suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
            .firstOrNull()  // Отримуємо перше значення з Flow
            ?.toModel()     // Конвертуємо Entity в Model
    }

    @WorkerThread
    override suspend fun authenticate(email: String, password: String): Boolean {
        return userDao.authenticateUser(email, password) > 0
    }
}

/**
 * Реалізація MealRepository
 */
class MealRepositoryImpl(
    private val mealDao: MealDao
) : MealRepository {

    @WorkerThread
    override suspend fun insertMeal(meal: Meal): Long {
        return mealDao.insertMeal(meal.toEntity())
    }

    @WorkerThread
    override suspend fun updateMeal(meal: Meal) {
        mealDao.updateMeal(meal.toEntity())
    }

    @WorkerThread
    override suspend fun deleteMeal(meal: Meal) {
        mealDao.deleteMeal(meal.toEntity())
    }

    override fun getMealById(mealId: Long): Flow<Meal?> {
        return mealDao.getMealById(mealId).map { it?.toModel() }
    }

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
}

/**
 * Реалізація NutritionRepository
 */
class NutritionRepositoryImpl(
    private val nutritionGoalDao: NutritionGoalDao
) : NutritionRepository {

    @WorkerThread
    override suspend fun insert(goal: NutritionGoal): Long {
        return nutritionGoalDao.insertOrUpdateGoal(goal.toEntity())
    }

    @WorkerThread
    override suspend fun update(goal: NutritionGoal) {
        nutritionGoalDao.updateGoal(goal.toEntity())
    }

    @WorkerThread
    override suspend fun delete(goal: NutritionGoal) {
        nutritionGoalDao.deleteGoal(goal.toEntity())
    }

    override fun getById(id: Long): Flow<NutritionGoal?> {
        return nutritionGoalDao.getGoalById(id).map { it?.toModel() }
    }

    override fun getActiveGoal(userId: Long): Flow<NutritionGoal?> {
        return nutritionGoalDao.getActiveGoalByUser(userId).map { it?.toModel() }
    }
}

private fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    email = email,
    password = password,
    name = name,
    birthDate = birthDate,
    height = height,
    weight = weight,
    gender = gender,
    createdAt = createdAt ?: Date()
)

private fun UserEntity.toModel(): User = User(
    id = id,
    email = email,
    password = password,
    name = name,
    birthDate = birthDate,
    height = height,
    weight = weight,
    gender = gender,
    createdAt = createdAt
)

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