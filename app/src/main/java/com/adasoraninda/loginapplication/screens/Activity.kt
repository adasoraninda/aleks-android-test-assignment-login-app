package com.adasoraninda.loginapplication.screens

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.adasoraninda.loginapplication.R
import com.adasoraninda.loginapplication.global.AuthenticationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class Activity : AppCompatActivity(R.layout.activity) {

    private val viewModel: AuthenticationViewModel by viewModels()

    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.main_nav_fragment) as NavHostFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.logout.onEach {
            Timber.d("logging out")
            navHostFragment.navController.navigate(R.id.action_logout)
        }.launchIn(lifecycleScope)
    }

}