package com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chat_screen

import com.dev_marinov.chatalyze.domain.model.chat.Message


data class ChatState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false
)
