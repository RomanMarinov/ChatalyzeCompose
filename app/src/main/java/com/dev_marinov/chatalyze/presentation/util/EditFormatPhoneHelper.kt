package com.dev_marinov.chatalyze.presentation.util


object EditFormatPhoneHelper {
    fun edit(phone: String): String {
        return if ('9' == phone.first()) {
            "+7".plus(phone)
        } else {
            "+1".plus(phone)
        }

    }
}