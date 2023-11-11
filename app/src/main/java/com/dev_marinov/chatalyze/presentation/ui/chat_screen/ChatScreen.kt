package com.dev_marinov.chatalyze.presentation.ui.chat_screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.dev_marinov.chatalyze.R
import com.dev_marinov.chatalyze.presentation.util.GradientBackgroundHelper
import com.dev_marinov.chatalyze.presentation.util.TextFieldHintWriteMessage
import com.dev_marinov.chatalyze.presentation.util.ScreenRoute
import com.dev_marinov.chatalyze.presentation.util.SystemUiControllerHelper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@OptIn(ExperimentalComposeUiApi::class, FlowPreview::class)
@Composable
fun ChatScreen(
    viewModel: ChatScreenViewModel = hiltViewModel(),
    navHostController: NavHostController,
    recipientName: String?,
    recipientPhone: String?,
    senderPhone: String?
) {
    Log.d("44444", " recipientName=" + recipientName)

    GradientBackgroundHelper.SetMonochromeBackground()
    SystemUiControllerHelper.SetStatusBarColorNoGradient()

    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    val chatName = "Маринов Роман"
//
    viewModel.saveToViewModel(recipient = recipientPhone, sender = senderPhone)
    viewModel.getChatPosition(userName = chatName)

    val myPhone = "89303493563"
    val chatPosition by viewModel.chatPosition.collectAsStateWithLifecycle()

    val chatMessage by viewModel.chatMessage.collectAsStateWithLifecycle()

    var textMessage by remember { mutableStateOf("") }
    var isInitOpenVisibleChatList by remember { mutableStateOf(false) }
    var sendClickState by remember { mutableStateOf(false) }

    val col = colorResource(id = R.color.main_violet_light)

    val lazyListState: LazyListState = if (chatPosition != 0) {
        rememberLazyListState(
            initialFirstVisibleItemIndex = chatPosition
        )
    } else {
        rememberLazyListState()
    }


    ////////////////////////////////////////////////////
    // lackner
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        viewModel.toastEvent.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.connectToChat()
            } else if (event == Lifecycle.Event.ON_STOP) {
                viewModel.disconnect()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    val state = viewModel.state.value
    // с помощью state я буду формировть список сообщений


    //////////////////////////////////////////



    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    softwareKeyboardController?.hide()
                }
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.main_violet_light))
        ) {

            val constraintsTop = ConstraintSet {

                val back = createRefFor("back")
                val iconUser = createRefFor("iconUser")
                val nameAndStatusNetworkUser = createRefFor("nameAndStatusNetworkUser")
                val iconVideoCall = createRefFor("iconVideoCall")
                val iconCall = createRefFor("iconCall")
                val contentChat = createRefFor("contentChat")

                constrain(back) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    // end.linkTo(iconVideoCall.start)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.value(40.dp)
                    height = Dimension.wrapContent
                }

                constrain(iconUser) {
                    top.linkTo(parent.top)
                    // start.linkTo(parent.start)
                    end.linkTo(nameAndStatusNetworkUser.start)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.wrapContent
                    height = Dimension.wrapContent
                }

                constrain(nameAndStatusNetworkUser) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.wrapContent
                    height = Dimension.wrapContent
                }

                constrain(iconVideoCall) {
                    top.linkTo(parent.top)
                    //start.linkTo(back.end)
                    end.linkTo(iconCall.start)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.wrapContent
                    height = Dimension.wrapContent
                }

                constrain(iconCall) {
                    top.linkTo(parent.top)
                    // start.linkTo(iconVideoCall.end, margin = 8.dp)
                    end.linkTo(parent.end, margin = 4.dp)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.wrapContent
                    height = Dimension.wrapContent
                }
            }

            ConstraintLayout(
                constraintSet = constraintsTop,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back_to_prev_screen),
                    contentDescription = "back",
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(30.dp)
                        .clip(RoundedCornerShape(50))
                        .clickable {
                            navHostController.navigate(ScreenRoute.ChatsScreen.route)
                            // viewModel.saveHideNavigationBar(false)
                        }
                        .layoutId("back")
                )

                Icon(
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(30.dp)
                        .layoutId("iconUser"),
                    painter = painterResource(id = R.drawable.ic_user),
                    contentDescription = "",
                    tint = Color.White,
                )

                Column(
                    modifier = Modifier
                        // .background(Color.Red)
                        .padding(4.dp)
                        .layoutId("nameAndStatusNetworkUser")
                ) {
                    recipientName?.let {
                        Text(
                            text = it,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Text(
                        text = "в сети",
                        color = Color.White,
                        fontSize = 12.sp,
                    )
                }

                IconButton(
                    modifier = Modifier
                        .width(40.dp)
                        .height(40.dp)
                        .layoutId("iconVideoCall")
                        //  .background(Color.Blue)
                        .size(35.dp)
                        .padding(start = 8.dp, end = 8.dp),
                    onClick = {

                    }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_video_call),
                        contentDescription = "",
                        tint = Color.White,
                    )
                }

                IconButton(
                    modifier = Modifier
                        .width(35.dp)
                        .height(35.dp)
                        .layoutId("iconCall")
                        // .background(Color.Gray)
                        .size(35.dp)
                        .padding(8.dp),
                    onClick = {

                    }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_call),
                        contentDescription = "",
                        tint = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(colorResource(id = R.color.main_yellow_new_chat_screen))
                    .fillMaxWidth()
            ) {

                val contentChat = ConstraintSet {
                    val chatContent = createRefFor("chatContent")

                    constrain(chatContent) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.wrapContent
                        height = Dimension.wrapContent
                    }
                }

                ConstraintLayout(
                    constraintSet = contentChat,
                    modifier = Modifier
                        .fillMaxWidth()
                        .layoutId("chatContent")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                        // .height(300.dp))
                    )
                    {
                        BoxWithConstraints {
//                            if (lazyListState == null) {
//                                isInitOpenVisibleChatList = true
//                            }
//                            if (isInitOpenVisibleChatList) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(start = 8.dp, end = 8.dp),
                                state = lazyListState
                            ) {
                                items(chatMessage) { item ->
                                    // add object message
                                    Log.d("4444", " item=" + item.sender + "  senderPhone=" + senderPhone)
                                    /////////////////////////////////

                                    val isOwnMessage = item.sender == senderPhone
                                    Box(
                                        contentAlignment = if (isOwnMessage) {
                                            Alignment.CenterEnd
                                        } else Alignment.CenterStart,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .width(200.dp)
                                                .drawBehind {
                                                    val cornerRadius = 10.dp.toPx()
                                                    val triangleHeight = 20.dp.toPx()
                                                    val triangleWidth = 25.dp.toPx()
                                                    val trianglePath = Path().apply {
                                                        if (isOwnMessage) {
                                                            moveTo(
                                                                size.width,
                                                                size.height - cornerRadius
                                                            )
                                                            lineTo(
                                                                size.width,
                                                                size.height + triangleHeight
                                                            )
                                                            lineTo(
                                                                size.width - triangleWidth,
                                                                size.height - cornerRadius
                                                            )
                                                            close()
                                                        } else {
                                                            moveTo(0f, size.height - cornerRadius)
                                                            lineTo(0f, size.height + triangleHeight)
                                                            lineTo(
                                                                triangleWidth,
                                                                size.height - cornerRadius
                                                            )
                                                            close()
                                                        }
                                                    }
                                                    drawPath(
                                                        path = trianglePath,
                                                        color = if (isOwnMessage) col else Color.DarkGray
                                                    )
                                                }
                                                .background(
                                                    color = if (isOwnMessage) col else Color.DarkGray,
                                                    shape = RoundedCornerShape(10.dp)
                                                )
                                                .padding(8.dp)
                                        ) {
                                            Text(
                                                text = item.sender,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = item.textMessage,
                                                color = Color.White
                                            )
                                            Text(
                                                text = item.createdAt,
                                                color = Color.White,
                                                modifier = Modifier.align(Alignment.End)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(32.dp))

                                    //////////////////////////////////////
                                }
                                // Добавьте другие элементы списка здесь
                            }
                           // }
                        }

                        LaunchedEffect(lazyListState) {
                            snapshotFlow {
                                lazyListState?.firstVisibleItemIndex
                            }
                                .debounce(500L)
                                .collectLatest { firstIndex ->
                                    firstIndex?.let {
                                    Log.d("4444", " lastVisibleIndex =" + it)
                                        viewModel.saveScrollChatPosition(
                                            keyUserName = chatName,
                                            position = it
                                        )
                                    }
                                }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .background(colorResource(id = R.color.main_violet_light))
                    .fillMaxWidth()
            ) {

                val constraintBottomControl = ConstraintSet {
                    val bottomControl = createRefFor("bottomControl")

                    constrain(bottomControl) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                        height = Dimension.wrapContent
                    }
                }

                ConstraintLayout(
                    constraintSet = constraintBottomControl,
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .padding(top = 8.dp, end = 8.dp, bottom = 8.dp)
                        .layoutId("bottomControl")
                ) {
                    Row(
                        modifier = Modifier
                            // .width(200.dp)

                            .background(colorResource(id = R.color.main_violet_light))
                            .layoutId("writeMessage"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .size(30.dp)
                                .clip(RoundedCornerShape(20))
                                .clickable {

                                },
                            painter = painterResource(id = R.drawable.ic_attach),
                            contentDescription = "",
                            tint = Color.White,
                        )

//                        TextFieldHintWriteMessage(
//                            value = textMessage,
//                            onValueChanged = { textMessage = it },
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(IntrinsicSize.Min)
//                                .clip(RoundedCornerShape(20))
//                                .background(MaterialTheme.colors.surface),
//                            viewModel = viewModel
//                        )

                        TextFieldHintWriteMessage(
                            value = viewModel.messageText.value,
                            onValueChanged = viewModel::onMessageChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min)
                                .clip(RoundedCornerShape(20))
                                .background(MaterialTheme.colors.surface),
                            viewModel = viewModel,
                            onSendClick = {
                                sendClickState = true
                            }
                        )
                    }
                }
            }

            LaunchedEffect(chatMessage) {
                if (sendClickState) {
                    lazyListState.animateScrollToItem(chatMessage.size)
                    sendClickState = false
                }
            }
        }
    }
}



