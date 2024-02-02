package com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.calls_screen.model

data class HistoryCall(
    val clientCallPhone: String,
    val senderPhone: String,
    val recipientPhone: String,
    val conversationTime: String,
    val createdAt: String
)

