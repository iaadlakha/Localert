package com.example.localert_app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "location_patterns")
data class LocationPattern(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val frequency: Int = 1,
    val lastUpdated: Date = Date()
) 