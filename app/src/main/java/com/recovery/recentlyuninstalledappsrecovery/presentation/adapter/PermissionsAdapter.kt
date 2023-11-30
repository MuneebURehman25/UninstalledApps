package com.recovery.recentlyuninstalledappsrecovery.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.recovery.recentlyuninstalledappsrecovery.R
import com.recovery.recentlyuninstalledappsrecovery.databinding.LayoutPermissionsBinding
import com.recovery.recentlyuninstalledappsrecovery.domain.model.PermissionItem
import com.recovery.recentlyuninstalledappsrecovery.utils.removePermissionPrefix

class PermissionsAdapter(private val permissionList: List<PermissionItem>, val context: Context) :
    RecyclerView.Adapter<PermissionsAdapter.PermissionViewHolder>() {

    inner class PermissionViewHolder(val binding:LayoutPermissionsBinding) : RecyclerView.ViewHolder(binding.root) {


        fun bind(permissionItem: PermissionItem) {
            binding.apply {
                val cleanedPermission = removePermissionPrefix(permissionItem.permission)
                permissionTxt.text = cleanedPermission
                permissionStatus.text = if (permissionItem.isAllowed) "Allowed" else "Not Allowed"
                if (permissionStatus.text.equals("Not Allowed")){
                    permissionStatus.setTextColor(ContextCompat.getColor(context, R.color.red))
                }
                else{
                    permissionStatus.setTextColor(ContextCompat.getColor(context, R.color.A_700_Black))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PermissionViewHolder {
        val binding = LayoutPermissionsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PermissionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PermissionViewHolder, position: Int) {
        val permissionItem = permissionList[position]
        holder.bind(permissionItem)
    }

    override fun getItemCount(): Int {
        return permissionList.size
    }
}
