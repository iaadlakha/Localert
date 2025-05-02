package com.example.localert_app.ui.viewmodel

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _currentRingtone = MutableStateFlow<String?>(null)
    val currentRingtone: StateFlow<String?> = _currentRingtone

    init {
        loadSavedRingtone()
    }

    fun setRingtoneUri(uri: Uri) {
        val ringtone = RingtoneManager.getRingtone(context, uri)
        _currentRingtone.value = ringtone.getTitle(context)
        // Save the ringtone URI to SharedPreferences
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .edit()
            .putString("ringtone_uri", uri.toString())
            .apply()
    }

    fun getCurrentRingtoneUri(): Uri? {
        val uriString = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getString("ringtone_uri", null)
        return uriString?.let { Uri.parse(it) }
    }

    private fun loadSavedRingtone() {
        val uriString = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getString("ringtone_uri", null)
        
        if (uriString != null) {
            try {
                val uri = Uri.parse(uriString)
                val ringtone = RingtoneManager.getRingtone(context, uri)
                _currentRingtone.value = ringtone.getTitle(context)
            } catch (e: Exception) {
                // If there's an error loading the saved ringtone, use the default
                _currentRingtone.value = null
            }
        }
    }
} 