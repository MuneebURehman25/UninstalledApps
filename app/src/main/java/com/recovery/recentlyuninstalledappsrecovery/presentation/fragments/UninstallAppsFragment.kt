package com.recovery.recentlyuninstalledappsrecovery.presentation.fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.recovery.recentlyuninstalledappsrecovery.R
import com.recovery.recentlyuninstalledappsrecovery.databinding.FragmentUninstallAppsBinding
import com.recovery.recentlyuninstalledappsrecovery.db.Database.AppDatabase
import com.recovery.recentlyuninstalledappsrecovery.db.dao.UninstallAppTable
import com.recovery.recentlyuninstalledappsrecovery.domain.model.RecentlyUninstallAppModel
import com.recovery.recentlyuninstalledappsrecovery.presentation.adapter.UninstallAppsListAdapter
import com.recovery.recentlyuninstalledappsrecovery.presentation.viewmodels.RecentlyUninstallAppViewModel
import com.recovery.recentlyuninstalledappsrecovery.utils.hide
import com.recovery.recentlyuninstalledappsrecovery.utils.invisible
import com.recovery.recentlyuninstalledappsrecovery.utils.show
import com.recovery.recentlyuninstalledappsrecovery.utils.snackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UninstallAppsFragment : Fragment() {

    @Inject
    lateinit var appDatabase: AppDatabase

    private val viewModel: RecentlyUninstallAppViewModel by viewModels()
    private lateinit var binding: FragmentUninstallAppsBinding
    private var packageNameLists: ArrayList<UninstallAppTable> = arrayListOf()
    private var isAllDeleteClicked = false

    private val uninstallAppsListAdapter by lazy {

        val listener = object : UninstallAppsListAdapter.PlayStoreClickCallback {
            override fun onAppClicked(folder: RecentlyUninstallAppModel, adapterPosition: Int) {
                goToPlayStore(folder.packageName)
            }
        }
        val listenerDelete = object : UninstallAppsListAdapter.DeleteCallback {
            override fun onAppClicked(folder: RecentlyUninstallAppModel, adapterPosition: Int) {

                if (!isAllDeleteClicked){
                    delSingleAppDialog(folder, adapterPosition)
                }
                else{
                    lifecycleScope.launch {
                        deleteAppDetails(folder.packageName,adapterPosition)
                    }
                }
            }
        }

        activity?.let { act ->
            UninstallAppsListAdapter(act,listener,listenerDelete)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentUninstallAppsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutPleaseWait.show()
        viewModel.getAllDatabaseData()
        observer()
        binding.layoutUninstallAppsBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.selectUnselect.setOnCheckedChangeListener { compoundButton, b ->

            if (b){
                uninstallAppsListAdapter?.selectAllItems()
                binding.delete.show()
            }
            else{
                uninstallAppsListAdapter?.unselectAllItems()
                binding.delete.hide()
            }
        }

        binding.delete.setOnClickListener {
            delAllAppDialog()
        }
    }

    // Inside your MainActivity or wherever the adapter is used
// Assuming you have a reference to your UninstallAppsListAdapter called uninstallAppsListAdapter

    fun deleteSelectedItems() {
        val selectedItems = mutableListOf<RecentlyUninstallAppModel>()

        // Iterate through all items in the adapter
        for (i in 0 until (uninstallAppsListAdapter?.itemCount ?: 0)) {
            val item = uninstallAppsListAdapter?.currentList?.get(i)

            // Check if the item is selected (based on your isSelected flag in the model)
            if (item?.isSelected == true) {
                selectedItems.add(item)
            }
        }

        // Now, 'selectedItems' contains all items that are selected for deletion
        // Perform deletion logic here
        for (selectedItem in selectedItems) {
            // You might perform deletion using your callback method in the adapter
            uninstallAppsListAdapter?.currentList?.indexOf(selectedItem)
                ?.let { uninstallAppsListAdapter?.deleteCallBack?.onAppClicked(selectedItem, it) }
        }

        if (selectedItems.isEmpty()){
            findNavController().popBackStack()
        }
    }

    private fun observer(){

        packageNameLists.clear()
        viewModel.unInstallAppsLiveData.observe(requireActivity()) { appList ->

            if (appList.size>0){
                binding.selectUnselect.show()
                uninstallAppsListAdapter?.submitList(appList.distinct())
                binding.rvUnInstallApps.adapter = uninstallAppsListAdapter
                binding.rvUnInstallApps.viewTreeObserver.addOnGlobalLayoutListener(object :
                    ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        // Check if the RecyclerView is visible and has at least one item
                        if (uninstallAppsListAdapter?.itemCount!! > 0) {
                            // RecyclerView is visible with data, hide the loader
                            lifecycleScope.launch {
                                delay(100)
                                binding.layoutPleaseWait.hide()
                                binding.rvUnInstallApps.show()
                            }
                            // Remove the listener to avoid multiple calls
                            binding.rvUnInstallApps.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        }
                    }
                })
            }
            else{
                binding.selectUnselect.invisible()
                activity?.let {
                    it.snackBar(binding.mainFragmentRecentlyUninstallApps,
                        getString(R.string.no_apps_found),700)
                }
                lifecycleScope.launch {
                    delay(100)
                    uninstallAppsListAdapter?.submitList(appList.distinct())
                    binding.rvUnInstallApps.adapter = uninstallAppsListAdapter
                    binding.layoutPleaseWait.hide()
                    binding.rvUnInstallApps.hide()
                }
            }
        }
    }


    suspend fun deleteAppDetails(packageName:String,position:Int){

        val job =  lifecycleScope.launch(Dispatchers.IO) {
            appDatabase.unInstallAppDao().deleteById(packageName)
            appDatabase.unInstallAppDao().deleteByPackageName(packageName)
        }

        job.join()
        activity?.let {
            it.snackBar(binding.mainFragmentRecentlyUninstallApps,getString(R.string.app_deleted_successfully),700)
        }
        delay(500)
        findNavController().popBackStack()

    }

    private fun goToPlayStore(appPackageName: String){
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
            setPackage("com.android.vending")
        }
        startActivity(intent)
    }


    private fun delSingleAppDialog(folder: RecentlyUninstallAppModel, adapterPosition: Int){
        activity?.let {activity->

        val builder = AlertDialog.Builder(activity)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.single_app_delete_dialog_layout, null)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCancelable(false)
        val btnYes =dialogView.findViewById<Button>(R.id.yes_del)
        val btnNo =dialogView.findViewById<Button>(R.id.no_del)

        btnYes.setOnClickListener {
            lifecycleScope.launch {
                deleteAppDetails(folder.packageName,adapterPosition)
            }
            alertDialog?.dismiss()
        }
        btnNo.setOnClickListener {
            alertDialog?.dismiss()
        }
        alertDialog.show()
        }

    }

    private fun delAllAppDialog(){
        activity?.let {activity->

            val builder = AlertDialog.Builder(activity)
            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.all_app_delete_dialog_layout, null)
            builder.setView(dialogView)
            val alertDialog = builder.create()
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertDialog.setCancelable(false)
            val btnYes =dialogView.findViewById<Button>(R.id.yes_del_all)
            val btnNo =dialogView.findViewById<Button>(R.id.no_del_all)

            btnYes.setOnClickListener {
                isAllDeleteClicked = true
                deleteSelectedItems()
                alertDialog?.dismiss()
            }
            btnNo.setOnClickListener {
                isAllDeleteClicked = false
                alertDialog?.dismiss()
            }
            alertDialog.show()
        }

    }
}