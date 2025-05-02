package com.example.localert_app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "location_history")
data class LocationHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val timestamp: LocalDateTime,
    val placeName: String?,
    val activityType: String? // e.g., "walking", "driving", "stationary"
)

@Entity(tableName = "location_patterns")
data class LocationPattern(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startLatitude: Double,
    val startLongitude: Double,
    val endLatitude: Double,
    val endLongitude: Double,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val dayOfWeek: Int,
    val confidence: Float, // How confident we are about this pattern
    val reminderText: String? // The reminder associated with this pattern
) 