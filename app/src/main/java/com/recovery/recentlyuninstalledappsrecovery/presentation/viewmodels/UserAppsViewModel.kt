package com.recovery.recentlyuninstalledappsrecovery.presentation.viewmodels


import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recovery.recentlyuninstalledappsrecovery.db.Database.AppDatabase
import com.recovery.recentlyuninstalledappsrecovery.domain.model.AppInfoDetailsModel
import com.recovery.recentlyuninstalledappsrecovery.domain.repository.AppInfoProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserAppsViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val appDataBase: AppDatabase,
    private val repository: AppInfoProvider
) : ViewModel(){
    val userAppInfoList = MutableLiveData<MutableList<AppInfoDetailsModel>?>()
    private var userAppsMutableStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)



    fun getUserAppsResult(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getUserAppDetails(context)
            }
        }
    }


    private suspend fun getUserAppDetails(context: Context) {
        userAppInfoList.postValue(arrayListOf())
        repository.userAppInfo(context).catch {
            userAppsMutableStateFlow.value = false

        }.collect {
            userAppInfoList.postValue(it)
            userAppsMutableStateFlow.value = true
        }

    }
}