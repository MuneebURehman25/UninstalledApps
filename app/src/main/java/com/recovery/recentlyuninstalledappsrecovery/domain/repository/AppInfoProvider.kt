package com.recovery.recentlyuninstalledappsrecovery.domain.repository

import android.content.Context
import com.recovery.recentlyuninstalledappsrecovery.domain.model.AppInfoDetailsModel
import kotlinx.coroutines.flow.Flow

interface AppInfoProvider {
    fun systemAppInfo(context: Context): Flow<MutableList<AppInfoDetailsModel>>
    fun userAppInfo(context: Context): Flow<MutableList<AppInfoDetailsModel>>
}


