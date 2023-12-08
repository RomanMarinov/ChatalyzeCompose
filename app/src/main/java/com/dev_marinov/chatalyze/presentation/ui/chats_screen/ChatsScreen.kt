package com.dev_marinov.chatalyze.presentation.ui.chats_screen

import android.annotation.SuppressLint
import android.os.Build
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.dev_marinov.chatalyze.R
import com.dev_marinov.chatalyze.domain.model.chats.Chat
import com.dev_marinov.chatalyze.presentation.ui.chats_screen.model.Contact
import com.dev_marinov.chatalyze.presentation.util.*
import kotlinx.coroutines.*
import kotlin.system.exitProcess

@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatsScreen(
    navController: NavHostController,
    viewModel: ChatsScreenViewModel = hiltViewModel()
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

    //viewModel.onClickHideNavigationBar(false)
    val chatList = viewModel.chatList.collectAsStateWithLifecycle()
    val contacts = viewModel.contacts.collectAsStateWithLifecycle(initialValue = listOf())

    // раскоментировать
    val isGrantedPermissions by viewModel.isGrantedPermissions.collectAsStateWithLifecycle(false)
    val getOwnPhoneSender by viewModel.getOwnPhoneSender.collectAsStateWithLifecycle("")
    val isTheLifecycleEventNow by viewModel.isTheLifecycleEventNow.collectAsStateWithLifecycle("")

    val isSessionState by viewModel.isSessionState.collectAsStateWithLifecycle("")

    // может из за false не работать но скорей всего работает
    val canGetChats by viewModel.canGetChats.collectAsStateWithLifecycle(false)



    // сюда добавить статус соедения удачу сокета
    LaunchedEffect(getOwnPhoneSender, isSessionState) {
        if (isSessionState == Constants.SESSION_SUCCESS) {
            Log.d("4444", " ChatsScreen isGrantedPermissions="
                    + isGrantedPermissions + " lifecycleEventOnStart=" + isTheLifecycleEventNow)

            viewModel.canGetChats(can =true)
        }
    }

    ////////////
    if (canGetChats) {
        viewModel.getChats()


        val contactsFlow = RememberContacts(context = context)
        scope.launch {
            contactsFlow.collect {
                viewModel.transferContacts(it)
            }
        }
    }
    /////////////



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
        scope.launch {
            delay(50L)
            openBottomSheet = !openBottomSheet
            isSheetOpen = true
            sheetState.show()
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
                        viewModel.openModalBottomSheet(isOpen =false)
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
                            items(chatList.value) { item ->
                               // Log.d("4444", " chats item=" + item)
                                Spacer(modifier = Modifier.height(16.dp))
                                ChatsContentItem(
                                    navController = navController,
                                    chat = item,
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomBackStackOnlyBottomSheetInChatsScreen(
    navController: NavHostController,
    isSheetOpen: Boolean,
    onSheetOpenChanged: (Boolean) -> Unit,
    sheetState: ModalBottomSheetState
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
    isHide: (Boolean) -> Unit
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
    chat: Chat,
    ownPhoneSender: String,
    viewModel: ChatsScreenViewModel,
) {
   // Log.d("4444", " BottomSheetContentItem ownPhoneNumber=" + CorrectNumberFormatHelper.getCorrectNumber(ownPhoneSender))
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
                        Log.d(
                            "4444",
                            " BottomSheetContentItem recipientName=" + "name потом исправить"
                                    + " recipientPhone=" + CorrectNumberFormatHelper.getCorrectNumber(
                                chat.recipient
                            )
                                    + " senderPhone=" + CorrectNumberFormatHelper.getCorrectNumber(
                                chat.sender
                            )
                        )

                        // правильные аргументы
                        // recipientName=Roman recipientPhone=9303454564 senderPhone=5551234567
                        // ошибочные аргументы
                        // BottomSheetContentItem recipientName=name потом исправить recipientPhone=9303454564 senderPhone=
                        navController.navigate(
                            route = ScreenRoute.ChatScreen.withArgs(
                                recipientName = CorrectNumberFormatHelper.getCorrectNumber(chat.recipient),
                                recipientPhone = CorrectNumberFormatHelper.getCorrectNumber(
                                    if (chat.recipient == CorrectNumberFormatHelper.getCorrectNumber(
                                            ownPhoneSender
                                        )
                                    )
                                        chat.sender else chat.recipient
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
        Icon(
            modifier = Modifier
                .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                .size(30.dp),
            painter = painterResource(id = R.drawable.ic_user),
            contentDescription = "",
            tint = colorResource(id = R.color.main_yellow_new_chat_screen),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = if (chat.sender != CorrectNumberFormatHelper.getCorrectNumber(ownPhoneSender))
                        chat.sender else chat.recipient,
                    color = colorResource(id = R.color.main_yellow_new_chat_screen),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = chat.createdAt.substring(0, 3),
                    color = colorResource(id = R.color.main_yellow_new_chat_screen),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.wrapContentWidth()
                )
            }

            Text(
                text = "от кого последнее " +  chat.sender,
                color = colorResource(id = R.color.main_yellow_splash_screen),
            )
            Text(
                text = chat.textMessage?: "",
                color = colorResource(id = R.color.main_yellow_splash_screen),
            )
            Divider(
                modifier = Modifier.padding(top = 4.dp),
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
    ownPhoneSender: String
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
                        recipientName = contact.name,
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
                text = contact.name,
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