package com.recovery.recentlyuninstalledappsrecovery.utils.helper

import android.content.pm.PackageManager
import java.text.SimpleDateFormat
import java.util.*

class AppDateHelper(private val packageManager: PackageManager) {

    fun getLastUpdateDate(packageName: String): String {
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val lastUpdateTime = packageInfo.lastUpdateTime

            // Convert to a readable date format
            val updateDate = Date(lastUpdateTime)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return dateFormat.format(updateDate)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "N/A"
    }
    fun getInstallDate(packageName: String): String {
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val lastUpdateTime = packageInfo.firstInstallTime

            // Convert to a readable date format
            val updateDate = Date(lastUpdateTime)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return dateFormat.format(updateDate)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "N/A"
    }
}
