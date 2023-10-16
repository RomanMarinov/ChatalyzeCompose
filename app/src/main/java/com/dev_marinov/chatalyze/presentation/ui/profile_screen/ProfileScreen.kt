package com.dev_marinov.chatalyze.presentation.ui.profile_screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import androidx.navigation.compose.currentBackStackEntryAsState
import com.dev_marinov.chatalyze.R
import com.dev_marinov.chatalyze.presentation.util.GradientBackgroundHelper
import com.dev_marinov.chatalyze.util.ScreenRoute
import com.dev_marinov.chatalyze.util.ShowToastHelper
import com.dev_marinov.chatalyze.util.SystemUiControllerHelper

@Composable
fun ProfileScreen(
    navHostController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel(),
    authHostController: NavHostController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    SystemUiControllerHelper.SetSystemBars(true)
    SystemUiControllerHelper.SetStatusBarColor()
    SystemUiControllerHelper.SetNavigationBars(isVisible = true)
    //SystemUiControllerHelper.SetStatusBarColorNoGradient()
    GradientBackgroundHelper.SetMonochromeBackground()
    //SystemUiControllerHelper.SetStatusBarColorNoGradient()


    //SystemUiControllerHelper.SetStatusBarColorNoGradient()
    // viewModel.onClickHideNavigationBar(false)

    val statusCode by viewModel.statusCode.collectAsStateWithLifecycle()

    val tryAgainLater = stringResource(id = R.string.try_again_later)

    LaunchedEffect(statusCode) {
        if (statusCode == 200) {
            authHostController.navigate(ScreenRoute.AuthScreen.route)
        } else if (statusCode != 0) {
            ShowToastHelper.createToast(message = tryAgainLater, context = context)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
        //  .background(colorResource(id = R.color.main_violet_light))
        //  .systemBarsPadding()
    ) {

        val constraints = ConstraintSet {
            val headerChatText = createRefFor("header_profile_text")
            val btLogOut = createRefFor("bt_log_out")
            val btDeleteProfile = createRefFor("bt_delete_profile")

            constrain(headerChatText) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                //end.linkTo(createChatIcon.start)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }

            constrain(btLogOut) {
                top.linkTo(headerChatText.bottom)
                start.linkTo(parent.start)
                //end.linkTo(createChatIcon.start)
                //bottom.linkTo(parent.bottom)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }

            constrain(btDeleteProfile) {
                top.linkTo(btLogOut.bottom)
                start.linkTo(parent.start)
                //end.linkTo(createChatIcon.start)
                //bottom.linkTo(parent.bottom)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }
        }

        ConstraintLayout(
            constraintSet = constraints,
            modifier = Modifier
                .fillMaxSize()
                //.background(Color.Red)
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.profile_screen_setting),
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .height(50.dp)
                    //ƒ .background(Color.Green)
                    .layoutId("header_profile_text")
            )

            Button(
                modifier = Modifier.layoutId("bt_log_out"),
                onClick = {
                    viewModel.executeLogout()
                }) {
                Text(text = "Logout")
            }

            Button(
                modifier = Modifier.layoutId("bt_delete_profile"),
                onClick = {
                    viewModel.executeDeleteProfile()
                }) {
                Text(text = "Delete profile")
            }

        }

    }

    CustomBackStackOnlyCallsScreen(
        navHostController = navHostController
    )
}

@Composable
fun CustomBackStackOnlyCallsScreen(
    navHostController: NavHostController
) {
    val currentRoute = navHostController.currentBackStackEntryAsState().value?.destination?.route
    BackHandler(enabled = currentRoute != null) {
        if (currentRoute == ScreenRoute.ProfileScreen.route) {
            navHostController.navigate(ScreenRoute.ChatsScreen.route)
        }
    }
}