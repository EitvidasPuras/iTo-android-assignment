package com.puras.itoandroidassignment.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.puras.itoandroidassignment.presentation.ui.theme.IToAndroidAssignmentTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IToAndroidAssignmentTheme(dynamicColor = false) {
                DestinationsNavHost(navGraph = NavGraphs.root)
            }
        }
    }
}