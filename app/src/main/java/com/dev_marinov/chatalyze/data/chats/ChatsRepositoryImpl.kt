package com.dev_marinov.chatalyze.data.chats

import android.util.Log
import com.dev_marinov.chatalyze.data.chats.dto.ChatDTO
import com.dev_marinov.chatalyze.data.chats.dto.SenderDTO
import com.dev_marinov.chatalyze.domain.model.chats.Chat
import com.dev_marinov.chatalyze.domain.model.chats.Sender
import com.dev_marinov.chatalyze.domain.repository.ChatsRepository
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ChatsRepositoryImpl @Inject constructor(
    private val chatsApiService: ChatsApiService
) : ChatsRepository {
    override suspend fun getChats(sender: String): List<Chat> {
        //val senderDTO = Sender(sender = sender).mapToData()
        val response = chatsApiService.getChats(senderDTO = SenderDTO(sender))
       // Log.d("4444", " response=" + response.body())
        return response.body()?.map {
            it.mapToDomain()
        } ?: listOf()
    }
}