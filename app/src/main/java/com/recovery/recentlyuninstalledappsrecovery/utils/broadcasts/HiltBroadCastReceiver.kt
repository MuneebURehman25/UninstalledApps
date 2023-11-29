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


abstract class HiltBroadCastReceiver : BroadcastReceiver() {

    @CallSuper
    override fun onReceive(context: Context, intent: Intent?) {

    }

}
    @AndroidEntryPoint
    open class UninstallReceiver : HiltBroadCastReceiver() {

        @Inject
        lateinit var appDataBase: AppDatabase

        override fun onReceive(context: Context, intent: Intent?) {
            super.onReceive(context, intent)

            Toast.makeText(context, "Receiver", Toast.LENGTH_SHORT).show()
            if (intent == null) return
            val action = intent.action

            if(action.equals("android.intent.action.PACKAGE_FULLY_REMOVED") || action.equals("android.intent.action.PACKAGE_REMOVED")){
                val pkg = getPackageName(intent)

                CoroutineScope(Dispatchers.IO).launch {
                    if (!appDataBase.unInstallAppDao().isRowIsExist(pkg)){
                        appDataBase.unInstallAppDao().insertApp(UninstallAppTable(packageName = pkg))
                        Log.d("BroadCastRun ","$pkg")
                    }
                }
            }


            if (!intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {

            }
        }

        private fun getPackageName(intent: Intent): String {
            return intent.data?.schemeSpecificPart.toString()
        }
    }