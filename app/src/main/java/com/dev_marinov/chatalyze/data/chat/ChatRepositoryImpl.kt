package com.dev_marinov.chatalyze.data.chat

import android.util.Log
import com.dev_marinov.chatalyze.data.chat.dto.ChatCompanionDTO
import com.dev_marinov.chatalyze.domain.model.auth.MessageResponse
import com.dev_marinov.chatalyze.domain.model.chat.ChatCompanion
import com.dev_marinov.chatalyze.domain.repository.ChatRepository
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chats_screen.model.Contact
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatApiService: ChatApiService,
) : ChatRepository {
    override suspend fun getChatMessage(): List<Contact> {
        return withContext(Dispatchers.IO) {
            val jobChat: Deferred<List<Contact>> = async {
                getMessages()
            }
            jobChat.await()
        }
    }

    override suspend fun saveCompanionOnTheServer(chatCompanion: ChatCompanion): MessageResponse? {

       Log.d("4444", " ChatRepositoryImpl saveCompanionOnTheServer chatCompanion=" + chatCompanion)
        val body = ChatCompanionDTO(
            senderPhone = chatCompanion.senderPhone,
            companionPhone = chatCompanion.companionPhone
        )
        val response = chatApiService.sendChatCompanion(body = body)

        return if (response.isSuccessful) {
            response.body()?.let {
                MessageResponse(
                    httpStatusCode = it.httpStatusCode,
                    message = it.message
                )
            }
        } else {
            response.body()?.let {
                MessageResponse(
                    httpStatusCode = it.httpStatusCode,
                    message = it.message
                )
            }
        }
    }

    private fun getMessages(): List<Contact> {
        val listItems = mutableListOf<Contact>()
        for (i in 0..150) {

            listItems.add(i, Contact("roman $i", "89303493563", photo = ""))

        }
        return listItems
    }
}