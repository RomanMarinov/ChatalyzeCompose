package com.dev_marinov.chatalyze.presentation.ui.calls_screen

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.dev_marinov.chatalyze.presentation.ui.chats_screen.CustomBackStackOnlyBottomSheetInChatsScreen
import com.dev_marinov.chatalyze.presentation.ui.chats_screen.model.CombineChat
import com.dev_marinov.chatalyze.presentation.ui.chats_screen.model.Contact
import com.dev_marinov.chatalyze.presentation.ui.chats_screen.uiFormatPhoneNumber
import com.dev_marinov.chatalyze.presentation.util.Constants
import com.dev_marinov.chatalyze.presentation.util.CorrectDateTimeHelper
import com.dev_marinov.chatalyze.presentation.util.CorrectNumberFormatHelper
import com.dev_marinov.chatalyze.presentation.util.GradientBackgroundHelper
import com.dev_marinov.chatalyze.presentation.util.RememberContacts
import com.dev_marinov.chatalyze.presentation.util.ScreenRoute
import com.dev_marinov.chatalyze.presentation.util.SystemUiControllerHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CallsScreen(
    navController: NavHostController,
    viewModel: CallsScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    SystemUiControllerHelper.SetSystemBars(true)
    SystemUiControllerHelper.SetStatusBarColor()
    SystemUiControllerHelper.SetNavigationBars(isVisible = true)
    //SystemUiControllerHelper.SetStatusBarColorNoGradient()
    GradientBackgroundHelper.SetMonochromeBackground()
   // SystemUiControllerHelper.SetStatusBarColorNoGradient()

    val contacts = viewModel.contacts.collectAsStateWithLifecycle(initialValue = listOf())
    val combineChatList =
        viewModel.combineChatList.collectAsStateWithLifecycle(initialValue = listOf())

    val isSessionState by viewModel.isSessionState.collectAsStateWithLifecycle("")
    val getOwnPhoneSender by viewModel.getOwnPhoneSender.collectAsStateWithLifecycle("")

    // может из за false не работать но скорей всего работает
    val getChatListFlag by viewModel.getChatListFlag.collectAsStateWithLifecycle(false)

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
        //  .background(colorResource(id = R.color.main_violet_light))
        //  .systemBarsPadding()
    ) {

        val constraints = ConstraintSet {
            val headerChatText = createRefFor("header_calls_text")
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
                text = stringResource(id = R.string.call_screen_calls),
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .height(50.dp)
                    .layoutId("header_calls_text")
            )
            IconButton(
                modifier = Modifier
                    .height(50.dp)
                    // .background(Color.Cyan)
                    .layoutId("create_chat_icon"),
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
                            items(items = combineChatList.value) { item ->
                                // Log.d("4444", " chats item=" + item)
                                Spacer(modifier = Modifier.height(16.dp))
                                CallContentItem(
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
                    BottomSheetCallContentTop(isHide = {
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



//    CustomBackStackOnlyCallsScreen(navHostController = navHostController)
}

//@Composable
//fun CustomBackStackOnlyCallsScreen(
//    navHostController: NavHostController
//) {
//    val currentRoute = navHostController.currentBackStackEntryAsState().value?.destination?.route
//    BackHandler(enabled = currentRoute != null) {
//        if (currentRoute == ScreenRoute.CallsScreen.route) {
//            navHostController.navigate(ScreenRoute.ChatsScreen.route)
//        }
//    }
//}

@Composable
fun CallContentItem(
    navController: NavHostController,
    combineChat: CombineChat,
    ownPhoneSender: String,
    viewModel: CallsScreenViewModel,
) {
    Log.d("4444", " ChatsContentItem combineChat=" + combineChat)
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
                                combineChat.recipient
                            )
                                    + " senderPhone=" + CorrectNumberFormatHelper.getCorrectNumber(
                                combineChat.sender
                            )
                        )

                        // правильные аргументы
                        // recipientName=Roman recipientPhone=9303454564 senderPhone=5551234567
                        // ошибочные аргументы
                        // BottomSheetContentItem recipientName=name потом исправить recipientPhone=9303454564 senderPhone=
                        navController.navigate(
                            route = ScreenRoute.CallScreen.withArgs2(
                                recipientName = CorrectNumberFormatHelper.getCorrectNumber(
                                    combineChat.recipient
                                ),
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
                painter = if (combineChat.onlineOrDate == "online") painterResource(id = R.drawable.ic_online_state)
                else painterResource(id = R.drawable.ic_offline_state),
                contentDescription = "",
                tint = colorResource(id = R.color.main_yellow_new_chat_screen),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {

                Text( // phone title // перепроверить
                    modifier = Modifier.weight(1f),
                    text = combineChat.name
                        ?: (uiFormatPhoneNumber(phone = combineChat.sender).takeUnless {
                            it == uiFormatPhoneNumber(phone = ownPhoneSender)
                        }
                            ?: uiFormatPhoneNumber(phone = combineChat.recipient)),
                    color = colorResource(id = R.color.main_yellow_new_chat_screen),
                    fontWeight = FontWeight.Bold
                )
                Text( // last message
                    text = CorrectDateTimeHelper.formatDateTime(combineChat.createdAt),
//                    text = combineChat.createdAt.substring(0, 3),
                    color = colorResource(id = R.color.main_yellow_new_chat_screen),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.wrapContentWidth()
                )
            }

            Text(
                text = "from " + uiFormatPhoneNumber(phone = combineChat.sender),
                color = colorResource(id = R.color.main_yellow_splash_screen),
            )
            Text(
                text = combineChat.textMessage ?: "",
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
//erer

                Log.d("4444", " navController.currentDestination=" + navController.currentDestination)


//                navController.navigate(
//                    route = ScreenRoute.CallScreen.withArgs2(
//                        recipientName = "romchik",
//                        recipientPhone = "3333",
//                        senderPhone = "3333"
//                    )
//                )

                navController.navigate(
                    route = ScreenRoute.CallScreen.withArgs2(
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