package com.recovery.recentlyuninstalledappsrecovery.db.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "uninstallApp_table")
data class UninstallAppTable(

    @PrimaryKey(autoGenerate = true)
    var id: Int=0,
    val packageName: String
)
