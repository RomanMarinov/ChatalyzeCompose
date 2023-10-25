package com.dev_marinov.chatalyze.presentation.util

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun NumberTextField() {
    val number = remember { mutableStateOf("") }

    Row(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.requiredWidthIn(max = 8.dp))
        Text(text = number.value, modifier = Modifier.padding(4.dp))
        Spacer(modifier = Modifier.requiredWidthIn(max = 8.dp))
    }

    TextField(
        value = number.value,
        onValueChange = { newInput ->
            if (newInput.length <= 5 && newInput.all { it.isDigit() }) {
                number.value = newInput
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}


//
//
//@Composable
//fun TextFieldHintCode(
//    value: String = "",
//    onValueChanged: (String) -> Unit,
//    modifier: Modifier = Modifier,
//    hintText: String = "",
//    textStyle: TextStyle = MaterialTheme.typography.body1,
//    maxLines: Int = 1,
//    icon: ImageVector
//) {
//    var isFocusedEmail by remember { mutableStateOf(false) }
//    var valueEmail by remember { mutableStateOf("") }
//    val context = LocalContext.current
//    val message = stringResource(id = R.string.email_length)
//
//    val constraints = ConstraintSet {
//        val viewIcon = createRefFor("viewIcon")
//        val viewBasicTextField = createRefFor("viewBasicTextField")
//
//        constrain(viewIcon) {
//            top.linkTo(parent.top)
//            start.linkTo(parent.start)
//            end.linkTo(viewBasicTextField.start)
//            bottom.linkTo(parent.bottom)
//            width = Dimension.value(20.dp)
//            height = Dimension.value(20.dp)
//        }
//
//        constrain(viewBasicTextField) {
//            top.linkTo(parent.top)
//            start.linkTo(viewIcon.end)
//            end.linkTo(parent.end)
//            bottom.linkTo(parent.bottom)
//            width = Dimension.matchParent
//            height = Dimension.matchParent
//        }
//    }
//
//    ConstraintLayout(
//        constraintSet = constraints,
//        modifier = modifier
//            .padding(start = 16.dp, end = 16.dp)
//    ) {
//
//        Icon(
//            imageVector = icon,
//            contentDescription = "",
//            modifier = Modifier
//                .layoutId("viewIcon")
//        )
//        BasicTextField(
//            value = value,
//            onValueChange = { newValue ->
////////////////////////
//
//                if (newValue.length <= 5 && newValue.all {
//                        it.isDigit()
//                    }) {
//
//                   // value = newInput
//                }
//
//
//                /////////////////////////////
//                if (newValue.length <= 30) {
//                    // Сохраняем новое значение в state
//                    onValueChanged(newValue)
//                    valueEmail = newValue
//
//                    // Проверяем количество символов и показываем Toast
//                    if (newValue.length == 30) {
//                        ShowToastHelper.createToast(
//                            message = message,
//                            context = context
//                        )
//                    }
//                } else {
//                    // Если превышен лимит символов, не обновляем значение
//                    onValueChanged(value)
//                    valueEmail = value
//                }
//            },
//            textStyle = textStyle,
//            maxLines = maxLines,
//            decorationBox = { innerTextField ->  // для расширения в высоту строк подсказки
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize(),
//                    contentAlignment = Alignment.Center,
//                ) {
//                    Row(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(start = 8.dp)
//                            .layoutId("viewBasicTextField"),
//                        verticalAlignment = Alignment.CenterVertically,
//
//                        ) {
//                        if (TextUtils.isEmpty(value)) {
//                            Text(
//                                modifier = Modifier,
//                                text = if (isFocusedEmail) "" else hintText,
//                                style = textStyle,
//                                color = LocalContentColor.current.copy(alpha = ContentAlpha.medium),
//                            )
//                        }
//                        innerTextField()
//                    }
//                }
//            },
//            modifier = Modifier.onFocusChanged { focusState ->
//                isFocusedEmail = focusState.isFocused
//            }
//        )
//    }
//}