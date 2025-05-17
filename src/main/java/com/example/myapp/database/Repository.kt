package com.example.myapp.database
import kotlinx.coroutines.flow.Flow

open class MealRepository(private val mealDao: MealDao) {
    val allMeals: Flow<List<Meal>> = mealDao.getAllMeals()

    suspend fun insert(meal: Meal): Long = mealDao.insert(meal)

    suspend fun update(meal: Meal) = mealDao.update(meal)

    suspend fun delete(meal: Meal) = mealDao.delete(meal)

    suspend fun getMealById(mealId: Long): Meal? = mealDao.getMealById(mealId)

    fun getMealsByDate(userId: Long, startDate: Long, endDate: Long): Flow<List<Meal>> {
        return mealDao.getMealsByDate(userId, startDate, endDate)
    }
}

open class UserRepository(private val userDao: UserDao) {
    suspend fun insert(user: User) = userDao.insert(user)

    suspend fun updateUser(user: User) = userDao.update(user)

    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)

    suspend fun getUserById(id: Long): User? = userDao.getUserById(id)

}