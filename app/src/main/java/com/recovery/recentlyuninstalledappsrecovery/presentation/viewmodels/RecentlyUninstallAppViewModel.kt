package com.recovery.recentlyuninstalledappsrecovery.presentation.viewmodels

import android.app.Application
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.recovery.recentlyuninstalledappsrecovery.db.Database.AppDatabase
import com.recovery.recentlyuninstalledappsrecovery.domain.model.RecentlyUninstallAppModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@HiltViewModel
class RecentlyUninstallAppViewModel
@Inject
constructor(var app: Application, private var appDataBase: AppDatabase): AndroidViewModel(app){

    private val unInstallAppsMutableLiveData = MutableLiveData<ArrayList<RecentlyUninstallAppModel>>()
    var unInstallAppsLiveData: MutableLiveData<ArrayList<RecentlyUninstallAppModel>> = unInstallAppsMutableLiveData
    private var uninstallAppsList: ArrayList<RecentlyUninstallAppModel> = arrayListOf()


    fun getAllDatabaseData(){

        uninstallAppsList.clear()
        viewModelScope.launch(Dispatchers.IO) {

            val list = appDataBase.unInstallAppDao().getAll().distinct()
            list.forEach { app ->
                if (available(app.packageName)) {
                    appDataBase.unInstallAppDao().deleteById(app.packageName)
                } else {
                    getDataOfUninstalledApps(app.packageName)
                }
            }
            if (list.isEmpty()) {
                unInstallAppsMutableLiveData.postValue(arrayListOf())
            }
        }
    }


    private fun getDataOfUninstalledApps(packageName: String) {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val current = formatter.format(time)

        viewModelScope.launch(Dispatchers.IO) {
            val getAppDataFromDB = appDataBase.unInstallAppDao().selectByPackageName(packageName)

            getAppDataFromDB.forEach { appData ->
                val image: Drawable = BitmapDrawable(app.resources, BitmapFactory.decodeByteArray(appData.appImage, 0, appData.appImage.size))
                uninstallAppsList.add(RecentlyUninstallAppModel(appData.title, appData.packageName, current, appData.appVersion,image))
            }
            unInstallAppsMutableLiveData.postValue(uninstallAppsList)
        }
    }


    private fun available(name: String): Boolean {
        var available = true
        try {
            app.packageManager.getPackageInfo(name, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            available = false
        }
        return available
    }
}