package com.dev_marinov.chatalyze.presentation.ui.signup_screen

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.dev_marinov.chatalyze.R
import com.dev_marinov.chatalyze.presentation.util.GradientBackgroundHelper
import com.dev_marinov.chatalyze.presentation.util.TextFieldHintEmail
import com.dev_marinov.chatalyze.presentation.util.TextFieldHintLogin
import com.dev_marinov.chatalyze.presentation.util.TextFieldHintPassword
import com.dev_marinov.chatalyze.util.ShowToastHelper
import com.dev_marinov.chatalyze.util.SystemUiControllerHelper

@Composable
fun SignUpScreen(
    navController: NavHostController,
    viewModel: SignUpScreenViewModel = hiltViewModel()
) {
    SystemUiControllerHelper.SetSystemBars(false)
    SystemUiControllerHelper.SetStatusBarColor()
    GradientBackgroundHelper.SetGradientBackground()

    Image(
        painter = painterResource(id = R.drawable.ic_back_to_prev_screen),
        contentDescription = "back",
        modifier = Modifier
            .padding(start = 8.dp, top = 16.dp)
            .systemBarsPadding() // Добавить отступ от скрытого статус-бара
            .size(30.dp)
            .clip(RoundedCornerShape(50))
            .clickable {
                navController.popBackStack("auth_screen", false)
            }
    )

    var textLoginState by remember { mutableStateOf("") }
    var textEmailState by remember { mutableStateOf("") }
    var textPasswordState by remember { mutableStateOf("") }
    val messageLogin = stringResource(id = R.string.login_warning)
    val messagePassword = stringResource(id = R.string.password_warning)
    val messageEmail = stringResource(id = R.string.email_warning)
    val messageLoginEmailPassword = stringResource(id = R.string.login_password_email_warning)
    val context = LocalContext.current
    var isFocusTextField by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 200.dp)
            //.imePadding()
        ,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            TextFieldHintLogin(
                value = textLoginState,
                onValueChanged = { textLoginState = it },
                hintText = stringResource(id = R.string.login),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(20))
                    .background(MaterialTheme.colors.surface)
                    .padding(start = 8.dp, end = 16.dp),
                icon = Icons.Rounded.Person,
                isFocus = {
                    isFocusTextField = it
                    Log.d("4444", " click isFocusTextField =" +isFocusTextField )
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
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
                    .padding(start = 8.dp, end = 16.dp),
                icon = Icons.Rounded.Mail
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth().imePadding()
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp),
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
        Log.d("4444", " isFocusTextField =" +isFocusTextField )
        if (!isFocusTextField) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
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

                        checkLengthAndSendRegisterRequest(
                            textLoginState = textLoginState,
                            textEmailState = textEmailState,
                            textPasswordState = textPasswordState,
                            messageLogin = messageLogin,
                            messagePassword = messagePassword,
                            messageLoginEmailPassword = messageLoginEmailPassword,
                            messageEmail = messageEmail,
                            context = context,
                            viewModel = viewModel
                        )
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

fun checkLengthAndSendRegisterRequest(
    textLoginState: String,
    textEmailState: String,
    textPasswordState: String,
    messageLogin: String,
    messagePassword: String,
    messageLoginEmailPassword: String,
    messageEmail: String,
    context: Context,
    viewModel: SignUpScreenViewModel
) {

    val isEmpty = textEmailState.isEmpty()
    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(textEmailState).matches()
    val containsCom = textEmailState.contains(".com")
    val containsGmailCom = textEmailState.contains("@gmail.com")
    val containsRu = textEmailState.contains(".ru")
    val containsGmailRu = textEmailState.contains("gmail.ru")

    Log.d("4444", " isEmpty" + isEmpty + " isEmailValid=" + isEmailValid
    + " containsCom=" + containsCom + " containsRu=" + containsRu)

    if ((textLoginState.length in 1..4)
        && (textPasswordState.length in 1..4)
        && (messageLoginEmailPassword.length in 1..4)
    ) {
        ShowToastHelper.createToast(
            message = messageLoginEmailPassword,
            context = context
        )
    } else if (textLoginState.isEmpty()
        && textPasswordState.isEmpty()
        && textEmailState.isEmpty()
    ) {
        ShowToastHelper.createToast(
            message = messageLoginEmailPassword,
            context = context
        )
    } else if (textLoginState.isEmpty() || (textLoginState.length in 1..4)) {
        ShowToastHelper.createToast(
            message = messageLogin,
            context = context
        )
    } else if (isEmpty || ((isEmailValid && !containsCom) && (isEmailValid && !containsGmailCom) && (isEmailValid && !containsRu))
        || ((!isEmailValid && !containsCom) && (!isEmailValid && !containsGmailCom) && (!isEmailValid && !containsRu))
        || (containsGmailCom && containsRu) || (containsGmailRu)) {
        ShowToastHelper.createToast(
            message = messageEmail,
            context = context
        )
    } else if (textPasswordState.isEmpty() || (textPasswordState.length in 1..4)) {
        ShowToastHelper.createToast(
            message = messagePassword,
            context = context
        )
    } else {
        ShowToastHelper.createToast(
            message = "выполняем запрос на регистрацию",
            context = context
        )
//        viewModel.registerUser(
//            login = textLoginState,
//            email = textEmailState,
//            password = textPasswordState
//        )
    }
}
