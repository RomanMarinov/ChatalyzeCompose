package com.dev_marinov.chatalyze.presentation.util

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import com.dev_marinov.chatalyze.R

@Composable
fun AnimatedCounter(
    count: Int,
    modifier: Modifier = Modifier, // модификатор по умолчанию
    style: TextStyle = MaterialTheme.typography.h3
) { // далее реализуем эту функцию
    var oldCount by remember {
        // начинается с нашего текущего счета
        mutableStateOf(count)
    }
    SideEffect {
        oldCount = count
    }
    Row(modifier = modifier) {
        val countString = count.toString()
        val oldCountString = oldCount.toString()
        for (i in countString.indices) {
            // сначала хотим получить ссылку на старый символ и после того как номер изменился
            // мы теперь хотим получить ссылку на старого персонажа, чтобы знать был ли там старый персонаж
            // getOrNull если значение равно нулю то мы отобазим здесь текст без анимации
            val oldChar = oldCountString.getOrNull(i)
            val newChar = countString[i]
//
            val char = if (oldChar == newChar) {
                oldCountString[i]
            } else {
                countString[i]
            }
            AnimatedContent(
                targetState = char,
                transitionSpec = {
                    slideInVertically { it } togetherWith slideOutVertically { -it }
                }
            ) { char ->
                Text(
                    text = char.toString(),
                    style = style,
                    softWrap = false,
                    color = Color.White
                )
            }
        }
    }
}