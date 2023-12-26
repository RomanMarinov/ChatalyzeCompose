package com.dev_marinov.chatalyze.data.call.dto

import com.dev_marinov.chatalyze.presentation.ui.call_screen.model.UserCall

data class UserCallDTO(
    val event: String,
    val sender: String,
    val recipient: String
) {
    fun mapToDomain() : UserCall {
        return UserCall(
            event = event,
            sender = sender,
            recipient = recipient
        )
    }
}

