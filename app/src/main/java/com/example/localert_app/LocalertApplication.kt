package com.example.localert_app

import android.app.Application
import com.example.localert_app.util.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import com.example.localert_app.data.database.LocalertDatabase

@HiltAndroidApp
class LocalertApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize the database
        LocalertDatabase.getDatabase(this)
        NotificationHelper.createNotificationChannels(this)
    }
} 