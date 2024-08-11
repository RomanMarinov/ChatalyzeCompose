package com.dev_marinov.chatalyze.presentation.ui.start_screen_activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.dev_marinov.chatalyze.presentation.ui.theme.ChatalyzeTheme
import com.dev_marinov.chatalyze.presentation.util.SystemUiControllerHelper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.tasks.await


@ExperimentalPermissionsApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityViewModel by viewModels()
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LaunchedEffect(key1 = true) {
                val firebaseToken = FirebaseMessaging.getInstance().token.await()
                viewModel.saveFirebaseToken(firebaseToken = firebaseToken)
            }

            ChatalyzeTheme {
                WindowCompat.setDecorFitsSystemWindows(window, false)
                SystemUiControllerHelper.SetSystemBars(false)
                SystemUiControllerHelper.SetStatusBarColor()

                StartScreensNavigationGraph(activity = this@MainActivity)
                LifecycleEventObserver()
            }
        }
    }
}

@Composable
fun LifecycleEventObserver() {
    val localLifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(
        key1 = localLifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_START -> {
                        Log.d("4444", " MainActivity Lifecycle.Event.ON_START")
                    }
                    Lifecycle.Event.ON_STOP -> {
                        Log.d("4444", " MainActivity Lifecycle.Event.ON_STOP")
                    }
                    Lifecycle.Event.ON_DESTROY -> {
                        Log.d("4444", " MainActivity Lifecycle.Event.ON_DESTROY")
                    }
                    else -> {}
                }
            }
            localLifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                localLifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )
}



