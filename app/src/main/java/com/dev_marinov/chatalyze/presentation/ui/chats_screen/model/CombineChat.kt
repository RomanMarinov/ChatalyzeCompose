package com.dev_marinov.chatalyze.presentation.ui.chats_screen.model

data class CombineChat(
    val sender: String,
    val recipient: String,
    val textMessage: String,
    val createdAt: String,
    val onlineOrDate: String?,
    val name: String?
)
