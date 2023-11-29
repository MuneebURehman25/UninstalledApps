package com.recovery.recentlyuninstalledappsrecovery.domain.model

import android.graphics.drawable.Drawable
import java.io.Serializable

data class AppInfoDetailsModel(

    @Transient
    val appIcon: Drawable?,
    val name : String?,
    val packageName : String?,
    val installDate : String?,
    val updateDate : String?,
    val processName : String?,
    val targetSdkVersion : Int?,
    val uid : Int?,
    val dataDir : String?,
    val appSize : String?,
    val sourceDir : String?,
    val permission : String?,
    val appVersion: String?
) : Serializable


