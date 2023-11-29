package com.recovery.recentlyuninstalledappsrecovery.presentation.viewmodels


import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recovery.recentlyuninstalledappsrecovery.domain.model.AppInfoDetailsModel
import com.recovery.recentlyuninstalledappsrecovery.domain.repository.AppInfoProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SystemAppsViewModel @Inject constructor (
    @ApplicationContext val context: Context,
    private val repository: AppInfoProvider
) : ViewModel(){

     val systemAppInfoList = MutableLiveData<MutableList<AppInfoDetailsModel>?>()
    private var systemAppsMutableStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)



    fun getSystemAppsResult(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getSystemAppDetails(context)
            }
        }
    }


    private suspend fun getSystemAppDetails(context: Context) {
        systemAppInfoList.postValue(arrayListOf())
        repository.systemAppInfo(context).catch {
            systemAppsMutableStateFlow.value = false

        }.collectLatest {
            systemAppInfoList.postValue(it)
            systemAppsMutableStateFlow.value = true
        }

    }
}