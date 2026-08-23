package com.aetheris.android

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AetherisApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            val alertsChannel = NotificationChannel(
                CHANNEL_ALERTS,
                "Server Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical server and billing alerts"
            }

            val statusChannel = NotificationChannel(
                CHANNEL_STATUS,
                "Status Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Server status change notifications"
            }

            manager.createNotificationChannel(alertsChannel)
            manager.createNotificationChannel(statusChannel)
        }
    }

    companion object {
        const val CHANNEL_ALERTS = "aetheris_alerts"
        const val CHANNEL_STATUS = "aetheris_status"
    }
}
