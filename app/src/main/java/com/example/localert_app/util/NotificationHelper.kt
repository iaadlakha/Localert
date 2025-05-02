package com.example.localert_app.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.localert_app.R

object NotificationHelper {
    const val ALARM_CHANNEL_ID = "alarm_channel"
    const val LOCATION_CHANNEL_ID = "location_channel"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Alarm notifications channel
            val alarmChannel = NotificationChannel(
                ALARM_CHANNEL_ID,
                "Alarm Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for time-based reminders"
            }

            // Location notifications channel
            val locationChannel = NotificationChannel(
                LOCATION_CHANNEL_ID,
                "Location Tracking",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for location-based reminders"
            }

            notificationManager.createNotificationChannels(listOf(alarmChannel, locationChannel))
        }
    }
} 