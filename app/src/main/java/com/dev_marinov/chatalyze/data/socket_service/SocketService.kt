package com.dev_marinov.chatalyze.data.socket_service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.dev_marinov.chatalyze.data.chatMessage.ChatSocketRepository
import com.dev_marinov.chatalyze.data.chatMessage.dto.MessageDto
import com.dev_marinov.chatalyze.data.chatMessage.dto.OnlineOrDate
import com.dev_marinov.chatalyze.data.chatMessage.dto.OnlineOrDateDTO
import com.dev_marinov.chatalyze.domain.model.chat.Message
import com.dev_marinov.chatalyze.domain.model.chat.MessageToSend
import com.dev_marinov.chatalyze.domain.model.chat.UserPairChat
import com.dev_marinov.chatalyze.presentation.util.Resource
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.webSocketSession
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@AndroidEntryPoint
class SocketService @Inject constructor() : Service(), ChatSocketRepository {

    @Inject
    lateinit var client: HttpClient

    constructor(httpClient: HttpClient) : this() {
        client = httpClient
    }

    private var socket: WebSocketSession? = null

    // Этот метод вызывается при создании службы и используется
    // для выполнения инициализации и подготовки перед работой службы.

    override fun onCreate() {
        super.onCreate()


    }

    // Этот метод вызывается каждый раз, когда служба запускается командой startService()
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("4444", " SocketService onStartCommand")
        when(intent?.action) {
            "open_session" -> {
                // Выполните код для соединения с сервером
                val senderIntent = intent.getStringExtra("sender")
                senderIntent?.let { senderPhone ->
                    CoroutineScope(Dispatchers.IO).launch {
                        // Вызовите ваш метод initSession() здесь
                        when(initSession(sender = senderPhone)) {
                            is Resource.Success -> {
                                // Инициализация успешна
                                // Можете выполнить дополнительные действия
                                Log.d("4444", " Resource.Success")

                                val successIntent = Intent("receiver_socket_action")
                                //successIntent.action = "SESSION_ACTION_SUCCESS"
                                //  successIntent.putExtra("success", true)
                                successIntent.putExtra("session", "session_success")
                                sendBroadcast(successIntent)
                            }
                            is Resource.Error -> {
                                // Произошла ошибка при инициализации сессии
                                // Обрабатывайте ошибку соединения
                                Log.d("4444", " Resource.Error")
                                val successIntent = Intent("receiver_socket_action")
                                successIntent.putExtra("session", "session_error")
                                //  successIntent.putExtra("success", true)
                                sendBroadcast(successIntent)
                            }
                            else -> {}
                        }
                    }
                }
            }
        }

//        if (intent != null) {
//            // Выполните код для соединения с сервером
//            val senderIntent = intent.getStringExtra("sender")
//            senderIntent?.let { senderPhone ->
//                CoroutineScope(Dispatchers.IO).launch {
//                    // Вызовите ваш метод initSession() здесь
//                    when(initSession(sender = senderPhone)) {
//                        is Resource.Success -> {
//                            // Инициализация успешна
//                            // Можете выполнить дополнительные действия
//                            Log.d("4444", " Resource.Success")
//                            val successIntent = Intent("SESSION_ACTION_SUCCESS")
//                          //  successIntent.putExtra("success", true)
//                            sendBroadcast(successIntent)
//                        }
//                        is Resource.Error -> {
//                            // Произошла ошибка при инициализации сессии
//                            // Обрабатывайте ошибку соединения
//                            Log.d("4444", " Resource.Error")
//                            val successIntent = Intent("SESSION_ACTION_ERROR")
//                            //  successIntent.putExtra("success", true)
//                            sendBroadcast(successIntent)
//                        }
//                    }
//                }
//            }
//        } else {
//            CoroutineScope(Dispatchers.IO).launch {
//                closeSession()
//            }
//        }

        // START_REDELIVER_INTENT если система убьет сервис то когда появяться ресурсы то она
        // снова воссоздаст его с последним intent намерением
        return START_STICKY //START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("4444", " SocketService onDestroy")
        // Выполните код для разрыва соединения с сервером при завершении службы
        closeSession()
        stopSelf()
    }

    private suspend fun initSession(sender: String): Resource<Unit> {
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

    private fun closeSession() {
        Log.d("4444", " SocketService closeSession")
        CoroutineScope(Dispatchers.IO).launch {
            socket?.close()
        }
        val successIntent = Intent("receiver_socket_action")
        successIntent.putExtra("session", "session_close")
        sendBroadcast(successIntent)
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
            Log.d("4444", " observeMessages")
            socket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    val json = (it as? Frame.Text)?.readText() ?: ""
                    val messageDto = Json.decodeFromString<MessageDto>(json)
                    messageDto.mapToDomain()
                } ?: flow {  }
        } catch(e: Exception) {
            e.printStackTrace()
            Log.d("4444", " try catch observeMessages e=" + e)
            flow {  }
        }
    }

    override suspend fun getAllMessages(userPairChat: UserPairChat): List<Message> {
        return try {
            Log.d("4444", " MessageServiceImpl getAllMessages поппытка отпарвить userPairChat=" + userPairChat)
            client.post<List<MessageDto>>(ChatSocketRepository.Endpoints.GetAllMessages.url) {
                contentType(ContentType.Application.Json)
                body = userPairChat
            }.map { it.mapToDomain() }
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

    override suspend fun getStateUsersConnection() {
        try {
            client.get(ChatSocketRepository.Endpoints.SocketStateUsersConnection.url)
        } catch (e: Exception) {
            Log.d("4444", " try cathc getStateUsersConnection e=" + e)
        }
    }

    override fun observePing(): Flow<List<OnlineOrDate>> {
        return try {
            Log.d("4444", " observeMessages")
            socket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    val json = (it as? Frame.Text)?.readText() ?: ""
                    val listOnlineOrDateDTO = Json.decodeFromString<List<OnlineOrDateDTO>>(json)
                    listOnlineOrDateDTO.map { onlineOrDateDTO ->
                        onlineOrDateDTO.mapToDomain() }
                } ?: flow {  }
        } catch(e: Exception) {
            e.printStackTrace()
            Log.d("4444", " try catch observeMessages e=" + e)
            flow {  }
        }
    }
}