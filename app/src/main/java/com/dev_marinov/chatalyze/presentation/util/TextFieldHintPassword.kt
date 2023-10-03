package com.dev_marinov.chatalyze.presentation.util

import android.text.TextUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.dev_marinov.chatalyze.R
import com.dev_marinov.chatalyze.util.ShowToastHelper


@Composable
fun TextFieldHintPassword(
    value: String = "",
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    hintText: String = "",
    textStyle: TextStyle = MaterialTheme.typography.body1,
    maxLines: Int = 1,
    icon: ImageVector
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var isFocusedPassword by remember { mutableStateOf(false) }
    var valuePassword by remember { mutableStateOf("") }
    val context = LocalContext.current
    val message = stringResource(id = R.string.password_length)


    val constraints = ConstraintSet {
        val viewIconStart = createRefFor("viewIconStart")
        val viewBasicTextField = createRefFor("viewBasicTextField")
        val viewIconEnd = createRefFor("viewIconEnd")

        constrain(viewIconStart) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(viewBasicTextField.start)
            bottom.linkTo(parent.bottom)
            width = Dimension.value(16.dp)
            height = Dimension.value(16.dp)

        }
        constrain(viewBasicTextField) {
            top.linkTo(parent.top)
            start.linkTo(viewIconStart.end)
            end.linkTo(viewIconEnd.start)
            bottom.linkTo(parent.bottom)
            width = Dimension.matchParent // Заполнить доступное пространство
            height = Dimension.matchParent
        }
        constrain(viewIconEnd) {
            top.linkTo(parent.top)
            start.linkTo(viewBasicTextField.end)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
            width = Dimension.value(16.dp)
            height = Dimension.value(16.dp)
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
                .layoutId("viewIconStart")
        )
        BasicTextField(
            value = value,
            onValueChange = { newValue ->

                if (newValue.length <= 30) {
                    // Сохраняем новое значение в state
                    onValueChanged(newValue)
                    valuePassword = newValue

                    // Проверяем количество символов и показываем Toast
                    if (newValue.length == 30) {

                        ShowToastHelper.createToast(
                            message = message,
                            context = context)
                    }
                } else {
                    // Если превышен лимит символов, не обновляем значение
                    onValueChanged(value)
                    valuePassword = value
                }
            },
            textStyle = textStyle,
            maxLines = maxLines,
            visualTransformation = if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            decorationBox = { innerTextField ->

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 8.dp)
                            .layoutId("viewBasicTextField"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (TextUtils.isEmpty(value)) {
                            Text(
                                modifier = Modifier,
                                text = if (isFocusedPassword) "" else hintText,
                                style = textStyle,
                                fontSize = 14.sp,
                                color = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
                            )
                        }
                        innerTextField()
                    }
                }
            },
            modifier = Modifier.onFocusChanged { focusState ->
                isFocusedPassword = focusState.isFocused
            }
        )

        Icon(
            modifier = Modifier
                .clickable { passwordVisible = !passwordVisible }
                .layoutId("viewIconEnd"),
            imageVector = if (passwordVisible) Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff,
            contentDescription = "Toggle password visibility",
        )
    }
}