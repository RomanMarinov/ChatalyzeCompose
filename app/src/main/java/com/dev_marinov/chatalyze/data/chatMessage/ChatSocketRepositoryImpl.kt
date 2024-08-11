package com.dev_marinov.chatalyze.data.chatMessage

import android.util.Log
import com.dev_marinov.chatalyze.data.call.dto.FirebaseCommandDTO
import com.dev_marinov.chatalyze.data.chat.ChatApiService
import com.dev_marinov.chatalyze.data.chatMessage.dto.MessageDto
import com.dev_marinov.chatalyze.data.chatMessage.dto.MessageWrapper
import com.dev_marinov.chatalyze.domain.model.auth.MessageResponse
import com.dev_marinov.chatalyze.domain.model.chat.Message
import com.dev_marinov.chatalyze.domain.model.chat.MessageToSend
import com.dev_marinov.chatalyze.domain.model.chat.UserPairChat
import com.dev_marinov.chatalyze.domain.repository.ChatSocketRepository
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.call_screen.model.FirebaseCommand
import com.dev_marinov.chatalyze.presentation.util.Resource
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ChatSocketRepositoryImpl @Inject constructor(
    private val client: HttpClient,
    private val chatApiService: ChatApiService
) : ChatSocketRepository {
    private var socket: WebSocketSession? = null

    override suspend fun initSession(sender: String): Resource<Unit> {
        return try {
            socket = client.webSocketSession {
                url("${ChatSocketRepository.Endpoints.SocketChat.url}?sender=$sender")
            }

            if (socket?.isActive == true) {
                Resource.Success(Unit)
            } else Resource.Error("Couldn't establish a connection.")
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.localizedMessage ?: "Unknown error")
        }
    }

    override suspend fun sendMessage(messageToSend: MessageToSend) {
        try {
            val gson = Gson()
            val messageToSendJson = gson.toJson(messageToSend)

            socket?.send(Frame.Text(messageToSendJson))
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("4444", " try catch ChatSocketServiceImpl sendMessage e=" + e)
        }
    }

    override suspend fun sendCommandToFirebase(firebaseCommand: FirebaseCommand): MessageResponse? {
        return try {
            val firebaseCommandDTO = FirebaseCommandDTO(
                topic = firebaseCommand.topic,
                senderPhone = firebaseCommand.senderPhone,
                recipientPhone = firebaseCommand.recipientPhone,
                textMessage = firebaseCommand.textMessage,
                typeFirebaseCommand = firebaseCommand.typeFirebaseCommand
            )
            val response = chatApiService.sendCommandToFirebase(firebaseCommandDTO = firebaseCommandDTO)
            return response.body()?.mapToDomain()
        } catch (e: Exception) {
            Log.d("4444", " try catch chatMessage e=" + e)
            null
        }
    }

    private val myJson = Json { ignoreUnknownKeys = true }
    override fun observeMessages(): Flow<MessageWrapper> {
        return try {
            socket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    val json = (it as? Frame.Text)?.readText() ?: ""
                    myJson.decodeFromString<MessageWrapper>(json)
                } ?: flow { }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("4444", " try catch observeMessages e=" + e)
            flow { }
        }
    }

    override suspend fun closeSession() {
        socket?.close()
    }

    override suspend fun getAllMessages(userPairChat: UserPairChat): List<Message> {
        return try {
            val response: HttpResponse = client.post(ChatSocketRepository.Endpoints.GetAllMessages.url) {
                contentType(ContentType.Application.Json)
                setBody(userPairChat)
            }
            val res = response.body<List<MessageDto>>().map {
                it.mapToDomain()
            }
            return res
        } catch (e: Exception) {
            Log.d("4444", " try catch ChatSocketRepositoryImpl getAllMessages e=" + e)
            emptyList()
        }
    }
}