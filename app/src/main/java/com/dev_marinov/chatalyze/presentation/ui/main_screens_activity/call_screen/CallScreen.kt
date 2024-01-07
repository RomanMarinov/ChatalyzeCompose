package com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.call_screen


import android.annotation.SuppressLint
import android.util.Log

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dev_marinov.chatalyze.R
import com.dev_marinov.chatalyze.presentation.util.Constants
import com.dev_marinov.chatalyze.presentation.util.SystemUiControllerHelper
import kotlin.math.roundToInt

//@RootNavGraph(true)
//@Destination(
//    deepLinks = [
//        DeepLink(
//            uriPattern = "https://www.example.com/home"
//        )
//    ]
//)

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun CallScreen(
    navController: NavHostController,
    recipientName: String?,
    recipientPhone: String?,
    senderPhone: String?,
    typeEvent: String?,
    viewModel: CallScreenViewModel = hiltViewModel(),
) {
    Log.d("4444", " CallScreen загрузился")

    SystemUiControllerHelper.SetStatusBarColor()

    viewModel.saveHideNavigationBar(true)

//            починить получение чатов при заходке и вообще




    // 1 исходящий
    // открыть OutgoingCallContent и отправить получателю объект и чекать participants.size == 2
    // как только 2 то сразу скрыть OutgoingCallContent и показать экран CallStreamConnection
    // потом придумать таймер 20 сек и посылать второй объект на уведомление о пропущенном


    // 2 входящий
    // если отрисовался пуш и юзер нажал на него то сразу отрыть CallStreamConnection
    // если не пуш то открыть IncomingCallContent
    // для кнопки принять - закрыть IncomingCallContent и открыть CallStreamConnection
    // для кнопки отклонить - IncomingCallContent

    var callStreamState by remember {
        mutableStateOf(false)
    }

    val isFinishCallScreen by viewModel.isFinishCallScreen.observeAsState()
    LaunchedEffect(isFinishCallScreen) {
        if (isFinishCallScreen == true) {
            navController.popBackStack()
        }
    }

//    if (isFinishCallScreen == true) {
//        LottieExample(isPlaying = false)
//    } else {
//        LottieExample(isPlaying = true)
//    }

    when (typeEvent) {
        Constants.OUTGOING_CALL_EVENT -> {
            OutgoingCallContent(
                recipientName = recipientName,
                recipientPhone = recipientPhone,
                senderPhone = senderPhone,
                viewModel = viewModel
//                onDeclineCall = {
//                    callStreamState = true
//                }
            )
        }

        Constants.INCOMING_CALL_EVENT -> {
            Log.d("4444", " Constants.INCOMING_CALL_EVENT=" + Constants.INCOMING_CALL_EVENT
            + " recipientName=" + recipientName + " recipientPhone=" + recipientPhone)
            IncomingCallContent(
                recipientName = recipientName,
                recipientPhone = recipientPhone,
                viewModel = viewModel,
                onAcceptCall = {
                    callStreamState = it
                }
            )
        }
    }

//    if (callStreamState) {
//        CallStreamConnection(
//            navController = navController,
//            recipientName = recipientName,
//            recipientPhone = recipientPhone,
//            viewModel = viewModel
//        )
//    }


    // надо сначала показать попытку дозвона
    // клиент на сервер отправит событие звонка
    // с объектом (event = "call", )

    //viewModel.tryingToCall(senderPhone = senderPhone, recipientPhone = recipientPhone)
    //CallConnection(recipientName = recipientName, recipientPhone = recipientPhone)
//    CallConnected(
//        navController = navController,
//        recipientName = recipientName,
//        recipientPhone = recipientPhone,
//        viewModel = viewModel
//    )
}


@Composable
fun OutgoingCallContent(
    recipientName: String?,
    recipientPhone: String?,
    senderPhone: String?,
    viewModel: CallScreenViewModel,
    //onDeclineCall: () -> Unit,
//    composition: LottieComposition?,
//    progress: Float,
) {
    val isSessionState by viewModel.isSessionState.collectAsStateWithLifecycle("")
    LaunchedEffect(isSessionState) {
        if (isSessionState == Constants.SESSION_SUCCESS) {
            viewModel.makeCall(senderPhone = senderPhone, recipientPhone = recipientPhone)
        }
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.outgoing_call_anim))

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever, // бесконечно
        isPlaying = true, // пауза/воспроизведение
        speed = 2.0f,
        restartOnPlay = false // передать false, чтобы продолжить анимацию на котором он был приостановлен
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.main_violet))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {  }
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f)) // Добавляет пустое пространство вверху с весом 1f

        Column(modifier = Modifier.fillMaxWidth().height(500.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                color = Color.White,
                fontSize = 36.sp,
                text = recipientName ?: recipientPhone ?: "",
            )

            Text(
                modifier = Modifier.padding(bottom = 106.dp),
                color = Color.White,
                fontSize = 26.sp,
                text = stringResource(id = R.string.trying_to_call),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                composition = composition,
                progress = progress,
                modifier = Modifier
                    .size(200.dp)
                //.alpha(visibility)
            )
        }

        Spacer(modifier = Modifier.weight(1f)) // Добавляет пустое пространство с весом 1f

        Column(
            modifier = Modifier
                .fillMaxWidth() // Занимаем всю ширину
                .padding(bottom = 56.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .shadow(elevation = 8.dp, CircleShape, clip = false)
                    .background(Color.Red, CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = false, color = Color.Gray)
                    ) {
                        //onDeclineCall()

                        viewModel.executeFinishCallScreen(finish = true)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier
                        .size(60.dp)
                        .padding(8.dp),
                    painter = painterResource(id = R.drawable.ic_video_call_decline),
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun IncomingCallContent(
    modifier: Modifier = Modifier,
    recipientName: String?,
    recipientPhone: String?,
    viewModel: CallScreenViewModel,
    onAcceptCall: (Boolean) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.main_violet))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {  }
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f)) // Добавляет пустое пространство вверху с весом 1f

        Column(modifier = Modifier.fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {  }
            ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                color = Color.White,
                fontSize = 36.sp,
                text = recipientName ?: recipientPhone ?: ""
            )
            Text(
                modifier = Modifier.padding(bottom = 106.dp),
                color = Color.White,
                fontSize = 26.sp,
                text = stringResource(id = R.string.incoming_call),
            )
        }

        Spacer(modifier = Modifier.weight(1f)) // Добавляет пустое пространство с весом 1f

        Column(
            modifier = Modifier
                .fillMaxWidth() // Занимаем всю ширину
                .padding(bottom = 56.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AcceptBand(
                    viewModel = viewModel,
                    onAcceptCall = {
                        onAcceptCall(it)
                    })
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                DeclineBand(viewModel = viewModel)
            }
        }
    }


}


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AcceptBand(
    viewModel: CallScreenViewModel,
    onAcceptCall: (Boolean) -> Unit,
) {
    val shimmerColorShades = listOf(
        colorResource(id = R.color.white).copy(1f),
        colorResource(id = R.color.white).copy(0.0f),
        colorResource(id = R.color.white).copy(1f)
    )

    val width = 300.dp
    val dragSize = 60.dp

    val transition = rememberInfiniteTransition(label = "")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 4000f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            // RepeatMode.Reverse
        ), label = ""
    )

    val brush = Brush.linearGradient(
        colors = shimmerColorShades,
        start = Offset(20f, 20f),
        end = Offset(translateAnim, translateAnim)
    )

    val swipeableState = rememberSwipeableState(ConfirmationState.Default)
    val sizePx = with(LocalDensity.current) { (width - dragSize).toPx() }
    val anchors = mapOf(0f to ConfirmationState.Default, sizePx to ConfirmationState.Confirmed)
    val progress = derivedStateOf {
        if (swipeableState.offset.value == 0f) 0f else swipeableState.offset.value / sizePx
    }

    Box(
        modifier = Modifier
            .width(width)
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.5f) },
                orientation = Orientation.Horizontal
            )
            .background(brush = brush, RoundedCornerShape(dragSize))
    ) {
        Column(
            Modifier
                .align(Alignment.Center)
                .alpha(1f - progress.value),
            //.alpha(if (progress.value in 0.0f..0.5f) 1f else 0f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("slide to answer", color = colorResource(id = R.color.main_violet), fontSize = 18.sp)
        }

        AcceptBall(
            modifier = Modifier
                .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
                .size(dragSize),
            progress = progress.value,
            viewModel = viewModel,
            onAcceptCall = {
                onAcceptCall(it)
            }
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
private fun AcceptBall(
    modifier: Modifier,
    progress: Float,
    viewModel: CallScreenViewModel,
    onAcceptCall: (Boolean) -> Unit,
) {
    Box(
        modifier
            .padding(4.dp)
            .shadow(elevation = 8.dp, CircleShape, clip = false)
            .background(Color.Green, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        val isConfirmed = derivedStateOf { progress >= 0.8f }
        Crossfade(targetState = isConfirmed.value, label = "") {
            if (it) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
                // viewModel.
                // тут надо сделать закрытие экрана

                onAcceptCall(true)

            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_video_call_accept),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

        }
    }
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeclineBand(
    viewModel: CallScreenViewModel,
) {
    val shimmerColorShades = listOf(
        colorResource(id = R.color.white).copy(1f),
        colorResource(id = R.color.white).copy(0.0f),
        colorResource(id = R.color.white).copy(1f)
    )

    val width = 300.dp
    val dragSize = 60.dp

    val transition = rememberInfiniteTransition(label = "")
    val translateAnim by transition.animateFloat(
        initialValue = 4000f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            //RepeatMode.Reverse
        ), label = ""
    )

    val brush = Brush.linearGradient(
        colors = shimmerColorShades,
        start = Offset(20f, 20f),
        end = Offset(translateAnim, translateAnim)
    )


    val swipeableState = rememberSwipeableState(ConfirmationState.Default)
    val sizePx = with(LocalDensity.current) { (width - dragSize).toPx() }
    val anchors = mapOf(
        0f to ConfirmationState.Confirmed,
        sizePx to ConfirmationState.Default
    ) // Измените порядок состояний по умолчанию и подтверждения
    val progress = derivedStateOf {
        if (swipeableState.offset.value == 0f) 0f else swipeableState.offset.value / sizePx
    }

    Box(
        modifier = Modifier
            .width(width)
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.5f) },
                orientation = Orientation.Horizontal
            )
            .background(brush, RoundedCornerShape(dragSize))
    ) {
        Column(
            Modifier
                .align(Alignment.Center)
                .alpha(progress.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("slide to decline", color = colorResource(id = R.color.main_violet), fontSize = 18.sp)
        }

        DeclineBall(
            modifier = Modifier
                .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
                .size(dragSize),
            progress = progress.value,
            viewModel = viewModel
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
private fun DeclineBall(
    modifier: Modifier,
    progress: Float,
    viewModel: CallScreenViewModel,
) {
    Box(
        modifier
            .padding(4.dp)
            .shadow(elevation = 8.dp, CircleShape, clip = false)
            .background(Color.Red, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        val isConfirmed = derivedStateOf { progress <= 0.2f }
        Crossfade(targetState = isConfirmed.value, label = "") {
            if (it) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
                viewModel.executeFinishCallScreen(finish = true)
                // тут надо сделать закрытие экрана
            } else {
                Icon(
                    modifier = Modifier.size(40.dp),
                    painter = painterResource(id = R.drawable.ic_video_call_decline),
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

enum class ConfirmationState {
    Default, Confirmed
}
