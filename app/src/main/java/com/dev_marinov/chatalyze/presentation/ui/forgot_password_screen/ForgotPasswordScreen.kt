package com.dev_marinov.chatalyze.presentation.ui.forgot_password_screen

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.dev_marinov.chatalyze.R
import com.dev_marinov.chatalyze.presentation.util.GradientBackgroundHelper
import com.dev_marinov.chatalyze.presentation.util.TextFieldHintLogin
import com.dev_marinov.chatalyze.util.ShowToastHelper
import com.dev_marinov.chatalyze.util.SystemUiControllerHelper

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ForgotPasswordScreen(
    navController: NavHostController,
    viewModel: ForgotPasswordScreenViewModel = hiltViewModel()
) {
    SystemUiControllerHelper.SetSystemBars(false)
    SystemUiControllerHelper.SetStatusBarColor()
    GradientBackgroundHelper.SetGradientBackground()

    val softwareKeyboardController = LocalSoftwareKeyboardController.current

    var textEmailState by remember { mutableStateOf("") }
    val messageEmail = stringResource(id = R.string.email_warning)
    val context = LocalContext.current

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 200.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    softwareKeyboardController?.hide()
                }
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//         //   verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.Center
//        ) {
//            Text(
//                modifier = Modifier,
//                color = Color.White,
//                fontSize = 16.sp,
//                text = stringResource(id = R.string.header_forgot_screen)
//            )
     //   }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                modifier = Modifier,
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.header_forgot_screen)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            TextFieldHintLogin(
                value = textEmailState,
                onValueChanged = { textEmailState = it },
                hintText = stringResource(id = R.string.email),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(20))
                    .background(MaterialTheme.colors.surface)
                    .padding(start = 8.dp, end = 16.dp),
                icon = Icons.Rounded.Mail,
                isFocus = {

                }
            )
        }

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
                        textEmailState = textEmailState,
                        messageEmail = messageEmail,
                        context = context,
                        viewModel = viewModel
                    )
                }
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = stringResource(id = R.string.get_password),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

    }
}

fun checkLengthAndSendRegisterRequest(
    textEmailState: String,
    messageEmail: String,
    context: Context,
    viewModel: ForgotPasswordScreenViewModel
) {

    val isEmpty = textEmailState.isEmpty()
    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(textEmailState).matches()
    val containsCom = textEmailState.contains(".com")
    val containsGmailCom = textEmailState.contains("@gmail.com")
    val containsRu = textEmailState.contains(".ru")
    val containsGmailRu = textEmailState.contains("gmail.ru")

    Log.d(
        "4444", " isEmpty" + isEmpty + " isEmailValid=" + isEmailValid
                + " containsCom=" + containsCom + " containsRu=" + containsRu
    )

    if (textEmailState.length in 1..4) {
        ShowToastHelper.createToast(
            message = messageEmail,
            context = context
        )
    } else if (textEmailState.isEmpty()) {
        ShowToastHelper.createToast(
            message = messageEmail,
            context = context
        )
    } else if (isEmpty || ((isEmailValid && !containsCom) && (isEmailValid && !containsGmailCom) && (isEmailValid && !containsRu))
        || ((!isEmailValid && !containsCom) && (!isEmailValid && !containsGmailCom) && (!isEmailValid && !containsRu))
        || (containsGmailCom && containsRu) || (containsGmailRu)
    ) {
        ShowToastHelper.createToast(
            message = messageEmail,
            context = context
        )
    } else {
        ShowToastHelper.createToast(
            message = "выполняем отправку пароля",
            context = context
        )
//        viewModel.registerUser(
//            login = textLoginState,
//            email = textEmailState,
//            password = textPasswordState
//        )
    }
}
