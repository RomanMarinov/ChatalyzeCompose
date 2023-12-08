package com.dev_marinov.chatalyze

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.core.view.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dev_marinov.chatalyze.presentation.ui.auth_screen.AuthScreen
import com.dev_marinov.chatalyze.presentation.ui.chatalyze_screen.ChatalyzeScreen
import com.dev_marinov.chatalyze.presentation.ui.code_screen.CodeScreen
import com.dev_marinov.chatalyze.presentation.ui.create_password_screen.CreatePasswordScreen
import com.dev_marinov.chatalyze.presentation.ui.forgot_password_screen.ForgotPasswordScreen
import com.dev_marinov.chatalyze.presentation.ui.signup_screen.SignUpScreen
import com.dev_marinov.chatalyze.presentation.ui.splash_screen.SplashScreen
import com.dev_marinov.chatalyze.presentation.ui.theme.ChatalyzeTheme
import com.dev_marinov.chatalyze.presentation.util.ScreenRoute
import com.dev_marinov.chatalyze.presentation.util.SystemUiControllerHelper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import dagger.hilt.android.AndroidEntryPoint

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
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {


            ChatalyzeTheme {
                Log.d("4444", " MainActivity loaded")
                //   window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                WindowCompat.setDecorFitsSystemWindows(window, false)
                SystemUiControllerHelper.SetSystemBars(false)
                SystemUiControllerHelper.SetStatusBarColor()
                SetNavigation()

            }
        }
    }



//    override fun onDestroy() {
//        lifecycle.removeObserver()
//        super.onDestroy()
//    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun SetNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = ScreenRoute.SplashScreen.route) {
        composable(ScreenRoute.SplashScreen.route) {
            SplashScreen(navController = navController)
        }
        composable(ScreenRoute.AuthScreen.route) {
            AuthScreen(navController = navController)
        }
        composable(ScreenRoute.SignUpScreen.route) {
            SignUpScreen(navController = navController)
        }

        composable(
            route = ScreenRoute.ForgotPasswordScreen.route
        ) {
            ForgotPasswordScreen(navController = navController)
        }

        composable(ScreenRoute.CodeScreen.route) {
            CodeScreen(navController = navController)
        }
        composable(ScreenRoute.CreatePasswordScreen.route) {
            CreatePasswordScreen(navController = navController)
        }
        composable(ScreenRoute.ChatalyzeScreen.route) {
            ChatalyzeScreen(authNavController = navController)
        }
    }
}


//object SystemUiControllerHelper {
//    @Composable
//    fun SystemBars(isVisible: Boolean) {
//        val systemUiController = rememberSystemUiController()
//        systemUiController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
//        SideEffect {
//            systemUiController.isSystemBarsVisible = isVisible
//            systemUiController.setSystemBarsColor(
//                color = Color.Transparent,
//                darkIcons = true
//            )
//
//
//        }
//    }
//
//    @Composable
//    fun StatusBarColor(isDarkIcons: Boolean) {
//        val systemUiController = rememberSystemUiController()
//        SideEffect {
//            systemUiController.setStatusBarColor(
//                color = Color.Transparent,
//                darkIcons = isDarkIcons
//            )
//        }
//    }
//}

