package com.dev_marinov.chatalyze.presentation.util

import androidx.compose.runtime.Composable

object EditFormatPhoneHelper {
    fun edit(phone: String): String {
        return if ('9' == phone.first()) {
            "+7".plus(phone)
        } else {
            "+1".plus(phone)
        }

    }
}