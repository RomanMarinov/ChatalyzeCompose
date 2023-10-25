package com.dev_marinov.chatalyze.presentation.ui.call_screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.dev_marinov.chatalyze.R
import com.dev_marinov.chatalyze.presentation.util.GradientBackgroundHelper
import com.dev_marinov.chatalyze.presentation.util.ScreenRoute
import com.dev_marinov.chatalyze.presentation.util.SystemUiControllerHelper

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CallsScreen(
    navHostController: NavHostController,
    viewModel: CallsScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    SystemUiControllerHelper.SetSystemBars(true)
    SystemUiControllerHelper.SetStatusBarColor()
    SystemUiControllerHelper.SetNavigationBars(isVisible = true)
    //SystemUiControllerHelper.SetStatusBarColorNoGradient()
    GradientBackgroundHelper.SetMonochromeBackground()
   // SystemUiControllerHelper.SetStatusBarColorNoGradient()


    val fakeCalls by viewModel.fakeCalls.collectAsStateWithLifecycle()

    // Добавьте эффект для загрузки данных после отображения экрана
    // пересмотреть место вызова тк подвисает экран в момент переключения
    LaunchedEffect(viewModel) {
        viewModel.getFakeCalls()
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
        //  .background(colorResource(id = R.color.main_violet_light))
        //  .systemBarsPadding()
    ) {

        val constraints = ConstraintSet {
            val headerChatText = createRefFor("header_calls_text")

            constrain(headerChatText) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                //end.linkTo(createChatIcon.start)
                bottom.linkTo(parent.bottom)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }
        }

        ConstraintLayout(
            constraintSet = constraints,
            modifier = Modifier
                .fillMaxWidth()
                //.background(Color.Red)
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.call_screen_calls),
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .height(50.dp)
                    .layoutId("header_calls_text")
            )
        }

        BoxWithConstraints {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 8.dp, end = 8.dp)
            ) {
                items(fakeCalls) { item ->
                    Text(text = item)
                }
                // Добавьте другие элементы списка здесь
            }
        }
    }
    CustomBackStackOnlyCallsScreen(
        navHostController = navHostController)
}

@Composable
fun CustomBackStackOnlyCallsScreen(
    navHostController: NavHostController
) {
    val currentRoute = navHostController.currentBackStackEntryAsState().value?.destination?.route
    BackHandler(enabled = currentRoute != null) {
        if (currentRoute == ScreenRoute.CallScreen.route) {
            navHostController.navigate(ScreenRoute.ChatsScreen.route)
        }
    }
}