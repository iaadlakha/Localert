package com.example.localert_app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.localert_app.R
import com.example.localert_app.data.entity.Reminder
import com.example.localert_app.data.repository.ReminderRepository
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GeofenceService : Service() {
    private val geofencingClient: GeofencingClient by lazy {
        LocationServices.getGeofencingClient(this)
    }
    private val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @Inject
    lateinit var reminderRepository: ReminderRepository

    init {
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Geofence Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for location-based reminders"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun addGeofence(reminder: Reminder) {
        val reminderId = reminder.id ?: return // Return early if id is null
        
        val geofence = Geofence.Builder()
            .setRequestId(reminderId.toString())
            .setCircularRegion(
                reminder.latitude ?: 0.0,
                reminder.longitude ?: 0.0,
                reminder.radius ?: 100f
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val intent = Intent(this, GeofenceBroadcastReceiver::class.java).apply {
            putExtra("reminder_id", reminderId)
            putExtra("title", reminder.title)
            putExtra("message", reminder.message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
    }

    fun removeGeofence(reminderId: Long) {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        geofencingClient.removeGeofences(pendingIntent)
    }

    companion object {
        const val CHANNEL_ID = "geofence_channel"
    }
} 