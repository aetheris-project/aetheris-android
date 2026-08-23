package com.aetheris.android.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.aetheris.android.AetherisApp
import com.aetheris.android.MainActivity
import com.aetheris.android.R
import kotlinx.coroutines.*

class NotificationService : Service() {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startMonitoring()
            ACTION_STOP -> stopMonitoring()
        }
        return START_STICKY
    }

    private fun startMonitoring() {
        startForeground(NOTIFICATION_ID, createNotification("Monitoring server status..."))

        scope.launch {
            while (isActive) {
                // Poll server status and send notifications
                // In production, this would check the API and compare with cached state
                delay(30_000L) // Check every 30 seconds
            }
        }
    }

    private fun stopMonitoring() {
        scope.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotification(text: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, AetherisApp.CHANNEL_STATUS)
            .setContentTitle("Aetheris Monitor")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_splash)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    companion object {
        const val ACTION_START = "com.aetheris.android.MONITOR_START"
        const val ACTION_STOP = "com.aetheris.android.MONITOR_STOP"
        private const val NOTIFICATION_ID = 1002
    }
}
