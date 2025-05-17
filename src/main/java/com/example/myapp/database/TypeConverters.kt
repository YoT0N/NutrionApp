package com.example.myapp.database

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromList(value: List<String>?): String? {
        return if (value == null) null else Json.encodeToString(value)
    }

    @TypeConverter
    fun toList(value: String?): List<String>? {
        return if (value == null) null else try {
            Json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }
}