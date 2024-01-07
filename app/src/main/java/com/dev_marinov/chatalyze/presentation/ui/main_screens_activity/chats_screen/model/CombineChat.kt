package com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chats_screen.model

data class CombineChat(
    val sender: String,
    val recipient: String,
    val textMessage: String,
    val createdAt: String,
    val onlineOrOffline: String?,
    val name: String?,

    val typeEvent: String
)
