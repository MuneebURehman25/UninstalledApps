package com.recovery.recentlyuninstalledappsrecovery.presentation.fragments

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.recovery.recentlyuninstalledappsrecovery.R
import com.recovery.recentlyuninstalledappsrecovery.databinding.FragmentDashboardBinding
import com.recovery.recentlyuninstalledappsrecovery.db.Database.AppDatabase
import com.recovery.recentlyuninstalledappsrecovery.db.dao.InstalledAppsTable
import com.recovery.recentlyuninstalledappsrecovery.presentation.viewmodels.DashboardViewModel
import com.recovery.recentlyuninstalledappsrecovery.presentation.viewmodels.SystemAppsViewModel
import com.recovery.recentlyuninstalledappsrecovery.presentation.viewmodels.UserAppsViewModel
import com.recovery.recentlyuninstalledappsrecovery.utils.formatSizeThousand
import com.recovery.recentlyuninstalledappsrecovery.utils.getBitmapFromDrawable
import com.recovery.recentlyuninstalledappsrecovery.utils.objectclass.AppSizes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject


@AndroidEntryPoint
class DashboardFragment : Fragment() {

    @Inject
    lateinit var appDatabase: AppDatabase

    private lateinit var binding: FragmentDashboardBinding
    private  val viewModel : SystemAppsViewModel by activityViewModels()
    private val userAppsViewModel : UserAppsViewModel by activityViewModels()
    private val dashboardViewModel: DashboardViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        fetchStorageData()
        clickListeners()

        userAppsViewModel.userAppInfoList.observe(requireActivity()){
            lifecycleScope.launch(Dispatchers.Default) {
                it?.forEach {

                    val exists = appDatabase.unInstallAppDao().exists(it.packageName.toString())
                    if (!exists){
                        val bitmap: Bitmap? = it.appIcon?.let { it1 ->
                            getBitmapFromDrawable(it1)
                        }
                        val stream = ByteArrayOutputStream()
                        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        val bitmapdata: ByteArray = stream.toByteArray()
                        appDatabase.unInstallAppDao().insertInstallApp(
                            InstalledAppsTable(0,
                            it.name.toString(),it.packageName.toString(),it.appVersion.toString(),bitmapdata)
                        )
                    }
                }
            }
        }

        backPressed()
    }


    private fun fetchStorageData() {
        binding.apply {
            activity?.let {context->

                lifecycleScope.launch(Dispatchers.IO) {
                    val (systemAppSize, userAppSize) = dashboardViewModel.calculateSystemAndUserAppSizes(context)
                    withContext(Dispatchers.Main){
                        binding.txtSystemAppsSpace.text = systemAppSize.formatSizeThousand()
                        binding.txtUserAppsSpace.text = userAppSize.formatSizeThousand()
                        AppSizes.systemAppsSize = ""
                        AppSizes.userAppsSize = ""
                        AppSizes.systemAppsSize = systemAppSize.formatSizeThousand()
                        AppSizes.userAppsSize = userAppSize.formatSizeThousand()
                    }
                }


                val (systemAppPercentage, userAppPercentage) = dashboardViewModel.calculatePercentageUsedByApps(context)


                val remainingSpace = dashboardViewModel.getRemainingStoragePercentage(context)
                circularProgressBar.setMaxProgress(userAppPercentage+systemAppPercentage+remainingSpace+3.0f)
                val progressData = floatArrayOf(userAppPercentage, remainingSpace,systemAppPercentage)
                circularProgressBar.setProgressData(progressData)

                val (totalSpace,usedSpace) = dashboardViewModel.getTotalStorageSpace(context)
                binding.txtTotalSpace.text = totalSpace
                binding.txtRemaningSpace.text = usedSpace

            }


        }
        // Handle the fetched data here
    }

    private fun clickListeners(){

        binding.systemAppsCard.setOnClickListener {
            viewModel.getSystemAppsResult()
            findNavController().navigate(R.id.action_dashboardFragment_to_systemAppsFragment)
        }

        binding.userAppsCard.setOnClickListener {
            userAppsViewModel.getUserAppsResult()
            findNavController().navigate(R.id.action_dashboardFragment_to_userAppsFragment)
        }

        binding.uninstalledAppsCard.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_uninstallAppsFragment)
        }
    }


    private fun backPressed() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            activity?.let {
                exitDialog()
            }
        }
    }
    private fun exitDialog(){
        activity?.let {


        val builder = AlertDialog.Builder(it)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.exit_dialog_layout, null)
        builder.setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCancelable(false)


        val btnYes =dialogView.findViewById<Button>(R.id.yes_tv)
        val btnNo =dialogView.findViewById<Button>(R.id.no_tv)

        btnYes.setOnClickListener {
            alertDialog?.dismiss()
            activity?.finish()
        }
        btnNo.setOnClickListener {
            alertDialog?.dismiss()
        }
        alertDialog.show()
    }
    }
}