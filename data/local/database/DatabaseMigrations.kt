package com.example.lab5.data.local.database

// app/src/main/java/com/example/nutritionapp/data/local/database/DatabaseMigrations.kt

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Contains all database migrations for the NutritionApp.
 * Each migration represents a change in database schema between versions.
 */
object DatabaseMigrations {

    // Migration from version 1 to 2
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add new 'meals' table
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS `meals` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `name` TEXT NOT NULL,
                    `calories` REAL NOT NULL,
                    `protein` REAL NOT NULL,
                    `carbs` REAL NOT NULL,
                    `fat` REAL NOT NULL,
                    `date_time` INTEGER NOT NULL,
                    `user_id` INTEGER
                )
            """)
        }
    }

    // Migration from version 2 to 3
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add nutrition goals table
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS `nutrition_goals` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `user_id` INTEGER NOT NULL DEFAULT 0,
                    `daily_calories` REAL NOT NULL,
                    `daily_protein` REAL NOT NULL,
                    `daily_carbs` REAL NOT NULL,
                    `daily_fat` REAL NOT NULL,
                    `is_active` INTEGER NOT NULL DEFAULT 1
                )
            """)
        }
    }

    // Migration from version 3 to 4
    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add user height and weight columns
            database.execSQL("""
                ALTER TABLE `users` 
                ADD COLUMN `height` INTEGER
            """)
            database.execSQL("""
                ALTER TABLE `users` 
                ADD COLUMN `weight` REAL
            """)
        }
    }

    // Migration from version 4 to 5
    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add index for better performance on user queries
            database.execSQL("""
                CREATE INDEX `index_users_email` ON `users` (`email`)
            """)
            database.execSQL("""
                CREATE INDEX `index_meals_user_id` ON `meals` (`user_id`)
            """)
        }
    }

    // Helper function to get all migrations
    fun getAllMigrations(): Array<Migration> {
        return arrayOf(
            MIGRATION_1_2,
            MIGRATION_2_3,
            MIGRATION_3_4,
            MIGRATION_4_5
        )
    }
}