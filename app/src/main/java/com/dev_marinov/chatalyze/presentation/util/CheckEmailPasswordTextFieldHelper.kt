package com.dev_marinov.chatalyze.presentation.util

import android.content.Context
import android.util.Patterns

object CheckEmailPasswordTextFieldHelper {
    fun check(
        textEmailState: String,
        textPasswordState: String,
        messagePassword: String,
        messageEmailPassword: String,
        messageEmail: String,
        context: Context
    ) : Boolean {

        val isEmpty = textEmailState.isEmpty()
        val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(textEmailState).matches()
        val containsCom = textEmailState.contains(".com")
        val containsGmailCom = textEmailState.contains("@gmail.com")
        val containsRu = textEmailState.contains(".ru")
        val containsGmailRu = textEmailState.contains("gmail.ru")

        if ((textPasswordState.length in 1..4)
            && (textEmailState.length in 1..4)
        ) {
            ShowToastHelper.createToast(
                message = messageEmailPassword,
                context = context
            )
            return false
        } else if (textPasswordState.isEmpty()
            && textEmailState.isEmpty()
        ) {
            ShowToastHelper.createToast(
                message = messageEmailPassword,
                context = context
            )
            return false
        } else if (isEmpty || ((isEmailValid && !containsCom) && (isEmailValid && !containsGmailCom) && (isEmailValid && !containsRu))
            || ((!isEmailValid && !containsCom) && (!isEmailValid && !containsGmailCom) && (!isEmailValid && !containsRu))
            || (containsGmailCom && containsRu) || (containsGmailRu)
        ) {
            ShowToastHelper.createToast(
                message = messageEmail,
                context = context
            )
            return false
        } else if (!textEmailState.contains("@") && !textEmailState.contains(".")) {
            ShowToastHelper.createToast(
                message = messageEmail,
                context = context
            )
            return false
        } else if (textEmailState.contains("@mail.com")) {
            ShowToastHelper.createToast(
                message = messageEmail,
                context = context
            )
            return false
        } else if (textPasswordState.isEmpty() || (textPasswordState.length in 1..4)) {
            ShowToastHelper.createToast(
                message = messagePassword,
                context = context
            )
            return false
        } else {
            return true
        }
    }
}