package com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chats_screen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import androidx.navigation.compose.currentBackStackEntryAsState
import com.dev_marinov.chatalyze.R
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.calls_screen.getCurrentDateTimeString
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chats_screen.model.CombineChat
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chats_screen.model.Contact
import com.dev_marinov.chatalyze.presentation.util.*
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess


private fun openSystemSettingNotification(context: Context) {
//    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//    context.startActivity(intent)


    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    context.startActivity(intent)


//APP_NOTIFICATION_SETTINGS
    //ACTION_APPLICATION_DETAILS_SETTINGS
    //ACTION_APP_NOTIFICATION_SETTINGS
    // "android.settings.APP_NOTIFICATION_SETTINGS"


    // ошибка
//    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
//    val packageName = context.packageName // замените на пакет вашего приложения
//    val uri = Uri.fromParts("package", packageName, null)
//    intent.data = uri
//    context.startActivity(intent)


//    val intent = Intent().apply {
//        when {
//            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
//                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
//                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
//            }
//            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
//                action = "android.settings.APP_NOTIFICATION_SETTINGS"
//                putExtra("app_package", context.packageName)
//                putExtra("app_uid", context.applicationInfo.uid)
//            }
//            else -> {
//                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//                addCategory(Intent.CATEGORY_DEFAULT)
//                data = Uri.parse("package:" + context.packageName)
//            }
//        }
//    }
//    context.startActivity(intent)


}


@RequiresApi(Build.VERSION_CODES.S)
@SuppressLint("Range", "HardwareIds")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatsScreen(
    navController: NavHostController,
    viewModel: ChatsScreenViewModel = hiltViewModel(),
) {
    Log.d("4444", " ChatsScreen loaded")

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    SystemUiControllerHelper.SetSystemBars(true)
    SystemUiControllerHelper.SetStatusBarColor()
    SystemUiControllerHelper.SetNavigationBars(isVisible = true)
    //SystemUiControllerHelper.SetStatusBarColorNoGradient()
    GradientBackgroundHelper.SetMonochromeBackground()
    // SystemUiControllerHelper.SetStatusBarColorNoGradient()

    val chatList = viewModel.chatList.collectAsStateWithLifecycle()
    val contacts = viewModel.filteredContacts.collectAsStateWithLifecycle(initialValue = listOf())
    val combineChatList =
        viewModel.combineChatList.collectAsStateWithLifecycle(initialValue = listOf())

    // раскоментировать
    val isGrantedPermissions by viewModel.isGrantedPermissions.collectAsStateWithLifecycle(false)
    val getOwnPhoneSender by viewModel.getOwnPhoneSender.collectAsStateWithLifecycle("")
    val isTheLifecycleEventNow by viewModel.isTheLifecycleEventNow.collectAsStateWithLifecycle("")

    val hideDialogPermissionNotificationFlow by viewModel.hideDialogPermissionNotificationFlow.collectAsStateWithLifecycle(
        true
    )

    var stateHideDialogNotification by remember { mutableStateOf(false) }

    val isSessionState by viewModel.isSessionState.collectAsStateWithLifecycle("")

    // может из за false не работать но скорей всего работает
    val getChatListFlag by viewModel.getChatListFlag.collectAsStateWithLifecycle(false)

    // сюда добавить статус соедения удачу сокета
    LaunchedEffect(getOwnPhoneSender, isSessionState, isTheLifecycleEventNow) {
        Log.d("4444", " ChatsScreen isSessionState=" + isSessionState)
        if (isSessionState == Constants.SESSION_SUCCESS) {
            Log.d(
                "4444", " ChatsScreen isGrantedPermissions="
                        + isGrantedPermissions + " lifecycleEventOnStart=" + isTheLifecycleEventNow
            )
            viewModel.canGetChatList(can = true)
        }
    }

    LaunchedEffect(Unit) { // нужно после возврата с экрана звонка
        delay(500L)
        viewModel.onClickHideNavigationBar(isHide = false)
    }



    if (getChatListFlag) {
        val contactsFlow = RememberContacts(context = context)
        LaunchedEffect(key1 = true) {
            scope.launch {
                contactsFlow.collect { originalContacts ->
                    Log.d("4444", " list contact =" + originalContacts)
                    viewModel.createContactsFlow(contacts = originalContacts)

                    viewModel.createChatListFlow() // список последних сообщений
                    viewModel.createCombineFlow() // список итоговый комбайн
                }
            }
        }
    }

    if (!hideDialogPermissionNotificationFlow) {
        if (!stateHideDialogNotification) {
            DialogShowSettingNotification(
                //viewModel,
                onDismiss = {
                    stateHideDialogNotification = it
                },
                onConfirm = {
                    openSystemSettingNotification(context = context)
                },
                onClickClose = {
                    viewModel.saveHideDialogPermissionNotification(hide = true)
                }
            )
        }
    }

    // openSystemSettingNotification(context = context)

    val localLifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(
        key1 = localLifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_START -> {
                        Log.d("4444", " ChatsScreen Lifecycle.Event.ON_START")
                        viewModel.saveCompanionOnTheServer(
                            senderPhone = viewModel.ownPhoneSender,
                            recipientPhone = ""
                        )
                    }

                    Lifecycle.Event.ON_STOP -> { // когда свернул
                        Log.d("4444", " ChatsScreen Lifecycle.Event.ON_STOP")
                    }

                    Lifecycle.Event.ON_DESTROY -> { // когда удалил из стека
                        Log.d("4444", " ChatsScreen Lifecycle.Event.ON_DESTROY")
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


//    var ownPhoneSender by remember { mutableStateOf("") }
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
    ) {
        val constraints = ConstraintSet {
            val headerChatText = createRefFor("header_chat_text")
            val createChatIcon = createRefFor("create_chat_icon")

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
                text = stringResource(id = R.string.chat_screen_chats),
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .height(50.dp)
                    // .background(Color.Green)
                    .layoutId("header_chat_text")
            )
            IconButton(
                modifier = Modifier
                    .height(50.dp)
                    // .background(Color.Cyan)
                    .layoutId("create_chat_icon"),
                onClick = {
                    Log.d("4444", " contacts.value=" + contacts.value)
                    scope.launch {
                        viewModel.openModalBottomSheet(isOpen = true)
                        delay(500L)
                        viewModel.openModalBottomSheet(isOpen = false)
                    }
                }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_create_new_chat),
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

                            //Log.d("4444", " combineChatList.value=" + combineChatList.value)
                            items(combineChatList.value) { item ->
                                // Log.d("4444", " chats item=" + item)
                                Spacer(modifier = Modifier.height(16.dp))
                                ChatsContentItem(
                                    navController = navController,
                                    combineChat = item,
                                    ownPhoneSender = viewModel.ownPhoneSender,
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
                    BottomSheetContentTop(isHide = {
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
                                BottomSheetContentItem(
                                    navController = navController,
                                    contact = item,
                                    ownPhoneSender = viewModel.ownPhoneSender
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


}

fun getCurrentDateTimeString2(): String {
    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return currentDateTime.format(formatter)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomBackStackOnlyBottomSheetInChatsScreen(
    navController: NavHostController,
    isSheetOpen: Boolean,
    onSheetOpenChanged: (Boolean) -> Unit,
    sheetState: ModalBottomSheetState,
) {
    val scope = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    BackHandler(enabled = currentRoute != null) {
        if (currentRoute == ScreenRoute.ChatsScreen.route) {
            if (isSheetOpen) {
                onSheetOpenChanged(false)
                scope.launch {
                    withContext(Dispatchers.Main) {
                        sheetState.hide()
                    }
                }
            } else {
                exitProcess(0)
            }
        }
    }
}

@Composable
fun BottomSheetContentTop(
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
                text = stringResource(id = R.string.header_new_chat_screen),
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
fun ChatsContentItem(
    navController: NavHostController,
    combineChat: CombineChat,
    ownPhoneSender: String,
    viewModel: ChatsScreenViewModel,
) {
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                Log.d("4444", " ChatsScreen click item")
                viewModel.onClickHideNavigationBar(isHide = true)
                scope.launch {
                    delay(50L) // костыль потому что ui у перехода не красивый
                    withContext(Dispatchers.Main) {

                        navController.navigate(
                            route = ScreenRoute.ChatScreen.withArgs(
                                recipientName = combineChat.name
                                    ?: CorrectNumberFormatHelper.getCorrectNumber(combineChat.recipient),
                                recipientPhone = CorrectNumberFormatHelper.getCorrectNumber(
                                    if (combineChat.recipient == CorrectNumberFormatHelper.getCorrectNumber(
                                            ownPhoneSender
                                        )
                                    )
                                        combineChat.sender else combineChat.recipient
                                ),
                                senderPhone = CorrectNumberFormatHelper.getCorrectNumber(
                                    ownPhoneSender
                                )
                            )
                        )
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
                painter = painterResource(id = R.drawable.ic_user),
                contentDescription = "",
                tint = colorResource(id = R.color.main_yellow_new_chat_screen),
            )



            Icon(
                // online state
                modifier = Modifier
                    // .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                    .padding(start = 25.dp)
                    .size(10.dp),
                painter = if (combineChat.onlineOrOffline == "online") painterResource(id = R.drawable.ic_online_state)
                else painterResource(id = R.drawable.ic_offline_state),
                contentDescription = "",
                tint = colorResource(id = R.color.main_yellow_new_chat_screen),
            )
        }

        // как получить имя
        // сравнить свой номер


        Log.d("4444", " ChatsScreen combineChat.recipientName=" + combineChat.name)
        val titleName =
            combineChat.name ?: (EditFormatPhoneHelper.edit(phone = combineChat.recipient))

//        val titleName = combineChat.senderName
//            ?: (EditFormatPhoneHelper.edit(phone = combineChat.sender).takeUnless {
//                it == EditFormatPhoneHelper.edit(phone = ownPhoneSender)
//            }
//                ?: EditFormatPhoneHelper.edit(phone = combineChat.recipient))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Log.d("4444", " ChatsScreen combineChat.name=" + combineChat.name)
                Text( // phone title // перепроверить
                    modifier = Modifier.weight(1f),
                    text = titleName,
                    color = colorResource(id = R.color.main_yellow_new_chat_screen),
                    fontWeight = FontWeight.Bold
                )
                Text( // last message
//                    text = CustomDateTimeHelper.formatDateTime(combineChat.createdAt),
                    text = CustomDateTimeHelper.formatDateTime(combineChat.createdAt),
//                    text = combineChat.createdAt.substring(0, 3),
                    color = colorResource(id = R.color.main_yellow_new_chat_screen),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.wrapContentWidth()
                )
            }

            // это от кого последнее сообщение
//            Text(
//                text = "from " + (combineChat.senderName ?: EditFormatPhoneHelper.edit(combineChat.sender)),
//                color = colorResource(id = R.color.main_yellow_splash_screen),
//            )
            Text(
                text = combineChat.textMessage ?: "",
                color = colorResource(id = R.color.main_yellow_splash_screen),
            )
            Divider(
                modifier = Modifier.padding(top = 16.dp),
                color = colorResource(id = R.color.main_yellow_new_chat_screen),
                thickness = 1.dp
            )
        }
    }
}

@Composable
fun BottomSheetContentItem(
    navController: NavHostController,
    contact: Contact,
    ownPhoneSender: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable {
//                navController.navigate(ScreenRoute.ChatScreen.route)
                // navController.navigate(ScreenRoute.ChatScreen.route + contact.name)
                // navController.navigate(ScreenRoute.ChatScreen.withArgs(contact.name))
                Log.d(
                    "4444", " BottomSheetContentItem recipientName=" + contact.name
                            + " recipientPhone=" + CorrectNumberFormatHelper.getCorrectNumber(
                        contact.phoneNumber
                    )
                            + " senderPhone=" + CorrectNumberFormatHelper.getCorrectNumber(
                        ownPhoneSender
                    )
                )

                navController.navigate(
                    route = ScreenRoute.ChatScreen.withArgs(
                        recipientName = contact.name ?: CorrectNumberFormatHelper.getCorrectNumber(
                            contact.phoneNumber
                        ),
                        recipientPhone = CorrectNumberFormatHelper.getCorrectNumber(contact.phoneNumber),
                        senderPhone = CorrectNumberFormatHelper.getCorrectNumber(ownPhoneSender)
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
fun RememberCurrentActivity(): ComponentActivity {
    val context = LocalContext.current
    return remember(context) { context as ComponentActivity }
}