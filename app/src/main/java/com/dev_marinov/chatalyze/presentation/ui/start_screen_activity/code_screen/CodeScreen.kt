package com.dev_marinov.chatalyze.presentation.ui.start_screen_activity.code_screen

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.dev_marinov.chatalyze.presentation.ui.start_screen_activity.code_screen.model.UserCode
import com.dev_marinov.chatalyze.presentation.util.AnimatedCounter
import com.dev_marinov.chatalyze.presentation.util.DecoratedTextField
import com.dev_marinov.chatalyze.presentation.util.GradientBackgroundHelper
import com.dev_marinov.chatalyze.presentation.util.ScreenRoute
import com.dev_marinov.chatalyze.presentation.util.ShowToastHelper
import com.dev_marinov.chatalyze.presentation.util.SystemUiControllerHelper
import kotlinx.coroutines.delay

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CodeScreen(
    navController: NavHostController,
    viewModel: CodeViewModel = hiltViewModel()
) {
    SystemUiControllerHelper.SetSystemBars(false)
    SystemUiControllerHelper.SetStatusBarColor()
    GradientBackgroundHelper.SetGradientBackground()

    val softwareKeyboardController = LocalSoftwareKeyboardController.current

    val emailFromPreferences by viewModel.getEmail.collectAsStateWithLifecycle("")
    val statusCode by viewModel.statusCode.collectAsStateWithLifecycle()
    val notice by viewModel.notice.collectAsStateWithLifecycle()

    var textCodeState by remember { mutableStateOf("") }
    val codeWarning = stringResource(id = R.string.code_warning)
    val context = LocalContext.current

    LaunchedEffect(statusCode) {
        if (statusCode == 200) {
            ShowToastHelper.createToast(message = notice, context = context)
            navController.navigate(ScreenRoute.CreatePasswordScreen.route)
        }  else if (statusCode == 404) {
            Log.d("4444", " notice=" + notice)
            ShowToastHelper.createToast(message = notice, context = context)
            // navController.popBackStack(ScreenRoute.ForgotPasswordScreen.route, false)
            // потом убрать
          //  navController.navigate(ScreenRoute.CreatePasswordScreen.route)
        } else if (statusCode == 410) {
            ShowToastHelper.createToast(message = notice, context = context)
            // navController.popBackStack(ScreenRoute.ForgotPasswordScreen.route, false)
            // потом убрать
            navController.navigate(ScreenRoute.CreatePasswordScreen.route)
        }
    }

    Column(
        modifier = Modifier
            .imePadding()
            .fillMaxSize()
            //  .padding(top = 200.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    softwareKeyboardController?.hide()
                }
            ),
        //   verticalArrangement = Arrangement.Center,
        //  horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val constraintsTop = ConstraintSet {

            val img_back = createRefFor("img_back")
            val counter = createRefFor("counter")
            val header = createRefFor("header")
            val code = createRefFor("code")
            val send_code = createRefFor("send_code")

            constrain(img_back) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }

            constrain(counter) {
                top.linkTo(img_back.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                // bottom.linkTo(code.top)

//                height = Dimension.wrapContent
            }

            constrain(header) {
                //top.linkTo(email.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(code.top)
//                width = Dimension.value(40.dp)
//                height = Dimension.wrapContent
            }

            constrain(code) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }

            constrain(send_code) {
                top.linkTo(code.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
//                bottom.linkTo(parent.bottom)
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
                        Log.d("4444", " clidk back")
                        navController.popBackStack(ScreenRoute.ForgotPasswordScreen.route, false)
                    }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .layoutId("counter")
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var count by remember { mutableStateOf(60) }

                AnimatedCounter(
                    count = count,
                    //style = MaterialTheme.typography.h1
                )

                LaunchedEffect(Unit) {
                    while (count > 0) {
                        delay(1000L)
                        count--
                        if (count == 0) {
                            navController.popBackStack(
                                ScreenRoute.ForgotPasswordScreen.route,
                                false
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .layoutId("header")
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    modifier = Modifier,
                    color = Color.White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    text = stringResource(id = R.string.header_code_screen)
                )
            }

            Box(
                modifier = Modifier
                    .layoutId("code")
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                DecoratedTextField(
                    value = textCodeState,
                    length = 5,
                    onValueChange = {
                        textCodeState = it
                    }
                )
            }
            Box(
                modifier = Modifier
                    .imePadding()
                    .layoutId("send_code")
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

                        if (textCodeState.length == 5) {
                            viewModel.sendCode(code = textCodeState.toInt())
                        } else {
                            ShowToastHelper.createToast(
                                message = codeWarning,
                                context = context
                            )
                        }
                    }
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = stringResource(id = R.string.send_code),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}