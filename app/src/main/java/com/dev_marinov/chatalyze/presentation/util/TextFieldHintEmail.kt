package com.dev_marinov.chatalyze.presentation.util

import android.text.TextUtils
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.dev_marinov.chatalyze.R
import com.dev_marinov.chatalyze.util.ShowToastHelper

@Composable
fun TextFieldHintEmail(
    value: String = "",
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    hintText: String = "",
    textStyle: TextStyle = MaterialTheme.typography.body1,
    maxLines: Int = 1,
    icon: ImageVector
) {

    var isFocusedLogin by remember { mutableStateOf(false) }
    var valueLogin by remember { mutableStateOf("") }
    val context = LocalContext.current
    val message = stringResource(id = R.string.email_length)

    val constraints = ConstraintSet {
        val viewIcon = createRefFor("viewIcon")
        val viewBasicTextField = createRefFor("viewBasicTextField")

        constrain(viewIcon) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(viewBasicTextField.start)
            bottom.linkTo(parent.bottom)
            width = Dimension.value(20.dp)
            height = Dimension.value(20.dp)
        }

        constrain(viewBasicTextField) {
            top.linkTo(parent.top)
            start.linkTo(viewIcon.end)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
            width = Dimension.matchParent // Заполнить доступное пространство
            height = Dimension.matchParent
        }
    }

    ConstraintLayout(
        constraintSet = constraints,
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp)
    ) {

        Icon(
            imageVector = icon,
            contentDescription = "",
            modifier = Modifier
                .layoutId("viewIcon")
        )
        BasicTextField(
            value = value,
            onValueChange = { newValue ->

                if (newValue.length <= 25) {
                    // Сохраняем новое значение в state
                    onValueChanged(newValue)
                    valueLogin = newValue

                    // Проверяем количество символов и показываем Toast
                    if (newValue.length == 25) {
                        ShowToastHelper.createToast(
                            message = message,
                            context = context)
                    }
                } else {
                    // Если превышен лимит символов, не обновляем значение
                    onValueChanged(value)
                    valueLogin = value
                }
            },
            textStyle = textStyle,
            maxLines = maxLines,
            decorationBox = { innerTextField ->  // для расширения в высоту строк подсказки
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 8.dp)
                            .layoutId("viewBasicTextField"),
                        verticalAlignment = Alignment.CenterVertically,

                        ) {
                        if (TextUtils.isEmpty(value)) {
                            Text(
                                modifier = Modifier,
                                text = if (isFocusedLogin) "" else hintText,
                                style = textStyle,
                                color = LocalContentColor.current.copy(alpha = ContentAlpha.medium),
                            )
                        }
                        innerTextField()
                    }
                }
            },
            modifier = Modifier.onFocusChanged { focusState ->
                isFocusedLogin = focusState.isFocused
            }
        )
    }
}
