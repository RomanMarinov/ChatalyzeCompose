package com.dev_marinov.chatalyze.data.chat

import com.dev_marinov.chatalyze.domain.model.chat.ChatMessage
import com.dev_marinov.chatalyze.domain.repository.ChatRepository
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatApiService: ChatApiService
) : ChatRepository {
    override suspend fun getChatMessage(): List<String> {
        return withContext(Dispatchers.IO) {
            val jobChat: Deferred<List<String>> = async {
                getMessages()
            }
            jobChat.await()
        }
    }

    private fun getMessages() : List<String> {
        val listItems = mutableListOf<String>()
        for (i in 0..150) {
            listItems.add(i, i.toString())
        }
        return listItems
    }
}