package com.recovery.recentlyuninstalledappsrecovery.presentation.diffutils

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.recovery.recentlyuninstalledappsrecovery.domain.model.RecentlyUninstallAppModel

class UninstalledAppDiffUtils: DiffUtil.ItemCallback<RecentlyUninstallAppModel>()  {
    override fun areItemsTheSame(oldItem: RecentlyUninstallAppModel, newItem: RecentlyUninstallAppModel): Boolean {
        return oldItem.title == newItem.title
                && oldItem.packageName == newItem.packageName
                && oldItem.unInstallDate == newItem.unInstallDate
                && oldItem.appVersion == newItem.appVersion
                && oldItem.appImage == newItem.appImage
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: RecentlyUninstallAppModel, newItem: RecentlyUninstallAppModel): Boolean {
        return  oldItem.title == newItem.title
                && oldItem.packageName == newItem.packageName
                && oldItem.unInstallDate == newItem.unInstallDate
                && oldItem.appVersion == newItem.appVersion
                && oldItem.appImage == newItem.appImage
    }
}