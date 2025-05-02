package com.example.localert_app.data.repository

import android.util.Log
import com.example.localert_app.data.dao.ReminderDao
import com.example.localert_app.data.entity.Reminder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepository @Inject constructor(
    private val reminderDao: ReminderDao
) {
    fun getAllReminders(): Flow<List<Reminder>> {
        Log.d("ReminderRepository", "Getting all reminders")
        return reminderDao.getAllReminders()
    }

    suspend fun getReminderById(id: Long): Reminder? {
        Log.d("ReminderRepository", "Getting reminder by id: $id")
        return reminderDao.getReminderById(id)
    }

    suspend fun insertReminder(reminder: Reminder): Long {
        Log.d("ReminderRepository", "Inserting reminder: ${reminder.title}")
        return try {
            val id = reminderDao.insertReminder(reminder)
            Log.d("ReminderRepository", "Successfully inserted reminder with id: $id")
            id
        } catch (e: Exception) {
            Log.e("ReminderRepository", "Error inserting reminder", e)
            throw e
        }
    }

    suspend fun updateReminder(reminder: Reminder) {
        Log.d("ReminderRepository", "Updating reminder: ${reminder.title}")
        try {
            reminderDao.updateReminder(reminder)
            Log.d("ReminderRepository", "Successfully updated reminder")
        } catch (e: Exception) {
            Log.e("ReminderRepository", "Error updating reminder", e)
            throw e
        }
    }

    suspend fun deleteReminder(reminder: Reminder) {
        Log.d("ReminderRepository", "Deleting reminder: ${reminder.title}")
        try {
            reminderDao.deleteReminder(reminder)
            Log.d("ReminderRepository", "Successfully deleted reminder")
        } catch (e: Exception) {
            Log.e("ReminderRepository", "Error deleting reminder", e)
            throw e
        }
    }

    fun getLocationBasedReminders(): Flow<List<Reminder>> {
        Log.d("ReminderRepository", "Getting location-based reminders")
        return reminderDao.getLocationBasedReminders()
    }

    fun getActiveReminders(): Flow<List<Reminder>> {
        Log.d("ReminderRepository", "Getting active reminders")
        return reminderDao.getActiveReminders()
    }

    suspend fun deactivateReminder(reminderId: Long) {
        Log.d("ReminderRepository", "Deactivating reminder: $reminderId")
        try {
            reminderDao.deactivateReminder(reminderId)
            Log.d("ReminderRepository", "Successfully deactivated reminder")
        } catch (e: Exception) {
            Log.e("ReminderRepository", "Error deactivating reminder", e)
            throw e
        }
    }
} 