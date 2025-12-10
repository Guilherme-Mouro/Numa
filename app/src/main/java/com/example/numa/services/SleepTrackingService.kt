package com.example.numa.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.numa.MainActivity
import com.example.numa.R
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.SleepSegmentRequest

class SleepTrackingService : Service() {

    private val NOTIFICATION_CHANNEL_ID = "SleepTrackingChannel"
    private val NOTIFICATION_ID = 1

    private val activityRecognitionClient by lazy { ActivityRecognition.getClient(this) }
    private lateinit var sleepPendingIntent: PendingIntent

    private val trackingPrefs by lazy {
        getSharedPreferences("sleep_tracking_prefs", Context.MODE_PRIVATE)
    }

    companion object {
        const val ACTION_START_TRACKING = "com.example.numa.START_TRACKING"
        const val ACTION_STOP_TRACKING = "com.example.numa.STOP_TRACKING"
    }

    override fun onCreate() {
        super.onCreate()
        val sleepDataIntent = Intent(applicationContext, SleepDataReceiver::class.java)
        
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        sleepPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            sleepDataIntent,
            flags
        )
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("SleepTrackingService", "Serviço recebendo comando: ${intent?.action}")

        when (intent?.action) {
            ACTION_START_TRACKING -> {
                startForegroundService()
                subscribeToSleepUpdates()
                trackingPrefs.edit().putBoolean("is_tracking", true).apply()
            }
            ACTION_STOP_TRACKING -> {
                stopSelf()
            }
        }

        return START_STICKY
    }

    private fun startForegroundService() {
        createNotificationChannel()

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Monitorando o Sono")
            .setContentText("O Numa está registrando sua noite de sono.")
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    @SuppressLint("MissingPermission")
    private fun subscribeToSleepUpdates() {
        val request = SleepSegmentRequest.getDefaultSleepSegmentRequest()

        activityRecognitionClient.requestSleepSegmentUpdates(sleepPendingIntent, request)
            .addOnSuccessListener {
                Log.d("SleepTrackingService", "Inscrito para atualizações de sono com sucesso.")
            }
            .addOnFailureListener { e ->
                Log.e("SleepTrackingService", "Falha ao se inscrever para atualizações de sono.", e)
            }
    }

    private fun unsubscribeFromSleepUpdates() {
        activityRecognitionClient.removeSleepSegmentUpdates(sleepPendingIntent)
            .addOnSuccessListener {
                Log.d("SleepTrackingService", "Inscrição de sono removida com sucesso.")
            }
            .addOnFailureListener { e ->
                Log.e("SleepTrackingService", "Falha ao remover inscrição de sono.", e)
            }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Canal de Rastreamento de Sono",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("SleepTrackingService", "Serviço destruído.")
        unsubscribeFromSleepUpdates()
        trackingPrefs.edit().putBoolean("is_tracking", false).apply()
    }
}
