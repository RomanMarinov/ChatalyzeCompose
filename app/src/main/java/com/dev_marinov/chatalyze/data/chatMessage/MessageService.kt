package com.dev_marinov.chatalyze.data.chatMessage

import com.dev_marinov.chatalyze.domain.model.chat.Message
import com.dev_marinov.chatalyze.domain.model.chat.UserPairChat

interface MessageService {

    suspend fun getAllMessages(userPairChat: UserPairChat): List<Message>

    companion object {
        const val BASE_URL = "http://192.168.0.101:8080/"
    }

    sealed class Endpoints(val url: String) {
        object GetAllMessages: Endpoints("$BASE_URL/messages")
    }
}