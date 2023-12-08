@file:OptIn(ExperimentalPermissionsApi::class)

package com.dev_marinov.chatalyze.presentation.ui.chatalyze_screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dev_marinov.chatalyze.R
import com.dev_marinov.chatalyze.data.socket_service.SocketService
import com.dev_marinov.chatalyze.presentation.ui.chatalyze_screen.model.ChatalyzeBottomNavItem
import com.dev_marinov.chatalyze.presentation.util.Constants
import com.dev_marinov.chatalyze.presentation.util.CorrectNumberFormatHelper
import com.dev_marinov.chatalyze.presentation.util.ScreenRoute

import com.dev_marinov.chatalyze.presentation.util.isAlwaysDenied
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ChatalyzeScreen(
    viewModel: ChatalyzeScreenViewModel = hiltViewModel(),
    authNavController: NavHostController,
) {
    Log.d("4444", " ChatalyzeScreen loaded")

    SetPermissionsAndNavigation(viewModel, authNavController)
}

@Composable
fun SetPermissionsAndNavigation(
    viewModel: ChatalyzeScreenViewModel,
    authNavController: NavHostController,
) {
    //Log.d("4444", " SetPermissionsAndNavigation loaded")

//    OneQuoteApp()
    ExecuteGrantedPermissions(viewModel = viewModel)

    //val backStackEntry = navController.currentBackStackEntryAsState()
    val navController = rememberNavController()

    val isHideBottomBar by viewModel.isHideBottomBar.collectAsStateWithLifecycle(false)

    viewModel.saveHideNavigationBar(false)
    //  viewModel.onMovieClickedHideNavigationBar(false)

    Scaffold(
        modifier = Modifier.navigationBarsPadding(),
        bottomBar = {
            //  Log.d("4444", " isHideBottomBar=" + isHideBottomBar)
            ChatalyzeBottomNavigationBar(
                modifier = Modifier
                    .background(colorResource(id = R.color.main_violet_light))
                    //  .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    //.animateContentSize(animationSpec = tween(durationMillis = 1500))
                    .height(height = if (isHideBottomBar == true) 0.dp else 70.dp),

                items = listOf(
                    ChatalyzeBottomNavItem(
                        name = "Chat",
                        route = ScreenRoute.ChatsScreen.route,
                        icon = Icons.Default.Chat,
                        badgeCount = 2
                    ),
                    ChatalyzeBottomNavItem(
                        name = "Call",
                        route = ScreenRoute.CallScreen.route,
                        icon = Icons.Default.Call,
                        badgeCount = 4
                    ),
                    ChatalyzeBottomNavItem(
                        name = "Profile",
                        route = ScreenRoute.ProfileScreen.route,
                        icon = Icons.Default.Person,
//                                badgeCount =
                    ),
                ),
                navController = navController,
                onItemClick = {
                    navController.navigate(it.route)
                }
            )
        },
    ) { paddingValues ->
        // передаем падинг чтобы список BottomNavigationBar не накладывался по поверх списка
        Box(
            modifier = Modifier
                .background(colorResource(id = R.color.main_violet_light))
                .padding(paddingValues = paddingValues)
        ) {
            ChatalyzeNavigationGraph(
                navHostController = navController,
                authHostController = authNavController,
                viewModel = viewModel
            )
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition", "RememberReturnType")
@ExperimentalPermissionsApi
@Composable
fun ExecuteGrantedPermissions(
    viewModel: ChatalyzeScreenViewModel,
) {

    val scope = rememberCoroutineScope()

    val isGrantedPermissions by viewModel.isGrantedPermissions.collectAsStateWithLifecycle(false)
    val isTheLifecycleEventNow by viewModel.isTheLifecycleEventNow.collectAsStateWithLifecycle("")
    val canStartService by viewModel.canStartService.collectAsStateWithLifecycle(false)

//    val context = LocalContext.current
//    val socketBroadcastReceiver = remember {
//        object : BroadcastReceiver() {
//            override fun onReceive(context: Context, intent: Intent) {
//                when (intent.action) {
//                    "receiver_action" -> {
//                        Log.d("4444", " socketBroadcastReceiver SESSION_ACTION_SUCCESS")
//                    }
//
//                    "SESSION_ACTION_ERROR" -> {
//                        Log.d("4444", " socketBroadcastReceiver SESSION_ACTION_ERROR")
//                    }
//
//                    "SESSION_ACTION_ClOSE" -> {
//                        Log.d("4444", " socketBroadcastReceiver SESSION_ACTION_ClOSE")
//                    }
//                }
//            }
//        }
//    }
//    LaunchedEffect(key1 = true){
//        val intentFilter = IntentFilter("receiver_action")
//        context.registerReceiver(socketBroadcastReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
//    }


    // сохранение ивентов socketBroadcastReceiver нужен только для возможности вызваать методы
    // получения чатов гет месседж и возможности писать звонить и тд
    val context = LocalContext.current
    DisposableEffect(context) {
        val socketBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == "receiver_socket_action") {
                    when (intent.getStringExtra("session")) {
                        "session_success" -> {
                            Log.d("4444", " socketBroadcastReceiver SESSION_ACTION_SUCCESS")
                            viewModel.saveSessionState(sessionState = Constants.SESSION_SUCCESS)
                        }
                        "session_error" -> {
                            Log.d("4444", " socketBroadcastReceiver SESSION_ACTION_ERROR")
                            viewModel.saveSessionState(sessionState = Constants.SESSION_ERROR)
                        }
                        "session_close" -> {
                            Log.d("4444", " socketBroadcastReceiver SESSION_ACTION_ClOSE")
                            viewModel.saveSessionState(sessionState = Constants.SESSION_CLOSE)
                        }
                    }
                }
            }
        }
        //Log.d("4444", " socketBroadcastReceiver registerReceiver")
        val filter = IntentFilter("receiver_socket_action")
        context.registerReceiver(socketBroadcastReceiver, filter, Context.RECEIVER_NOT_EXPORTED)

        onDispose {
            //Log.d("4444", " socketBroadcastReceiver unregisterReceiver")
            context.unregisterReceiver(socketBroadcastReceiver)
        }
    }

    Log.d("4444", " 1 ExecuteGrantedPermissions isGrantedPermissions=" + isGrantedPermissions + " lifecycleEventOnStart=" + isTheLifecycleEventNow + " canStartService=" + canStartService)
    LaunchedEffect(isGrantedPermissions, canStartService) {
        Log.d("4444", " 2 ExecuteGrantedPermissions isGrantedPermissions=" + isGrantedPermissions + " lifecycleEventOnStart=" + isTheLifecycleEventNow + " canStartService=" + canStartService)

        if (isGrantedPermissions
            && isTheLifecycleEventNow == Constants.EVENT_ON_START
            && canStartService) {

            val ownPhoneSender = getOwnPhoneSender(context = context)
            if (ownPhoneSender.isNotEmpty()) {
                viewModel.saveOwnPhoneSender(ownPhoneSender = ownPhoneSender)
                    Log.d("4444", " выполнился context.startService")
                    Intent(context, SocketService::class.java).also {
                    it.action = "open_session"
                    it.putExtra("sender", viewModel.phoneSender)
                    context.startService(it)
                }
                viewModel.canStartService(can = false)
            }
        }
    }

//    // пробелма в том после сворачивания сервис не стартует
//    Log.d("4444", " 0 LaunchedEffect canStartService="+ canStartService)
//    LaunchedEffect(canStartService) {
//        Log.d("4444", " 1 LaunchedEffect canStartService="+ canStartService)
//        if (canStartService) {
//            Log.d("4444", " 2 LaunchedEffect canStartService="+ canStartService)
//            Intent(context, SocketService::class.java).also {
//                it.action = "open_session"
//                it.putExtra("sender", viewModel.phoneSender)
//                context.startService(it)
//            }
//        }
//    }

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_PHONE_NUMBERS,
            Manifest.permission.READ_CONTACTS
        )
    )
    val localLifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(
        key1 = localLifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_START -> {
                        Log.d("4444", " ExecuteGrantedPermissions Lifecycle.Event.ON_START")
                        viewModel.saveLifecycleEvent(eventType = Constants.EVENT_ON_START)
                        permissionsState.launchMultiplePermissionRequest()

                        //viewModel.canStartService(can = true)
                        tryCallService(scope = scope, viewModel = viewModel)
                    }

                    Lifecycle.Event.ON_STOP -> { // когда свернул
                        Log.d("4444", " ExecuteGrantedPermissions Lifecycle.Event.ON_STOP")
                        viewModel.saveLifecycleEvent(eventType = Constants.EVENT_ON_STOP)
                        Intent(context, SocketService::class.java).also { context.stopService(it) }
                    }

                    Lifecycle.Event.ON_DESTROY -> { // когда удалил из стека
                        Log.d("4444", " ExecuteGrantedPermissions Lifecycle.Event.ON_DESTROY")
                        viewModel.saveLifecycleEvent(eventType = Constants.EVENT_ON_DESTROY)
                        Intent(context, SocketService::class.java).also {
                            context.stopService(it)
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
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        permissionsState.permissions.forEach {
            when (it.permission) {
                Manifest.permission.READ_PHONE_NUMBERS -> {
                    when {
                        it.hasPermission -> {
                            Log.d("4444", "READ_PHONE_NUMBERS 1 ")
                            savePermissionReadPhoneNumbers(viewModel = viewModel, isGranted = true)
                        }

                        it.shouldShowRationale -> {
                            Log.d("4444", "READ_PHONE_NUMBERS 2 ")
                            savePermissionReadPhoneNumbers(viewModel = viewModel, isGranted = false)
                            DialogPermissions(
                                message = stringResource(id = R.string.justification_READ_PHONE_NUMBERS),
                                onDismiss = { },
                                onConfirm = {
                                    openAppSettings(context = context)
                                }
                            )
                        }

                        it.isAlwaysDenied() -> {
                            Log.d("4444", "READ_PHONE_NUMBERS 3 ")
                            savePermissionReadPhoneNumbers(viewModel = viewModel, isGranted = false)
                            DialogPermissions(
                                message = stringResource(id = R.string.justification_denied_READ_PHONE_NUMBERS),
                                onDismiss = { },
                                onConfirm = {
                                    openAppSettings(context = context)
                                }
                            )
                        }
                    }
                }

                Manifest.permission.READ_CONTACTS -> {
                    when {
                        it.hasPermission -> {
                            Log.d("4444", "READ_CONTACTS 1 ")
                            savePermissionReadContacts(viewModel = viewModel, isGranted = true)
                        }

                        it.shouldShowRationale -> {
                            savePermissionReadContacts(viewModel = viewModel, isGranted = false)
                            DialogPermissions(
                                message = stringResource(id = R.string.justification_CONTACTS),
                                onDismiss = { },
                                onConfirm = {
                                    openAppSettings(context = context)
                                }
                            )
                        }

                        it.isAlwaysDenied() -> {
                            savePermissionReadContacts(viewModel = viewModel, isGranted = false)
                            DialogPermissions(
                                message = stringResource(id = R.string.justification_denied_CONTACTS),
                                onDismiss = { },
                                onConfirm = {
                                    openAppSettings(context = context)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

fun tryCallService(scope: CoroutineScope, viewModel: ChatalyzeScreenViewModel) {
    scope.launch {
        Log.d("4444", " сработал tryCallService")
        viewModel.canStartService(can = true)
//        delay(500L)
//        viewModel.canStartService(can = false)
    }
}

fun savePermissionReadPhoneNumbers(viewModel: ChatalyzeScreenViewModel, isGranted: Boolean) {
    viewModel.savePermissions(
        key = Constants.KEY_READ_PHONE_NUMBERS,
        isGranted = isGranted
    )
}

fun savePermissionReadContacts(viewModel: ChatalyzeScreenViewModel, isGranted: Boolean) {
    viewModel.savePermissions(
        key = Constants.KEY_READ_CONTACTS,
        isGranted = isGranted
    )
}

@Composable
fun DialogPermissions(
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
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
                    text = stringResource(id = R.string.dialog_permissions_justification),
                    color = Color.White
                )
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorResource(id = R.color.main_yellow_new_chat_screen))
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        text = message,
                        color = colorResource(id = R.color.main_violet_dialog_permission)
                    )

                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.main_violet_light)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, end = 4.dp, bottom = 4.dp)
                            .clip(RoundedCornerShape(24.dp))
                    ) {
                        Text(
                            text = stringResource(id = R.string.dialog_permissions_settings),
                            color = Color.White,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

fun getOwnPhoneSender(context: Context) : String {
    Log.d("4444", " getOwnPhoneSender запрашиваем номер телефона")
    return if (checkReadPhoneNumberPermission(context = context)) {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        val ownPhoneSender = telephonyManager?.line1Number.toString()
        return CorrectNumberFormatHelper.getCorrectNumber(number = ownPhoneSender)
    } else {
        ""
    }
}

fun checkReadPhoneNumberPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_PHONE_NUMBERS
    ) == PackageManager.PERMISSION_GRANTED
}

fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        .setData(Uri.fromParts("package", context.packageName, null))
    context.startActivity(intent)
}