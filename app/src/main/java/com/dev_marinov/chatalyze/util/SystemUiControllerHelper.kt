package com.dev_marinov.chatalyze.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowInsetsControllerCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController


object SystemUiControllerHelper {
    @Composable
    fun SystemBars(isShow: Boolean) {
        val systemUiController = rememberSystemUiController()
        systemUiController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        SideEffect {
            systemUiController.setSystemBarsColor(
                color = Color.Transparent
            )
        }
        systemUiController.isNavigationBarVisible = isShow
    }

    @Composable
    fun StatusBarColor() {
        val systemUiController = rememberSystemUiController()
        SideEffect {
            systemUiController.setStatusBarColor(
                color = Color.Transparent,
                darkIcons = true
            )
        }
    }
}