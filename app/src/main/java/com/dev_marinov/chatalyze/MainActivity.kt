package com.dev_marinov.chatalyze

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.core.view.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dev_marinov.chatalyze.ui.auth_screen.AuthScreen
import com.dev_marinov.chatalyze.ui.forgot_password_screen.ForgotPasswordScreen
import com.dev_marinov.chatalyze.ui.signup_screen.SignUpScreen
import com.dev_marinov.chatalyze.ui.splash_screen.SplashScreen
import com.dev_marinov.chatalyze.ui.theme.ChatalyzeTheme
import com.dev_marinov.chatalyze.util.SystemUiControllerHelper
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatalyzeTheme {
                Log.d("4444", " MainActivity loaded")
                WindowCompat.setDecorFitsSystemWindows(window, false)
                SystemUiControllerHelper.SystemBars(false)
                SystemUiControllerHelper.StatusBarColor()
                SetNavigation()

            }
        }
    }
}

@Composable
fun SetNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash_screen") {
        composable("splash_screen") {
            SplashScreen(navController = navController)
        }
        composable("auth_screen") {
            AuthScreen(navController = navController)
        }
        composable("sign_up_screen") {
            SignUpScreen(navController = navController)
        }
        composable("forgot_password_screen") {
            ForgotPasswordScreen(navController = navController)
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