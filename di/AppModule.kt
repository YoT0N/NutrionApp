package com.example.lab5.di
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.lab5.data.local.database.NutritionDatabase
import com.example.lab5.data.local.dao.MealDao
import com.example.lab5.data.local.dao.NutritionGoalDao
import com.example.lab5.data.local.dao.UserDao
import com.example.lab5.data.repository.*

object AppDependencies {

    // Database instance (lazy initialization)
    private lateinit var database: NutritionDatabase

    // Initialize all dependencies (call this in Application class)
    fun initialize(context: Context) {
        database = Room.databaseBuilder(
            context,
            NutritionDatabase::class.java,
            "nutrition_db"
        )
            .fallbackToDestructiveMigration() // Only for development!
            .build()
    }

    // DAOs
    val userDao: UserDao get() = database.userDao()
    val mealDao: MealDao get() = database.mealDao()
    val nutritionGoalDao: NutritionGoalDao get() = database.nutritionGoalDao()

    // Repositories
    val userRepository: UserRepository by lazy { UserRepositoryImpl(userDao) }
    val mealRepository: MealRepository by lazy { MealRepositoryImpl(mealDao) }
    val nutritionRepository: NutritionRepository by lazy { NutritionRepositoryImpl(nutritionGoalDao) }

    // SharedPreferences
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }
}
