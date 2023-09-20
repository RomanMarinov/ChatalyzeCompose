package com.dev_marinov.chatalyze.data.chat

import com.dev_marinov.chatalyze.domain.model.chat.ChatMessage
import com.dev_marinov.chatalyze.domain.repository.ChatRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatApiService: ChatApiService) : ChatRepository {
    override suspend fun sendMessage(chatMessage: ChatMessage) : String? {
        val response = chatApiService.sendMessage(chatMessage.mapFromDomain())
        return response.body()
    }
}