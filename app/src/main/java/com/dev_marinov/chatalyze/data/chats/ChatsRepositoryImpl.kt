package com.dev_marinov.chatalyze.data.chats

import com.dev_marinov.chatalyze.data.chats.dto.SenderDTO
import com.dev_marinov.chatalyze.domain.model.chats.Chat
import com.dev_marinov.chatalyze.domain.repository.ChatsRepository
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ChatsRepositoryImpl @Inject constructor(
    private val chatsApiService: ChatsApiService
) : ChatsRepository {

    override suspend fun getChats(sender: String, refreshToken: String): List<Chat> {
        val response = chatsApiService.getChats(senderDTO = SenderDTO(sender = sender, refreshToken = refreshToken))
        return response.body()?.map {
            it.mapToDomain()
        } ?: listOf()
    }
}