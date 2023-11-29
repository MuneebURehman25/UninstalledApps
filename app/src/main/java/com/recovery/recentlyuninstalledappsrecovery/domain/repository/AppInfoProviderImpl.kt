package com.recovery.recentlyuninstalledappsrecovery.domain.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import com.recovery.recentlyuninstalledappsrecovery.domain.model.AppInfoDetailsModel
import com.recovery.recentlyuninstalledappsrecovery.utils.Constants.getAppSize
import com.recovery.recentlyuninstalledappsrecovery.utils.formatSizeThousand
import com.recovery.recentlyuninstalledappsrecovery.utils.getBitmapFromDrawable
import com.recovery.recentlyuninstalledappsrecovery.utils.helper.AppDateHelper
import com.recovery.recentlyuninstalledappsrecovery.utils.helper.AppIconHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppInfoProviderImpl @Inject constructor()  : AppInfoProvider {

    override fun systemAppInfo(context: Context): Flow<MutableList<AppInfoDetailsModel>>  = flow{
        val packageManager = context.packageManager
        val packages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager?.getInstalledApplications(PackageManager.ApplicationInfoFlags.of(0L))
        } else {
            packageManager?.getInstalledApplications(PackageManager.GET_META_DATA)
        }
        val systemApps = mutableListOf<ApplicationInfo>()
        val systemAppsList = mutableListOf<AppInfoDetailsModel>()
        packages?.let {
            for (appInfo in packages) {
                if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
                    // This is a system app
                    systemApps.add(appInfo)
                }
            }
        }

        for (appInfo in systemApps) {
            // You can access information about each system app
            val appName = appInfo.loadLabel(packageManager).toString()
            val packageName = appInfo.packageName
            val sourceDir = appInfo.sourceDir
            val appSize  = getAppSize(sourceDir).formatSizeThousand()

//            val appInfo = packageManager.getApplicationInfo(packageName, 0)
//            var d: Drawable? = appInfo.loadIcon(packageManager)
//            val bitmap: Bitmap? = d?.let { it1 ->
//                getBitmapFromDrawable(it1)
//            }
//            val stream = ByteArrayOutputStream()
//            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
//            val bitmapdata: ByteArray = stream.toByteArray()
            val appIconHelper = AppIconHelper(packageManager)
            val appIcon = appIconHelper.getAppIcon(packageName)
            val appDateHelper = AppDateHelper(packageManager)
            val lastUpdateDate = appDateHelper.getLastUpdateDate(packageName)
            val installDate = appDateHelper.getInstallDate(packageName)
            val targetSdkVersion = appInfo.targetSdkVersion
            val uid = appInfo.uid
            val dataDir = appInfo.dataDir
            val processName = appInfo.processName
            val sourceDirs = appInfo.sourceDir
            val permission = appInfo.permission
            val info = packageManager.getPackageInfo(appInfo.packageName, 0)
            val version = info.versionName

            systemAppsList += AppInfoDetailsModel(appIcon,appName,packageName,installDate,lastUpdateDate,processName,targetSdkVersion,uid,dataDir,appSize,sourceDirs,permission,version)
            Log.e("Details","System App : $systemAppsList")

        }
        emit(systemAppsList)
    }.flowOn(Dispatchers.IO)

    override fun userAppInfo(context: Context): Flow<MutableList<AppInfoDetailsModel>> = flow {

        val packageManager = context.packageManager
        val packages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager?.getInstalledApplications(PackageManager.ApplicationInfoFlags.of(0L))
        } else {
            packageManager?.getInstalledApplications(PackageManager.GET_META_DATA)
        }
        val userApps = mutableListOf<ApplicationInfo>()
        val systemAppsList = mutableListOf<AppInfoDetailsModel>()
        packages?.let {
            for (appInfo in packages) {
                if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                    // This is a system app
                    userApps.add(appInfo)
                }
            }
        }

        for (appInfo in userApps) {
            // You can access information about each system app
            val appName = appInfo.loadLabel(packageManager).toString()
            val packageName = appInfo.packageName
            val sourceDir = appInfo.sourceDir
            val appSize  = getAppSize(sourceDir).formatSizeThousand()
//            val appInfo = packageManager.getApplicationInfo(packageName, 0)
//            var d: Drawable? = appInfo.loadIcon(packageManager)
//            val bitmap: Bitmap? = d?.let { it1 ->
//                getBitmapFromDrawable(it1)
//            }
//            val stream = ByteArrayOutputStream()
//            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
//            val bitmapdata: ByteArray = stream.toByteArray()
            val appIconHelper = AppIconHelper(packageManager)
            val appIcon = appIconHelper.getAppIcon(packageName)
            val appDateHelper = AppDateHelper(packageManager)
            val lastUpdateDate = appDateHelper.getLastUpdateDate(packageName)
            val installDate = appDateHelper.getInstallDate(packageName)
            val targetSdkVersion = appInfo.targetSdkVersion
            val uid = appInfo.uid
            val dataDir = appInfo.dataDir
            val processName = appInfo.processName
            val sourceDirs = appInfo.sourceDir
            val permission = appInfo.permission
            val info = packageManager.getPackageInfo(appInfo.packageName, 0)
            val version = info.versionName

            systemAppsList += AppInfoDetailsModel(appIcon,appName,packageName,installDate,lastUpdateDate,processName,targetSdkVersion,uid,dataDir,appSize,sourceDirs,permission,version)
            Log.e("Details","System App : $systemAppsList")

        }
        emit(systemAppsList)

    }.flowOn(Dispatchers.IO)
}