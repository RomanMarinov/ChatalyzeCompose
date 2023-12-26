package com.dev_marinov.chatalyze.data.chatMessage

import android.util.Log
import com.dev_marinov.chatalyze.data.call.dto.UserCallDTO
import com.dev_marinov.chatalyze.data.chatMessage.dto.MessageDto
import com.dev_marinov.chatalyze.data.chatMessage.dto.MessageWrapper
import com.dev_marinov.chatalyze.domain.model.auth.MessageResponse
import com.dev_marinov.chatalyze.domain.model.chat.Message
import com.dev_marinov.chatalyze.domain.model.chat.MessageToSend
import com.dev_marinov.chatalyze.domain.model.chat.UserPairChat
import com.dev_marinov.chatalyze.domain.repository.ChatSocketRepository
import com.dev_marinov.chatalyze.presentation.ui.call_screen.model.UserCall
import com.dev_marinov.chatalyze.presentation.util.Resource
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.cio.websocket.*
import io.ktor.http.contentType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ChatSocketRepositoryImpl @Inject constructor(

    private val client: HttpClient,
) : ChatSocketRepository {

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
            Log.d("4444", " ChatSocketServiceImpl sendMessage messageToSend=" + messageToSend)
            val gson = Gson()
            val messageToSendJson = gson.toJson(messageToSend)

            socket?.send(Frame.Text(messageToSendJson))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val myJson = Json { ignoreUnknownKeys = true }
    override fun observeOnlineUserState(): Flow<MessageWrapper> {
        return try {
            Log.d("4444", " observeMessages")
            socket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    val json = (it as? Frame.Text)?.readText() ?: ""
                    myJson.decodeFromString<MessageWrapper>(json)

                } ?: flow { }
        }  catch (e: Exception) {
            e.printStackTrace()
            Log.d("4444", " try catch observeMessages e=$e")
            flow {  }
        }
    }

    private fun cancelobserveOnlineUserState() {

    }

    // хуй пока закрыл ебаная ошибка
    override fun observeMessages(): Flow<MessageWrapper> {
        return try {
            Log.d("4444", " observeMessages")
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
            Log.d(
                "4444",
                " MessageServiceImpl getAllMessages поппытка отпарвить userPairChat=" + userPairChat
            )
            client.post<List<MessageDto>>(ChatSocketRepository.Endpoints.GetAllMessages.url) {
                contentType(ContentType.Application.Json)
                body = userPairChat
            }.map { it.mapToDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

/////////////////////

}


//    override fun observeOnlineUserState(): Flow<List<OnlineUserState>> {
//        return try {
//            Log.d("4444", " observeMessages")
//            socket?.incoming
//                ?.receiveAsFlow()
//                ?.filter { it is Frame.Text }
//                ?.map {
//                    val json = (it as? Frame.Text)?.readText() ?: ""
//                    val listOnlineUserStateDTO = Json.decodeFromString<List<OnlineUserStateDTO>>(json)
//                    listOnlineUserStateDTO.map { onlineUserStateDTO ->
//                        onlineUserStateDTO.mapToDomain()
//                    }
//                } ?: flow { emit(emptyList<OnlineUserState>()) }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.d("4444", " try catch observeMessages e=$e")
//            flow { emit(emptyList<OnlineUserState>()) }
//        }
//    }
//
//    // хуй пока закрыл ебаная ошибка
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
//                } ?: flow { }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.d("4444", " try catch observeMessages e=" + e)
//            flow { }
//        }
//    }

//


    // Классы обертки сообщений:
//    @Serializable
//    data class MessageWrapper(
//        val type: String,
//        @SerializedName("payload")
//        val payloadJson: String
//    )


//    override fun observeOnlineUserState(): List<SocketEvent> {
//        val events = mutableListOf<SocketEvent>()
//
//        try {
//            socket?.incoming
//                ?.receiveAsFlow()
//                ?.filter { it is Frame.Text }
//                ?.map {
//                    val json = (it as? Frame.Text)?.readText() ?: ""
//                    val res = Json.decodeFromString<MessageWrapper>(json)
//                    Log.d("4444", " это вообще срабатывает?'")
//                    when (res.type) {
//                        "singleMessage" -> {
//                            val message = Json.decodeFromString<Message>(res.payloadJson)
//                            events.add(SingleMessageEvent(message))
//
//                            Log.d("4444", " singleMessage")
//
//                            // Обработка обычного сообщения...
//                        }
//
//                        "userList" -> {
//                            val users =
//                                Json.decodeFromString<List<OnlineUserState>>(res.payloadJson)
//                            events.add(UserListEvent(users))
//                            // Обработка списка пользователей...
//                            Log.d("4444", " userList")
//                        }
//                        // Возможны другие типы сообщений...
//                        else -> {}
//                    }
//                }
//        } catch (e: Exception) {
//            // Здесь можно обработать исключение, если требуется
//        }
//
//        return events
//    }





//    override fun observeOnlineUserState(): Flow<OnlineUsers> {
//        return socket?.incoming
//            ?.receiveAsFlow()
//            ?.filter { it is Frame.Text }
//            ?.flatMapConcat { frame ->
//                flow {
//                    try {
//                        val json = (frame as? Frame.Text)?.readText() ?: ""
//                        Log.d("4444", "observeOnlineUserState json=$json")
//                        val listOnlineUserState = runBlocking {
//                            try {
//                                Json.decodeFromString<List<OnlineUserStateDTO>>(json)
//                                    .map { it.mapToDomain() }
//                            } catch (e: Exception) {
//                                Log.d("4444", "JSON parsing error: ", e)
//                                emptyList<OnlineUserState>()
//                            }
//                        }
//                        emit(listOnlineUserState)
//                    } catch (e : Exception) {
//                        Log.d("4444", "JSON parsing 2 error: ", e)
//                    }
//
//                }
//            } ?: flowOf(emptyList())
//    }
////////////////////////////

//    val json = Json { ignoreUnknownKeys = true } // Включите эту строку на клиенте, если она ещё не добавлена
//    override fun observeOnlineUserState(): Flow<String> {
//        return try {
//            socket?.incoming
//                ?.receiveAsFlow()
//                ?.filter { it is Frame.Text }
//                ?.map {
//                    val jsonText = (it as Frame.Text).readText()
//                    jsonText
//                    //json.decodeFromString<TestClassText>(jsonText)
//                } ?: flow { }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.d("4444", "try catch observeMessages e=$e")
//            flow { }
//        }
//    }



    /////////////////////
//    override fun observeOnlineUserState(): Flow<OnlineUsers> {
//        return try {
//            socket?.incoming
//                ?.receiveAsFlow()
//                ?.filter { it is Frame.Text }
//                ?.map {
//                    val json = (it as? Frame.Text)?.readText() ?: ""
//                    val listOnlineUserStateDTO = Json.decodeFromString<OnlineUsersDTO>(json)
//                    listOnlineUserStateDTO.mapToDomain()
//                } ?: flow { }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.d("4444", " try catch observeMessages e=" + e)
//            flow { }
//        }
//    }


//    override fun observeOnlineUserState(): Flow<List> {
//        return try {
//            socket?.incoming
//                ?.receiveAsFlow()
//                ?.filter { it is Frame.Text }
//                ?.map {
//                    val json = (it as? Frame.Text)?.readText() ?: ""
//                    Log.d("4444", " observeOnlineUserState json=" + json)
//
//
//
//
//                } ?: flow {  }
//        } catch(e: Exception) {
//            Log.d("4444", " try catch observeMessages e=" + e)
//            flow {  }
//        }
//    }


//    override fun observePing(): Flow<String> {
//        return try {
//            Log.d("4444", " observeMessages")
//            socket?.incoming
//                ?.receiveAsFlow()
//                ?.filter { it is Frame.Ping }
//                ?.map {
//                    val response = String((it as Frame.Ping).data) // конвертируем ByteArray в String
//                    Log.d("4444", "Received Ping: $response")
//                    response // Возвращаем полученное сообщение PING
//                } ?: flow {  }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.d("4444", " try catch observeMessages e=" + e)
//            flow { }
//        }
//    }


//    override fun observeMessages(): Flow<Any> {
//        return try {
//            Log.d("4444", " observeMessages")
//            socket?.incoming
//                ?.receiveAsFlow()
//                ?.filter { it is Frame.Text }
//                ?.map {
//                    when (it) {
//                        is Frame.Text -> {
//                            // Обработка текстовых фреймов
//                            val json = it.readText()
//                            val messageDto = Json.decodeFromString<MessageDto>(json)
//                            messageDto.mapToDomain()
//                        }
//                        is Frame.Ping -> {
//                            // Обработка пинг-фреймов
//                            Log.d("4444", "Received Ping: ${it.buffer}")
//                            //  pingHandler.handlePing()
//                            // null // Возможно, вам не нужно возвращать значение в данном случае
//                        }
//                        else -> null
//                    }
//                }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.d("4444", " try catch observeMessages e=" + e)
//            flow { }
//        }
//    }


//
//    override fun observeMessages(): Flow<Message> = flow {
//        try {
//            socket?.incoming
//                ?.receiveAsFlow()
//                ?.filter { it is Frame.Text }
//                ?.collect { frame -> // здесь используется collect для обработки каждого элемента
//                    val json = (frame as Frame.Text).readText()
//                    val res = Json.decodeFromString<MessageWrapper>(json)
//
//                    if (res.type == "singleMessage") {
//                        val message = Json.decodeFromString<Message>(res.payloadJson) // предположим, что json userList находится в поле data
//                        emit(message)
//                    } else {
//                        // Если тип сообщения другой, может быть можно вам нужно обработать его по-другому.
//                    }
//                }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.d("4444", " try catch observeMessages e=$e")
//           // emit(Message()) // В случае ошибки выпускаем пустой список
//        }
//    }


//        val json1 = Json { ignoreUnknownKeys = true } // Включите эту строку на клиенте, если она ещё не добавлена
//    override fun observeOnlineUserState(): Flow<List<OnlineUserState>> = flow {
//        try {
//            socket?.incoming
//                ?.receiveAsFlow()
//                ?.filter { it is Frame.Text }
//                ?.collect { frame -> // здесь используется collect для обработки каждого элемента
//                    val json = (frame as Frame.Text).readText()
//                    val res = json1.decodeFromString<MessageWrapper>(json)
//
//                    if (res.type == "userList") {
//                        val listOnlineUserStateDTO = Json.decodeFromString<List<OnlineUserStateDTO>>(res.payloadJson) // предположим, что json userList находится в поле data
//                        val list = listOnlineUserStateDTO.map { onlineUserStateDTO ->
//                            onlineUserStateDTO.mapToDomain()
//                        }
//                        emit(list)
//                    } else {
//                        // Если тип сообщения другой, может быть можно вам нужно обработать его по-другому.
//                    }
//                }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.d("4444", " try catch observeOnlineUserState e=$e")
//            emit(emptyList<OnlineUserState>()) // В случае ошибки выпускаем пустой список
//        }
//    }
