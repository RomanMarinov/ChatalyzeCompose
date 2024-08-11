package com.dev_marinov.chatalyze.presentation.util

import android.annotation.SuppressLint
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.colorResource
import com.dev_marinov.chatalyze.R


object GradientBackgroundHelper {
    @SuppressLint("UnrememberedMutableState")
    @Composable
    fun SetGradientBackground() {
        val colorAnimation = rememberInfiniteTransition()
        val color1 by colorAnimation.animateColor(
            initialValue = colorResource(id = R.color.main_violet),
            targetValue = colorResource(id = R.color.main_violet),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 5000),
                repeatMode = RepeatMode.Reverse
            ), label = ""
        )
        val color2 by colorAnimation.animateColor(
            initialValue = colorResource(id = R.color.main_violet),
            targetValue = colorResource(id = R.color.main_yellow),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 3000),
                repeatMode = RepeatMode.Reverse
            ), label = ""
        )
        val color3 by colorAnimation.animateColor(
            initialValue = colorResource(id = R.color.main_violet),
            targetValue = colorResource(id = R.color.main_yellow),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2000),
                repeatMode = RepeatMode.Reverse
            ), label = ""
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = mutableStateListOf(color1, color2, color3),
                        start = Offset.Zero,
                        end = Offset.Infinite
                    )
                )
        ) {
        }
    }

    @Composable
    fun SetMonochromeBackground() {

        val colorAnimation = rememberInfiniteTransition()
        val color1 by colorAnimation.animateColor(
            initialValue = colorResource(id = R.color.main_violet_light),
            targetValue = colorResource(id = R.color.main_violet_light),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 5000),
                repeatMode = RepeatMode.Reverse
            ), label = ""
        )

        val colorStops = listOf(
            color1 to 0.0f,
            color1 to 1.0f
        )

        val colors = colorStops.map { it.first }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = colors,
                        start = Offset.Zero,
                        end = Offset.Infinite
                    )
                )
        )
    }
}