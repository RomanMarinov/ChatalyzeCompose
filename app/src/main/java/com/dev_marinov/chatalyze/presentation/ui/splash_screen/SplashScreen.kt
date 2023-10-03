package com.dev_marinov.chatalyze.presentation.ui.splash_screen

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.dev_marinov.chatalyze.util.ScreenRoute
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashScreenViewModel = hiltViewModel()
) {
    StartAnimationLogoAndCheckTokenSignIn(
        navController = navController,
        viewModel = viewModel
    )
}

@Composable
fun StartAnimationLogoAndCheckTokenSignIn(
    navController: NavController,
    viewModel: SplashScreenViewModel
) {
    val getTokenSignIn by viewModel.getTokenSignIn.collectAsStateWithLifecycle("")

    val scale = remember {
        Animatable(0f)
    }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.7f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = { // степень смягчения
                    OvershootInterpolator(5f).getInterpolation(it)
                }
            )
        )
        delay(1000L)

       if (getTokenSignIn.isNotEmpty()) {
           navController.navigate(ScreenRoute.ChatalyzeScreen.route)
       } else {
           navController.navigate(ScreenRoute.AuthScreen.route)
       }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = com.dev_marinov.chatalyze.R.color.main_yellow_splash_screen)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = com.dev_marinov.chatalyze.R.drawable.ic_splash_screen),
            contentDescription = "Logo",
            modifier = Modifier.scale(scale.value)
        )
    }
}