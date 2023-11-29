package com.recovery.recentlyuninstalledappsrecovery.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object Constants {

    const val SERVICE_CLASS_PATH = "com.recovery.recentlyuninstalledappsrecovery.utils.service.BroadcastObserverService"

     fun getAppSize(sourceDir: String): Long {
        val appDir = File(sourceDir)
        return if (appDir.exists()) {
            getAppSizeRecursive(appDir)
        } else {
            0
        }
    }
     private fun getAppSizeRecursive(directory: File): Long {
        var size: Long = 0
        if (directory.isDirectory) {
            val files = directory.listFiles()
            if (files != null) {
                for (file in files) {
                    size += if (file.isDirectory) {
                        getAppSizeRecursive(file)
                    } else {
                        file.length()
                    }
                }
            }
        } else {
            size = directory.length()
        }
        return size
    }

    fun getPermissionsForApp(context: Context, packageName: String): List<String>? {
        val packageManager = context.packageManager

        try {
            val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            if (packageInfo.requestedPermissions != null) {
                return packageInfo.requestedPermissions.toList()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return null
    }




    suspend fun checkForAppUpdates(context: Context, packageName: String): String {
        return withContext(Dispatchers.IO) {
            val packageInfo: PackageInfo
            try {
                packageInfo = context.packageManager.getPackageInfo(packageName, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                // Package not found
                return@withContext "Package not found"
            }

            val currentVersionCode = packageInfo.versionCode

            try {
                val url = URL("https://play.google.com/store/apps/details?id=$packageName")
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()

                val inStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                val page = response.toString()

                val startIndex = page.indexOf("softwareVersion")
                if (startIndex == -1) {
                    // Version information not found on the Play Store page
                    return@withContext "Version information not found"
                }

                val endIndex = page.indexOf("<", startIndex)
                val versionString = page.substring(startIndex, endIndex)

                val versionCode = versionString.split(" ")[1].toInt()

                return@withContext if (versionCode > currentVersionCode) {
                    "New version available"
                } else {
                    "No new version available"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext "Error checking for updates"
            }
        }
    }


    var appName:String = ""
    var currentVersion: String = ""
    var packageName: String? = ""
    var appIcon: Drawable? = null
    var installDate:String? = ""
    var updateDate:String? = ""
    var processName:String? = ""
    var targetSdkVersion:Int? = 0
    var uid:Int? = 0
    var dataDir:String? = ""
    var appSize:String? = ""
    var sourceDir:String? = ""

}