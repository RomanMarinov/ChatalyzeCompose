//package com.dev_marinov.chatalyze.data.socket_service
//
//import android.app.Service
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.content.IntentFilter
//import android.os.Binder
//import android.os.IBinder
//import android.util.Log
//import androidx.compose.runtime.rememberCoroutineScope
//import com.dev_marinov.chatalyze.data.chatMessage.ChatSocketRepository
//import com.dev_marinov.chatalyze.data.chatMessage.dto.MessageDto
//import com.dev_marinov.chatalyze.data.chatMessage.dto.OnlineOrDate
//import com.dev_marinov.chatalyze.data.chatMessage.dto.OnlineOrDateDTO
//import com.dev_marinov.chatalyze.domain.model.chat.Message
//import com.dev_marinov.chatalyze.domain.model.chat.MessageToSend
//import com.dev_marinov.chatalyze.domain.model.chat.UserPairChat
//import com.dev_marinov.chatalyze.presentation.util.Resource
//import com.google.gson.Gson
//import dagger.hilt.android.AndroidEntryPoint
//import io.ktor.client.HttpClient
//import io.ktor.client.features.websocket.webSocketSession
//import io.ktor.client.request.get
//import io.ktor.client.request.post
//import io.ktor.client.request.url
//import io.ktor.http.ContentType
//import io.ktor.http.cio.websocket.Frame
//import io.ktor.http.cio.websocket.WebSocketSession
//import io.ktor.http.cio.websocket.close
//import io.ktor.http.cio.websocket.readText
//import io.ktor.http.contentType
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Deferred
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.async
//import kotlinx.coroutines.channels.broadcast
//import kotlinx.coroutines.coroutineScope
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.collect
//import kotlinx.coroutines.flow.filter
//import kotlinx.coroutines.flow.filterNotNull
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.flow.firstOrNull
//import kotlinx.coroutines.flow.flow
//import kotlinx.coroutines.flow.map
//import kotlinx.coroutines.flow.mapNotNull
//import kotlinx.coroutines.flow.receiveAsFlow
//import kotlinx.coroutines.isActive
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.sync.Mutex
//import kotlinx.coroutines.sync.withLock
//import kotlinx.serialization.decodeFromString
//import kotlinx.serialization.json.Json
//import javax.inject.Inject
//import javax.inject.Singleton
//
//@AndroidEntryPoint
//class SocketService @Inject constructor() : Service(), ChatSocketRepository {
//
//    @Inject
//    lateinit var client: HttpClient
//
////    lateinit var chatSocketRepository: ChatSocketRepository
//
//    private val binder = MyBinder()
//
//    constructor(httpClient: HttpClient) : this() {
//        client = httpClient
//    }
//
//    private val filter = IntentFilter("receiver_socket_action_2")
//
//    private var socket: WebSocketSession? = null
//    lateinit var socket2: WebSocketSession
//
//    private val scopeService = CoroutineScope(Dispatchers.IO)
//
//
//    private val receiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent) {
//            when (intent.action) {
//                "receiver_socket_action_2" -> {
//                    Log.d("4444", " SocketService BroadcastReceiver send_message")
//                    sendMessage(
//                        MessageToSend(
//                            sender = intent.getStringExtra("sender") ?: "",
//                            recipient = intent.getStringExtra("recipient") ?: "",
//                            textMessage = intent.getStringExtra("messageText") ?: "",
//                            refreshToken = intent.getStringExtra("refreshToken") ?: ""
//                        )
//                    )
//                }
//            }
//        }
//    }
//
//    // Этот метод вызывается при создании службы и используется
//    // для выполнения инициализации и подготовки перед работой службы.
//
//    override fun onCreate() {
//        super.onCreate()
//
//    }
//
//    private fun getSocketObject(): WebSocketSession? {
//        var mySocket: WebSocketSession? = null
//        mySocket = this.socket
//        return mySocket
//    }
//
//    // Этот метод вызывается каждый раз, когда служба запускается командой startService()
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        Log.d("4444", " SocketService onStartCommand")
//        when (intent?.action) {
//            "open_session" -> {
//                // Выполните код для соединения с сервером
//                val senderIntent = intent.getStringExtra("sender")
//                senderIntent?.let { senderPhone ->
//                    CoroutineScope(Dispatchers.IO).launch {
//                        // Вызовите ваш метод initSession() здесь
//                        when (initSession(sender = senderPhone)) {
//                            is Resource.Success -> {
//                                // Инициализация успешна
//                                Log.d("4444", " Resource.Success")
//                                registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
//                                val successIntent = Intent("receiver_socket_action")
//                                successIntent.putExtra("session", "session_success")
//                                sendBroadcast(successIntent)
//
//
//                                // из за то что этот код тут я не получаю списки
//                                // диалогов и контактов
//
//                                scopeService.launch {
//                                    val res = observeMessages().firstOrNull()
//                                    Log.d("4444", " observeMessages() res=" + res)
//                                }
//
//
////                                scopeService.launch {
////                                    val jobObserver: Deferred<Message?> = async {
////                                        observeMessages().firstOrNull()
////                                    }
////                                    val message = jobObserver.await()
////                                    val gson = Gson()
////                                    val messageToJson = gson.toJson(message)
////
////                                    val messageIntent = Intent("receiver_socket_message")
////                                    messageIntent.putExtra(
////                                        "observer_incoming_message",
////                                        messageToJson
////                                    )
////                                    //messageIntent.putExtra("observer", "incoming_message")
////                                    sendBroadcast(messageIntent)
////                                }
//                            }
//
//                            is Resource.Error -> {
//                                // Произошла ошибка при инициализации сессии
//                                // Обрабатывайте ошибку соединения
//                                Log.d("4444", " Resource.Error")
//                                val successIntent = Intent("receiver_socket_action")
//                                successIntent.putExtra("session", "session_error")
//                                //  successIntent.putExtra("success", true)
//                                sendBroadcast(successIntent)
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        // START_REDELIVER_INTENT если система убьет сервис то когда появяться ресурсы то она
//        // снова воссоздаст его с последним intent намерением
//        return START_STICKY //START_REDELIVER_INTENT
//    }
//
//    private fun testMethod() {
//
//    }
//
//    inner class MyBinder : Binder() {
//        fun getService(): SocketService {
//            return this@SocketService
//        }
//    }
//
//    override fun onBind(intent: Intent?): IBinder? {
//        // Возвращаем экземпляр Binder для клиентов
//        return binder
//    }
//
//    override fun onRebind(intent: Intent?) {
//        super.onRebind(intent)
//    }
//
//    override fun onUnbind(intent: Intent?): Boolean {
//        return super.onUnbind(intent)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        Log.d("4444", " SocketService onDestroy")
//        unregisterReceiver(receiver)
//        // Выполните код для разрыва соединения с сервером при завершении службы
//        closeSession()
//        stopSelf()
//    }
//
//    private suspend fun initSession(sender: String): Resource<Unit> {
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
//    private fun closeSession() {
//        Log.d("4444", " SocketService closeSession")
//        CoroutineScope(Dispatchers.IO).launch {
//            if (socket == null) {
//                Log.d("4444", " SocketService closeSession socket NULL")
//            } else {
//                Log.d("4444", " SocketService closeSession socket NOT NULL")
//            }
//            socket?.close()
//        }
//        val successIntent = Intent("receiver_socket_action")
//        successIntent.putExtra("session", "session_close")
//        sendBroadcast(successIntent)
//    }
//
//    private fun sendMessage(messageToSend: MessageToSend) {
//        try {
//            Log.d("4444", " SocketService sendMessage messageToSend=" + messageToSend)
//            val gson = Gson()
//            val messageToSendJson = gson.toJson(messageToSend)
//            CoroutineScope(Dispatchers.IO).launch {
//                socket?.send(Frame.Text(messageToSendJson))
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.d("4444", " try catch ChatSocketServiceImpl e=" + e)
//        }
//    }
//
//    private fun observeMessages(): Flow<Message> {
//        return try {
//            Log.d("4444", "observeMessages")
//            if (socket == null) {
//                Log.d("4444", "socket NULL observeMessages")
//            } else {
//                Log.d("4444", "socket не NULL observeMessages")
//            }
//            socket?.incoming
//                ?.receiveAsFlow()
//                ?.filter { it is Frame.Text }
//                ?.map {
//                    val json = (it as? Frame.Text)?.readText() ?: ""
//                    val messageDto = Json.decodeFromString<MessageDto>(json)
//                    messageDto.mapToDomain()
//                } ?: flow { }
//
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.d("4444", "try catch observeMessages e=$e")
//            flow { }
//        }
//    }
//
//
////    override suspend fun observeMessages(): Flow<Message> {
////        return try {
////
////
////            scopeService.launch {
////                Log.d("4444", "observeMessages")
////                if (socket == null) {
////                    Log.d("4444", "socket NULL observeMessages")
////                } else {
////                    Log.d("4444", "socket не NULL observeMessages")
////                }
////            }
////
////
////
////            socket?.incoming
////                ?.receiveAsFlow()
////                ?.filter { it is Frame.Text }
////                ?.map {
////                    val json = (it as? Frame.Text)?.readText() ?: ""
////                    val messageDto = Json.decodeFromString<MessageDto>(json)
////                    messageDto.mapToDomain()
////                } ?: flow { }
////
////
////        } catch (e: Exception) {
////            e.printStackTrace()
////            Log.d("4444", "try catch observeMessages e=$e")
////            flow { }
////        }
////    }
//
//
////    override fun observeMessages(): Flow<Message> {
////        return try {
////            Log.d("4444", " observeMessages")
////            if (socket == null) {
////                Log.d("4444", " socket NULL observeMessages")
////            } else {
////                Log.d("4444", " socket не NULL observeMessages")
////            }
////
////            socket?.incoming
////                ?.receiveAsFlow()
////                ?.filter { it is Frame.Text }
////                ?.map {
////                    val json = (it as? Frame.Text)?.readText() ?: ""
////                    val messageDto = Json.decodeFromString<MessageDto>(json)
////                    messageDto.mapToDomain()
////                } ?: flow { }
////        } catch (e: Exception) {
////            e.printStackTrace()
////            Log.d("4444", " try catch observeMessages e=" + e)
////            flow { }
////        }
////    }
//
//    override suspend fun getAllMessages(userPairChat: UserPairChat): List<Message> {
//        return try {
//            Log.d(
//                "4444",
//                " MessageServiceImpl getAllMessages поппытка отпарвить userPairChat=" + userPairChat
//            )
//            client.post<List<MessageDto>>(ChatSocketRepository.Endpoints.GetAllMessages.url) {
//                contentType(ContentType.Application.Json)
//                body = userPairChat
//            }.map { it.mapToDomain() }
//        } catch (e: Exception) {
//            emptyList()
//        }
//    }
//
////    override suspend fun getAllMessages(userPairChat: UserPairChat): List<Message> {
////        return try {
////            client.get<List<MessageDto>>(MessageService.Endpoints.GetAllMessages.url) {
////                contentType(ContentType.Application.Json)
////                body = userPairChat
////            }.map { it.toMessage() }
////        } catch (e: Exception) {
////            emptyList()
////        }
////    }
//
////    override suspend fun getAllMessages(userPairChat: UserPairChat): List<Message> {
////        return try {
////            client.get<List<MessageDto>>(MessageService.Endpoints.GetAllMessages.url)
////                .map { it.toMessage() }
////        } catch (e: Exception) {
////            emptyList()
////        }
////    }
//
//    override suspend fun getStateUsersConnection() {
//        try {
//            //client.get(ChatSocketRepository.Endpoints.SocketStateUsersConnection.url)
//        } catch (e: Exception) {
//            Log.d("4444", " try cathc getStateUsersConnection e=" + e)
//        }
//    }
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
//                        onlineOrDateDTO.mapToDomain()
//                    }
//                } ?: flow { }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.d("4444", " try catch observeMessages e=" + e)
//            flow { }
//        }
//    }
//}