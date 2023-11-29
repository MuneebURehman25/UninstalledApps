package com.recovery.recentlyuninstalledappsrecovery.presentation.activities


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.recovery.recentlyuninstalledappsrecovery.R
import com.recovery.recentlyuninstalledappsrecovery.databinding.ActivityMainBinding
import com.recovery.recentlyuninstalledappsrecovery.databinding.ExitDialogLayoutBinding
import com.recovery.recentlyuninstalledappsrecovery.utils.service.BroadcastObserverService
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_home_activity) as NavHostFragment
        navController = navHostFragment.navController

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(this, BroadcastObserverService::class.java)
            ContextCompat.startForegroundService(this, intent)
        } else {
            val intent = Intent(this, BroadcastObserverService::class.java)
            startService(intent)
        }
    }

}