package com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.call_screen

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.dev_marinov.chatalyze.R
import io.getstream.video.android.compose.permission.LaunchCallPermissions
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.ui.components.call.controls.ControlActions
import io.getstream.video.android.compose.ui.components.call.controls.actions.DeclineCallAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.FlipCameraAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.ToggleCameraAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.ToggleMicrophoneAction
import io.getstream.video.android.compose.ui.components.call.renderer.FloatingParticipantVideo
import io.getstream.video.android.compose.ui.components.video.VideoRenderer
import io.getstream.video.android.core.GEO
import io.getstream.video.android.core.RealtimeConnection
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.core.StreamVideoBuilder
import io.getstream.video.android.model.User
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock


@SuppressLint("CoroutineCreationDuringComposition", "UnrememberedMutableState")
@Composable
fun CallStreamConnection(
    navController: NavHostController,
    recipientName: String?,
    recipientPhone: String?,
    viewModel: CallScreenViewModel,
) {



    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val callTimeDuration by viewModel.callTimeDuration.collectAsStateWithLifecycle()
    val isDeclineCallAction = remember { mutableStateOf(false) }

    val userToken =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoidjhybXQzZmFlcmNrIn0.48dmUEoA5zMV_dRwTh7M_sz5qCfOx78aMc1oEN0Hs3g"
//    val userToken =
    //      ".eyJ1c2VyX2lkIjoidjhybXQzZmFlcmNrIn0.48dmUEoA5zMV_dRwTh7M_sz5qCfOx78aMc1oEN0Hs3g"

    val userId = "RomanMarinov"
    // например это будет номер sender устройства
    val callId = "VIVO"
    // val callId = "MACBOOK"
    // и зная номер получателя я отрисую ему пуш или покажу экран

    // step1 - create a user.
    val user = User(
        id = userId, // any string
        name = "Tutorial", // name and image are used in the UI
    )

    StreamVideo.removeClient() // перед созданием клиента удаляем

    // шаг 2 — инициализируем StreamVideo. Для производственного приложения мы
    // рекомендуем добавить клиент в класс приложения или модуль di.
    val client: StreamVideo = StreamVideoBuilder(
        context = context,
        apiKey = "v8rmt3faerck", // demo API key
        geo = GEO.GlobalEdgeNetwork,
        user = user,
        token = userToken,
    ).build()

    // шаг 3 - присоединение к вызову типа `default` и идентификатора `123`.
    val call = client.call("default", callId) // это все раскоментить потом
    scope.launch {
        val result = call.join(create = true)
        result.onError {
            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
        }
    }

    //  setContent {
    LaunchCallPermissions(call = call)

    // step4 - apply VideoTheme
    VideoTheme {

        val remoteParticipants by call.state.remoteParticipants.collectAsState()
        val remoteParticipant = remoteParticipants.firstOrNull()
        val me by call.state.me.collectAsState()
        val connection by call.state.connection.collectAsState()
        var parentSize: IntSize by remember { mutableStateOf(IntSize(0, 0)) }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                // .background(colorResource(id = R.color.main_violet_light))
                //.background(VideoTheme.colors.appBackground)
                .onSizeChanged { parentSize = it }
        ) {
            if (remoteParticipant != null) {
                val remoteVideo by remoteParticipant.video.collectAsState()

                // зачем
                Column(modifier = Modifier.fillMaxSize()) {
                    VideoRenderer(
                        modifier = Modifier.weight(1f),
                        call = call,
                        video = remoteVideo
                    )
                }
            } else { // пока никто не присоединен
                if (connection != RealtimeConnection.Connected && !isDeclineCallAction.value) {

                    // это будет загрузка своего видео потока
                    if (recipientName != null) { // если в контактах есть имя
                        Text(
                            text = "Try to call\n$recipientName...",
                            fontSize = 30.sp,
                            color = VideoTheme.colors.textHighEmphasis
                        )
                    } else { // иначе в контактах нет имеми
                        Text(
                            text = "Try to call\n$recipientPhone...",
                            fontSize = 30.sp,
                            color = VideoTheme.colors.textHighEmphasis
                        )
                    }
                } else {


                    LaunchedEffect(Unit) {
                        viewModel.currentTimeUnix(Clock.System.now().epochSeconds)
                        viewModel.startCallingLoop()
                    }
                }
            }

            // floating video UI for the local video participant
            me?.let { localVideo ->
                FloatingParticipantVideo(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 16.dp),
                    call = call,
                    participant = localVideo,
                    parentBounds = parentSize
                )
            }

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = callTimeDuration,
                fontSize = 30.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
            )
        }


        // step5 - define required properties.
        val participants by call.state.participants.collectAsState()
        // val connection by call.state.connection.collectAsState()

        // step6 - render texts that display connection status.
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
            //.background(colorResource(id = R.color.main_violet_light))
        ) {
            if (connection != RealtimeConnection.Connected && !isDeclineCallAction.value) {
                Text("loading...", fontSize = 30.sp)
            } else {

                // написать условие если participants.size больше 1 то включать таймер

                Text(
                    "\n\n\n\nCall ${call.id} has ${participants.size} participants",
                    fontSize = 30.sp
                )
            }
        }

//                CallContent(
//                    modifier = Modifier.fillMaxSize(),
//                    call = call,
//                    onBackPressed = { onBackPressed() },
//                )

        val isCameraEnabled by call.camera.isEnabled.collectAsState()
        val isMicrophoneEnabled by call.microphone.isEnabled.collectAsState()
//                CallContent(
//                    modifier = Modifier.background(color = VideoTheme.colors.appBackground),
//                    call = call,
//                    onBackPressed = { onBackPressed() },
//                    controlsContent = {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom

        ) {
            ControlActions(
                backgroundColor = colorResource(id = R.color.main_yellow_new_chat_screen),
                call = call,
                actions = listOf(
                    {
                        ToggleCameraAction(
                            modifier = Modifier.size(52.dp),
                            isCameraEnabled = isCameraEnabled,
                            enabledColor = colorResource(id = R.color.main_violet_light),
                            disabledColor = Color.White,
                            onCallAction = { call.camera.setEnabled(it.isEnabled) }
                        )
                    },
                    {
                        ToggleMicrophoneAction(
                            modifier = Modifier.size(52.dp),
                            enabledColor = colorResource(id = R.color.main_violet_light),
                            disabledColor = Color.White,
                            isMicrophoneEnabled = isMicrophoneEnabled,
                            onCallAction = { call.microphone.setEnabled(it.isEnabled) }
                        )
                    },
                    {
                        FlipCameraAction(
                            modifier = Modifier.size(52.dp),
                            enabledColor = colorResource(id = R.color.main_violet_light),
                            disabledColor = Color.White,
                            onCallAction = {
                                call.camera.flip()
                            }
                        )
                    }, {
                        DeclineCallAction(
                            modifier = Modifier.size(52.dp),
                            onCallAction = {
                                scope.launch {
                                    call.end()

                                    isDeclineCallAction.value = true
                                    StreamVideo.removeClient()

                                    viewModel.cancelCallingLoop()
                                    // delay(1500L)
                                    // navController.popBackStack(ScreenRoute.CallsScreen.route, false)
                                }
                            }
                        )
                    }
                )
            )
        }
    }
}