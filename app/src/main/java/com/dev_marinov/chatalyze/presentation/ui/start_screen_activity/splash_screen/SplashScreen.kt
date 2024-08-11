package com.dev_marinov.chatalyze.presentation.ui.start_screen_activity.splash_screen

import android.app.Activity
import android.content.Intent
import android.util.Log
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.MainScreensActivity
import com.dev_marinov.chatalyze.presentation.util.DecodeToken
import com.dev_marinov.chatalyze.presentation.util.ScreenRoute
import com.dev_marinov.chatalyze.presentation.util.TokenPayload
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun SplashScreen(
    activity: Activity,
    navController: NavController,
    viewModel: SplashScreenViewModel = hiltViewModel(),
) {
    StartAnimationLogoAndCheckTokenSignIn(
        activity = activity,
        navController = navController,
        viewModel = viewModel
    )
}

@Composable
fun StartAnimationLogoAndCheckTokenSignIn(
    activity: Activity,
    navController: NavController,
    viewModel: SplashScreenViewModel,
) {
    val context = LocalContext.current

    val refreshToken by viewModel.refreshToken.collectAsStateWithLifecycle("")
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

        if (refreshToken.isNotEmpty()) {
           val intent = Intent(context, MainScreensActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            activity.finish()
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