package com.dev_marinov.chatalyze.presentation.ui.start_screen_activity.create_password_screen

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.dev_marinov.chatalyze.R
import com.dev_marinov.chatalyze.presentation.util.GradientBackgroundHelper
import com.dev_marinov.chatalyze.presentation.util.TextFieldHintPassword
import com.dev_marinov.chatalyze.presentation.util.ScreenRoute
import com.dev_marinov.chatalyze.presentation.util.ShowToastHelper
import com.dev_marinov.chatalyze.presentation.util.SystemUiControllerHelper

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CreatePasswordScreen(
    navController: NavHostController,
    viewModel: CreatePasswordViewModel = hiltViewModel()
) {
    SystemUiControllerHelper.SetSystemBars(false)
    SystemUiControllerHelper.SetStatusBarColor()
    GradientBackgroundHelper.SetGradientBackground()

    val softwareKeyboardController = LocalSoftwareKeyboardController.current

    val statusCode by viewModel.statusCode.collectAsStateWithLifecycle()
    val notice by viewModel.notice.collectAsStateWithLifecycle()

    var textPasswordState by remember { mutableStateOf("") }
    var textPasswordConfirmState by remember { mutableStateOf("") }
    val passwordMismatch = stringResource(id = R.string.password_mismatch)
    val messagePassword = stringResource(id = R.string.password_warning)
    val messageEmailPassword = stringResource(id = R.string.email_password_warning)
    val context = LocalContext.current
    var isFocusTextFiled by remember { mutableStateOf(false) }

    var isClicked by remember { mutableStateOf(false) }

    LaunchedEffect(statusCode) {
        when (statusCode) {
            200 -> {
                navController.popBackStack(ScreenRoute.AuthScreen.route, false)
            }
            500 -> {
                ShowToastHelper.createToast(message = notice, context = context)
            }
            400 -> {
                ShowToastHelper.createToast(message = notice, context = context)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    softwareKeyboardController?.hide()
                }
            )
    ) {

        val constraints = ConstraintSet {

            val img_back = createRefFor("img_back")
            val password = createRefFor("password")
            val password_confirm = createRefFor("password_confirm")
            val create_password = createRefFor("create_password")

            constrain(img_back) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }

            constrain(password) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }

            constrain(password_confirm) {
                top.linkTo(password.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }

            constrain(create_password) {
                top.linkTo(password_confirm.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }
        }

        ConstraintLayout(
            constraintSet = constraints,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp, bottom = 8.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.ic_back_to_prev_screen),
                contentDescription = "back",
                modifier = Modifier
                    .layoutId("img_back")
                    .padding(start = 8.dp, top = 16.dp)
                    .systemBarsPadding() // Добавить отступ от скрытого статус-бара
                    .size(30.dp)
                    .clip(RoundedCornerShape(50))
                    .clickable {
                        Log.d("4444", " clidk back")
                        navController.popBackStack(ScreenRoute.ForgotPasswordScreen.route, false)
                    }
            )

            Box(
                modifier = Modifier
                    // .imePadding()
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp)
                    .layoutId("password"),
                contentAlignment = Alignment.Center

            ) {
                TextFieldHintPassword(
                    value = textPasswordState,
                    onValueChanged = { textPasswordState = it },
                    hintText = stringResource(id = R.string.password),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(20))
                        .background(MaterialTheme.colors.surface)
                        .padding(start = 8.dp, end = 8.dp),
                    icon = Icons.Rounded.Lock
                )
            }

            Box(
                modifier = Modifier
                    // .imePadding()
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp)
                    .layoutId("password_confirm"),
                contentAlignment = Alignment.Center
            ) {
                TextFieldHintPassword(
                    value = textPasswordConfirmState,
                    onValueChanged = { textPasswordConfirmState = it },
                    hintText = stringResource(id = R.string.password_confirm),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(20))
                        .background(MaterialTheme.colors.surface)
                        .padding(start = 8.dp, end = 8.dp),
                    icon = Icons.Rounded.Lock
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .width(250.dp)
                    .height(50.dp)
                    .layoutId("create_password"),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        if (textPasswordState == textPasswordConfirmState) {
                            viewModel.sendRefreshPassword(password = textPasswordState)
                        } else {
                            ShowToastHelper.createToast(message = passwordMismatch, context = context)
                        }
                    },
                    shape = RoundedCornerShape(100),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                    border = BorderStroke(1.dp, Color.White),
                    modifier = Modifier
                        .width(250.dp)
                        .height(50.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                        text = stringResource(id = R.string.create_password),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}