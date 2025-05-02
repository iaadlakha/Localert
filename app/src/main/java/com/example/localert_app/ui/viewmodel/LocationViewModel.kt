package com.example.localert_app.ui.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.localert_app.data.entity.Reminder
import com.example.localert_app.data.repository.ReminderRepository
import com.example.localert_app.service.LocationTrackingWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    application: Application,
    private val reminderRepository: ReminderRepository
) : AndroidViewModel(application) {
    private val TAG = "LocalertApp"
    private val workManager = WorkManager.getInstance(application)

    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders

    init {
        try {
            Log.i(TAG, "LocationViewModel: Initializing")
            if (hasLocationPermission()) {
                Log.i(TAG, "LocationViewModel: Permissions granted")
                startLocationTracking()
                loadReminders()
            } else {
                Log.w(TAG, "LocationViewModel: Permissions not granted")
            }
        } catch (e: Exception) {
            Log.e(TAG, "LocationViewModel: Init error", e)
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            getApplication(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startLocationTracking() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val locationWorkRequest = PeriodicWorkRequestBuilder<LocationTrackingWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "location_tracking",
            ExistingPeriodicWorkPolicy.KEEP,
            locationWorkRequest
        )
    }

    private fun loadReminders() {
        viewModelScope.launch {
            reminderRepository.getAllReminders().collect { remindersList ->
                _reminders.value = remindersList
            }
        }
    }

    fun addReminder(title: String, message: String, latitude: Double, longitude: Double, radius: Float) {
        viewModelScope.launch {
            try {
                Log.i(TAG, "LocationViewModel: Adding new reminder: $title")
                val reminder = Reminder(
                    title = title,
                    message = message,
                    latitude = latitude,
                    longitude = longitude,
                    radius = radius,
                    isLocationBased = true
                )
                reminderRepository.insertReminder(reminder)
                Log.i(TAG, "LocationViewModel: Successfully added reminder: $title")
            } catch (e: Exception) {
                Log.e(TAG, "LocationViewModel: Error adding reminder", e)
            }
        }
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            try {
                Log.i(TAG, "LocationViewModel: Deleting reminder: ${reminder.id}")
                reminderRepository.deleteReminder(reminder)
                Log.i(TAG, "LocationViewModel: Successfully deleted reminder: ${reminder.id}")
            } catch (e: Exception) {
                Log.e(TAG, "LocationViewModel: Error deleting reminder", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            Log.i(TAG, "LocationViewModel: Clearing")
            workManager.cancelUniqueWork("location_tracking")
            Log.i(TAG, "LocationViewModel: Location tracking work cancelled")
        } catch (e: Exception) {
            Log.e(TAG, "LocationViewModel: Error cancelling location tracking work", e)
        }
    }
} 