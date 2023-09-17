package com.dev_marinov.chatalyze.presentation.ui.chat_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.dev_marinov.chatalyze.R
import com.dev_marinov.chatalyze.presentation.util.TextFieldHintLogin
import com.dev_marinov.chatalyze.presentation.util.TextFieldHintWriteMessage
import com.dev_marinov.chatalyze.util.SystemUiControllerHelper

@Composable
fun ChatScreen(
    navHostController: NavHostController,
    viewModel: ChatScreenViewModel = hiltViewModel()
) {
    //viewModel.onMovieClickedHideNavigationBar(true)
    SystemUiControllerHelper.SetSystemBars(true)
    // SystemUiControllerHelper.StatusBarColor()
    SystemUiControllerHelper.SetStatusBarColorNoGradient()
    SystemUiControllerHelper.SetNavigationBars(isVisible = true)
//    GradientBackgroundHelper.SetGradientBackground()
    // viewModel.onMovieClickedHideNavigationBar(isHide = true)

    // Обработка нажатия на кнопку назад
    val onBackPressed = {
        navHostController.navigateUp()
    }

    var textMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(colorResource(id = R.color.main_violet_light))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { }
            )


        //  .systemBarsPadding()
    ) {

        val constraintsTop = ConstraintSet {

            val back = createRefFor("back")
            val iconUser = createRefFor("iconUser")
            val nameAndStatusNetworkUser = createRefFor("nameAndStatusNetworkUser")
            val iconVideoCall = createRefFor("iconVideoCall")
            val iconCall = createRefFor("iconCall")
            val contentChat = createRefFor("contentChat")

            constrain(back) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                // end.linkTo(iconVideoCall.start)
                bottom.linkTo(parent.bottom)
                width = Dimension.value(40.dp)
                height = Dimension.wrapContent
            }

            constrain(iconUser) {
                top.linkTo(parent.top)
                // start.linkTo(parent.start)
                end.linkTo(nameAndStatusNetworkUser.start)
                bottom.linkTo(parent.bottom)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }

            constrain(nameAndStatusNetworkUser) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }

            constrain(iconVideoCall) {
                top.linkTo(parent.top)
                //start.linkTo(back.end)
                end.linkTo(iconCall.start)
                bottom.linkTo(parent.bottom)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }

            constrain(iconCall) {
                top.linkTo(parent.top)
                // start.linkTo(iconVideoCall.end, margin = 8.dp)
                end.linkTo(parent.end, margin = 4.dp)
                bottom.linkTo(parent.bottom)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }

//            constrain(contentChat) {
//                top.linkTo(parent.top)
//                // start.linkTo(iconVideoCall.end, margin = 8.dp)
//                end.linkTo(parent.end, margin = 4.dp)
//                bottom.linkTo(parent.bottom)
//                width = Dimension.wrapContent
//                height = Dimension.wrapContent
//            }
        }

        ConstraintLayout(
            constraintSet = constraintsTop,
            modifier = Modifier
                .fillMaxWidth()
                //.background(Color.Green)
                .padding(top = 16.dp, bottom = 16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back_to_prev_screen),
                contentDescription = "back",
                modifier = Modifier
                    .fillMaxWidth()
                    //  .systemBarsPadding() // Добавить отступ от скрытого статус-бара
                    .size(30.dp)
                    .clip(RoundedCornerShape(50))
                    .clickable {
                        onBackPressed()
                    }
                    .layoutId("back")
                //  .background(Color.Cyan)
            )

            Icon(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(30.dp)
                    .layoutId("iconUser"),
//                        .layoutId("icon_person"),
                painter = painterResource(id = R.drawable.ic_user),
                contentDescription = "",
//                tint = colorResource(id = R.color.main_violet),
                tint = Color.White,
            )

            Column(
                modifier = Modifier
                    // .background(Color.Red)
                    .padding(4.dp)
                    .layoutId("nameAndStatusNetworkUser")
            ) {
                Text(
                    text = "Маринов Роман",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "в сети",
                    color = Color.White,
                    fontSize = 12.sp,
                )
            }

            IconButton(
                modifier = Modifier
                    .layoutId("iconVideoCall")
                    //  .background(Color.Blue)
                    .size(35.dp),
                onClick = {

                }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_video_call),
                    contentDescription = "",
                    tint = Color.White,
                )
            }

            IconButton(
                modifier = Modifier
                    .layoutId("iconCall")
                    // .background(Color.Gray)
                    .size(35.dp),
                onClick = {

                }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_call),
                    contentDescription = "",
                    tint = Color.White,
                )
            }
        }


        val constraintContentChat = ConstraintSet {
            val contentChat = createRefFor("contentChat")

            constrain(contentChat) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.start)
                bottom.linkTo(parent.top)
                width = Dimension.wrapContent
                height = Dimension.matchParent
            }
        }


        ConstraintLayout(
            constraintSet = constraintContentChat,
            modifier = Modifier
            //    .fillMaxWidth()
                .fillMaxSize()
               // .height(300.dp)
                .background(colorResource(id = R.color.main_yellow_new_chat_screen))
                //.background(Color.Green)
                .padding(top = 16.dp, bottom = 16.dp)
                .layoutId("contentChat")
        ) {

        }

        val constraintBottomControl = ConstraintSet {
            val contentChat = createRefFor("bottomControl")

            constrain(contentChat) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.start)
                bottom.linkTo(parent.top)
                width = Dimension.wrapContent
                height = Dimension.matchParent
            }
        }

        ConstraintLayout(
            constraintSet = constraintBottomControl,
            modifier = Modifier
                //    .fillMaxWidth()
                .fillMaxWidth()
                .height(400.dp)
                .background(colorResource(id = R.color.main_violet_light))
                //.background(Color.Green)
                .padding(top = 16.dp, bottom = 16.dp)
                .layoutId("bottomControl")
        ) {

        }
    }
}