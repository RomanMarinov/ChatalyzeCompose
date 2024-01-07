package com.dev_marinov.chatalyze.data.call.dto

import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.call_screen.model.UserCall

data class UserCallDTO(
    val topic: String,
    val sender: String,
    val recipient: String
) {
    fun mapToDomain() : UserCall {
        return UserCall(
            topic = topic,
            sender = sender,
            recipient = recipient
        )
    }
}

