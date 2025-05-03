package com.example.localert_app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.localert_app.R
import com.example.localert_app.data.repository.ReminderRepository
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GeofenceBroadcastReceiver : BroadcastReceiver() {
    @Inject
    lateinit var reminderRepository: ReminderRepository

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("GeofenceReceiver", "onReceive called")
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent == null) {
            Log.e("GeofenceReceiver", "GeofencingEvent is null")
            return
        }
        if (geofencingEvent.hasError()) {
            Log.e("GeofenceReceiver", "Geofencing error: ${geofencingEvent.errorCode}")
            return
        }
        Log.d("GeofenceReceiver", "Transition: ${geofencingEvent.geofenceTransition}")
        if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            geofencingEvent.triggeringGeofences?.forEach { geofence ->
                val requestId = geofence.requestId.toLongOrNull() ?: run {
                    Log.e("GeofenceReceiver", "Invalid requestId: ${geofence.requestId}")
                    return@forEach
                }
                Log.d("GeofenceReceiver", "Triggered geofence with requestId: $requestId")
                CoroutineScope(Dispatchers.IO).launch {
                    val reminder = reminderRepository.getReminderById(requestId)
                    if (reminder != null) {
                        Log.d("GeofenceReceiver", "Found reminder: ${reminder.title}")
                        val notification = NotificationCompat.Builder(context, GeofenceService.CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_notification)
                            .setContentTitle(reminder.title)
                            .setContentText(reminder.message)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setAutoCancel(true)
                            .build()
                        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
                        notificationManager.notify(requestId.toInt(), notification)
                        Log.d("GeofenceReceiver", "Notification sent for reminder: ${reminder.title}")
                    } else {
                        Log.e("GeofenceReceiver", "No reminder found for id: $requestId")
                    }
                }
            }
        } else {
            Log.d("GeofenceReceiver", "Transition was not ENTER: ${geofencingEvent.geofenceTransition}")
        }
    }
} 