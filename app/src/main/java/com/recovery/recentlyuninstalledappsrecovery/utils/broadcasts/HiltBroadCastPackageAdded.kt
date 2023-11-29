package com.recovery.recentlyuninstalledappsrecovery.utils.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.Toast
import androidx.annotation.CallSuper
import com.recovery.recentlyuninstalledappsrecovery.db.Database.AppDatabase
import com.recovery.recentlyuninstalledappsrecovery.db.dao.InstalledAppsTable
import com.recovery.recentlyuninstalledappsrecovery.db.dao.UninstallAppTable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

abstract class HiltBroadCastPackageAdded: BroadcastReceiver() {

    @CallSuper
    override fun onReceive(context: Context, intent: Intent?) {

    }
}
@AndroidEntryPoint
open class InstallReceiver : HiltBroadCastPackageAdded() {

    @Inject
    lateinit var appDataBase: AppDatabase

    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)
        Toast.makeText(context, "Receiver - package Added", Toast.LENGTH_SHORT).show()
        if (intent == null) return
        val action = intent.action
        if(action.equals("android.intent.action.PACKAGE_ADDED")){
            val pkgName = getPackageName(intent)
            CoroutineScope(Dispatchers.Default).launch {

                val exists = appDataBase.unInstallAppDao().exists(pkgName)
                if (!exists) {
                    // The app does not exist in the database, add its details
                    val appInfo = getAppInfo(context, pkgName)
                    if (appInfo != null) {
                        appDataBase.unInstallAppDao().insertInstallApp(appInfo)
                    }
                }
            }
        }

        if (!intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {

        }
    }

    private fun getPackageName(intent: Intent): String {
        return intent.data?.schemeSpecificPart.toString()
    }
    private fun getBitmapFromDrawable(drawable: Drawable): Bitmap? {
        val bmp = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bmp)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bmp
    }

    private fun getAppInfo(context: Context, pkgName: String): InstalledAppsTable? {
        val pm = context.packageManager
        val appInfo: ApplicationInfo?
        val appIcon: Drawable?
        val appVersion: String?

        try {
            appInfo = pm.getApplicationInfo(pkgName, 0)
            appIcon = pm.getApplicationIcon(appInfo)
            appVersion = pm.getPackageInfo(pkgName, 0).versionName

            val bitmap = getBitmapFromDrawable(appIcon)
            val stream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val bitmapdata: ByteArray = stream.toByteArray()

            return InstalledAppsTable(0, appInfo.loadLabel(pm).toString(), pkgName, appVersion, bitmapdata)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return null
    }
}