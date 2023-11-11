package com.dev_marinov.chatalyze.presentation.ui.chat_screen

import com.dev_marinov.chatalyze.domain.model.chat.Message


data class ChatState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false
)
