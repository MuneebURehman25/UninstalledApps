package com.recovery.recentlyuninstalledappsrecovery.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.recovery.recentlyuninstalledappsrecovery.R
import com.recovery.recentlyuninstalledappsrecovery.domain.model.RecentlyUninstallAppModel
import com.recovery.recentlyuninstalledappsrecovery.presentation.diffutils.UninstalledAppDiffUtils
import com.recovery.recentlyuninstalledappsrecovery.databinding.LayoutUninstallAppsBinding
import com.recovery.recentlyuninstalledappsrecovery.utils.hide
import com.recovery.recentlyuninstalledappsrecovery.utils.invisible
import com.recovery.recentlyuninstalledappsrecovery.utils.show


class UninstallAppsListAdapter(private val context: Context, private val playStoreCallBack: PlayStoreClickCallback, val deleteCallBack: DeleteCallback):ListAdapter<RecentlyUninstallAppModel, UninstallAppsListAdapter.ViewHolder>(
    UninstalledAppDiffUtils()
) {


    private val selectedItems = HashSet<Int>()

    fun selectAllItems() {
        selectedItems.clear()
        for (index in 0 until itemCount) {
            selectedItems.add(index)
            getItem(index)?.isSelected = true
        }
        notifyDataSetChanged()
    }

    // Function to unselect all items
    fun unselectAllItems() {
        selectedItems.clear()
        for (index in 0 until itemCount) {
            getItem(index)?.isSelected = false
        }
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutUninstallAppsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, context,playStoreCallBack,deleteCallBack)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uninstallAppList = getItem(position)

        if (uninstallAppList !=null){
            holder.bind(uninstallAppList)
            val isSelected = selectedItems.contains(position)
            holder.itemView.isSelected = isSelected
        }
    }

    class ViewHolder(private val layoutBinding: LayoutUninstallAppsBinding,
                     private val context: Context,
                     private val playStoreCallBack: PlayStoreClickCallback,
                     private val deleteCallBack: DeleteCallback
    ): RecyclerView.ViewHolder(layoutBinding.root){


        fun bind(uninstallAppsModel: RecentlyUninstallAppModel){

            layoutBinding.apply {

                Glide.with(context)
                    .load(uninstallAppsModel.appImage)
                    .placeholder(R.drawable.ic_placeholder) // Optional: Placeholder image while loading
                    .fitCenter() // Optional: Image scaling and cropping options
                    .into(layoutBinding.appIcon)

                if (uninstallAppsModel.title.contains("<h1>") && uninstallAppsModel.title.contains("</h1>") ){
                   val value =  uninstallAppsModel.title.replace("<h1>","")
                    val finalValue = value.replace("</h1>","")
                    layoutBinding.appName.text = finalValue
                }
                else{
                    layoutBinding.appName.text = uninstallAppsModel.title
                }

                layoutBinding.packageName.text = uninstallAppsModel.packageName

                layoutBinding.imgDelete.setOnClickListener {

                    deleteCallBack.onAppClicked(uninstallAppsModel,adapterPosition)
                }

                layoutBinding.imgInstall.setOnClickListener {
                    playStoreCallBack.onAppClicked(uninstallAppsModel,adapterPosition)
                }

                if (uninstallAppsModel.isSelected) {
                    layoutBinding.selectAll.show()
                    layoutBinding.appIcon.invisible()
                } else {
                    layoutBinding.selectAll.hide()
                    layoutBinding.appIcon.show()
                }

            }

        }
    }


    interface PlayStoreClickCallback {
        fun onAppClicked(folder: RecentlyUninstallAppModel, adapterPosition: Int)
    }

    interface DeleteCallback {
        fun onAppClicked(folder: RecentlyUninstallAppModel, adapterPosition: Int)
    }
}