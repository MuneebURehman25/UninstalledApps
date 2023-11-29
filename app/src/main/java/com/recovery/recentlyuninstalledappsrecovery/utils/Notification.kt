package com.recovery.recentlyuninstalledappsrecovery.utils

import android.Manifest
import android.app.Notification
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.recovery.recentlyuninstalledappsrecovery.R

class Notification(private val context: Context) {
    private val patternVibrate = longArrayOf(1000, 1000)
    private val path: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    private val soundPath: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    private val notificationManager by lazy { NotificationManagerCompat.from(context) }
    private val audioAttributes = AudioAttributes.Builder()
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
        .build()
    private val channelId = "id_channel"
    private val createChannel by lazy {
        NotificationChannelCompat.Builder(channelId, NotificationManagerCompat.IMPORTANCE_DEFAULT)
            .setDescription("Application running of ${context.resources.getString(R.string.app_name)}")
            .setName(context.resources.getString(R.string.app_name))
            .setShowBadge(false)
            .setSound(path, audioAttributes)
            .build().also { notificationManager.createNotificationChannel(it) }
    }

    private val notificationId = 101
    private val persistentNotification by lazy {
        NotificationCompat.Builder(context, createChannel.id)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.resources.getString(R.string.app_name))
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setVibrate(patternVibrate)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setTicker("text")
            .setSound(soundPath)
            .setAutoCancel(false)
            .setOngoing(true)
            .setWhen(System.currentTimeMillis()).setChannelId(channelId)


    }

    fun createNotification(): Notification {
        val notification = persistentNotification.build()
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

        }
        notificationManager.notify(notificationId, notification)
        return notification
    }
}