package com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.stream_screen

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.dev_marinov.chatalyze.R
import com.dev_marinov.chatalyze.presentation.util.ScreenRoute
import io.getstream.video.android.compose.permission.LaunchCallPermissions
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.ui.components.call.controls.ControlActions
import io.getstream.video.android.compose.ui.components.call.controls.actions.DeclineCallAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.FlipCameraAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.ToggleCameraAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.ToggleMicrophoneAction
import io.getstream.video.android.compose.ui.components.call.renderer.FloatingParticipantVideo
import io.getstream.video.android.compose.ui.components.video.VideoRenderer
import io.getstream.video.android.core.Call
import io.getstream.video.android.core.GEO
import io.getstream.video.android.core.RealtimeConnection
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.core.StreamVideoBuilder
import io.getstream.video.android.model.User
import kotlinx.coroutines.launch

//// https://getstream.io/video/docs/api/authentication/
//// https://getstream.io/chat/docs/react/token_generator/

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun StreamScreen(
    navController: NavHostController,
    recipientName: String?,
    recipientPhone: String?,
    senderPhone: String?,
    typeEvent: String?,
    viewModel: StreamScreenViewModel = hiltViewModel(),
) {
    Log.d("4444", " StreamScreen loaded")

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val userToken =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoidjhybXQzZmFlcmNrIn0.48dmUEoA5zMV_dRwTh7M_sz5qCfOx78aMc1oEN0Hs3g"

    //val callTimeDuration by viewModel.callTimeDuration.collectAsStateWithLifecycle()

    val user = User(id = "RomanMarinov", name = "Tutorial")

    StreamVideo.removeClient()
    // val client = ClientStreamVideo.getClient(context = context, user = user, userToken = userToken)

    val client = StreamVideoBuilder(
        context = context,
        apiKey = "v8rmt3faerck", // demo API key
        geo = GEO.GlobalEdgeNetwork,
        user = user,
        token = userToken
    ).build()

    Log.d("4444", " senderPhone=" + senderPhone)
    val call = client.call("default", senderPhone ?: "") // это все раскоментить потом

    scope.launch {
        val result = call.join(create = true)
        result.onError {
            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
        }
    }

//    LaunchedEffect(senderPhone, recipientPhone) {
//        IfLetHelper.execute(senderPhone, recipientPhone) { phoneList ->
//            viewModel.sendStateReadyToStream(
//                firebaseCommand =
//                FirebaseCommand(
//                    topic = "",
//                    senderPhone = phoneList[0],
//                    recipientPhone = phoneList[1],
//                    typeFirebaseCommand = Constants.TYPE_FIREBASE_MESSAGE_READY_STREAM
//                )
//            )
//        }
//    }

    LaunchCallPermissions(call = call)

    VideoTheme {
        val remoteParticipants by call.state.remoteParticipants.collectAsState()
        val remoteParticipant = remoteParticipants.firstOrNull()
        val me by call.state.me.collectAsState()
        val connection by call.state.connection.collectAsState()
        val connectionState = remember { mutableStateOf(connection) }

        val participants by call.state.participants.collectAsState() // юзеры
        var parentSize: IntSize by remember { mutableStateOf(IntSize(0, 0)) }

        Log.d("4444", " me=" + me)
        Log.d("4444", " remoteParticipant=" + remoteParticipant)
        Log.d("4444", " connection=" + connection)
        Log.d("4444", " connectionState.value=" + connectionState.value)
        Log.d("4444", " participants=" + participants)
        Log.d("4444", " parentSize=" + parentSize)


        LaunchedEffect(connection) {
            if (connection == RealtimeConnection.Connected) {
                Log.d("4444", " connection Connected=" + connection)
                // тут надо записывась в бд иторию звонка
                // просто от кого и кому
                viewModel.saveHistoryCalls(
                    recipientPhone = recipientPhone ?: "",
                    senderPhone = senderPhone ?: "",
                    clientCallPhone = senderPhone ?: "",
                )
            }
        }

        // это этот юзер принимает и не он сбрасывает то этот переход назад не работает на этом юзере
        LaunchedEffect(connection) {
            if (connection == RealtimeConnection.Disconnected) {
                Log.d("4444", " Disconnected check")
                // условие для того кто является принимающим
                if (viewModel.ownPhoneSender != senderPhone) {
                    val uri = "scheme_chatalyze://calls_screen".toUri()
                    val deepLink = Intent(Intent.ACTION_VIEW, uri)
                    val pendingIntent: PendingIntent =
                        TaskStackBuilder.create(context).run {
                            addNextIntentWithParentStack(deepLink)
                            getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                            )
                        }
                    pendingIntent.send()
                }
            }
        }

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

                Column(modifier = Modifier.fillMaxSize()) {
                    VideoRenderer(
                        modifier = Modifier.weight(1f),
                        call = call,
                        video = remoteVideo
                    )
                }
            } else {
//                PreJoin
//                InProgress
//                Joined
//                RealtimeConnection.Connected



                LaunchedEffect(connection) {

                    Log.d("4444", " 1 connectionState.value=" + connectionState.value)

                    if(connectionState.value == RealtimeConnection.Disconnected) {
                        Log.d("4444", " 2 connectionState.value=" + connectionState.value)
                    }
                }


                if (connection != RealtimeConnection.Connected) {



                    // убрать джоины или прогресс бар или анимацию или ничего

                  //  еще если виво звонил а мак принял и если виво отключил то мак не уходит с экрана звонка
                    if (recipientName != null) {
                        Text(
                            text = "${connectionState.value}\n$recipientName...",
                            fontSize = 30.sp,
                            color = VideoTheme.colors.textHighEmphasis
                        )
                    } else {
                        Text(
                            text = "${connectionState.value}\n$recipientPhone...",
                            fontSize = 30.sp,
                            color = VideoTheme.colors.textHighEmphasis
                        )
                    }
                } else {

                }

//                if (connection != RealtimeConnection.Connected) {
//                    if (recipientName != null) {
//                        Text(
//                            text = "$connection\n$recipientName...",
//                            fontSize = 30.sp,
//                            color = VideoTheme.colors.textHighEmphasis
//                        )
//                    } else {
//                        Text(
//                            text = "$connection\n$recipientPhone...",
//                            fontSize = 30.sp,
//                            color = VideoTheme.colors.textHighEmphasis
//                        )
//                    }
//                } else {
//
//                }
            }


//                        Text(
//                            modifier = Modifier.fillMaxWidth(),
//                            text = callTimeDuration,
//                            fontSize = 30.sp,
//                            color = Color.Black,
//                            textAlign = TextAlign.Center,
//                        )


            me?.let { localVideo ->
                FloatingParticipantVideo(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 16.dp),
                    call = call,
                    participant = localVideo,
                    parentBounds = parentSize,
                )
            }

            val isCameraEnabled by call.camera.isEnabled.collectAsState()
            val isMicrophoneEnabled by call.microphone.isEnabled.collectAsState()

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom

            ) {
                ControlActions(
                    backgroundColor = colorResource(id = R.color.main_yellow_splash_screen),
                    call = call,
                    actions = listOf(
                        {
                            ToggleCameraAction(
                                modifier = Modifier.size(52.dp),
                                isCameraEnabled = isCameraEnabled,
                                enabledColor = colorResource(id = R.color.main_violet),
                                disabledColor = Color.White,
                                onCallAction = { call.camera.setEnabled(it.isEnabled) }
                            )
                        },
                        {
                            ToggleMicrophoneAction(
                                modifier = Modifier.size(52.dp),
                                enabledColor = colorResource(id = R.color.main_violet),
                                disabledColor = Color.White,
                                isMicrophoneEnabled = isMicrophoneEnabled,
                                onCallAction = { call.microphone.setEnabled(it.isEnabled) }
                            )
                        },
                        {
                            FlipCameraAction(
                                modifier = Modifier.size(52.dp),
                                enabledColor = colorResource(id = R.color.main_violet),
                                disabledColor = Color.White,
                                onCallAction = {
                                    call.camera.flip()
                                }
                            )
                        }, {
                            DeclineCallAction(
                                modifier = Modifier.size(52.dp),
                                onCallAction = {
//не работает закрытие экрана
                                    //
                                    scope.launch {
                                        Log.d("4444", " StreamScreen сброс")
                                        // StreamVideo.removeClient()
                                        //  navController.popBackStack(ScreenRoute.CallsScreen.route, false)
//                                        isDeclineCallAction.value = true
                                        // StreamVideo.removeClient() // перед созданием клиента удаляем
                                        call.leave()
                                        call.end()
                                        call.screenShare.mediaManager.call.end()
                                        navController.popBackStack(ScreenRoute.CallsScreen.route, false)
                                        //navController.popBackStack(ScreenRoute.CallsScreen.route, false)
//                                    viewModel.saveCallTimeDuration(
//                                        CombineCall(
//                                            name = recipientName,
//                                            sender = senderPhone,
//                                            recipient = recipientPhone,
//                                            conversationTime = callTimeDuration,
//                                            typeCall = typeEvent
//                                        )
//                                    )
                                        // viewModel.cancelCallingLoop()
                                        // delay(1500L)
                                        // navController.popBackStack(ScreenRoute.CallsScreen.route, false)
                                    }


//
                                }
                            )
                        }
                    )
                )
            }
        }
    }
}