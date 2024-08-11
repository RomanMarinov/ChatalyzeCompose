package com.dev_marinov.chatalyze.presentation.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.dev_marinov.chatalyze.R
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chat_screen.ChatScreenViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TextFieldHintWriteMessage(
    value: String = "",
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.body1,
    maxLines: Int = 20,
    viewModel: ChatScreenViewModel,
    onSendClick: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    var isNotNullMessageText by remember { mutableStateOf(false) }

    val constraints = ConstraintSet {
        val viewIcon = createRefFor("viewIcon")
        val viewBasicTextField = createRefFor("viewBasicTextField")

        constrain(viewBasicTextField) {
            top.linkTo(parent.top)
            start.linkTo(viewIcon.end)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
            width = Dimension.matchParent // Заполнить доступное пространство
            height = Dimension.matchParent
        }

        constrain(viewIcon) {
            top.linkTo(parent.top)
            //  start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
            width = Dimension.value(30.dp)
            height = Dimension.value(30.dp)
        }

    }

    ConstraintLayout(
        constraintSet = constraints,
        modifier = modifier
            .padding(start = 8.dp, end = 8.dp)
    ) {
        BasicTextField(
            value = value,
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.Sentences),
            onValueChange = { newValue ->
                onValueChanged(newValue)
                messageText = newValue

                isNotNullMessageText = newValue.isNotEmpty()
            },
            textStyle = textStyle,
            maxLines = maxLines,
            singleLine = false,
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->  // для расширения в высоту строк подсказки
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 8.dp, end = 40.dp)
                            .layoutId("viewBasicTextField"),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        innerTextField()
                    }
                }
            }
        )
        val scope = rememberCoroutineScope()
        Icon(
            painter = painterResource(
                if (isNotNullMessageText) R.drawable.ic_send_message
                else R.drawable.ic_send_message_invisible
            ),
            tint = colorResource(id = R.color.main_violet),
            contentDescription = "",
            modifier = Modifier
                .width(40.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(20))
                .clickable {
                    scope.launch {
                        onSendClick()
                        viewModel.sendMessage()
                    }
                }
                .layoutId("viewIcon")
        )
    }
}
