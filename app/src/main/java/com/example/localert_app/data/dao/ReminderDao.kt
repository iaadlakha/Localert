package com.example.localert_app.data.dao

import androidx.room.*
import com.example.localert_app.data.entity.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY createdAt DESC")
    fun getAllReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderById(id: Long): Reminder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder): Long

    @Update
    suspend fun updateReminder(reminder: Reminder)

    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    @Query("SELECT * FROM reminders WHERE isLocationBased = 1 ORDER BY createdAt DESC")
    fun getLocationBasedReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getActiveReminders(): Flow<List<Reminder>>

    @Query("UPDATE reminders SET isActive = 0 WHERE id = :reminderId")
    suspend fun deactivateReminder(reminderId: Long)
} 