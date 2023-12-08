package com.dev_marinov.chatalyze.presentation.util

object CorrectNumberFormatHelper {
    fun getCorrectNumber(number: String): String {
        return if (number.length >= 10) {
            number.takeLast(10).replace("\"", "")
        } else {
            ""
        }
    }
}