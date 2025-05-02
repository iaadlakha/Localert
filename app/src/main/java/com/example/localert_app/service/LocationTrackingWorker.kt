package com.example.localert_app.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.localert_app.R
import com.example.localert_app.data.entity.Reminder
import com.example.localert_app.data.repository.ReminderRepository
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import androidx.hilt.work.HiltWorker
import kotlinx.coroutines.withContext

@HiltWorker
class LocationTrackingWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val reminderRepository: ReminderRepository
) : CoroutineWorker(appContext, workerParams) {

    private val TAG = "LocationTrackingWorker"
    private val CHANNEL_ID = "location_tracking_channel"
    private val NOTIFICATION_ID = 1

    private val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        try {
            Log.i(TAG, "LocationTrackingWorker: Initializing")
            createNotificationChannel()
        } catch (e: Exception) {
            Log.e(TAG, "LocationTrackingWorker: Init error", e)
        }
    }

    private fun createNotificationChannel() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.i(TAG, "LocationTrackingWorker: Creating notification channel")
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Location Tracking",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Notifications for location-based reminders"
                }
                notificationManager.createNotificationChannel(channel)
                Log.i(TAG, "LocationTrackingWorker: Notification channel created")
            }
        } catch (e: Exception) {
            Log.e(TAG, "LocationTrackingWorker: Error creating notification channel", e)
        }
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "LocationTrackingWorker: Starting work")
            
            if (!hasLocationPermission()) {
                Log.w(TAG, "LocationTrackingWorker: Permissions not granted")
                return@withContext Result.failure()
            }

            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    try {
                        locationResult.lastLocation?.let { location ->
                            Log.i(TAG, "LocationTrackingWorker: Location update: ${location.latitude}, ${location.longitude}")
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    reminderRepository.getAllReminders().collect { reminders ->
                                        Log.i(TAG, "LocationTrackingWorker: Checking ${reminders.size} reminders")
                                        reminders.filter { it.isLocationBased && it.isActive }.forEach { reminder ->
                                            try {
                                                reminder.latitude?.let { lat ->
                                                    reminder.longitude?.let { lng ->
                                                        reminder.radius?.let { radius ->
                                                            val distance = calculateDistance(
                                                                location.latitude,
                                                                location.longitude,
                                                                lat,
                                                                lng
                                                            )
                                                            Log.i(TAG, "LocationTrackingWorker: Distance to reminder ${reminder.id}: $distance meters")
                                                            if (distance <= radius) {
                                                                showNotification(reminder)
                                                            }
                                                        }
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                Log.e(TAG, "LocationTrackingWorker: Error processing reminder ${reminder.id}", e)
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e(TAG, "LocationTrackingWorker: Error collecting reminders", e)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "LocationTrackingWorker: Error in location callback", e)
                    }
                }
            }

            val locationRequest = LocationRequest.Builder(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                10000L // intervalMillis
            )
                .setMinUpdateIntervalMillis(5000L)
                .build()

            suspendCancellableCoroutine { continuation ->
                try {
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        applicationContext.mainLooper
                    )
                    Log.i(TAG, "LocationTrackingWorker: Location updates requested")
                    continuation.resume(Result.success())
                } catch (e: SecurityException) {
                    Log.e(TAG, "LocationTrackingWorker: Security exception", e)
                    continuation.resume(Result.failure())
                } catch (e: Exception) {
                    Log.e(TAG, "LocationTrackingWorker: Error requesting location updates", e)
                    continuation.resume(Result.failure())
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "LocationTrackingWorker: Error in doWork", e)
            Result.failure()
        }
    }

    private fun hasLocationPermission(): Boolean {
        return try {
            val fineLocation = ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            
            val backgroundLocation = ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            
            Log.i(TAG, "LocationTrackingWorker: Fine location permission: $fineLocation")
            Log.i(TAG, "LocationTrackingWorker: Background location permission: $backgroundLocation")
            
            fineLocation && backgroundLocation
        } catch (e: Exception) {
            Log.e(TAG, "LocationTrackingWorker: Permission check error", e)
            false
        }
    }

    private fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Float {
        return try {
            val results = FloatArray(1)
            android.location.Location.distanceBetween(lat1, lon1, lat2, lon2, results)
            results[0]
        } catch (e: Exception) {
            Log.e(TAG, "LocationTrackingWorker: Error calculating distance", e)
            Float.MAX_VALUE
        }
    }

    private fun showNotification(reminder: Reminder) {
        try {
            val notificationId = reminder.id.toInt()
            
            val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(reminder.title)
                .setContentText(reminder.message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(notificationId, notification)
            Log.i(TAG, "LocationTrackingWorker: Notification shown for reminder ${reminder.id}")
        } catch (e: Exception) {
            Log.e(TAG, "LocationTrackingWorker: Error showing notification for reminder ${reminder.id}", e)
        }
    }
} 