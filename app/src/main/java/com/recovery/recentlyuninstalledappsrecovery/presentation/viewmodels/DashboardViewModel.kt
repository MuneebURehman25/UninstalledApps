package com.recovery.recentlyuninstalledappsrecovery.presentation.viewmodels

import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Environment
import android.os.Parcel
import android.os.StatFs
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.text.format.Formatter
import androidx.lifecycle.ViewModel
import com.recovery.recentlyuninstalledappsrecovery.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class DashboardViewModel : ViewModel() {


    private fun getTotalDeviceStorageSpace(): Long {
        val externalStorageDirectory = Environment.getExternalStorageDirectory()

        if (externalStorageDirectory != null) {
            val statFs = StatFs(externalStorageDirectory.path)
            val blockSize = statFs.blockSizeLong
            val totalBlocks = statFs.blockCountLong

            return blockSize * totalBlocks
        }

        return -1 // Return -1 to indicate an error or if external storage is not available
    }

    fun calculateSystemAndUserAppSizes(context: Context): Pair<Long, Long> {
        val packageManager = context.packageManager
        val packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)

        val (totalSystemAppSize, totalUserAppSize) = packages.filter {
            isSystemApp(it.applicationInfo)
        }.fold(0L to 0L) { acc, packageInfo ->
            val sourceDir = packageInfo.applicationInfo.sourceDir
            val appSize = Constants.getAppSize(sourceDir)
            acc.first + appSize to acc.second
        }

        val totalUserAppSizeFinal = packages.fold(totalUserAppSize) { acc, packageInfo ->
            if (!isSystemApp(packageInfo.applicationInfo)) {
                val sourceDir = packageInfo.applicationInfo.sourceDir
                val appSize = Constants.getAppSize(sourceDir)
                acc + appSize
            } else {
                acc
            }
        }

        return totalSystemAppSize to totalUserAppSizeFinal
    }


//    fun calculateSystemAndUserAppSizes(context: Context): Pair<Long, Long> {
//        val packageManager = context.packageManager
//        val packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
//        var totalSystemAppSize: Long = 0
//        var totalUserAppSize: Long = 0
//        var appSize : Long = 0
//
//        for (packageInfo in packages) {
//            if (isSystemApp(packageInfo.applicationInfo)) {
//                val sourceDir = packageInfo.applicationInfo.sourceDir
//                appSize = Constants.getAppSize(sourceDir)
//                totalSystemAppSize += appSize
//            } else {
//                val sourceDir = packageInfo.applicationInfo.sourceDir
//                appSize = Constants.getAppSize(sourceDir)
//                totalUserAppSize += appSize
//            }
//        }
//
//        // Sizes are in bytes, return as a Pair
//        return Pair(totalSystemAppSize, totalUserAppSize)
//    }

    private fun isSystemApp(applicationInfo: ApplicationInfo): Boolean {
        // Check if the app is a system app (FLAG_SYSTEM set)
        return applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }
    fun calculatePercentageUsedByApps(context: Context): Pair<Float, Float> {
        // Get the total space on the device
        val totalStorageSpace = getTotalDeviceStorageSpace().toFloat()

        // Calculate the sizes of system and user apps
        val (totalSystemAppSize, totalUserAppSize) = calculateSystemAndUserAppSizes(context)

        // Calculate the percentages
        val systemAppPercentage = (totalSystemAppSize / totalStorageSpace) * 100.0f
        val userAppPercentage = (totalUserAppSize / totalStorageSpace) * 100.0f

        // Return percentages as a Pair
        return Pair(systemAppPercentage, userAppPercentage)
    }

    fun getTotalStorageSpace(context: Context): Pair<String, String> {
        val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val storageVolumes = storageManager.primaryStorageVolume
        val storageStatsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        var totalSpace = 0L
        var freeSpace = 0L

        if (storageVolumes.isPrimary) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                totalSpace = storageStatsManager.getTotalBytes(StorageManager.UUID_DEFAULT)
                freeSpace = storageStatsManager.getFreeBytes(StorageManager.UUID_DEFAULT)
            }
        } else {
            val path = getPaths(context, storageVolumes)
            if (path != null) {
                val file = File(path)
                totalSpace = file.totalSpace
                freeSpace =  file.freeSpace
            }
        }
        val remainingSpace = freeSpace

        val totalSpaceFormatted = Formatter.formatFileSize(context, totalSpace)
        val remainingSpaceFormatted = Formatter.formatFileSize(context, remainingSpace)

        return Pair(totalSpaceFormatted, remainingSpaceFormatted)
    }

    private fun getPaths(context: Context, storageVolume: StorageVolume): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !storageVolume.isPrimary) {
            return null // Exclude secondary storage
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return storageVolume.directory?.absolutePath
        }
        try {
            return storageVolume.javaClass.getMethod("getPath").invoke(storageVolume) as String
        } catch (e: Exception) {
        }
        try {
            return (storageVolume.javaClass.getMethod("getPathFile").invoke(storageVolume) as File).absolutePath
        } catch (e: Exception) {
        }

        val extDirs = context.getExternalFilesDirs(null)
        for (extDir in extDirs) {
            val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
            val fileStorageVolume: StorageVolume = storageManager.getStorageVolume(extDir)
                ?: continue
            if (fileStorageVolume == storageVolume) {
                var file = extDir
                while (true) {
                    val parent = file.parentFile ?: return file.absolutePath
                    val parentStorageVolume = storageManager.getStorageVolume(parent)
                        ?: return file.absolutePath
                    if (parentStorageVolume != storageVolume)
                        return file.absolutePath
                    file = parent
                }
            }
        }
        try {
            val parcel = Parcel.obtain()
            storageVolume.writeToParcel(parcel, 0)
            parcel.setDataPosition(0)
            parcel.readString()
            return parcel.readString()
        } catch (e: Exception) {
        }
        return null
    }



    fun getRemainingStoragePercentage(context: Context): Float {
        val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val storageVolumes = storageManager.primaryStorageVolume
        val storageStatsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        var totalSpace = 0L
        var usedSpace = 0L

        if (storageVolumes.isPrimary) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                totalSpace = storageStatsManager.getTotalBytes(StorageManager.UUID_DEFAULT)
                usedSpace = totalSpace - storageStatsManager.getFreeBytes(StorageManager.UUID_DEFAULT)
            }
        } else {
            val path = getPaths(context, storageVolumes)
            if (path != null) {
                val file = File(path)
                totalSpace = file.totalSpace
                usedSpace = totalSpace - file.freeSpace
            }
        }

        if (totalSpace == 0L) {
            return 0f // Avoid division by zero
        }

        val remainingSpacePercentage = (usedSpace.toFloat() / totalSpace.toFloat()) * 100f
        return remainingSpacePercentage
    }
}