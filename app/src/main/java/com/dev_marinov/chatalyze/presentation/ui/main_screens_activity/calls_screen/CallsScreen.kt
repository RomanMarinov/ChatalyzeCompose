package com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.calls_screen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
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
import com.dev_marinov.chatalyze.domain.model.call.HistoryCallWithName
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chats_screen.CustomBackStackOnlyBottomSheetInChatsScreen
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chats_screen.model.Contact
import com.dev_marinov.chatalyze.presentation.util.Constants
import com.dev_marinov.chatalyze.presentation.util.CorrectNumberFormatHelper
import com.dev_marinov.chatalyze.presentation.util.EditFormatPhoneHelper
import com.dev_marinov.chatalyze.presentation.util.GradientBackgroundHelper
import com.dev_marinov.chatalyze.presentation.util.RememberContacts
import com.dev_marinov.chatalyze.presentation.util.ScreenRoute
import com.dev_marinov.chatalyze.presentation.util.SystemUiControllerHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CallsScreen(
    navController: NavHostController,
    viewModel: CallsScreenViewModel = hiltViewModel(),
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    SystemUiControllerHelper.SetSystemBars(true)
    SystemUiControllerHelper.SetStatusBarColor()
    SystemUiControllerHelper.SetNavigationBars(isVisible = true)
    GradientBackgroundHelper.SetMonochromeBackground()

    val contacts = viewModel.filteredContacts.collectAsStateWithLifecycle(initialValue = emptyList())
    val contactsState = remember { mutableStateOf(contacts) }

    val historyCallsCombine =
        viewModel.historyCallsCombine.collectAsStateWithLifecycle(initialValue = emptyList())

    val isSessionState by viewModel.isSessionState.collectAsStateWithLifecycle("")
    val getOwnPhoneSender by viewModel.getOwnPhoneSender.collectAsStateWithLifecycle("")

    val getChatListFlag by viewModel.getChatListFlag.collectAsStateWithLifecycle(false)

    val pushTypeDisplay by viewModel.pushTypeDisplay.collectAsStateWithLifecycle(0)

    val isSheetOpen = rememberSaveable { mutableStateOf(false) }
    val openBottomSheet = rememberSaveable { mutableStateOf(false) }

    val hasOverlayPermissionState = remember { mutableStateOf(false) }


    val localLifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(
        key1 = localLifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_START -> {
                        Log.d("4444", " CallsScreen Lifecycle.Event.ON_START")
                        hasOverlayPermissionState.value = Settings.canDrawOverlays(context)
                    }

                    Lifecycle.Event.ON_STOP -> { // когда свернул
                        Log.d("4444", " CallsScreen Lifecycle.Event.ON_STOP")
                    }

                    Lifecycle.Event.ON_DESTROY -> { // когда удалил из стека
                        Log.d("4444", " CallsScreen Lifecycle.Event.ON_DESTROY")
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

    LaunchedEffect(getOwnPhoneSender, isSessionState) {
        if (isSessionState == Constants.SESSION_SUCCESS) {
            viewModel.canGetChatList(can = true)
        }
    }

    if (getChatListFlag) {
        val contactsFlow = RememberContacts(context = context)
        LaunchedEffect(Unit) {
            scope.launch {
                contactsFlow.collect {
                    viewModel.createContactsFlow(it)
                }
            }
        }
    }

    if (!hasOverlayPermissionState.value && pushTypeDisplay != 0) {
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

    val isOpenModalBottomSheet by viewModel.isOpenModalBottomSheet.collectAsStateWithLifecycle()
    if (isOpenModalBottomSheet) {
        viewModel.onClickHideNavigationBar(isHide = true)
        LaunchedEffect(Unit) {
            scope.launch {
                delay(50L)
                openBottomSheet.value = !openBottomSheet.value
                isSheetOpen.value = true
                sheetState.show()
            }
        }
    }
    LaunchedEffect(Unit) {
        snapshotFlow { sheetState.currentValue }
            .collect {
                if (sheetState.currentValue == ModalBottomSheetValue.Hidden) {
                    scope.launch {
                        isSheetOpen.value = false
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
            val headerChatText = createRefFor("header_calls_text")
            val createChatIcon = createRefFor("create_call_icon")

            constrain(headerChatText) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                bottom.linkTo(parent.bottom)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }

            constrain(createChatIcon) {
                top.linkTo(parent.top)
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
                    .layoutId("create_call_icon"),
                onClick = {
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
                        ) {
                            items(items = historyCallsCombine.value.reversed()) { item ->
                                Spacer(modifier = Modifier.height(8.dp))
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

        if (isSheetOpen.value) {
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
                                isSheetOpen.value = false
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
                            items(contactsState.value.value) { item ->
                                Log.d("4444", " item=" + item)
                                BottomSheetCallContentItem(
                                    navController = navController,
                                    contact = item,
                                    ownPhoneSender = viewModel.ownPhoneSenderLocal
                                )
                            }
                        }
                    }
                }) {

            }
        }
    }

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
            navHostController.popBackStack(ScreenRoute.ChatsScreen.route, false)
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
    Log.d("4444", " CallContentItem loaded")

    val isMakeCallState = remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                isMakeCallState.value = true
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column{
            Icon(
                modifier = Modifier
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {

                Text(
                    modifier = Modifier.weight(1f),
                    text = if (historyCallWithName.clientCallPhone == viewModel.ownPhoneSenderLocal) "outgoing" else "incoming",
                    color = colorResource(id = R.color.main_yellow_splash_screen),
                )

                Text(
                    text = historyCallWithName.createdAt,
                    color = colorResource(id = R.color.main_yellow_new_chat_screen),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.wrapContentWidth()
                )
            }

            val phone = if (historyCallWithName.clientCallPhone != viewModel.ownPhoneSenderLocal) {
                historyCallWithName.senderPhoneName.ifEmpty { EditFormatPhoneHelper.edit(historyCallWithName.clientCallPhone) }
            } else {
                historyCallWithName.recipientPhoneName.ifEmpty { EditFormatPhoneHelper.edit(historyCallWithName.recipientPhone) }
            }

            Text(
                text =  phone,
                color = colorResource(id = R.color.main_yellow_new_chat_screen),
                fontWeight = FontWeight.Bold
            )

            Divider(
                modifier = Modifier.padding(top = 8.dp),
                color = colorResource(id = R.color.main_yellow_new_chat_screen),
                thickness = 1.dp
            )
        }
    }

    if (isMakeCallState.value) {

        val recipientName = if (historyCallWithName.clientCallPhone != viewModel.ownPhoneSenderLocal) {
            historyCallWithName.senderPhoneName.ifEmpty { EditFormatPhoneHelper.edit(historyCallWithName.clientCallPhone) }
        } else {
            historyCallWithName.recipientPhoneName.ifEmpty { EditFormatPhoneHelper.edit(historyCallWithName.recipientPhone) }
        }

        val recipientPhone = if (historyCallWithName.clientCallPhone != viewModel.ownPhoneSenderLocal) {
            EditFormatPhoneHelper.edit(historyCallWithName.clientCallPhone)
        } else {
            EditFormatPhoneHelper.edit(historyCallWithName.recipientPhone)
        }

        DialogShowMakeCallFromCallsScreen(
            recipientName = recipientName,
            recipientPhone = recipientPhone,
            onDismiss = {
                isMakeCallState.value = false
            },
            onConfirm = {
                isMakeCallState.value = false
                viewModel.onClickHideNavigationBar(isHide = true)
                scope.launch {
                    delay(50L) // костыль потому что ui у перехода не красивый
                    withContext(Dispatchers.Main) {
                        navController.navigate(
                            route = ScreenRoute.CallScreen.withArgs2(
                                recipientName = recipientName ?: recipientPhone,
                                recipientPhone = CorrectNumberFormatHelper.getCorrectNumber(recipientPhone),
                                senderPhone = CorrectNumberFormatHelper.getCorrectNumber(historyCallWithName.senderPhone),
                                typeEvent = Constants.OUTGOING_CALL_EVENT
                            )
                        )
                    }
                }
            }
        )
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
                bottom.linkTo(parent.bottom)
                width = Dimension.matchParent
                height = Dimension.wrapContent
            }

            constrain(closeIcon) {
                top.linkTo(parent.top)
                end.linkTo(parent.end)
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