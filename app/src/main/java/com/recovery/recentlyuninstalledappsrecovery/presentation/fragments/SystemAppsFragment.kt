package com.recovery.recentlyuninstalledappsrecovery.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.recovery.recentlyuninstalledappsrecovery.databinding.FragmentSystemAppsBinding
import com.recovery.recentlyuninstalledappsrecovery.domain.model.AppInfoDetailsModel
import com.recovery.recentlyuninstalledappsrecovery.presentation.adapter.SystemDataAdapter
import com.recovery.recentlyuninstalledappsrecovery.presentation.viewmodels.SystemAppsViewModel
import com.recovery.recentlyuninstalledappsrecovery.utils.hide
import com.recovery.recentlyuninstalledappsrecovery.utils.objectclass.AppSizes
import com.recovery.recentlyuninstalledappsrecovery.utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SystemAppsFragment : Fragment() {

    private lateinit var binding: FragmentSystemAppsBinding
    private  val viewModel : SystemAppsViewModel by activityViewModels()
    private lateinit var systemDataAdapter: SystemDataAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSystemAppsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.layoutPleaseWait.show()
        binding.txtSystemAppsSpace.text = AppSizes.systemAppsSize
        viewModel.systemAppInfoList.observe(requireActivity()){
            if (it != null) {
                setUpAdapter(it)
            }
        }

        binding.layoutSystemAppsBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setUpAdapter(appInfoDetailsModel: List<AppInfoDetailsModel>){
        activity?.let {
            systemDataAdapter  = SystemDataAdapter(it,appInfoDetailsModel){

                findNavController().navigate(
                    SystemAppsFragmentDirections.actionSystemAppsFragmentToAppDetailsFragment(it)
                )
            }
        }

        binding.rvSystem.adapter = systemDataAdapter
        systemDataAdapter.submitList(appInfoDetailsModel)
        binding.rvSystem.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Check if the RecyclerView is visible and has at least one item
                if (systemDataAdapter.itemCount > 0) {
                    // RecyclerView is visible with data, hide the loader
                    lifecycleScope.launch {
                        delay(200)
                        binding.layoutPleaseWait.hide()
                        binding.rvSystem.show()
                    }
                    // Remove the listener to avoid multiple calls
                    binding.rvSystem.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        })
    }

}