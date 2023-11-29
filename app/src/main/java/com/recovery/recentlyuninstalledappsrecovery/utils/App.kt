package com.recovery.recentlyuninstalledappsrecovery.utils

import android.app.Application
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
 class App : Application() {

    override fun onCreate() {
        super.onCreate()
    }

}