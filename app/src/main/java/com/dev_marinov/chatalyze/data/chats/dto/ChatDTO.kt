package com.dev_marinov.chatalyze.data.chats.dto

import com.dev_marinov.chatalyze.domain.model.chats.Chat
import com.google.gson.annotations.SerializedName

data class ChatDTO(
    @SerializedName("sender")
    val sender: String,
    @SerializedName("recipient")
    val recipient: String,
    @SerializedName("text_message")
    val textMessage: String,
    @SerializedName("created_at")
    val createdAt: String
) {
    fun mapToDomain(): Chat {
        return Chat(
            sender = sender,
            recipient = recipient,
            textMessage = textMessage,
            createdAt = createdAt
        )
    }
}
