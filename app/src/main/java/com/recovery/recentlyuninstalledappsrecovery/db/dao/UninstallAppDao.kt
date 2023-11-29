package com.recovery.recentlyuninstalledappsrecovery.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UninstallAppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(uninstallAppsModel: UninstallAppTable)

    @Query("SELECT EXISTS(SELECT * FROM uninstallApp_table WHERE packageName = :packageName)")
    fun isRowIsExist(packageName : String) : Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstallApp(installedAppsModel: InstalledAppsTable)

    @Query("SELECT EXISTS (SELECT 1 FROM installApp_table WHERE packageName = :packageName)")
    fun exists(packageName: String): Boolean

    @Query("SELECT * FROM installApp_table WHERE packageName = :packageName")
    fun selectByPackageName(packageName: String) : List<InstalledAppsTable>

    @Query("DELETE FROM installApp_table WHERE packageName = :packageName")
    fun deleteByPackageName(packageName: String)

    @Query("SELECT * FROM installApp_table")
    fun getAllInstalledApps(): List<InstalledAppsTable>

    @Query("SELECT * FROM uninstallApp_table")
    fun getAll(): List<UninstallAppTable>

    @Query("DELETE FROM uninstallApp_table WHERE packageName = :packageName")
    fun deleteById(packageName: String)

}