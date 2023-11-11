package com.dev_marinov.chatalyze.data.chat

import com.dev_marinov.chatalyze.domain.model.chat.ChatMessage
import com.dev_marinov.chatalyze.domain.repository.ChatRepository
import com.dev_marinov.chatalyze.presentation.ui.chats_screen.model.Contact
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatApiService: ChatApiService
) : ChatRepository {
    override suspend fun getChatMessage(): List<Contact> {
        return withContext(Dispatchers.IO) {
            val jobChat: Deferred<List<Contact>> = async {
                getMessages()
            }
            jobChat.await()
        }
    }

    private fun getMessages() : List<Contact> {
        val listItems = mutableListOf<Contact>()
        for (i in 0..150) {

            listItems.add(i, Contact("roman $i", "89303493563", photo = ""))

        }
        return listItems
    }
}