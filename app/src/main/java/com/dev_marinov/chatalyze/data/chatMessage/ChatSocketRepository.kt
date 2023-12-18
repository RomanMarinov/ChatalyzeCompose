package com.dev_marinov.chatalyze.data.chatMessage

import com.dev_marinov.chatalyze.data.chatMessage.dto.MessageWrapper
import com.dev_marinov.chatalyze.data.chatMessage.dto.OnlineUserState
import com.dev_marinov.chatalyze.data.chatMessage.dto.OnlineUsers
import com.dev_marinov.chatalyze.data.chatMessage.dto.OnlineUsersDTO
import com.dev_marinov.chatalyze.data.chatMessage.dto.TestClassText
import com.dev_marinov.chatalyze.domain.model.chat.Message
import com.dev_marinov.chatalyze.domain.model.chat.MessageToSend
import com.dev_marinov.chatalyze.domain.model.chat.UserPairChat
import com.dev_marinov.chatalyze.presentation.util.Resource
import kotlinx.coroutines.flow.Flow

interface ChatSocketRepository {

    suspend fun initSession(sender: String): Resource<Unit>
    suspend fun closeSession()

    //  suspend fun getStateUsersConnection(): List<OnlineOrDate>


//    suspend fun sendMessage(message: String)
    suspend fun sendMessage(messageToSend: MessageToSend)
    suspend fun getAllMessages(userPairChat: UserPairChat): List<Message>


    fun observeMessages(): Flow<MessageWrapper>
    fun observeOnlineUserState() : Flow<MessageWrapper>


   //// хуй пока закрыл ебаная ошибка
//    fun observeMessages(): Flow<Message>
//    fun observeOnlineUserState() : Flow<List<OnlineUserState>>
//    fun observeOnlineUserState() : List<ChatSocketRepositoryImpl.SocketEvent>

    // fun observePing() : Flow<String>

    companion object {
//        val baseUrl = "http://10.35.101.146:8080/"
        val baseUrl = "http://10.35.101.146:8080/"

     //   const val BASE_URL_WS = "ws://10.35.101.146:8080"
      //  const val BASE_URL_HTTPS = "http://10.35.101.146:8080/"
        const val BASE_URL_WS = "ws://192.168.0.101:8080"
        const val BASE_URL_HTTPS = "http://192.168.0.101:8080/"
    }

    sealed class Endpoints(val url: String) {
        object SocketChat : Endpoints("$BASE_URL_WS/chatsocket")
        object SocketStateUsersConnection : Endpoints("$BASE_URL_WS/state_user_connection")
        object SocketStateUsersConnection2 : Endpoints("$BASE_URL_WS/ws")
        object GetAllMessages: Endpoints("$BASE_URL_HTTPS/messages")
    }
}