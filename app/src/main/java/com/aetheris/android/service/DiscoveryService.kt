package com.aetheris.android.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.aetheris.android.MainActivity
import com.aetheris.android.R
import kotlinx.coroutines.*

class DiscoveryService : Service() {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startDiscovery()
            ACTION_STOP -> stopDiscovery()
        }
        return START_STICKY
    }

    private fun startDiscovery() {
        startForeground(NOTIFICATION_ID, createNotification("Scanning for panels on your network..."))

        scope.launch {
            // Periodic LAN discovery every 60 seconds
            while (isActive) {
                try {
                    // Discover panels
                    val servers = com.aetheris.android.util.LanDiscovery.discover()
                    if (servers.isNotEmpty()) {
                        // Send broadcast with discovered servers
                        val resultIntent = Intent(ACTION_DISCOVERY_RESULT).apply {
                            putExtra(EXTRA_SERVER_COUNT, servers.size)
                        }
                        sendBroadcast(resultIntent)
                    }
                } catch (_: Exception) {}

                delay(60_000L)
            }
        }
    }

    private fun stopDiscovery() {
        scope.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotification(text: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, "aetheris_status")
            .setContentTitle("Aetheris Discovery")
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
        const val ACTION_START = "com.aetheris.android.DISCOVERY_START"
        const val ACTION_STOP = "com.aetheris.android.DISCOVERY_STOP"
        const val ACTION_DISCOVERY_RESULT = "com.aetheris.android.DISCOVERY_RESULT"
        const val EXTRA_SERVER_COUNT = "server_count"
        private const val NOTIFICATION_ID = 1001
    }
}
