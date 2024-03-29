package com.dev_marinov.chatalyze.presentation.ui.main_screens_activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Scaffold
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
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.dev_marinov.chatalyze.R
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.model.ChatalyzeBottomNavItem
import com.dev_marinov.chatalyze.presentation.ui.theme.ChatalyzeTheme
import com.dev_marinov.chatalyze.presentation.util.Constants
import com.dev_marinov.chatalyze.presentation.util.CorrectNumberFormatHelper
import com.dev_marinov.chatalyze.presentation.util.ScreenRoute
import com.dev_marinov.chatalyze.presentation.util.isAlwaysDenied
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainScreensActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Log.d("4444", " MainScreensActivity loaded")
            WindowCompat.setDecorFitsSystemWindows(window, false)
            ChatalyzeTheme {
                SetPermissionsAndNavigation()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        when (intent?.action) {
            "notification_action" -> {
                Log.d("4444", " MainScreensActivity notification_action")

                val name = intent.getStringExtra("name")
                val sender = intent.getStringExtra("sender")
                val recipient = intent.getStringExtra("recipient")

                val deepLink =
                    Uri.parse("scheme_chatalyze://chat_screen/{$name}/{$sender}/{$recipient}")
                //val deepLink = Uri.parse("scheme_chatalyze2://chat_screen2/$name/$sender/$recipient")

                val taskDetailIntent = Intent(
                    Intent.ACTION_VIEW,
                    deepLink,
//                    this,
//                    MainScreensActivity::class.java
                    //MainActivity::class.java
                )

                val pendingIntent: PendingIntent = TaskStackBuilder.create(applicationContext).run {
                    addNextIntentWithParentStack(taskDetailIntent)
                    // addParentStack(MainScreensActivity::class.java)
                    getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
                }
                pendingIntent.send()
            }
        }
    }
}

@SuppressLint("RememberReturnType")
@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SetPermissionsAndNavigation(
    viewModel: MainScreensViewModel = hiltViewModel(),
) {
    Log.d("4444", " MainScreensActivity SetPermissionsAndNavigation выполнился")

    ExecuteGrantedPermissions(viewModel = viewModel)
    val navController = rememberNavController()

    //val backStackEntry = navController.currentBackStackEntryAsState()

    val isHideBottomBar by viewModel.isHideBottomBar.collectAsStateWithLifecycle(false)

    LaunchedEffect(Unit) {
        viewModel.saveHideNavigationBar(false)
    }

    Scaffold(
        modifier = Modifier.navigationBarsPadding(),
        bottomBar = {
            BottomNavigationBarItem(
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
                        // badgeCount = 2
                    ),
                    ChatalyzeBottomNavItem(
                        name = "Call",
                        route = ScreenRoute.CallsScreen.route,
                        icon = Icons.Default.Call,
                        // badgeCount = 4
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
//                navController = authNavController,
//                onItemClick = {
//                    authNavController.navigate(it.route)
//                }
            )
        },
    ) { paddingValues ->
        // передаем падинг чтобы список BottomNavigationBar не накладывался по поверх списка
        Box(
            modifier = Modifier
                .background(colorResource(id = R.color.main_violet_light))
                .padding(paddingValues = paddingValues)
        ) {
            // было
            Log.d("4444", " MainScreensActivity SetPermissionsAndNavigation box ")
            //вызывается 3 раза

            MainScreensNavigationGraph(navHostController = navController)
        }
    }
}

@SuppressLint(
    "CoroutineCreationDuringComposition", "RememberReturnType",
    "PermissionLaunchedDuringComposition"
)
@ExperimentalPermissionsApi
@Composable
fun ExecuteGrantedPermissions(
    viewModel: MainScreensViewModel,
) {
    val context = LocalContext.current
    val isGrantedPermissions by viewModel.isGrantedPermissions.collectAsStateWithLifecycle(false)
    val isTheLifecycleEventNow by viewModel.isTheLifecycleEventNow.collectAsStateWithLifecycle("")
    val canStartWebSocket by viewModel.canStartService.collectAsStateWithLifecycle(false)

    LaunchedEffect(isGrantedPermissions, canStartWebSocket, isTheLifecycleEventNow) {
        Log.d(
            "4444",
            " MainScreensActivity ExecuteGrantedPermissions isGrantedPermissions=" + isGrantedPermissions + " lifecycleEventOnStart=" + isTheLifecycleEventNow + " canStartService=" + canStartWebSocket
        )

        if (isGrantedPermissions
            && isTheLifecycleEventNow == Constants.EVENT_ON_START
            && canStartWebSocket
        ) {
            val ownPhoneSender = getOwnPhoneSender(context = context)
            Log.d("4444", " MainScreensActivity выполнился метод запроса телефона getOwnPhoneSender=" + ownPhoneSender)
            if (ownPhoneSender.isNotEmpty()) {
                viewModel.saveOwnPhoneSender(ownPhoneSender = ownPhoneSender)

                viewModel.openServerWebSocketConnection(
                    ownPhoneSender = ownPhoneSender,
                    context = context
                )
                viewModel.canStartWebSocket(can = false)
            }
        }
    }

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_PHONE_NUMBERS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    )

    val localLifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(
        key1 = localLifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_START -> {
                        Log.d("4444", " MainScreensActivity ExecuteGrantedPermissions Lifecycle.Event.ON_START")
                        viewModel.saveLifecycleEvent(eventType = Constants.EVENT_ON_START)
                        permissionsState.launchMultiplePermissionRequest()
                        viewModel.canStartWebSocket(can = true)
                        //tryCallService(scope = scope, viewModel = viewModel)
                    }

                    Lifecycle.Event.ON_STOP -> { // когда свернул
                        Log.d("4444", " MainScreensActivity ExecuteGrantedPermissions Lifecycle.Event.ON_STOP")
                        viewModel.saveLifecycleEvent(eventType = Constants.EVENT_ON_STOP)
                        //viewModel.closeWebSocketConnection()
                    }

                    Lifecycle.Event.ON_DESTROY -> { // когда удалил из стека
                        Log.d("4444", " MainScreensActivity ExecuteGrantedPermissions Lifecycle.Event.ON_DESTROY")
                        viewModel.saveLifecycleEvent(eventType = Constants.EVENT_ON_DESTROY)
                        viewModel.closeWebSocketConnection()
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
                        it.status.isGranted -> {
                            Log.d("4444", " MainScreensActivity READ_PHONE_NUMBERS isGranted")
                            savePermissionReadPhoneNumbers(viewModel = viewModel, isGranted = true)
                        }

                        it.status.shouldShowRationale -> {
                            Log.d("4444", " MainScreensActivity READ_PHONE_NUMBERS shouldShowRationale ")
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
                            Log.d("4444", " MainScreensActivity READ_PHONE_NUMBERS isAlwaysDenied")
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
                        it.status.isGranted -> {
                            Log.d("4444", " MainScreensActivity READ_CONTACTS isGranted")
                            savePermissionReadContacts(viewModel = viewModel, isGranted = true)
                        }

                        it.status.shouldShowRationale -> {
                            Log.d("4444", " MainScreensActivity READ_CONTACTS shouldShowRationale")
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
                            Log.d("4444", " MainScreensActivity READ_CONTACTS isAlwaysDenied")
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

                Manifest.permission.CAMERA -> {
                    when {
                        it.status.isGranted -> {
                            Log.d("4444", " MainScreensActivity CAMERA isGranted")
                            savePermissionCamera(viewModel = viewModel, isGranted = true)
                        }

                        it.status.shouldShowRationale -> {
                            Log.d("4444", " MainScreensActivity CAMERA shouldShowRationale")
                            savePermissionCamera(viewModel = viewModel, isGranted = false)
                            DialogPermissions(
                                message = stringResource(id = R.string.justification_CAMERA),
                                onDismiss = { },
                                onConfirm = {
                                    openAppSettings(context = context)
                                }
                            )
                        }

                        it.isAlwaysDenied() -> {
                            Log.d("4444", " MainScreensActivity CAMERA isAlwaysDenied")
                            savePermissionCamera(viewModel = viewModel, isGranted = false)
                            DialogPermissions(
                                message = stringResource(id = R.string.justification_denied_CAMERA),
                                onDismiss = { },
                                onConfirm = {
                                    openAppSettings(context = context)
                                }
                            )
                        }
                    }
                }

                Manifest.permission.RECORD_AUDIO -> {
                    when {
                        it.status.isGranted -> {
                            Log.d("4444", " MainScreensActivity RECORD_AUDIO isGranted")
                            savePermissionRecordAudio(viewModel = viewModel, isGranted = true)
                        }

                        it.status.shouldShowRationale -> {
                            Log.d("4444", " MainScreensActivity RECORD_AUDIO shouldShowRationale")
                            savePermissionRecordAudio(viewModel = viewModel, isGranted = false)
                            DialogPermissions(
                                message = stringResource(id = R.string.justification_RECORD_AUDIO),
                                onDismiss = { },
                                onConfirm = {
                                    openAppSettings(context = context)
                                }
                            )
                        }

                        it.isAlwaysDenied() -> {
                            Log.d("4444", " MainScreensActivity RECORD_AUDIO isAlwaysDenied")
                            savePermissionRecordAudio(viewModel = viewModel, isGranted = false)
                            DialogPermissions(
                                message = stringResource(id = R.string.justification_denied_RECORD_AUDIO),
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

fun savePermissionReadPhoneNumbers(viewModel: MainScreensViewModel, isGranted: Boolean) {
    viewModel.savePermissions(
        key = Constants.KEY_READ_PHONE_NUMBERS,
        isGranted = isGranted
    )
}

fun savePermissionReadContacts(viewModel: MainScreensViewModel, isGranted: Boolean) {
    viewModel.savePermissions(
        key = Constants.KEY_READ_CONTACTS,
        isGranted = isGranted
    )
}

fun savePermissionCamera(viewModel: MainScreensViewModel, isGranted: Boolean) {
    viewModel.savePermissions(
        key = Constants.KEY_CAMERA,
        isGranted = isGranted
    )
}

fun savePermissionRecordAudio(viewModel: MainScreensViewModel, isGranted: Boolean) {
    viewModel.savePermissions(
        key = Constants.KEY_RECORD_AUDIO,
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
                androidx.compose.material.Text(
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
                    androidx.compose.material.Text(
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
                        androidx.compose.material.Text(
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


@SuppressLint("HardwareIds")
fun getOwnPhoneSender(context: Context): String {
    return if (checkReadPhoneNumberPermission(context = context)) {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        val ownPhoneSender = telephonyManager?.line1Number.toString()
        Log.d("4444", " MainScreensActivity getOwnPhoneSender ownPhoneSender=" + ownPhoneSender)
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


