package com.example.localert_app.util

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

class DateConverter {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    // Convert between Date and Long (for Room database)
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    // Convert between LocalDateTime and String (for Room database)
    @TypeConverter
    fun fromLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, formatter) }
    }

    @TypeConverter
    fun localDateTimeToString(date: LocalDateTime?): String? {
        return date?.format(formatter)
    }

    // Convert between Date and LocalDateTime
    fun dateToLocalDateTime(date: Date?): LocalDateTime? {
        return date?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
    }

    fun localDateTimeToDate(localDateTime: LocalDateTime?): Date? {
        return localDateTime?.atZone(ZoneId.systemDefault())?.toInstant()?.let { Date(it.toEpochMilli()) }
    }

    // Format date for display
    fun formatDateForDisplay(date: Date): String {
        return java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault())
            .format(date)
    }

    fun formatLocalDateTimeForDisplay(localDateTime: LocalDateTime): String {
        return localDateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
    }
} 