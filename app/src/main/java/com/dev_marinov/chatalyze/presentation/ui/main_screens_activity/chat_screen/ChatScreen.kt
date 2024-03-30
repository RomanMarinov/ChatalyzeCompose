package com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chat_screen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
import com.dev_marinov.chatalyze.domain.model.chat.Message
import com.dev_marinov.chatalyze.presentation.util.Constants
import com.dev_marinov.chatalyze.presentation.util.CorrectNumberFormatHelper
import com.dev_marinov.chatalyze.presentation.util.CustomDateTimeHelper
import com.dev_marinov.chatalyze.presentation.util.EditFormatPhoneHelper
import com.dev_marinov.chatalyze.presentation.util.GradientBackgroundHelper
import com.dev_marinov.chatalyze.presentation.util.IfLetHelper
import com.dev_marinov.chatalyze.presentation.util.ScreenRoute
import com.dev_marinov.chatalyze.presentation.util.SnackBarHostHelper
import com.dev_marinov.chatalyze.presentation.util.SystemUiControllerHelper
import com.dev_marinov.chatalyze.presentation.util.TextFieldHintWriteMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatScreen(
    viewModel: ChatScreenViewModel = hiltViewModel(),
    navHostController: NavHostController,
    recipientName: String,
    recipientPhone: String,
    senderPhone: String,
) {
    Log.d("4444", " ChatScreen loaded")

    Log.d("4444", " ChatScreen пришло при открытии recipientPhone=" + recipientPhone
            + " senderPhone=" + senderPhone)

    // вывод такой что при открытии из пуша на vivo
    // то recipient не vivo, а sender - номер vivo

    SystemUiControllerHelper.SetStatusBarColorNoGradient()
    SystemUiControllerHelper.SetNavigationBars(isVisible = true)
    GradientBackgroundHelper.SetMonochromeBackground()

    val recipientNameState = remember {
        mutableStateOf(
            recipientName.replace(
                Regex("[{}]", RegexOption.IGNORE_CASE),
                ""
            )
        )
    }
    val recipientPhoneState = remember {
        mutableStateOf(
            recipientPhone.replace(
                Regex("[{}]", RegexOption.IGNORE_CASE),
                ""
            )
        )
    }
    val senderPhoneState =
        remember { mutableStateOf(senderPhone.replace(Regex("[{}]", RegexOption.IGNORE_CASE), "")) }

    val isMakeCallState = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(10L)
        viewModel.saveHideNavigationBar(true)
    }

    BackHandler {
        navHostController.navigate(ScreenRoute.ChatsScreen.route)
        viewModel.saveHideNavigationBar(false)
    }

    val colorViolet = colorResource(id = R.color.main_violet_light)
    val colorVioletChat = colorResource(id = R.color.color_companion_chat)

    val softwareKeyboardController = LocalSoftwareKeyboardController.current

    val context = LocalContext.current

    val isSessionState by viewModel.isSessionState.collectAsStateWithLifecycle("")
    val isGrantedPermissions by viewModel.isGrantedPermissions.collectAsStateWithLifecycle(false)
    val getStateUnauthorized by viewModel.getStateUnauthorized.collectAsStateWithLifecycle(false)
    val onlineUserStateList by viewModel.onlineUserStateList.collectAsStateWithLifecycle(emptyList())
    var onlineOrOffline by remember { mutableStateOf("") }

    val isOpenScrollState = remember { mutableStateOf(true) }
    val chatPosition by viewModel.chatPosition.collectAsStateWithLifecycle()
    val isSaveScroll = remember { mutableStateOf(false) }
    val isScrollDown = remember { mutableStateOf(false) }

    // не знаю пока зачем
    val chatMessage by viewModel.chatMessage.collectAsStateWithLifecycle()

    val sendClickState = remember { mutableStateOf(false) }

    // избавляет от 4 перекомановок
    val state: ChatState = viewModel.state.value

    LaunchedEffect(key1 = true) {
        viewModel.toastEvent.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(senderPhoneState.value, recipientPhoneState.value) {
        viewModel.saveLocallyUserPairChat(
            senderPhone = senderPhoneState.value,
            recipientPhone = recipientPhoneState.value
        )
    }

    LaunchedEffect(key1 = true) {
        Log.d("4444", " ChatScreen save recipientPhoneState.value=" + recipientPhoneState.value
        + " senderPhoneState.value=" + senderPhoneState.value)
        viewModel.saveToViewModel(
            recipient = recipientPhoneState.value,
            sender = senderPhoneState.value
        )
    }

    // два LaunchedEffect закоментированными избавляют от 4 перекомпановки
    LaunchedEffect(recipientNameState.value) {
        Log.d("4444", " ChatScreen getChatPosition recipientNameState.value=" + recipientNameState.value)
        viewModel.getChatPosition(keyUserName = recipientNameState.value)
    }

    LaunchedEffect(isSessionState) {
        if (isSessionState == Constants.SESSION_SUCCESS) {
            Log.d("4444", " ChatScreen isSessionState == Constants.SESSION_SUCCESS")
            viewModel.getAllMessageChat()

            viewModel.saveCompanionOnTheServer(
                senderPhone = senderPhoneState.value,
                recipientPhone = recipientPhoneState.value
            )
        }

        if (isSessionState == Constants.SESSION_ERROR) {
            Log.d("4444", " ChatScreen isSessionState == Constants.SESSION_ERROR")
        }
        if (isSessionState == Constants.SESSION_CLOSE) {
            Log.d("4444", " ChatScreen isSessionState == Constants.SESSION_CLOSE")
        }
    }

    LaunchedEffect(onlineUserStateList) {
        Log.d("4444", " ChatScreen onlineUserStateList=" + onlineUserStateList)
        onlineUserStateList.forEach {
            if (it.userPhone == recipientPhoneState.value) {
                onlineOrOffline = it.onlineOrOffline
                return@forEach
            }
        }
    }


    // оставил на всякий
//    snapshotFlow { // чтобы не часто срабатывало
//        lazyListState.firstVisibleItemIndex
//    }
//        .debounce(500L)
//        .collectLatest { firstIndex ->
//            Log.d("4444", " lastVisibleIndex =" + firstIndex)
//            viewModel.saveScrollChatPosition(
//                keyUserName = recipientNameState.value,
//                position = firstIndex
//            )
//        }

    // не влияет
    val lazyListState: LazyListState = rememberLazyListState()
    val layoutInfo = remember {
        derivedStateOf {
            var lastVisibleItem = 0
            val visibleItemsInfo: List<LazyListItemInfo> = lazyListState.layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isNotEmpty()) {
                lastVisibleItem = visibleItemsInfo.last().index
            }
            lastVisibleItem
        }
    }

   // layoutInfo.value // значение скрола текущего
    //isSaveScroll.value // флаг становиться true когда произойдет быстрый скролл к цели

    // на 2 меньше перекомпановки
    LaunchedEffect(layoutInfo.value, isSaveScroll.value) {
        Log.d("4444", " ChatScreen recipientNameState.value=" + recipientNameState.value
        + " isSaveScroll.value=" + isSaveScroll.value + " layoutInfo.value=" + layoutInfo.value)
        if (recipientNameState.value.isNotEmpty() && isSaveScroll.value) {
            Log.d("4444", " ChatScreen saveScrollPosition")
            viewModel.saveScrollChatPosition(
                keyUserName = recipientNameState.value,
                position = layoutInfo.value
            )
        } else if(recipientNameState.value.isNotEmpty() && layoutInfo.value > 0) {
            viewModel.saveScrollChatPosition(
                keyUserName = recipientNameState.value,
                position = layoutInfo.value
            )
        }
    }

    // не влияиет
    LaunchedEffect(chatPosition, state.messages.size) {
        if (state.messages.size == 1) {
            viewModel.saveScrollChatPosition(
                keyUserName = recipientNameState.value,
                position = layoutInfo.value
            )
        }

        Log.d("4444", " ChatScreen chatPosition=" + chatPosition)
        if (chatPosition != 0 && isOpenScrollState.value) {
            Log.d("4444", " 333 ChatScreen LaunchedEffect(chatPosition)= " + chatPosition)
            lazyListState.scrollToItem(chatPosition)
            isOpenScrollState.value = false
            isSaveScroll.value = true
        }
    }

    val isShowScrollDownButton by remember(state.messages) {
        derivedStateOf {
            var targetScroll = false
            val visibleItemsInfo: List<LazyListItemInfo> = lazyListState.layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isNotEmpty()) {
                val lastVisibleItem = visibleItemsInfo.last().index
                targetScroll = lastVisibleItem <= state.messages.size - 5
            }
            targetScroll
        }
    }

    // не влияет (даже увеличивает кол-во перекомпановки)
    val isReceiverRegistered = remember { mutableStateOf(false) }
    val socketBroadcastReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    "receiver_single_message" -> {
                        Log.d("4444", " socketBroadcastReceiver SESSION_ACTION_SUCCESS")

                        val sender = intent.getStringExtra("sender")
                        val recipient = intent.getStringExtra("recipient")
                        val textMes = intent.getStringExtra("textMessage")
                        val createdAt = intent.getStringExtra("createdAt")

                        IfLetHelper.execute(sender, recipient, textMes, createdAt) {
                            viewModel.observeMessages2(
                                sender = it[0],
                                recipient = it[1],
                                textMessage = it[2],
                                createdAt = it[3]
                            )
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = true) {
        val intentFilter = IntentFilter("receiver_single_message")
        context.registerReceiver(
            socketBroadcastReceiver,
            intentFilter,
            Context.RECEIVER_NOT_EXPORTED
        )
        isReceiverRegistered.value = true
    }

    val localLifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(
        key1 = localLifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_START -> {
                        Log.d("4444", " ChatScreen Lifecycle.Event.ON_START")
                    }

                    Lifecycle.Event.ON_STOP -> { // когда свернул
                        Log.d("4444", " ChatScreen Lifecycle.Event.ON_STOP")
                        viewModel.saveCompanionOnTheServer(
                            senderPhone = senderPhoneState.value,
                            recipientPhone = ""
                        )

                        if (isReceiverRegistered.value) {
                            context.unregisterReceiver(socketBroadcastReceiver)
                            isReceiverRegistered.value = false
                        }
                    }

                    Lifecycle.Event.ON_DESTROY -> { // когда удалил из стека
                        Log.d("4444", " ChatScreen Lifecycle.Event.ON_DESTROY")
                        viewModel.saveCompanionOnTheServer(
                            senderPhone = senderPhoneState.value,
                            recipientPhone = ""
                        )
                        if (isReceiverRegistered.value) {
                            context.unregisterReceiver(socketBroadcastReceiver)
                            isReceiverRegistered.value = false
                        }
                    }

                    else -> {}
                }
            }
            localLifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                localLifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )

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
                        .padding(4.dp)
                        .layoutId("nameAndStatusNetworkUser")
                ) {
                    Text(
                        text = recipientNameState.value,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = onlineOrOffline,
                        color = Color.White,
                        fontSize = 12.sp,
                    )
                }

                IconButton(
                    modifier = Modifier
                        .width(46.dp)
                        .height(46.dp)
                        .layoutId("iconVideoCall")
                        .size(35.dp)
                        .padding(start = 8.dp, end = 8.dp),
                    onClick = {
                        isMakeCallState.value = true
                    }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_video_call),
                        contentDescription = "",
                        tint = Color.White,
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
                    val floatingButton = createRefFor("floatingButton")

                    constrain(chatContent) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.wrapContent
                        height = Dimension.wrapContent
                    }

                    constrain(floatingButton) {
                      //  top.linkTo(chatContent.top)
                       // start.linkTo(chatContent.start)
                        end.linkTo(chatContent.end)
                        bottom.linkTo(chatContent.bottom)
                        width = Dimension.wrapContent
                        height = Dimension.wrapContent
                    }
                }

                // если закоментировать это будет на 3 меньше перекомпановки ChatScreen loaded
                ConstraintLayout(
                    constraintSet = contentChat,
                    modifier = Modifier
                        .fillMaxWidth()
                        .layoutId("chatContent")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        BoxWithConstraints {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(start = 16.dp, end = 16.dp),
                                state = lazyListState
                            ) {

                                Log.d("4444", " ChatScreen state.messages=" + state.messages.size)
                                items(state.messages) { item ->

                                    val isOwnMessage = item.sender == senderPhoneState.value
                                    Spacer(modifier = Modifier.height(8.dp))
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
                                                    val triangleHeight = 5.dp.toPx()
                                                    val triangleWidth = 30.dp.toPx()

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
                                                        color = if (isOwnMessage) colorViolet else Color.LightGray
                                                    )
                                                }
                                                .background(
                                                    color = if (isOwnMessage) colorViolet else Color.LightGray,
                                                    shape = RoundedCornerShape(10.dp)
                                                )
                                                .padding(8.dp)
                                        ) {
                                            val titleName = if (isOwnMessage) {
                                                EditFormatPhoneHelper.edit(phone = senderPhoneState.value)
                                            } else {
                                                recipientNameState.value.ifEmpty {
                                                    EditFormatPhoneHelper.edit(
                                                        phone = recipientPhoneState.value
                                                    )
                                                }
                                            }

                                            Text(
                                                text = titleName,
                                                //fontWeight = FontWeight.Bold,
                                                color = if (isOwnMessage) Color.White else colorVioletChat
                                            )
                                            Text(
                                                text = item.textMessage,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isOwnMessage) Color.White else colorVioletChat
                                            )
                                            Text(
                                                text = CustomDateTimeHelper.formatDateTime(
                                                    dateTimeString = item.createdAt
                                                ),
                                                color = if (isOwnMessage) Color.White else colorVioletChat,
                                                modifier = Modifier.align(Alignment.End)
                                            )
                                        }
                                        // Spacer(modifier = Modifier.height(8.dp))
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                            // Spacer(modifier = Modifier.height(8.dp))

                            if(isShowScrollDownButton) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(end = 16.dp, bottom = 16.dp)
                                            .size(50.dp)
                                            .clip(RoundedCornerShape(100))
                                            .background(colorResource(id = R.color.main_yellow_new_chat_screen))
                                    ) {
                                        Icon(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(100))
                                                .clickable {
                                                    isScrollDown.value = true
                                                }
                                                .layoutId("floatingButton"),
                                            painter = painterResource(id = R.drawable.ic_scroll_down),
                                            contentDescription = "",
                                            tint = colorResource(id = R.color.main_violet)
                                        )
                                    }
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
                                sendClickState.value = true
                            }
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(state.messages) {
        // у отправителя виво не стирается сообщение и не отобржаается у себя
        if (sendClickState.value) { // это сработает если я отправитель
            viewModel.clearMessageTextField()
            lazyListState.animateScrollToItem(state.messages.size)
            sendClickState.value = false
        } else { // это сработает если я получатель
            lazyListState.animateScrollToItem(state.messages.size)
        }
    }

    LaunchedEffect(isScrollDown.value) {
        if (isScrollDown.value) {
            lazyListState.animateScrollToItem(state.messages.size)
            isScrollDown.value = false
        }
    }

    if (isMakeCallState.value) {
        DialogShowMakeCall(
            recipientName = recipientNameState.value,
            recipientPhone = recipientPhoneState.value,
            onDismiss = {
                isMakeCallState.value = false
            },
            onConfirm = {
                isMakeCallState.value = false
                navHostController.navigate(
                    route = ScreenRoute.CallScreen.withArgs2(
                        recipientName = recipientNameState.value ?: recipientPhoneState.value,
                        recipientPhone = CorrectNumberFormatHelper.getCorrectNumber(
                            recipientPhoneState.value
                        ),
                        senderPhone = CorrectNumberFormatHelper.getCorrectNumber(senderPhoneState.value),
                        typeEvent = Constants.OUTGOING_CALL_EVENT
                    )
                )
            }
        )
    }

    if (getStateUnauthorized) {
        SnackBarHostHelper.Show(message = stringResource(id = R.string.unauthorized_access))
        Log.d("4444", " getStateUnauthorized показать тост")
        // тут перейти на экран входа
    }
}