package com.dev_marinov.chatalyze.data.chatMessage

import android.util.Log
import com.dev_marinov.chatalyze.data.chatMessage.dto.MessageDto
import com.dev_marinov.chatalyze.domain.model.chat.Message
import com.dev_marinov.chatalyze.domain.model.chat.UserPairChat
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.response.*
import io.ktor.http.*

class MessageServiceImpl(
    private val client: HttpClient
): MessageService {

    override suspend fun getAllMessages(userPairChat: UserPairChat): List<Message> {
        return try {
            Log.d("4444", " MessageServiceImpl getAllMessages поппытка отпарвить")
            client.post<List<MessageDto>>(MessageService.Endpoints.GetAllMessages.url) {
                contentType(ContentType.Application.Json)
                body = userPairChat
            }.map { it.toMessage() }
        } catch (e: Exception) {
            emptyList()
        }
    }


//    override suspend fun getAllMessages(userPairChat: UserPairChat): List<Message> {
//        return try {
//            client.get<List<MessageDto>>(MessageService.Endpoints.GetAllMessages.url) {
//                contentType(ContentType.Application.Json)
//                body = userPairChat
//            }.map { it.toMessage() }
//        } catch (e: Exception) {
//            emptyList()
//        }
//    }

//    override suspend fun getAllMessages(userPairChat: UserPairChat): List<Message> {
//        return try {
//            client.get<List<MessageDto>>(MessageService.Endpoints.GetAllMessages.url)
//                .map { it.toMessage() }
//        } catch (e: Exception) {
//            emptyList()
//        }
//    }
}