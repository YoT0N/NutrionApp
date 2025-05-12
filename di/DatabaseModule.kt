package com.example.lab5.di

import android.content.Context
import androidx.room.Room
import com.example.lab5.data.local.database.NutritionDatabase
import com.example.lab5.data.local.dao.MealDao
import com.example.lab5.data.local.dao.NutritionGoalDao
import com.example.lab5.data.local.dao.UserDao
import com.example.lab5.data.repository.MealRepository
import com.example.lab5.data.repository.MealRepositoryImpl
import com.example.lab5.data.repository.NutritionRepository
import com.example.lab5.data.repository.NutritionRepositoryImpl
import com.example.lab5.data.repository.UserRepository
import com.example.lab5.data.repository.UserRepositoryImpl

object DatabaseModule {
    // Lazy initialization of database
    private lateinit var database: NutritionDatabase

    /**
     * Initialize database module (call this in Application class)
     * @param context Application context
     * @param useInMemory For testing purposes only
     */
    fun init(context: Context, useInMemory: Boolean = false) {
        database = if (useInMemory) {
            Room.inMemoryDatabaseBuilder(context, NutritionDatabase::class.java)
                .fallbackToDestructiveMigration()
                .build()
        } else {
            Room.databaseBuilder(
                context,
                NutritionDatabase::class.java,
                "nutrition.db"
            )
                .fallbackToDestructiveMigration() // Remove in production!
                .build()
        }
    }

    // Database access
    fun getDatabase(): NutritionDatabase {
        check(::database.isInitialized) { "DatabaseModule not initialized!" }
        return database
    }

    // DAOs providers
    fun provideUserDao(): UserDao = getDatabase().userDao()
    fun provideMealDao(): MealDao = getDatabase().mealDao()
    fun provideNutritionGoalDao(): NutritionGoalDao = getDatabase().nutritionGoalDao()

    // Repository providers
    fun provideUserRepository(): UserRepository = UserRepositoryImpl(provideUserDao())
    fun provideMealRepository(): MealRepository = MealRepositoryImpl(provideMealDao())
    fun provideNutritionRepository(): NutritionRepository =
        NutritionRepositoryImpl(provideNutritionGoalDao())
}