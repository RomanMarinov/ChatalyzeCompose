package com.dev_marinov.chatalyze.domain.repository

import com.dev_marinov.chatalyze.domain.model.chat.ChatMessage

interface ChatRepository {
    suspend fun sendMessage(chatMessage: ChatMessage) : String?
}