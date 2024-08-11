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