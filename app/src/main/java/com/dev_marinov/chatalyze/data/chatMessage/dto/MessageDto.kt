package com.dev_marinov.chatalyze.data.chatMessage.dto

import com.dev_marinov.chatalyze.domain.model.chat.Message
import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val sender: String,
    val recipient: String,
    val textMessage: String,
    val createdAt: String
) {
    fun mapToDomain(): Message {
        return Message(
            sender = sender,
            recipient = recipient,
            textMessage = textMessage,
            createdAt = createdAt
        )
    }
}