package com.dev_marinov.chatalyze.presentation.ui.start_screen_activity

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dev_marinov.chatalyze.presentation.ui.start_screen_activity.auth_screen.AuthScreen
import com.dev_marinov.chatalyze.presentation.ui.start_screen_activity.code_screen.CodeScreen
import com.dev_marinov.chatalyze.presentation.ui.start_screen_activity.create_password_screen.CreatePasswordScreen
import com.dev_marinov.chatalyze.presentation.ui.start_screen_activity.forgot_password_screen.ForgotPasswordScreen
import com.dev_marinov.chatalyze.presentation.ui.start_screen_activity.signup_screen.SignUpScreen
import com.dev_marinov.chatalyze.presentation.ui.start_screen_activity.splash_screen.SplashScreen
import com.dev_marinov.chatalyze.presentation.util.ScreenRoute
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun StartScreensNavigationGraph(activity: MainActivity) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = ScreenRoute.SplashScreen.route) {
        composable(ScreenRoute.SplashScreen.route) {
            SplashScreen(activity = activity, navController = navController)
        }
        composable(route = ScreenRoute.AuthScreen.route) {
            AuthScreen(activity = activity, navController = navController)
        }
        composable(ScreenRoute.SignUpScreen.route) {
            SignUpScreen(navController = navController)
        }
        composable(route = ScreenRoute.ForgotPasswordScreen.route) {
            ForgotPasswordScreen(navController = navController)
        }
        composable(ScreenRoute.CodeScreen.route) {
            CodeScreen(navController = navController)
        }
        composable(ScreenRoute.CreatePasswordScreen.route) {
            CreatePasswordScreen(navController = navController)
        }
    }
}