package com.example.localert_app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.example.localert_app.data.entity.Reminder
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeofenceService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Geofence Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for location-based reminders"
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun addGeofence(reminder: Reminder) {
        val reminderId = reminder.id ?: return
        val latitude = reminder.latitude ?: return
        val longitude = reminder.longitude ?: return
        val radius = reminder.radius ?: return

        val geofence = Geofence.Builder()
            .setRequestId(reminderId.toString())
            .setCircularRegion(latitude, longitude, radius)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        try {
            geofencingClient.addGeofences(geofencingRequest, getGeofencePendingIntent())
                .addOnSuccessListener {
                    Log.d("GeofenceService", "Geofence added successfully for reminder: ${reminder.title}")
                }
                .addOnFailureListener { e ->
                    Log.e("GeofenceService", "Error adding geofence", e)
                }
        } catch (e: SecurityException) {
            Log.e("GeofenceService", "Location permission not granted", e)
        }
    }

    fun removeGeofence(reminderId: Long) {
        try {
            geofencingClient.removeGeofences(listOf(reminderId.toString()))
                .addOnSuccessListener {
                    Log.d("GeofenceService", "Geofence removed successfully for reminder ID: $reminderId")
                }
                .addOnFailureListener { e ->
                    Log.e("GeofenceService", "Error removing geofence", e)
                }
        } catch (e: SecurityException) {
            Log.e("GeofenceService", "Location permission not granted", e)
        }
    }

    private fun getGeofencePendingIntent() = android.app.PendingIntent.getBroadcast(
        context,
        0,
        android.content.Intent(context, GeofenceBroadcastReceiver::class.java),
        android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_MUTABLE
    )

    companion object {
        const val CHANNEL_ID = "geofence_channel"
    }
} 