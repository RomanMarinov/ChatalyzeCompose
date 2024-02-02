package com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chat_screen

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.dev_marinov.chatalyze.R
import com.dev_marinov.chatalyze.presentation.util.Constants
import com.dev_marinov.chatalyze.presentation.util.GradientBackgroundHelper
import com.dev_marinov.chatalyze.presentation.util.ScreenRoute
import com.dev_marinov.chatalyze.presentation.util.SystemUiControllerHelper
import com.dev_marinov.chatalyze.presentation.util.TextFieldHintWriteMessage
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@OptIn(ExperimentalComposeUiApi::class, FlowPreview::class)
@Composable
fun ChatScreen(
    viewModel: ChatScreenViewModel = hiltViewModel(),
    navHostController: NavHostController,
    recipientName: String?,
    recipientPhone: String?,
    senderPhone: String?,
) {

    BackHandler {
        navHostController.navigate(ScreenRoute.ChatsScreen.route)
        viewModel.saveHideNavigationBar(false)
    }

    GradientBackgroundHelper.SetMonochromeBackground()
    SystemUiControllerHelper.SetStatusBarColorNoGradient()

    val col = colorResource(id = R.color.main_violet_light)

    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    val chatName = "Маринов Роман"

    val isSessionState by viewModel.isSessionState.collectAsStateWithLifecycle("")
    val isGrantedPermissions by viewModel.isGrantedPermissions.collectAsStateWithLifecycle(false)

    val onlineUserStateList by viewModel.onlineUserStateList.collectAsStateWithLifecycle(emptyList())
    var onlineOrOffline by remember { mutableStateOf("") }

    val name by viewModel.recipientName.collectAsStateWithLifecycle("")

    val myPhone = "89303493563"
    val chatPosition by viewModel.chatPosition.collectAsStateWithLifecycle()
    val chatMessage by viewModel.chatMessage.collectAsStateWithLifecycle()

    var textMessage by remember { mutableStateOf("") }
    var isInitOpenVisibleChatList by remember { mutableStateOf(false) }
    var sendClickState by remember { mutableStateOf(false) }



    viewModel.saveLocallyUserPairChat(senderPhone = senderPhone, recipientPhone = recipientPhone)
    viewModel.saveToViewModel(recipient = recipientPhone, sender = senderPhone)
    viewModel.getChatPosition(userName = chatName)

    viewModel.getNameAndOnlineOrOffline(recipientPhone = recipientPhone)

    LaunchedEffect(isSessionState) {
        if (isSessionState == Constants.SESSION_SUCCESS) {
            Log.d("4444", " ChatScreen isSessionState == Constants.SESSION_SUCCESS")
            viewModel.getAllMessageChat()
            // хуй пока закрыл ебаная ошибка
            viewModel.observeMessages()
        }
    }

    LaunchedEffect(onlineUserStateList) {
        onlineUserStateList.forEach {
            if (it.userPhone == recipientPhone) {
                onlineOrOffline = it.onlineOrOffline
                return@forEach
            }
        }
    }

    val lazyListState: LazyListState = if (chatPosition != 0) {
        rememberLazyListState(
            initialFirstVisibleItemIndex = chatPosition
        )
    } else {
        rememberLazyListState()
    }

//    val localLifecycleOwner = LocalLifecycleOwner.current
//    DisposableEffect(
//        key1 = localLifecycleOwner,
//        effect = {
//            val observer = LifecycleEventObserver { _, event ->
//                when (event) {
//                    Lifecycle.Event.ON_START -> {
//                        Log.d("4444", " ChatScreen Lifecycle.Event.ON_START")
//                    }
//
//                    Lifecycle.Event.ON_STOP -> { // когда свернул
//                        Log.d("4444", " ChatScreen Lifecycle.Event.ON_STOP")
//                    }
//
//                    Lifecycle.Event.ON_DESTROY -> { // когда удалил из стека
//                        Log.d("4444", " ChatScreen Lifecycle.Event.ON_DESTROY")
//                    }
//                    else -> {}
//                }
//            }
//            localLifecycleOwner.lifecycle.addObserver(observer)
//            onDispose {
//                localLifecycleOwner.lifecycle.removeObserver(observer)
//            }
//        }
//    )
//    ////////////////////////////////////////////////////

    // lackner
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        viewModel.toastEvent.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    // с помощью state я буду формировть список сообщений
    val state = viewModel.state.value

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
                            viewModel.saveHideNavigationBar(false)
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
                   // (name.ifEmpty { recipientPhone })?.let {
                        Text(
                            text = "it",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                //    }
                    Text(
                        text = onlineOrOffline,
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
                                items(state.messages) { item ->
                                    // add object message
                                    // Log.d("4444", " item=" + item.sender + "  senderPhone=" + senderPhone)


                                    /////////////////////////////////
                                    //  Log.d("4444", " chatMessage=" + chatMessage)
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
                                        // Log.d("4444", " lastVisibleIndex =" + it)
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
                        .layoutId("bottomControl"),
                ) {
                    Row(
                        modifier = Modifier
                            // .width(200.dp)
                            .background(colorResource(id = R.color.main_violet_light))
                            .layoutId("writeMessage"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
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
                                Log.d(
                                    "4444",
                                    " TextFieldHintWriteMessage sendClickState=" + sendClickState
                                )
                            }
                        )
                    }
                }
            }

            LaunchedEffect(state.messages) {
                Log.d(
                    "4444",
                    " сработал LaunchedEffect(chatMessage) sendClickState=" + sendClickState
                )
                viewModel.clearMessageTextField()
                if (sendClickState) { // это сработает у отправителя
                    lazyListState.animateScrollToItem(state.messages.size)
                    sendClickState = false
                } else {
                    // это сработает у получателя
                    Log.d(
                        "4444",
                        " сработал LaunchedEffect(chatMessage) chatPosition=" + chatPosition
                    )
                    Log.d(
                        "4444",
                        " сработал LaunchedEffect(chatMessage) chatPosstate.messages.size=" + state.messages.size
                    )

                    if (chatPosition in state.messages.size - 7..state.messages.size) {
                        lazyListState.animateScrollToItem(state.messages.size)
                    }
                }
            }
        }
    }
}