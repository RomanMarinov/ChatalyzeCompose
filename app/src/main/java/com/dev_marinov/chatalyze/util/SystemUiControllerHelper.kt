package com.dev_marinov.chatalyze.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.dev_marinov.chatalyze.R
import com.google.accompanist.systemuicontroller.rememberSystemUiController


object SystemUiControllerHelper {
    @Composable
    fun SetSystemBars(isShow: Boolean) {
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
    fun SetNavigationBars(isVisible: Boolean) {
        val systemUiController = rememberSystemUiController()
        SideEffect {
            systemUiController.setNavigationBarColor(
                color = Color.Black,
                darkIcons = true
            )
        }
        systemUiController.isNavigationBarVisible = isVisible
    }

    @Composable
    fun SetStatusBarColor() {
        val systemUiController = rememberSystemUiController()
        SideEffect {
            systemUiController.setStatusBarColor(
                color = Color.Transparent,
                darkIcons = true
            )
        }
    }

    @Composable
    fun SetStatusBarColorNoGradient() {
        val systemUiController = rememberSystemUiController()
        val context = LocalContext.current
        SideEffect {
            systemUiController.setStatusBarColor(
                color = Color(ContextCompat.getColor(context, R.color.main_violet_light)),
                darkIcons = true
            )
        }
    }
}