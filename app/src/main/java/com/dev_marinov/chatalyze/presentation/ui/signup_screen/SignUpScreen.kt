package com.dev_marinov.chatalyze.presentation.ui.signup_screen

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
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
import com.dev_marinov.chatalyze.presentation.util.TextFieldHintEmail
import com.dev_marinov.chatalyze.presentation.util.TextFieldHintPassword
import com.dev_marinov.chatalyze.util.ScreenRoute
import com.dev_marinov.chatalyze.util.ShowToastHelper
import com.dev_marinov.chatalyze.util.SystemUiControllerHelper

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignUpScreen(
    navController: NavHostController,
    viewModel: SignUpScreenViewModel = hiltViewModel()
) {
    SystemUiControllerHelper.SetSystemBars(false)
    SystemUiControllerHelper.SetStatusBarColor()
    GradientBackgroundHelper.SetGradientBackground()

//    var textUserNameState by remember { mutableStateOf("") }
    var textEmailState by remember { mutableStateOf("") }
    var textPasswordState by remember { mutableStateOf("") }
    val messagePassword = stringResource(id = R.string.password_warning)
    val messageEmail = stringResource(id = R.string.email_warning)
    val messageEmailPassword = stringResource(id = R.string.email_password_warning)
    val successfulRegistration = stringResource(id = R.string.successful_registration)
    val context = LocalContext.current
    var isFocusTextField by remember { mutableStateOf(false) }

    val softwareKeyboardController = LocalSoftwareKeyboardController.current

    val scope = rememberCoroutineScope()

    val notice by viewModel.notice.collectAsStateWithLifecycle()
    val statusCode by viewModel.statusCode.collectAsStateWithLifecycle()

    LaunchedEffect(notice) {
        if (notice.isNotEmpty()) {
            ShowToastHelper.createToast(message = notice, context = context)
        }
    }

    LaunchedEffect(notice) {
        if (statusCode == 200) {
            navController.popBackStack(ScreenRoute.AuthScreen.route, false)
        }
    }

    Column(
        modifier = Modifier
            .imePadding()
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    softwareKeyboardController?.hide()
                }
            ),
        //.padding(top = 200.dp)
        //.imePadding()
        //verticalArrangement = Arrangement.Center
    ) {


        val constraintsTop = ConstraintSet {

            val img_back = createRefFor("img_back")
            val email = createRefFor("email")
            val password = createRefFor("password")
            val bt_sign_up = createRefFor("bt_sign_up")

            constrain(img_back) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }

            constrain(email) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
//                width = Dimension.value(40.dp)
//                height = Dimension.wrapContent
            }

            constrain(password) {
                top.linkTo(email.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
//                bottom.linkTo(parent.bottom)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }

            constrain(bt_sign_up) {
                top.linkTo(password.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                // bottom.linkTo(parent.bottom)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }
        }

        ConstraintLayout(
            constraintSet = constraintsTop,
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
                        navController.popBackStack("auth_screen", false)
                    }
            )

            Box(
                modifier = Modifier
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
                    .fillMaxWidth()
                    .imePadding()
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

            if (!isFocusTextField) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .layoutId("bt_sign_up"),
                    contentAlignment = Alignment.Center,
                ) {
                    OutlinedButton(
                        modifier = Modifier
                            .width(300.dp)
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .border(
                                border = BorderStroke(1.dp, Color.White),
                                shape = RoundedCornerShape(100.dp)
                            ),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                        onClick = {

//                            val emailPasswordIsValid = CheckEmailPasswordTextFieldHelper.check(
//                                textEmailState = textEmailState,
//                                textPasswordState = textPasswordState,
//                                messagePassword = messagePassword,
//                                messageEmailPassword = messageEmailPassword,
//                                messageEmail = messageEmail,
//                                context = context,
//                            )
//                            if (emailPasswordIsValid) {
//                                ShowToastHelper.createToast(
//                                    message = "выполняем запрос на регистрацию",
//                                    context = context
//                                )
////                                viewModel.registerUser(
////                                    email = textEmailState,
////                                    password = textPasswordState
////                                )
                            viewModel.registerUser(
                                email = "marinov37@mail.ru",
                                password = "2"
                            )
                            //  }
                        }
                    ) {
                        Text(
                            modifier = Modifier.padding(8.dp),
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
}
