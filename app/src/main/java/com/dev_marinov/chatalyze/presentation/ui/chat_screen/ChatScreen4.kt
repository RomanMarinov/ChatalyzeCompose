package com.dev_marinov.chatalyze.presentation.ui.chat_screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.dev_marinov.chatalyze.R
import com.dev_marinov.chatalyze.presentation.util.TextFieldHintWriteMessage
import com.dev_marinov.chatalyze.util.SystemUiControllerHelper
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce


@OptIn(ExperimentalComposeUiApi::class, FlowPreview::class)
@Composable
fun ChatScreen4(
    viewModel: ChatScreenViewModel = hiltViewModel(),
    navHostController: NavHostController
) {

    SystemUiControllerHelper.SetStatusBarColorNoGradient()

    val chatName = "Маринов Роман"
    viewModel.getChatPosition(userName = chatName)
    val chatPosition by viewModel.chatPosition.collectAsStateWithLifecycle()
    var textMessage by remember { mutableStateOf("") }

    val lazyListState = chatPosition?.let {
        rememberLazyListState(
            initialFirstVisibleItemIndex = it
        )
    }

    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    val listItems = mutableListOf<String>()
    for (i in 0..150) {
        listItems.add(i, i.toString())
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            //.imePadding()
            .statusBarsPadding()
            // .background(colorResource(id = R.color.main_violet_light))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    Log.d("4444", " сработало сокрытие false")
                    softwareKeyboardController?.hide()
                    //viewModel.deliveringStateFocus(focused = false)
                }
            )
        //  .systemBarsPadding()
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                //.imePadding()
                .background(colorResource(id = R.color.main_violet_light))
        ) {

            val constraintsTop = ConstraintSet {

                val topControl = createRefFor("topControl")

                constrain(topControl) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.value(40.dp)
                    height = Dimension.wrapContent
                }

            }

            ConstraintLayout(
                constraintSet = constraintsTop,
                modifier = Modifier
                    .fillMaxWidth()
                    //.background(Color.Green)
                    .padding(top = 16.dp, bottom = 16.dp)
                    .layoutId("topControl")
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

                        }
                        .layoutId("back")
                    //  .background(Color.Cyan)
                )

            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                //.imePadding()
                .background(colorResource(id = R.color.main_yellow_new_chat_screen))
                .fillMaxWidth()
        ) {

            val contentChat = ConstraintSet {
                val chatContent = createRefFor("chatContent")

                constrain(chatContent) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.wrapContent
                    height = Dimension.wrapContent
                }
            }

            ConstraintLayout(
                constraintSet = contentChat,
                modifier = Modifier
                    .fillMaxWidth()
                    //.imePadding()
                    //  .height(300.dp)
                    //  .fillMaxSize()
                    .layoutId("chatContent")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                    // .height(300.dp))
                )
                {
                    BoxWithConstraints {
                        lazyListState?.let {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(start = 8.dp, end = 8.dp),
                                state = it
                            ) {
                                items(listItems) { item ->
                                    Text(text = item)
                                }
                                // Добавьте другие элементы списка здесь
                            }
                        }
                    }

                    LaunchedEffect(lazyListState) {
                        snapshotFlow {
                            lazyListState?.firstVisibleItemIndex
                        }
                            .debounce(500L)
                            .collectLatest { firstIndex ->
                                firstIndex?.let {
//                                    Log.d(" lastVisibleIndex =" + lastVisibleIndex)
                                    viewModel.saveScrollChatPosition(
                                        keyUserName = chatName,
                                        position = it
                                    )
                                }
                            }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .background(colorResource(id = R.color.main_violet_light))
                .fillMaxWidth()
        ) {

            val constraintBottomControl = ConstraintSet {
                val bottomControl = createRefFor("bottomControl")

                constrain(bottomControl) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                }
            }

            ConstraintLayout(
                constraintSet = constraintBottomControl,
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .padding(16.dp)
                    .layoutId("bottomControl")
            ) {
                TextFieldHintWriteMessage(
                    value = textMessage,
                    onValueChanged = { textMessage = it },
                    // hintText = stringResource(id = R.string.email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(20))
                        .background(MaterialTheme.colors.surface)
                        .padding(start = 8.dp, end = 16.dp),
                    icon = Icons.Rounded.Clear

                )
            }
        }
    }
}