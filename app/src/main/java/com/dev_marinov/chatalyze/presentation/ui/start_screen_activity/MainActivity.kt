package com.dev_marinov.chatalyze.presentation.ui.start_screen_activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.view.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dev_marinov.chatalyze.presentation.ui.start_screen_activity.auth_screen.AuthScreen
import com.dev_marinov.chatalyze.presentation.ui.start_screen_activity.code_screen.CodeScreen
import com.dev_marinov.chatalyze.presentation.ui.start_screen_activity.create_password_screen.CreatePasswordScreen
import com.dev_marinov.chatalyze.presentation.ui.start_screen_activity.forgot_password_screen.ForgotPasswordScreen
import com.dev_marinov.chatalyze.presentation.ui.start_screen_activity.signup_screen.SignUpScreen
import com.dev_marinov.chatalyze.presentation.ui.start_screen_activity.splash_screen.SplashScreen
import com.dev_marinov.chatalyze.presentation.ui.theme.ChatalyzeTheme
import com.dev_marinov.chatalyze.presentation.util.ScreenRoute
import com.dev_marinov.chatalyze.presentation.util.SystemUiControllerHelper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.tasks.await

//@Composable
//fun PerformOnLifecycle(
//    lifecycleOwner: LifecycleOwner,
//    onStart: () -> Unit,
//    onStop: () -> Unit,
//) {
//    DisposableEffect(lifecycleOwner) {
//        val observer = LifecycleEventObserver { _, event ->
//            when (event) {
//                Lifecycle.Event.ON_START -> { onStart() }
//                Lifecycle.Event.ON_STOP -> { onStop() }
//                else -> {}
//            }
//        }
//        lifecycleOwner.lifecycle.addObserver(observer)
//        return@DisposableEffect onDispose {
//            lifecycleOwner.lifecycle.removeObserver(observer)
//        }
//
//    }
//}
//
//
//@RequiresApi(Build.VERSION_CODES.TIRAMISU)
//@Composable
//fun OneQuoteApp(
//    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
//    context: Context,
//    viewModel: MainActivityViewModel = hiltViewModel()
//) {
//    val socketBroadcastReceiver = SocketBroadcastReceiver()
//    val filter = IntentFilter("receiver_action")
//
//    PerformOnLifecycle(
//        lifecycleOwner = lifecycleOwner,
//        onStart = {
//            Log.d("4444", " PerformOnLifecycle onStart")
//            context.registerReceiver(socketBroadcastReceiver, filter, RECEIVER_NOT_EXPORTED)
////            Intent(context, SocketService::class.java).also {
////                it.putExtra("sender", "5551234567")
////                context.startService(it)
////            }
//        },
//        onStop = {
//            Log.d("4444", " PerformOnLifecycle onStop")
////            Intent(context, SocketService::class.java).also {
////                context.stopService(it)
////            }
//            context.unregisterReceiver(socketBroadcastReceiver)
//        })
//}

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
                Log.d("4444", " firebaseToken=" + firebaseToken)
                viewModel.saveFirebaseToken(firebaseToken = firebaseToken)
                // firebaseToken=cf2jBrO0TbmWNhVkaQX7vc:APA91bE2AbQBOzpQJpFW2TIastWXyjmTWjS6zMMrADNhy5hIXHt2bXlT62V_LCb-mraeLI_LFTBomJ7rvzdrQcY4rnz1aJKZ3--FVTnFR5Dkj0Jz3ut38aJ_0kinzlMxS8bO-1V7AfK7
            }

            ChatalyzeTheme {
                Log.d("4444", " MainActivity loaded")
                //   window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                WindowCompat.setDecorFitsSystemWindows(window, false)
                SystemUiControllerHelper.SetSystemBars(false)
                SystemUiControllerHelper.SetStatusBarColor()

                StartScreensNavigationGraph()
            }
        }
    }
}



