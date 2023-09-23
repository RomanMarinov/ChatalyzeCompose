package com.dev_marinov.chatalyze.data.chat.dto

import com.dev_marinov.chatalyze.domain.model.chat.ChatMessage

data class ChatMessageDTO(
    val messageText: String,
    val currentDateTime: String
) {
    fun mapToDomain() : ChatMessage {
        return ChatMessage(
            messageText = messageText,
            currentDateTime = currentDateTime
        )
    }
}
