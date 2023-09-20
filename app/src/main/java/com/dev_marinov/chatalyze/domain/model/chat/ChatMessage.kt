package com.dev_marinov.chatalyze.domain.model.chat

import com.dev_marinov.chatalyze.data.chat.dto.ChatMessageDTO

data class ChatMessage(
    val messageText: String,
    val currentDateTime: String
) {
    fun mapFromDomain() : ChatMessageDTO {
        return ChatMessageDTO(
            messageText = messageText,
            currentDateTime = currentDateTime
        )
    }
}
