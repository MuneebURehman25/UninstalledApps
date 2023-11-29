package com.recovery.recentlyuninstalledappsrecovery.utils.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.recovery.recentlyuninstalledappsrecovery.utils.service.BroadcastObserverService

class RestartServiceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        Toast.makeText(context, "Restarting service...", Toast.LENGTH_SHORT).show()

        val serviceIntent = Intent(context, BroadcastObserverService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context?.startForegroundService(serviceIntent)
        } else {
            context?.startService(serviceIntent)
        }
    }
}