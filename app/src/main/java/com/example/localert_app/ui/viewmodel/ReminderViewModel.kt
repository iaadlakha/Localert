package com.example.localert_app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localert_app.data.entity.Reminder
import com.example.localert_app.data.repository.ReminderRepository
import com.example.localert_app.service.AlarmService
import com.example.localert_app.service.GeofenceService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val alarmService: AlarmService,
    private val geofenceService: GeofenceService
) : ViewModel() {

    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _editingReminder = MutableStateFlow<Reminder?>(null)
    val editingReminder: StateFlow<Reminder?> = _editingReminder.asStateFlow()

    init {
        loadReminders()
    }

    private fun loadReminders() {
        viewModelScope.launch {
            reminderRepository.getAllReminders()
                .catch { e ->
                    Log.e("ReminderViewModel", "Error loading reminders", e)
                    _error.value = "Failed to load reminders: ${e.message}"
                }
                .collect { remindersList ->
                    Log.d("ReminderViewModel", "Loaded ${remindersList.size} reminders")
                    _reminders.value = remindersList
                }
        }
    }

    fun startEditing(reminder: Reminder) {
        _editingReminder.value = reminder
    }

    fun cancelEditing() {
        _editingReminder.value = null
    }

    fun insertReminder(reminder: Reminder) {
        Log.d("ReminderViewModel", "Attempting to insert reminder: ${reminder.title}")
        viewModelScope.launch {
            try {
                val id = reminderRepository.insertReminder(reminder)
                Log.d("ReminderViewModel", "Successfully inserted reminder with id: $id")
                
                if (reminder.isLocationBased) {
                    geofenceService.addGeofence(reminder.copy(id = id))
                } else {
                    alarmService.setAlarm(reminder.copy(id = id))
                }
            } catch (e: Exception) {
                Log.e("ReminderViewModel", "Error inserting reminder", e)
                _error.value = "Failed to add reminder: ${e.message}"
            }
        }
    }

    fun updateReminder(reminder: Reminder) {
        viewModelScope.launch {
            try {
                reminderRepository.updateReminder(reminder)
                if (reminder.isLocationBased) {
                    geofenceService.removeGeofence(reminder.id)
                    geofenceService.addGeofence(reminder)
                } else {
                    alarmService.cancelAlarm(reminder.id)
                    alarmService.setAlarm(reminder)
                }
                _editingReminder.value = null
                Log.d("ReminderViewModel", "Successfully updated reminder: ${reminder.title}")
            } catch (e: Exception) {
                Log.e("ReminderViewModel", "Error updating reminder", e)
                _error.value = "Failed to update reminder: ${e.message}"
            }
        }
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            try {
                if (reminder.isLocationBased) {
                    reminder.id?.let { geofenceService.removeGeofence(it) }
                } else {
                    reminder.id?.let { alarmService.cancelAlarm(it) }
                }
                reminderRepository.deleteReminder(reminder)
                Log.d("ReminderViewModel", "Successfully deleted reminder: ${reminder.title}")
            } catch (e: Exception) {
                Log.e("ReminderViewModel", "Error deleting reminder", e)
                _error.value = "Failed to delete reminder: ${e.message}"
            }
        }
    }

    fun deactivateReminder(reminderId: Long) {
        viewModelScope.launch {
            try {
                reminderRepository.deactivateReminder(reminderId)
                Log.d("ReminderViewModel", "Successfully deactivated reminder: $reminderId")
            } catch (e: Exception) {
                Log.e("ReminderViewModel", "Error deactivating reminder", e)
                _error.value = "Failed to deactivate reminder: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun getTimeReminders(): Flow<List<Reminder>> = reminders.map { it.filter { !it.isLocationBased } }
    fun getLocationReminders(): Flow<List<Reminder>> = reminders.map { it.filter { it.isLocationBased } }
    fun getActiveReminders(): Flow<List<Reminder>> = reminders.map { it.filter { it.isActive } }
} 