package com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.calls_screen

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dev_marinov.chatalyze.R
import com.dev_marinov.chatalyze.domain.model.call.HistoryCallWithName
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.calls_screen.model.HistoryCall
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chats_screen.CustomBackStackOnlyBottomSheetInChatsScreen
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chats_screen.model.Contact
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chats_screen.uiFormatPhoneNumber
import com.dev_marinov.chatalyze.presentation.util.Constants
import com.dev_marinov.chatalyze.presentation.util.CorrectNumberFormatHelper
import com.dev_marinov.chatalyze.presentation.util.GradientBackgroundHelper
import com.dev_marinov.chatalyze.presentation.util.RememberContacts
import com.dev_marinov.chatalyze.presentation.util.ScreenRoute
import com.dev_marinov.chatalyze.presentation.util.SystemUiControllerHelper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
@Composable
fun CallsScreen(
    navController: NavHostController,
    viewModel: CallsScreenViewModel = hiltViewModel(),
) {

    Log.d("4444", " CallsScreen загрузился")
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    SystemUiControllerHelper.SetSystemBars(true)
    SystemUiControllerHelper.SetStatusBarColor()
    SystemUiControllerHelper.SetNavigationBars(isVisible = true)
    //SystemUiControllerHelper.SetStatusBarColorNoGradient()
    GradientBackgroundHelper.SetMonochromeBackground()
    // SystemUiControllerHelper.SetStatusBarColorNoGradient()

//    val contacts = viewModel.contacts.collectAsStateWithLifecycle(initialValue = listOf())
    val contacts = viewModel.filteredContacts.collectAsStateWithLifecycle(initialValue = listOf())


    val historyCallsCombine =
        viewModel.historyCallsCombine.collectAsStateWithLifecycle(initialValue = listOf())

    val isSessionState by viewModel.isSessionState.collectAsStateWithLifecycle("")
    val getOwnPhoneSender by viewModel.getOwnPhoneSender.collectAsStateWithLifecycle("")

    // может из за false не работать но скорей всего работает
    val getChatListFlag by viewModel.getChatListFlag.collectAsStateWithLifecycle(false)

    val pushTypeDisplay by viewModel.pushTypeDisplay.collectAsStateWithLifecycle(0)

    val makeCallStatusCode by viewModel.makeCallStatusCode.collectAsStateWithLifecycle()

    Log.d("4444", " combineChatList=" + historyCallsCombine.value)
    // сюда добавить статус соедения удачу сокета
    LaunchedEffect(getOwnPhoneSender, isSessionState) {
        if (isSessionState == Constants.SESSION_SUCCESS) {
            viewModel.canGetChatList(can = true)
            // viewModel.ebnutCombine()
        }
    }

    if (getChatListFlag) {
        //viewModel.createOnlineUserStateList()

        val contactsFlow = RememberContacts(context = context)
        LaunchedEffect(Unit) {
            scope.launch {
                contactsFlow.collect {
                    viewModel.createContactsFlow(it)

                    //viewModel.createOnlineUserStateList()
                    //viewModel.createCombineFlow()
//                    viewModel.createChatListFlow()
//                    viewModel.createCombineFlow()
                }
            }
        }
    }

    // есть ли у приложения разрешение на отображение наложений поверх других приложений
    val hasOverlayPermission = Settings.canDrawOverlays(context)
    //LaunchedEffect(hasOverlayPermission) {
    if (!hasOverlayPermission && pushTypeDisplay != 0) {
        DialogShowPushOrScreen(
            onDismiss = { },
            onConfirm = { selectedBoxIndex ->
                if (selectedBoxIndex == 0) {
                    viewModel.savePushTypeDisplay(selectedBoxIndex = selectedBoxIndex)
                } else {
                    openSystemSettingOverlayApp(context = context)
                }
            }
        )
    }

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Expanded,
        skipHalfExpanded = true
    )

    var isSheetOpen by rememberSaveable { mutableStateOf(false) }
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }

    val isOpenModalBottomSheet by viewModel.isOpenModalBottomSheet.collectAsStateWithLifecycle()
    if (isOpenModalBottomSheet) {
        viewModel.onClickHideNavigationBar(isHide = true)
        LaunchedEffect(Unit) {
            scope.launch {
                delay(50L)
                openBottomSheet = !openBottomSheet
                isSheetOpen = true
                sheetState.show()
            }
        }
    }

    // закрытие не через крестик
    LaunchedEffect(Unit) {
        snapshotFlow { sheetState.currentValue }
            .collect {
                if (sheetState.currentValue == ModalBottomSheetValue.Hidden) {
                    scope.launch {
                        isSheetOpen = false
                        viewModel.onClickHideNavigationBar(isHide = false)
                        sheetState.hide()
                    }
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
        //  .background(colorResource(id = R.color.main_violet_light))
        //  .systemBarsPadding()
    ) {

        val constraints = ConstraintSet {
            val headerChatText = createRefFor("header_calls_text")
            val createChatIcon = createRefFor("create_call_icon")

            constrain(headerChatText) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                //end.linkTo(createChatIcon.start)
                bottom.linkTo(parent.bottom)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }

            constrain(createChatIcon) {
                top.linkTo(parent.top)
                //start.linkTo(headerChatText.end)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }
        }

        ConstraintLayout(
            constraintSet = constraints,
            modifier = Modifier
                .fillMaxWidth()
                //.background(Color.Red)
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.call_screen_calls),
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .height(50.dp)
                    .layoutId("header_calls_text")
                    .clickable {
                        try {
                            Log.d("4444", " StreamScreen click")

                          //  navController.navigate(route = ScreenRoute.StreamScreen.route)

//                            navController.navigate(
//                                route = ScreenRoute.StreamScreen.withArgs(
//                                    recipientName = "roma",
//                                    recipientPhone = "9303454564",
//                                    senderPhone = "5551234567",
//                                    typeEvent = Constants.TYPE_FIREBASE_MESSAGE_READY_STREAM
//                                )
//                            )
//                            Log.d(
//                                "4444",
//                                " ScreenRoute.CallScreen.route=" + ScreenRoute.CallScreen.route
//                            )
//                            Log.d("4444", " ScreenRoute.CallScreen=" + ScreenRoute.CallScreen)

//                            navController.navigate(
//                                route = ScreenRoute.StreamScreen.withArgs(
//                                    recipientName = "roman",
//                                    recipientPhone = "5551234567",
//                                    senderPhone = "9303454564",
//                                    typeEvent = Constants.TYPE_FIREBASE_MESSAGE_READY_STREAM // не используется хуй
//                                )
//                            )

//                        val uri = "chatalyze://${"call_screen"}/${"roma"}${"9303454564"}/${"5551234567"}/${Constants.OUTGOING_CALL_EVENT}".toUri()
                            // val uri = "scheme_chatalyze://${"call_screen"}/${"roma"}/${"9303454564"}/${"5551234567"}/${Constants.INCOMING_CALL_EVENT}".toUri()

                            //val uri = "scheme_chatalyze://${"create_password_screen"}".toUri()

//                            val deepLink = Intent(Intent.ACTION_VIEW, uri, context, MainActivity::class.java)
//                            deepLink.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or
//                                    Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
//                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
//                                    Intent.FLAG_ACTIVITY_NEW_TASK
//
//                            val pendingIntent: PendingIntent = TaskStackBuilder.create(context).run {
//                                addNextIntentWithParentStack(deepLink)
//                                    .getPendingIntent(1, PendingIntent.FLAG_IMMUTABLE)
//                            }
//                            pendingIntent.send()

//                            val deepLinkIntent = Intent(
//                                Intent.ACTION_VIEW,
//                                Uri.parse("profile_screen"),
////                                Uri.parse("www.schemechatalyze.com"),
////                                Uri.parse("www.schemechatalyze.com/${"profile_screen"}"),
//                                context,
//                                MainScreensActivity::class.java
//                            )
//
//                            val deepLinkPendingIntent = TaskStackBuilder.create(context).run {
//                                        addNextIntentWithParentStack(deepLinkIntent)
//                                        getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
//                            }
//                            deepLinkPendingIntent?.send()


//                            val intent = Intent(context, MainActivity::class.java).apply {
//                                putExtra("type", "хуй")
//                            }
//                            val pendingIntent = TaskStackBuilder.create(context).run {
//                                addNextIntentWithParentStack(intent)
//                                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
//                            }
//                            pendingIntent.send()

////////////////////
//                            val deepLinkIntent = Intent(
//                                Intent.ACTION_VIEW,
//                               // Uri.parse("profile_screen"),
////                                Uri.parse("profile_screen"),
//                                Uri.parse("create_password_screen"),
////                                Uri.parse("www.schemechatalyze.com/${"profile_screen"}"),
//                                context,
//                                MainActivity::class.java
//                            )
//
//                            val deepLinkPendingIntent = TaskStackBuilder.create(context).run {
//                                        addNextIntentWithParentStack(deepLinkIntent)
//                                        getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
//                            }
//                            deepLinkPendingIntent?.send()
/////////////////////////////


//                            android:host="create_password_screen"
//                            android:scheme="scheme_chatalyze" />


                            //проверить эту ебаную ссылку просто повесить клик на любую кноку и че будет
//                            val uri = "scheme_chatalyze://${ScreenRoute.CallScreen.route}/${"roma"}/${"9303454564"}/${"5551234567"}/${Constants.INCOMING_CALL_EVENT}".toUri()
//                            val uri =
//                                "scheme_chatalyze://call_screen/roma/9303454564/5551234567/${Constants.INCOMING_CALL_EVENT}".toUri()
//                            Log.d("4444", " usri блять=" + uri)
//
//                            val deepLink = Intent(
//                                Intent.ACTION_VIEW,
//                                uri,
////                                context,
////                                MainScreensActivity::class.java
//                            )
//
//                            val pendingIntent: PendingIntent = TaskStackBuilder
//                                .create(context)
//                                .run {
//                                    addNextIntentWithParentStack(deepLink)
//                                    getPendingIntent(
//                                        0,
//                                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//                                    )
//                                }
//                            pendingIntent.send()


                        } catch (e: Exception) {
                            Log.d("4444", " try catch CALLSSCREEN=" + e)
                        }
                    }
            )
            IconButton(
                modifier = Modifier
                    .height(50.dp)
                    // .background(Color.Cyan)
                    .layoutId("create_call_icon"),
                onClick = {
                    // Log.d("4444", " contacts.value=" + contacts.value)
                    scope.launch {
                        viewModel.openModalBottomSheet(isOpen = true)
                        delay(500L)
                        viewModel.openModalBottomSheet(isOpen = false)
                    }
                }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_create_new_call),
                    contentDescription = "",
                    tint = Color.White,
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .background(colorResource(id = R.color.main_violet_light))
                .fillMaxWidth()
        ) {
            val contentChat = ConstraintSet {
                val chatContent = createRefFor("chats")

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
                    .layoutId("chats")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                        .clip(RoundedCornerShape(20.dp))
                )
                {
                    BoxWithConstraints {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(
                                    width = 1.dp,
                                    color = colorResource(id = R.color.main_yellow_new_chat_screen),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(start = 8.dp, end = 8.dp),
                            //  state = lazyListState
                        ) {
//                            items(chatList.value) { item ->
//                               // Log.d("4444", " chats item=" + item)
//                                Spacer(modifier = Modifier.height(16.dp))
//                                ChatsContentItem(
//                                    navController = navController,
//                                    chat = item,
//                                    ownPhoneSender = viewModel.ownPhoneSender,
//                                    viewModel = viewModel
//                                )
//                            }

                            //  Log.d("4444", " combineChatList.value=" + combineChatList.value)
                            items(items = historyCallsCombine.value.reversed()) { item ->
                                // Log.d("4444", " chats item=" + item)
                                Spacer(modifier = Modifier.height(16.dp))
                                CallContentItem(
                                    navController = navController,
                                    historyCallWithName = item,
                                    ownPhoneSender = viewModel.ownPhoneSenderLocal,
                                    viewModel = viewModel
                                )
                            }
                        }
                    }
                }
            }
        }

        if (isSheetOpen) {
            ModalBottomSheetLayout(
                modifier = Modifier
                    .fillMaxSize(),
                sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                scrimColor = Color.Transparent,
                sheetState = sheetState,
                sheetBackgroundColor = colorResource(id = R.color.main_yellow_new_chat_screen),
                sheetContent = {
                    BottomSheetCallContentTop(
                        isHide = {
                            scope.launch {
                                isSheetOpen = false
                                if (it) {
                                    viewModel.onClickHideNavigationBar(isHide = false)
                                    sheetState.hide()
                                }
                            }
                        })
                    BoxWithConstraints {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 8.dp, end = 8.dp)
                        ) {
                            items(contacts.value) { item ->
                                Log.d("4444", " item=" + item)
                                BottomSheetCallContentItem(
                                    navController = navController,
                                    contact = item,
                                    ownPhoneSender = viewModel.ownPhoneSenderLocal
                                )
                            }
                            // Добавьте другие элементы списка здесь
                        }
                    }
                }) {

            }
        }
    }
    CustomBackStackOnlyBottomSheetInChatsScreen(
        navController = navController,
        isSheetOpen = isSheetOpen,
        onSheetOpenChanged = { isOpen ->
            isSheetOpen = isOpen
            if (!isOpen) {
                viewModel.onClickHideNavigationBar(isHide = false)
            }
        },
        sheetState = sheetState
    )

    CustomBackStackOnlyCallsScreen(navHostController = navController)
}

fun getCurrentDateTimeString(): String {
    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
    return currentDateTime.format(formatter)
}

private fun openSystemSettingOverlayApp(context: Context) {
    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}


@Composable
fun CustomBackStackOnlyCallsScreen(
    navHostController: NavHostController,
) {
    val currentRoute = navHostController.currentBackStackEntryAsState().value?.destination?.route
    BackHandler(enabled = currentRoute != null) {
        if (currentRoute == ScreenRoute.CallsScreen.route) {
            navHostController.navigate(ScreenRoute.ChatsScreen.route)
        }
    }
}

@Composable
fun CallContentItem(
    navController: NavHostController,
    historyCallWithName: HistoryCallWithName,
    ownPhoneSender: String,
    viewModel: CallsScreenViewModel,
) {
    Log.d("4444", " CallContentItem combineChat=" + historyCallWithName)
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            // .clip(RoundedCornerShape(50.dp))
            .clickable {
                viewModel.onClickHideNavigationBar(isHide = true)
                scope.launch {
                    delay(50L) // костыль потому что ui у перехода не красивый
                    withContext(Dispatchers.Main) {
                        // правильные аргументы
                        // recipientName=Roman recipientPhone=9303454564 senderPhone=5551234567
                        // ошибочные аргументы
                        // BottomSheetContentItem recipientName=name потом исправить recipientPhone=9303454564 senderPhone=

                       // клик по элементу из списка
//                        navController.navigate(
//                            route = ScreenRoute.CallScreen.withArgs2(
//                                recipientName = getRecipientName(historyCall = historyCall),
//                                recipientPhone = getRecipientPhone(
//                                    historyCall = historyCall,
//                                    ownPhoneSender = ownPhoneSender
//                                ),
//                                senderPhone = CorrectNumberFormatHelper.getCorrectNumber(
//                                    ownPhoneSender
//                                ),
//                                typeEvent = Constants.OUTGOING_CALL_EVENT
//                            )
//                        )
                    }
                }

            },

        // .border(width = 1.dp, color = Color.Gray, shape = CircleShape),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
//            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                modifier = Modifier
                    //.padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                    .size(30.dp),
                painter = if (viewModel.ownPhoneSenderLocal == historyCallWithName.clientCallPhone) {
                    painterResource(id = R.drawable.ic_call_outgoing)
                } else {
                    painterResource(id = R.drawable.ic_call_incoming)
                },
                contentDescription = "",
                tint = colorResource(id = R.color.main_yellow_new_chat_screen),
            )
        }



        val res = if (historyCallWithName.clientCallPhone == viewModel.ownPhoneSenderLocal) "outgoing" else "incoming"
        if (res == "incoming") {

        }

        Log.d("4444", " historyCallWithName.senderPhoneName=" + historyCallWithName.senderPhoneName)
        Log.d("4444", " historyCallWithName.recipientPhoneName=" + historyCallWithName.recipientPhoneName)
        Log.d("4444", " historyCallWithName.clientCallPhone=" + historyCallWithName.clientCallPhone)



        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text( // phone title // перепроверить
                    modifier = Modifier.weight(1f),
                    text =  if (historyCallWithName.clientCallPhone == viewModel.ownPhoneSenderLocal) {
                        historyCallWithName.senderPhoneName.ifEmpty { uiFormatPhoneNumber(phone = historyCallWithName.senderPhone) }
                    } else {
                        historyCallWithName.clientCallPhone.ifEmpty { uiFormatPhoneNumber(phone = historyCallWithName.recipientPhone) }
                    },
                    color = colorResource(id = R.color.main_yellow_new_chat_screen),
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = historyCallWithName.createdAt,
//                    text = combineChat.createdAt.substring(0, 3),
                    color = colorResource(id = R.color.main_yellow_new_chat_screen),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.wrapContentWidth()
                )
            }

            Text(
                text = if (historyCallWithName.clientCallPhone == viewModel.ownPhoneSenderLocal) "outgoing" else "incoming",
                color = colorResource(id = R.color.main_yellow_splash_screen),
            )
//            Text(
//                text = historyCallWithName.conversationTime,
//                color = colorResource(id = R.color.main_yellow_splash_screen),
//            )
            Divider(
                modifier = Modifier.padding(top = 4.dp),
                color = colorResource(id = R.color.main_yellow_new_chat_screen),
                thickness = 1.dp
            )
        }
    }
}

private fun getRecipientName(historyCall: HistoryCall): String {
    var recipientName = ""
    "callHistory.name"?.let {
        recipientName = it
        return recipientName
    }
    historyCall.recipientPhone?.let {
        recipientName = CorrectNumberFormatHelper.getCorrectNumber(it)
        return recipientName
    }
    return recipientName
}

private fun getRecipientPhone(historyCall: HistoryCall, ownPhoneSender: String): String {
    var recipientPhone = ""
    historyCall.recipientPhone?.let { recipient ->
        if (recipient == CorrectNumberFormatHelper.getCorrectNumber(ownPhoneSender)) {
            historyCall.senderPhone?.let { sender ->
                recipientPhone = sender
                return recipientPhone
            }
        } else {
            recipientPhone = recipient
            return recipientPhone
        }
    }
    return recipientPhone


//    CorrectNumberFormatHelper.getCorrectNumber(
//        if (combineCall.recipient == CorrectNumberFormatHelper.getCorrectNumber(ownPhoneSender)) {
//            combineCall.sender
//        } else {
//            combineCall.recipient
//        }
//    )
}

@Composable
fun BottomSheetCallContentTop(
    isHide: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {

        val constraints = ConstraintSet {
            val headerText = createRefFor("header_new_chat_text")
            val closeIcon = createRefFor("create_close_new_chat_icon")

            constrain(headerText) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                //end.linkTo(createChatIcon.start)
                bottom.linkTo(parent.bottom)
                width = Dimension.matchParent
                height = Dimension.wrapContent
            }

            constrain(closeIcon) {
                top.linkTo(parent.top)
                //start.linkTo(headerChatText.end)
                end.linkTo(parent.end)
                // bottom.linkTo(parent.bottom)
                width = Dimension.value(30.dp) // Заполнить доступное пространство
                height = Dimension.value(30.dp)
            }
        }

        ConstraintLayout(
            constraintSet = constraints,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.header_new_call_screen),
                color = colorResource(id = R.color.main_violet),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .layoutId("header_new_chat_text")
            )
            IconButton(
                modifier = Modifier.layoutId("create_close_new_chat_icon"),
                onClick = {
                    isHide.invoke(true)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close_new_chat),
                    contentDescription = "",
                    tint = colorResource(id = R.color.main_violet)
                )
            }
        }
    }
}


@Composable
fun BottomSheetCallContentItem(
    navController: NavHostController,
    contact: Contact,
    ownPhoneSender: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                navController.navigate(
                    route = ScreenRoute.CallScreen.withArgs2(
                        recipientName = contact.name ?: contact.phoneNumber,
                        recipientPhone = CorrectNumberFormatHelper.getCorrectNumber(contact.phoneNumber),
                        senderPhone = CorrectNumberFormatHelper.getCorrectNumber(ownPhoneSender),
                        typeEvent = Constants.OUTGOING_CALL_EVENT
                    )
                )
            },
        // .border(width = 1.dp, color = Color.Gray, shape = CircleShape),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                .size(30.dp),
            painter = painterResource(id = R.drawable.ic_user),
            contentDescription = "",
            tint = colorResource(id = R.color.main_violet),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp)
        ) {
            Text(
                text = contact.name ?: contact.phoneNumber,
                color = colorResource(id = R.color.main_violet),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = contact.phoneNumber,
                color = Color.Gray
            )
            Divider(
                modifier = Modifier.padding(top = 4.dp),
                color = Color.Gray,
                thickness = 1.dp
            )
        }
    }
}

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