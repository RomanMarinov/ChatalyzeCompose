package com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.call_screen.model

data class FirebaseCommand(
    val topic: String,
    val senderPhone: String,
    val recipientPhone: String,
    val textMessage: String,
    val typeFirebaseCommand: String
)