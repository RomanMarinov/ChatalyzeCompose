package com.dev_marinov.chatalyze.presentation.util

import android.content.Context
import android.util.Log
import android.util.Patterns
import com.dev_marinov.chatalyze.presentation.ui.start_screen_activity.forgot_password_screen.ForgotPasswordScreenViewModel

object CheckEmailTextFieldHelper {
    fun check(
        textEmailState: String,
        messageEmail: String,
        context: Context,
        viewModel: ForgotPasswordScreenViewModel
    ): Boolean {

        val isEmpty = textEmailState.isEmpty()
        val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(textEmailState).matches()
        val containsCom = textEmailState.contains(".com")
        val containsGmailCom = textEmailState.contains("@gmail.com")
        val containsRu = textEmailState.contains(".ru")
        val containsGmailRu = textEmailState.contains("gmail.ru")

        if (textEmailState.length in 1..4) {
            ShowToastHelper.createToast(
                message = messageEmail,
                context = context
            )
            return false
        } else if (textEmailState.isEmpty()) {
            ShowToastHelper.createToast(
                message = messageEmail,
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
        } else {
            return true
        }
    }
}