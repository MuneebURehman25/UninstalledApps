package com.recovery.recentlyuninstalledappsrecovery.presentation.fragments

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.recovery.recentlyuninstalledappsrecovery.R
import com.recovery.recentlyuninstalledappsrecovery.databinding.FragmentAppDetailsBinding
import com.recovery.recentlyuninstalledappsrecovery.domain.model.AppInfoDetailsModel
import com.recovery.recentlyuninstalledappsrecovery.domain.model.PermissionItem
import com.recovery.recentlyuninstalledappsrecovery.presentation.adapter.PermissionsAdapter
import com.recovery.recentlyuninstalledappsrecovery.utils.Constants
import com.recovery.recentlyuninstalledappsrecovery.utils.hide
import com.recovery.recentlyuninstalledappsrecovery.utils.isPermissionGranted
import com.recovery.recentlyuninstalledappsrecovery.utils.show
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AppDetailsFragment : Fragment() {

    val args: AppDetailsFragmentArgs by navArgs()
    private lateinit var appInfoDetails: AppInfoDetailsModel
    val permissionItemList: ArrayList<PermissionItem> = arrayListOf()
    private val PERMISSION_REQUEST_CODE = 1001
    private lateinit var binding: FragmentAppDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAppDetailsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appInfoDetails = args.appInfoDetailsModel

        setView(appInfoDetails)
        binding.layoutAppsDetailsBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setView(appInfoDetails: AppInfoDetailsModel){

        activity?.let {context->
            binding.apply {
                appIcons.setImageDrawable(appInfoDetails.appIcon)
                appNames.text = appInfoDetails.name
                txtAppPackage.text = appInfoDetails.packageName
                txtAppsDetailSpace.text = appInfoDetails.appSize
                txtDataDir.text = appInfoDetails.dataDir
                txtSourceDir.text = appInfoDetails.sourceDir
                txtinstallDate.text = appInfoDetails.installDate
                txtUpdateDate.text = appInfoDetails.updateDate
                txtProcessName.text = appInfoDetails.processName
                txtTargetSdk.text = appInfoDetails.targetSdkVersion.toString()
                txtAppVersion.text = appInfoDetails.appVersion
            }
        }



        binding.btnPermissions.setOnClickListener {
            binding.cvPermissions.show()
            binding.btnBack.show()
            binding.btnPermissions.hide()
            binding.scrollItems.hide()

            permissionItemList.clear()
            activity?.let {activity->

                val permissions = Constants.getPermissionsForApp(activity,appInfoDetails.packageName.toString())
                permissions?.forEach {permission->
                    // Check if the permission is granted
                    val isAllowed = activity.isPermissionGranted(permission)
                    val permissionItem = PermissionItem(permission, isAllowed)
                    permissionItemList.add(permissionItem)
                }

                val adapter = PermissionsAdapter(permissionItemList,activity)
                binding.rvPermissions.adapter = adapter
            }

        }

        binding.btnBack.setOnClickListener {
            binding.cvPermissions.hide()
            binding.btnPermissions.show()
            binding.scrollItems.show()
            binding.btnBack.hide()
        }





        binding.launchApp.setOnClickListener {
            activity?.let {
                try {
                    val intent = it.packageManager.getLaunchIntentForPackage(appInfoDetails.packageName.toString())
                    launchApp.launch(intent)

                }
                catch (e: ActivityNotFoundException) {

                } catch (e: NullPointerException) {

                }
            }

        }
        binding.openPlayStore.setOnClickListener {
            goToPlayStore(appInfoDetails.packageName.toString())

        }

        binding.unInstallApp.setOnClickListener {

            packageName(appInfoDetails.packageName.toString())
        }
    }


    private fun goToPlayStore(appPackageName: String){
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
            setPackage("com.android.vending")
        }
        startActivity(intent)
    }



    private fun packageName(packageName: String) {
        try {

            val intent = Intent(Intent.ACTION_DELETE)
            intent.data = Uri.parse("package:$packageName")
            intent.putExtra(Intent.EXTRA_RETURN_RESULT, true)
            unInstall.launch(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var unInstall =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                Activity.RESULT_OK -> {
                    view.let {
                        it?.let { it1 -> Navigation.findNavController(it1).navigate(R.id.action_appDetailsFragment_to_dashboardFragment) }
                    }
                }
                Activity.RESULT_CANCELED -> {

                }
                Activity.RESULT_FIRST_USER -> {
                }
            }

        }
    private var launchApp =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                Activity.RESULT_OK -> {
                }
                Activity.RESULT_CANCELED -> {
                }
                Activity.RESULT_FIRST_USER -> {
                }
            }
        }
}