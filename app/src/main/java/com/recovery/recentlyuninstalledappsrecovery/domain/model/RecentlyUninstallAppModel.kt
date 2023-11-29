package com.recovery.recentlyuninstalledappsrecovery.domain.model

import android.graphics.drawable.Drawable


data class RecentlyUninstallAppModel(
    var title:String,
    var packageName:String,
    var unInstallDate: String,
    var appVersion: String,
    var appImage: Drawable,
    var isSelected: Boolean = false
)
