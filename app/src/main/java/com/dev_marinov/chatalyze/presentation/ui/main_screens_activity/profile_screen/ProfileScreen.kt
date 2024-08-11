package com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.profile_screen

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.dev_marinov.chatalyze.presentation.ui.start_screen_activity.MainActivity
import com.dev_marinov.chatalyze.R
import com.dev_marinov.chatalyze.presentation.util.Constants
import com.dev_marinov.chatalyze.presentation.util.EditFormatPhoneHelper
import com.dev_marinov.chatalyze.presentation.util.GradientBackgroundHelper
import com.dev_marinov.chatalyze.presentation.util.ScreenRoute
import com.dev_marinov.chatalyze.presentation.util.ShowToastHelper
import com.dev_marinov.chatalyze.presentation.util.SystemUiControllerHelper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ProfileScreen(
    navHostController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel(),
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    SystemUiControllerHelper.SetSystemBars(true)
    SystemUiControllerHelper.SetStatusBarColor()
    SystemUiControllerHelper.SetNavigationBars(isVisible = true)
    GradientBackgroundHelper.SetMonochromeBackground()
    val ownPhoneSender by viewModel.ownPhoneSender.collectAsStateWithLifecycle("")
    val ownPhoneSenderState = remember { mutableStateOf("") }
    val statusCode by viewModel.statusCode.collectAsStateWithLifecycle()

    val descriptionLogout = stringResource(id = R.string.execute_logout)
    val descriptionDeleteProfile = stringResource(id = R.string.execute_delete_profile)
    val descriptionDeleteHistoryCall = stringResource(id = R.string.execute_delete_history_calls)

    var showDialog by remember { mutableStateOf(false) }
    var descriptionDialog by remember { mutableStateOf("") }
    var typeDialog by remember { mutableStateOf("") }
    if (showDialog) {
        DialogProfile(
            onDismiss = { showDialog = false },
            onConfirm = { executeTypeClicked ->
                showDialog = false
                when (executeTypeClicked) {
                    Constants.TYPE_LOGOUT -> {
                        Log.d("4444", " ProfileScreen TYPE_LOGOUT")
                        viewModel.executeLogout()
                    }

                    Constants.TYPE_DELETE_PROFILE -> {
                        Log.d("4444", " ProfileScreen TYPE_DELETE_PROFILE")
                        viewModel.executeDeleteProfile()
                    }

                    Constants.TYPE_DELETE_HISTORY_CALLS -> {
                        viewModel.executeDeleteHistoryCalls()
                    }
                }
            },
            description = descriptionDialog,
            executeType = typeDialog
        )
    }

    val tryAgainLater = stringResource(id = R.string.try_again_later)

    if (statusCode == 200 || statusCode == 404) {
        scope.launch {
            delay(100L)
        }
        TransitionToAuthScreen()
    } else if (statusCode == 409 || statusCode == 500) {
        ShowToastHelper.createToast(message = tryAgainLater, context = context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {

        val constraints = ConstraintSet {
            val headerChatText = createRefFor("header_profile_text")
            val ownPhoneSender = createRefFor("own_phone_sender")
            val btLogOut = createRefFor("bt_log_out")
            val btDeleteProfile = createRefFor("bt_delete_profile")
            val btDeleteHistoryCall = createRefFor("bt_delete_history_calls")

            constrain(headerChatText) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }

            constrain(ownPhoneSender) {
                top.linkTo(headerChatText.bottom)
                start.linkTo(parent.start)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }

            constrain(btLogOut) {
                top.linkTo(ownPhoneSender.bottom)
                start.linkTo(parent.start)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }

            constrain(btDeleteProfile) {
                top.linkTo(btLogOut.bottom)
                start.linkTo(parent.start)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }

            constrain(btDeleteHistoryCall) {
                top.linkTo(btDeleteProfile.bottom)
                start.linkTo(parent.start)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }
        }

        ConstraintLayout(
            constraintSet = constraints,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.profile_screen_setting),
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .height(50.dp)
                    .layoutId("header_profile_text")
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .layoutId("own_phone_sender")
                    .padding(top = 16.dp)
            ) {
                LaunchedEffect(ownPhoneSender) {
                    if (ownPhoneSender.isNotEmpty()) {
                        ownPhoneSenderState.value = EditFormatPhoneHelper.edit(phone = ownPhoneSender)
                    }
                }
                Text(
                    text = "Your phone number " + ownPhoneSenderState.value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Button(
                modifier = Modifier
                    .layoutId("bt_log_out")
                    .padding(top = 24.dp),
                onClick = {
                    showDialog = true
                    descriptionDialog = descriptionLogout
                    typeDialog = Constants.TYPE_LOGOUT
                },
                border = BorderStroke(1.dp, Color.White),
                shape = RoundedCornerShape(50), // = 50% percent
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.main_violet))
            ) {
                Text(
                    text = "Logout",
                    color = Color.White)
            }

            Button(
                modifier = Modifier
                    .layoutId("bt_delete_profile")
                    .padding(top = 8.dp),
                onClick = {
                    showDialog = true
                    descriptionDialog = descriptionDeleteProfile
                    typeDialog = Constants.TYPE_DELETE_PROFILE
                },
                border = BorderStroke(1.dp, Color.White),
                shape = RoundedCornerShape(50), // = 50% percent
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.main_violet))
            ) {
                Text(
                    text = "Delete profile",
                    color = Color.White)
            }

            Button(
                modifier = Modifier
                    .layoutId("bt_delete_history_calls")
                    .padding(top = 8.dp),
                onClick = {
                    showDialog = true
                    descriptionDialog = descriptionDeleteHistoryCall
                    typeDialog = Constants.TYPE_DELETE_HISTORY_CALLS
                },
                border = BorderStroke(1.dp, Color.White),
                shape = RoundedCornerShape(50), // = 50% percent
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.main_violet))
            ) {
                Text(
                    text = "Delete history calls",
                    color = Color.White)
            }
        }
    }

    CustomBackStackOnlyCallsScreen(
        navHostController = navHostController
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TransitionToAuthScreen() {
    val context = LocalContext.current
    val deepLinkIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("auth_screen"),
        context,
        MainActivity::class.java
    )

    val taskStackBuilder = TaskStackBuilder.create(context).apply {
        addNextIntentWithParentStack(deepLinkIntent)
    }

    val deepLinkPendingIntent = taskStackBuilder.editIntentAt(0)?.run {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        PendingIntent.getActivity(context, 0, this, PendingIntent.FLAG_IMMUTABLE)
    }

    deepLinkPendingIntent?.send()
}

@Composable
fun CustomBackStackOnlyCallsScreen(
    navHostController: NavHostController,
) {
    val currentRoute = navHostController.currentBackStackEntryAsState().value?.destination?.route
    BackHandler(enabled = currentRoute != null) {
        if (currentRoute == ScreenRoute.ProfileScreen.route) {
            navHostController.navigate(ScreenRoute.ChatsScreen.route)
        }
    }
}

@Composable
fun DialogProfile(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    description: String,
    executeType: String,
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
                        .padding(20.dp),
                    text = description,
                    color = Color.White
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.cancel),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Button(
                        onClick = {
                            when (executeType) {
                                Constants.TYPE_LOGOUT -> {
                                    onConfirm(Constants.TYPE_LOGOUT)
                                }

                                Constants.TYPE_DELETE_PROFILE -> {
                                    onConfirm(Constants.TYPE_DELETE_PROFILE)
                                }

                                Constants.TYPE_DELETE_HISTORY_CALLS -> {
                                    onConfirm(Constants.TYPE_DELETE_HISTORY_CALLS)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                    ) {
                        Text(
                            text = stringResource(id = R.string.ok),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}