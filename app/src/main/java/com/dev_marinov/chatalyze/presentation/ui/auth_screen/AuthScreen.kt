package com.dev_marinov.chatalyze.presentation.ui.auth_screen

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
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
import com.dev_marinov.chatalyze.presentation.util.TextFieldHintEmail
import com.dev_marinov.chatalyze.presentation.util.TextFieldHintPassword
import com.dev_marinov.chatalyze.util.ScreenRoute
import com.dev_marinov.chatalyze.util.ShowToastHelper
import com.dev_marinov.chatalyze.util.SystemUiControllerHelper

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AuthScreen(
    navController: NavHostController,
    viewModel: AuthScreenViewModel = hiltViewModel()
) {
    SystemUiControllerHelper.SetSystemBars(false)
    SystemUiControllerHelper.SetStatusBarColor()
    GradientBackgroundHelper.SetGradientBackground()

    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    val refreshToken by viewModel.refreshToken.collectAsStateWithLifecycle("")
    val notice by viewModel.notice.collectAsStateWithLifecycle()

    var textEmailState by remember { mutableStateOf("") }
    var textPasswordState by remember { mutableStateOf("") }
    val messageEmail = stringResource(id = R.string.email_warning)
    val messagePassword = stringResource(id = R.string.password_warning)
    val messageEmailPassword = stringResource(id = R.string.email_password_warning)
    val context = LocalContext.current
    var isFocusTextFiled by remember { mutableStateOf(false) }

    var isClicked by remember { mutableStateOf(false) }
    Log.d("4444", " refreshToken=" + refreshToken)

    LaunchedEffect(refreshToken) {
        Log.d("4444", " refreshToken=" + refreshToken)
        if (refreshToken.isNotEmpty()) {
            navController.navigate(ScreenRoute.ChatalyzeScreen.route)
        }
    }

    LaunchedEffect(notice) {
        if (notice.isNotEmpty()) {
            ShowToastHelper.createToast(message = notice, context = context)
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

            val email = createRefFor("email")
            val password = createRefFor("password")
            val forgot_password = createRefFor("forgot_password")
            val bt_sign_in = createRefFor("bt_sign_in")
            val bt_sign_up = createRefFor("bt_sign_up")

            constrain(email) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }

            constrain(password) {
                top.linkTo(email.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }

            constrain(forgot_password) {
                top.linkTo(password.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }

            constrain(bt_sign_in) {
                top.linkTo(forgot_password.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }

            constrain(bt_sign_up) {
                top.linkTo(bt_sign_in.bottom)
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
            Box(
                modifier = Modifier
                    // .imePadding()
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp)
                    .layoutId("email"),
                contentAlignment = Alignment.Center

            ) {
                TextFieldHintEmail(
                    value = textEmailState,
                    onValueChanged = { textEmailState = it },
                    hintText = stringResource(id = R.string.email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(20))
                        .background(MaterialTheme.colors.surface)
                        .padding(start = 12.dp, end = 16.dp),
                    icon = Icons.Rounded.Mail
                )
            }

            Box(
                modifier = Modifier
                    // .imePadding()
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp)
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .layoutId("forgot_password"),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .clip(RoundedCornerShape(50))
                        .clickable {
                            isClicked = !isClicked
                            navController.navigate("forgot_password_screen")
                        },
                    color = Color.White,
                    fontSize = 14.sp,
                    text = stringResource(id = R.string.forgot_password)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .width(250.dp)
                    .height(50.dp)
                    .layoutId("bt_sign_in"),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.signInAndSaveTokenSignIn(
                            email = "marinov37@mail.ru",
                            password = "2"
                        )
                        //navController.navigate(ScreenRoute.ChatalyzeScreen.route)
//                        val emailPasswordIsValid = CheckEmailPasswordTextFieldHelper.check(
//                            textEmailState = textEmailState,
//                            textPasswordState = textPasswordState,
//                            messagePassword = messagePassword,
//                            messageEmailPassword = messageEmailPassword,
//                            messageEmail = messageEmail,
//                            context = context,
//                        )
//                        if (emailPasswordIsValid) {
//                            ShowToastHelper.createToast(
//                                message = "выполняем проверку на вход",
//                                context = context
//                            )
////                                viewModel.registerUser(
////                                    email = textEmailState,
////                                    password = textPasswordState
////                                )
////                                viewModel.registerUser(
////                                    email = "m@yandex.ru",
////                                    password = "12345"
////                                )
//                        }
                    },
                    shape = RoundedCornerShape(100),
                    colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.main_violet)),
                    border = BorderStroke(1.dp, Color.White),
                    modifier = Modifier
                        .width(250.dp)
                        .height(50.dp)
                ) {
                    Text(
                        modifier = Modifier,
                        text = stringResource(id = R.string.auth_bt_sign_in),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .width(250.dp)
                    .height(50.dp)
                    .layoutId("bt_sign_up"),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        navController.navigate("sign_up_screen")
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
                        text = stringResource(id = R.string.auth_bt_sign_up),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}