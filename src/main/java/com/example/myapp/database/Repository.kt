package com.example.myapp.database
import kotlinx.coroutines.flow.Flow

class MealRepository(private val mealDao: MealDao) {
    val allMeals: Flow<List<Meal>> = mealDao.getAllMeals()

    suspend fun insert(meal: Meal): Long = mealDao.insert(meal)

    suspend fun update(meal: Meal) = mealDao.update(meal)

    suspend fun delete(meal: Meal) = mealDao.delete(meal)

    suspend fun getMealById(mealId: Long): Meal? = mealDao.getMealById(mealId)

    fun getMealsByDate(startDate: Long, endDate: Long): Flow<List<Meal>> {
        return mealDao.getMealsByDate(startDate, endDate)
    }
}

class UserRepository(private val userDao: UserDao) {
    suspend fun insert(user: User) = userDao.insert(user)

    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)
}