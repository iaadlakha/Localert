package com.example.localert_app.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.example.localert_app.R

object NotificationHelper {
    const val ALARM_CHANNEL_ID = "alarm_channel"
    const val GEOFENCE_CHANNEL_ID = "geofence_channel"
    const val LOCATION_CHANNEL_ID = "location_channel"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Alarm Channel
            val alarmChannel = NotificationChannel(
                ALARM_CHANNEL_ID,
                "Time Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for time-based reminders"
                enableVibration(true)
                enableLights(true)
            }

            // Geofence Channel
            val geofenceChannel = NotificationChannel(
                GEOFENCE_CHANNEL_ID,
                "Location Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for location-based reminders"
                enableVibration(true)
                enableLights(true)
            }

            // Location Tracking Channel
            val locationChannel = NotificationChannel(
                LOCATION_CHANNEL_ID,
                "Location Tracking",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for location tracking service"
                enableVibration(false)
                enableLights(false)
            }

            try {
                notificationManager.createNotificationChannels(listOf(alarmChannel, geofenceChannel, locationChannel))
                Log.i("NotificationHelper", "Notification channels created successfully")
            } catch (e: Exception) {
                Log.e("NotificationHelper", "Error creating notification channels", e)
            }
        }
    }
} 