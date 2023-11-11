package com.dev_marinov.chatalyze.data.chatMessage

import android.util.Log
import com.dev_marinov.chatalyze.data.chatMessage.dto.MessageDto
import com.dev_marinov.chatalyze.domain.model.chat.Message
import com.dev_marinov.chatalyze.domain.model.chat.MessageToSend
import com.dev_marinov.chatalyze.presentation.util.Resource
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ChatSocketServiceImpl(
    private val client: HttpClient
): ChatSocketService {

    private var socket: WebSocketSession? = null
//
//    override suspend fun initSession(fromUserPhone: String): Resource<Unit> {
//        return try {
//            socket = client.webSocketSession {
//                url("${ChatSocketService.Endpoints.ChatSocket.url}?username=$fromUserPhone")
//            }
//            if(socket?.isActive == true) {
//                Resource.Success(Unit)
//            } else Resource.Error("Couldn't establish a connection.")
//        } catch(e: Exception) {
//            e.printStackTrace()
//            Resource.Error(e.localizedMessage ?: "Unknown error")
//        }
//    }
//
    override suspend fun initSession(sender: String): Resource<Unit> {
        return try {
            socket = client.webSocketSession {
                url("${ChatSocketService.Endpoints.ChatSocket.url}?sender=$sender")
            }
            if(socket?.isActive == true) {
                Resource.Success(Unit)
            } else Resource.Error("Couldn't establish a connection.")
        } catch(e: Exception) {
            e.printStackTrace()
            Resource.Error(e.localizedMessage ?: "Unknown error")
        }
    }

    override suspend fun sendMessage(messageToSend: MessageToSend) {
        try {
            Log.d("4444", " ChatSocketServiceImpl sendMessage messageToSend=" + messageToSend)
            val gson = Gson()
            val messageToSendJson = gson.toJson(messageToSend)

            socket?.send(Frame.Text(messageToSendJson))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun observeMessages(): Flow<Message> {
        return try {
            socket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    val json = (it as? Frame.Text)?.readText() ?: ""
                    val messageDto = Json.decodeFromString<MessageDto>(json)
                    messageDto.toMessage()
                } ?: flow {  }
        } catch(e: Exception) {
            e.printStackTrace()
            flow {  }
        }
    }

    override suspend fun closeSession() {
        socket?.close()
    }
}