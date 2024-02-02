package com.dev_marinov.chatalyze.data.call.dto

import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.call_screen.model.FirebaseCommand

data class FirebaseCommandDTO(
    val topic: String,
    val senderPhone: String,
    val recipientPhone: String,
    val typeFirebaseCommand: String
) {
    fun mapToDomain() : FirebaseCommand {
        return FirebaseCommand(
            topic = topic,
            senderPhone = senderPhone,
            recipientPhone = recipientPhone,
            typeFirebaseCommand = typeFirebaseCommand
        )
    }
}

