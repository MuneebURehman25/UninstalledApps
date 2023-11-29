package com.recovery.recentlyuninstalledappsrecovery.presentation.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.recovery.recentlyuninstalledappsrecovery.R
import com.recovery.recentlyuninstalledappsrecovery.databinding.LayoutAppsBinding
import com.recovery.recentlyuninstalledappsrecovery.domain.model.AppInfoDetailsModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SystemDataAdapter(private val context: Context,
                        private var applicationInfo: List<AppInfoDetailsModel>,
                        private val onItemClick: (AppInfoDetailsModel) -> Unit) : RecyclerView.Adapter<SystemDataAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = LayoutAppsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = applicationInfo[position]

        holder.bind(currentItem)

    }

    override fun getItemCount(): Int {
        return applicationInfo.size
    }

    fun submitList(newList: List<AppInfoDetailsModel>) {
        val diffCallback = MyDiffCallback(applicationInfo, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        applicationInfo = newList
        diffResult.dispatchUpdatesTo(this)
    }

    inner class MyViewHolder(private val binding: LayoutAppsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AppInfoDetailsModel) {
            Glide.with(context)
                .load(item.appIcon)
                .placeholder(R.drawable.ic_placeholder) // Optional: Placeholder image while loading
                .centerCrop() // Optional: Image scaling and cropping options
                .into(binding.appIcon)

            binding.packageName.text = item.packageName
            binding.appName.text = item.name
            binding.appSize.text = item.appSize
            binding.layoutRv.setOnClickListener {
                onItemClick(item)
            }

        }
    }

    private class MyDiffCallback(
        private val oldList: List<AppInfoDetailsModel>,
        private val newList: List<AppInfoDetailsModel>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].packageName == newList[newItemPosition].packageName
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
