package com.example.localert_app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val message: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val radius: Float? = null,
    val isLocationBased: Boolean = false,
    val isActive: Boolean = true,
    val createdAt: Date = Date()
) 