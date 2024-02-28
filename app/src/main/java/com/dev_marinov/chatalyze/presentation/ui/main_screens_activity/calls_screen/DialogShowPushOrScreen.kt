package com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.calls_screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dev_marinov.chatalyze.R

@Composable
fun DialogShowPushOrScreen(
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.finger_tap_dialog))

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever, // бесконечно
        isPlaying = true, // пауза/воспроизведение
        speed = 1f,
        restartOnPlay = false // передать false, чтобы продолжить анимацию на котором он был приостановлен
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(1.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(colorResource(id = R.color.main_violet_light)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()

                        .padding(16.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    text = stringResource(id = R.string.important),
                    color = Color.White
                )
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorResource(id = R.color.main_yellow_new_chat_screen))
                ) {

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            modifier = Modifier
                                .weight(1f)
                                .padding(16.dp),
                            fontSize = 20.sp,
                            text = stringResource(id = R.string.select_display),
                            color = colorResource(id = R.color.main_violet_dialog_permission)
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(top = 8.dp, end = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            LottieAnimation(
                                composition = composition,
                                progress = progress,
                                modifier = Modifier
                                    .size(100.dp)
                                //.alpha(visibility)
                            )
                        }
                    }

                    val boxes = listOf("Box 1", "Box 2")
                    var selectedBoxIndex by remember { mutableIntStateOf(-1) }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in boxes.indices) {
                            val borderColor =
                                if (selectedBoxIndex == i) colorResource(id = R.color.main_violet) else Color.Transparent
                            val painter: Painter = when (i) {
                                0 -> painterResource(id = R.drawable.image_call_push)
                                1 -> painterResource(id = R.drawable.image_call_screen)
                                else -> {
                                    painterResource(id = R.drawable.image_call_screen)
                                }
                            }
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    //.background(backgroundColor)
                                    .border(
                                        BorderStroke(4.dp, borderColor),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable(onClick = {
                                        selectedBoxIndex = i
                                    })
                                    .size(
                                        if (selectedBoxIndex == i) 100.dp else 98.dp,
                                        if (selectedBoxIndex == i) 210.dp else 208.dp
                                    )
                            ) {
                                Image(
                                    painter = painter,
                                    contentDescription = "Logo",
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(4.dp)),
                                    contentScale = ContentScale.Crop // Пример использования contentScale
                                )
                            }
                        }
                    }

                    if (selectedBoxIndex == 1) {
                        Text(
                            modifier = Modifier
                                .padding(start = 16.dp, top = 16.dp, end = 16.dp),
                            fontSize = 20.sp,
                            text = stringResource(id = R.string.enable_сhatalyze),
                            color = colorResource(id = R.color.main_violet_dialog_permission)
                        )
                    }

                    Button(
                        onClick = { onConfirm(selectedBoxIndex) },
                        enabled = selectedBoxIndex != -1,
                        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.main_violet_light)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, top = 16.dp, end = 4.dp, bottom = 4.dp)
                            .clip(RoundedCornerShape(24.dp))
                    ) {
                        Text(
                            text = if (selectedBoxIndex == 0) {
                                stringResource(id = R.string.accept_push_type)
                            } else {
                                stringResource(id = R.string.go_to_settings)
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}