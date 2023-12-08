//package com.dev_marinov.chatalyze.data.chatMessage
//
//import android.util.Log
//import com.dev_marinov.chatalyze.data.chatMessage.dto.MessageDto
//import com.dev_marinov.chatalyze.data.chatMessage.dto.OnlineOrDate
//import com.dev_marinov.chatalyze.data.chatMessage.dto.OnlineOrDateDTO
//import com.dev_marinov.chatalyze.data.chats.ChatsApiService
//import com.dev_marinov.chatalyze.data.chats.dto.SenderDTO
//import com.dev_marinov.chatalyze.domain.model.chat.Message
//import com.dev_marinov.chatalyze.domain.model.chat.MessageToSend
//import com.dev_marinov.chatalyze.domain.model.chat.UserPairChat
//import com.dev_marinov.chatalyze.domain.model.chats.Chat
//import com.dev_marinov.chatalyze.presentation.util.Resource
//import com.google.gson.Gson
//import io.ktor.client.*
//import io.ktor.client.features.websocket.*
//import io.ktor.client.request.*
//import io.ktor.http.ContentType
//import io.ktor.http.cio.websocket.*
//import io.ktor.http.contentType
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.isActive
//import kotlinx.serialization.decodeFromString
//import kotlinx.serialization.json.Json
//
//class ChatSocketRepositoryImpl(
//    private val client: HttpClient
//) : ChatSocketRepository {
//
//    private val pingHandler = PingHandler()
//    private var socket: WebSocketSession? = null
//
//    //
////    override suspend fun initSession(fromUserPhone: String): Resource<Unit> {
////        return try {
////            socket = client.webSocketSession {
////                url("${ChatSocketService.Endpoints.ChatSocket.url}?username=$fromUserPhone")
////            }
////            if(socket?.isActive == true) {
////                Resource.Success(Unit)
////            } else Resource.Error("Couldn't establish a connection.")
////        } catch(e: Exception) {
////            e.printStackTrace()
////            Resource.Error(e.localizedMessage ?: "Unknown error")
////        }
////    }
////
//    override suspend fun initSession(sender: String): Resource<Unit> {
//        return try {
//            socket = client.webSocketSession {
//                url("${ChatSocketRepository.Endpoints.SocketChat.url}?sender=$sender")
//            }
//            if (socket?.isActive == true) {
//                Resource.Success(Unit)
//            } else Resource.Error("Couldn't establish a connection.")
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Resource.Error(e.localizedMessage ?: "Unknown error")
//        }
//    }
//
//    override suspend fun sendMessage(messageToSend: MessageToSend) {
//        try {
//            Log.d("4444", " ChatSocketServiceImpl sendMessage messageToSend=" + messageToSend)
//            val gson = Gson()
//            val messageToSendJson = gson.toJson(messageToSend)
//
//            socket?.send(Frame.Text(messageToSendJson))
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//
//    override fun observePing(): Flow<List<OnlineOrDate>> {
//        return try {
//            Log.d("4444", " observeMessages")
//            socket?.incoming
//                ?.receiveAsFlow()
//                ?.filter { it is Frame.Text }
//                ?.map {
//                    val json = (it as? Frame.Text)?.readText() ?: ""
//                    val listOnlineOrDateDTO = Json.decodeFromString<List<OnlineOrDateDTO>>(json)
//                    listOnlineOrDateDTO.map { onlineOrDateDTO ->
//                        onlineOrDateDTO.mapToDomain() }
//                } ?: flow {  }
//        } catch(e: Exception) {
//            e.printStackTrace()
//            Log.d("4444", " try catch observeMessages e=" + e)
//            flow {  }
//        }
//    }
//
////    override fun observePing(): Flow<String> {
////        return try {
////            Log.d("4444", " observeMessages")
////            socket?.incoming
////                ?.receiveAsFlow()
////                ?.filter { it is Frame.Ping }
////                ?.map {
////                    val response = String((it as Frame.Ping).data) // конвертируем ByteArray в String
////                    Log.d("4444", "Received Ping: $response")
////                    response // Возвращаем полученное сообщение PING
////                } ?: flow {  }
////        } catch (e: Exception) {
////            e.printStackTrace()
////            Log.d("4444", " try catch observeMessages e=" + e)
////            flow { }
////        }
////    }
//
//
////    override fun observeMessages(): Flow<Any> {
////        return try {
////            Log.d("4444", " observeMessages")
////            socket?.incoming
////                ?.receiveAsFlow()
////                ?.filter { it is Frame.Text }
////                ?.map {
////                    when (it) {
////                        is Frame.Text -> {
////                            // Обработка текстовых фреймов
////                            val json = it.readText()
////                            val messageDto = Json.decodeFromString<MessageDto>(json)
////                            messageDto.mapToDomain()
////                        }
////                        is Frame.Ping -> {
////                            // Обработка пинг-фреймов
////                            Log.d("4444", "Received Ping: ${it.buffer}")
////                            //  pingHandler.handlePing()
////                            // null // Возможно, вам не нужно возвращать значение в данном случае
////                        }
////                        else -> null
////                    }
////                }
////        } catch (e: Exception) {
////            e.printStackTrace()
////            Log.d("4444", " try catch observeMessages e=" + e)
////            flow { }
////        }
////    }
//
//    override fun observeMessages(): Flow<Message> {
//        return try {
//            Log.d("4444", " observeMessages")
//            socket?.incoming
//                ?.receiveAsFlow()
//                ?.filter { it is Frame.Text }
//                ?.map {
//                    val json = (it as? Frame.Text)?.readText() ?: ""
//                    val messageDto = Json.decodeFromString<MessageDto>(json)
//                    messageDto.mapToDomain()
//                } ?: flow {  }
//        } catch(e: Exception) {
//            e.printStackTrace()
//            Log.d("4444", " try catch observeMessages e=" + e)
//            flow {  }
//        }
//    }
//
//    override suspend fun getStateUsersConnection() {
//        try {
//         //   client.get(ChatSocketService.Endpoints.SocketStateUsersConnection.url)
//        } catch (e: Exception) {
//            Log.d("4444", " try cathc getStateUsersConnection e=" + e)
//        }
//    }
//
////    override suspend fun getStateUsersConnection(): List<OnlineOrDate> {
////        return try {
////            client.get<List<OnlineOrDateDTO>>(ChatSocketService.Endpoints.SocketStateUsersConnection.url)
////                .map { it.mapToDomain() }
////        } catch (e: Exception) {
////            emptyList()
////        }
////    }
//
//    override suspend fun closeSession() {
//        socket?.close()
//    }
//
//    override suspend fun getAllMessages(userPairChat: UserPairChat): List<Message> {
//        return try {
//            Log.d("4444", " MessageServiceImpl getAllMessages поппытка отпарвить userPairChat=" + userPairChat)
//            client.post<List<MessageDto>>(MessageRepository.Endpoints.GetAllMessages.url) {
//                contentType(ContentType.Application.Json)
//                body = userPairChat
//            }.map { it.mapToDomain() }
//        } catch (e: Exception) {
//            emptyList()
//        }
//    }
///////////////////////
//
//}