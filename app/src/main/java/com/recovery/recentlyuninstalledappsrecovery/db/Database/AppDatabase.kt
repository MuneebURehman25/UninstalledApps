package com.recovery.recentlyuninstalledappsrecovery.db.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.recovery.recentlyuninstalledappsrecovery.db.dao.InstalledAppsTable
import com.recovery.recentlyuninstalledappsrecovery.db.dao.UninstallAppDao
import com.recovery.recentlyuninstalledappsrecovery.db.dao.UninstallAppTable

@Database(entities = [UninstallAppTable::class, InstalledAppsTable::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun unInstallAppDao(): UninstallAppDao
}