package com.example.lab5.utils

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Type converters for Room database with backward compatibility.
 */
class Converters {

    // Date format for backward compatibility
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    // LocalDate converters (API < 26)
    @TypeConverter
    fun fromDateString(value: String?): Date? {
        return value?.let { dateFormat.parse(it) }
    }

    @TypeConverter
    fun dateToDateString(date: Date?): String? {
        return date?.let { dateFormat.format(it) }
    }

    // LocalDateTime converters (API < 26)
    @TypeConverter
    fun fromTimestamp(value: String?): Date? {
        return value?.let { dateTimeFormat.parse(it) }
    }

    @TypeConverter
    fun dateTimeToTimestamp(date: Date?): String? {
        return date?.let { dateTimeFormat.format(it) }
    }
}
