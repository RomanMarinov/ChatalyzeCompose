package com.dev_marinov.chatalyze

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.view.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dev_marinov.chatalyze.presentation.ui.auth_screen.AuthScreen
import com.dev_marinov.chatalyze.presentation.ui.chatalyze_screen.ChatalyzeScreen
import com.dev_marinov.chatalyze.presentation.ui.forgot_password_screen.ForgotPasswordScreen
import com.dev_marinov.chatalyze.presentation.ui.signup_screen.SignUpScreen
import com.dev_marinov.chatalyze.presentation.ui.splash_screen.SplashScreen
import com.dev_marinov.chatalyze.presentation.ui.theme.ChatalyzeTheme
import com.dev_marinov.chatalyze.util.ScreenRoute
import com.dev_marinov.chatalyze.util.SystemUiControllerHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


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
}

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
        composable(ScreenRoute.ForgotScreen.route) {
            ForgotPasswordScreen(navController = navController)
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