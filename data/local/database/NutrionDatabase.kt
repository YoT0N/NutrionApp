package com.example.lab5.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.lab5.data.local.dao.MealDao
import com.example.lab5.data.local.dao.NutritionGoalDao
import com.example.lab5.data.local.dao.UserDao
import com.example.lab5.data.local.entities.MealEntity
import com.example.lab5.data.local.entities.NutritionGoalEntity
import com.example.lab5.data.local.entities.UserEntity
import com.example.lab5.utils.Converters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Database(
    entities = [
        UserEntity::class,
        MealEntity::class,
        NutritionGoalEntity::class
    ],
    version = 5,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class NutritionDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun mealDao(): MealDao
    abstract fun nutritionGoalDao(): NutritionGoalDao

    companion object {
        @Volatile
        private var INSTANCE: NutritionDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
        ): NutritionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NutritionDatabase::class.java,
                    "nutrition_database.db"
                )
                    .addCallback(NutritionDatabaseCallback(scope))
                    .addMigrations(*DatabaseMigrations.getAllMigrations())
                    .fallbackToDestructiveMigrationFrom(1, 2) // For development only
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class NutritionDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        prepopulateDatabase(database.userDao())
                    }
                }
            }
        }

        private suspend fun prepopulateDatabase(userDao: UserDao) {
            val defaultUser = UserEntity(
                email = "test@example.com",
                password = "test123",
                name = "Test User"
            )
            userDao.insertUser(defaultUser)
        }
    }
}