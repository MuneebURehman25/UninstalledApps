package com.recovery.recentlyuninstalledappsrecovery.utils.helper

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

class AppIconHelper(private val packageManager: PackageManager) {

    fun getAppIcon(packageName: String): Drawable? {
        try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            return appInfo.loadIcon(packageManager)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null
    }
}
