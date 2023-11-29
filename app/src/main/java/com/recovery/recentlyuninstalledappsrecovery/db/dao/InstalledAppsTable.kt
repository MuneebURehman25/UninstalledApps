package com.recovery.recentlyuninstalledappsrecovery.db.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "installApp_table")
data class InstalledAppsTable(

    @PrimaryKey(autoGenerate = true)
    var id: Int=0,
    var title:String,
    var packageName:String,
    var appVersion: String,
    var appImage: ByteArray
)
