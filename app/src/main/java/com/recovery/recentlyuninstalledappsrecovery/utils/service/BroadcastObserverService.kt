package com.recovery.recentlyuninstalledappsrecovery.utils.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.recovery.recentlyuninstalledappsrecovery.R
import com.recovery.recentlyuninstalledappsrecovery.presentation.activities.MainActivity
import com.recovery.recentlyuninstalledappsrecovery.utils.broadcasts.InstallReceiver
import com.recovery.recentlyuninstalledappsrecovery.utils.broadcasts.RestartServiceReceiver
import com.recovery.recentlyuninstalledappsrecovery.utils.broadcasts.UninstallReceiver


class BroadcastObserverService : Service() {

    private val uninstallReceiver = UninstallReceiver()
    private val installReceiver = InstallReceiver()
    private val restartServiceReceiver = RestartServiceReceiver()


    override fun onCreate() {
        super.onCreate()

        restartReceiver()
        val notification = createNotification()
        startForeground(FOREGROUND_NOTIFICATION_ID, notification)



    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        registerUninstallReceiver()
        registerInstallReceiver()

        // Return START_STICKY to indicate that the service should be restarted if it's killed
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }



    private fun createNotification(): Notification {
        // Create a notification with a pending intent
        val notificationChannelId = "broadcast_observer_channel_id"

        // Build a notification content intent (you can customize this)
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE)

        // Create the notification
        val notification = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Broadcast Observer Service")
            .setContentText("Listening for package removal events.")
            .setSmallIcon(R.drawable.ic_user_apps)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                notificationChannelId,
                "Broadcast Observer Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            )

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        return notification
    }

    companion object {
        private const val FOREGROUND_NOTIFICATION_ID = 1
        private const val RESTART_INTERVAL = (60 * 1000).toLong()
    }

    private fun registerUninstallReceiver() {
        // Register your uninstall receiver dynamically
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.PACKAGE_FULLY_REMOVED")
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED")

        registerReceiver(uninstallReceiver, intentFilter)
    }

    private fun registerInstallReceiver() {
        // Register your install receiver dynamically
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED")
        intentFilter.addDataScheme("package")
        registerReceiver(installReceiver, intentFilter)
    }

    private fun restartReceiver(){
        val intentFilter = IntentFilter()
        intentFilter.addAction("RESTART_SERVICE")
        registerReceiver(restartServiceReceiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        val restartIntent = Intent(this, RestartServiceReceiver::class.java)
        sendBroadcast(restartIntent)
    }
}