package com.example.localert_app.service

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.localert_app.R
import com.example.localert_app.data.entity.Reminder
import com.example.localert_app.data.repository.ReminderRepository
import com.example.localert_app.util.NotificationHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Singleton
class AlarmService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NotificationHelper.ALARM_CHANNEL_ID,
                "Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for time-based reminders"
                enableVibration(true)
                enableLights(true)
                setSound(getRingtoneUri(), null)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getRingtoneUri(): Uri {
        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val savedRingtoneUri = sharedPreferences.getString("ringtone_uri", null)
        return if (savedRingtoneUri != null) {
            Uri.parse(savedRingtoneUri)
        } else {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }
    }

    fun setAlarm(reminder: Reminder) {
        val reminderId = reminder.id ?: return
        val alarmTime = reminder.createdAt.time

        if (alarmTime <= System.currentTimeMillis()) {
            Log.w("AlarmService", "Cannot set alarm for past time")
            return
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("reminder_id", reminderId)
            putExtra("title", reminder.title)
            putExtra("message", reminder.message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        alarmTime,
                        pendingIntent
                    )
                    Log.i("AlarmService", "Alarm set successfully for reminder: ${reminder.title}")
                } else {
                    Log.e("AlarmService", "Cannot schedule exact alarms")
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    alarmTime,
                    pendingIntent
                )
                Log.i("AlarmService", "Alarm set successfully for reminder: ${reminder.title}")
            }
        } catch (e: SecurityException) {
            Log.e("AlarmService", "Failed to set alarm", e)
        }
    }

    fun cancelAlarm(reminderId: Long) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.cancel(pendingIntent)
            Log.i("AlarmService", "Alarm cancelled for reminder ID: $reminderId")
        } catch (e: Exception) {
            Log.e("AlarmService", "Failed to cancel alarm", e)
        }
    }

    companion object {
        const val CHANNEL_ID = "alarm_channel"
    }
} 