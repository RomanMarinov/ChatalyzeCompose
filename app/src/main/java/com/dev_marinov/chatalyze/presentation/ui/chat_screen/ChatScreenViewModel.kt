package com.dev_marinov.chatalyze.presentation.ui.chat_screen

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.data.chatMessage.ChatSocketRepository
import com.dev_marinov.chatalyze.domain.model.chat.Message
import com.dev_marinov.chatalyze.domain.model.chat.MessageToSend
import com.dev_marinov.chatalyze.domain.model.chat.UserPairChat
import com.dev_marinov.chatalyze.domain.repository.AuthRepository
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.domain.repository.ChatRepository
import com.dev_marinov.chatalyze.presentation.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatScreenViewModel @Inject constructor(
    private val preferencesDataStoreRepository: PreferencesDataStoreRepository,
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,
    //private val messageRepository: MessageRepository,
    private val chatSocketRepository: ChatSocketRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val isSessionState = preferencesDataStoreRepository.isSessionState
    val isGrantedPermissions = preferencesDataStoreRepository.isGrantedPermissions

    val refreshToken = authRepository.getRefreshTokensFromDataStore

//    private var _userPairChat = MutableStateFlow<UserPairChat?>(null)
//    private val userPairChat: StateFlow<UserPairChat?> = _userPairChat

    private var _userPairChat = mutableStateOf(UserPairChat())
    private val userPairChat: State<UserPairChat> = _userPairChat

    private var _chatPosition = MutableStateFlow(0)
    val chatPosition: StateFlow<Int> = _chatPosition

    private var _chatMessage: MutableStateFlow<List<Message>> = MutableStateFlow(listOf())
    val chatMessage: StateFlow<List<Message>> = _chatMessage

    private var _recipient = ""
    private var _sender = ""
    private var _refreshToken = ""

    init {
        // getFakeChatMessage()
        saveRefreshTokenToViewModel()

        //connectToChat()
    }

    private fun saveRefreshTokenToViewModel() {
        viewModelScope.launch(Dispatchers.IO) {
            refreshToken.collect {
                _refreshToken = it
            }
        }
    }

//    private fun getFakeChatMessage() {
//        viewModelScope.launch(Dispatchers.IO) {
//            val result = chatRepository.getChatMessage()
//            _chatMessage.value = result
//        }
//    }

    fun saveHideNavigationBar(isHide: Boolean) {
        viewModelScope.launch {
            preferencesDataStoreRepository.saveHideNavigationBar(
                Constants.HIDE_BOTTOM_BAR,
                isHide = isHide
            )
        }
    }

    fun getChatPosition(userName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result =
                preferencesDataStoreRepository.getScrollChatPosition(keyUserName = userName)
            result.collectLatest { position ->
                position?.let {

                    //    _chatPosition.emit(it)
                    _chatPosition.value = it
                }
            }
        }
    }

    fun saveScrollChatPosition(keyUserName: String, position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesDataStoreRepository.saveScrollChatPosition(
                key = keyUserName,
                position = position
            )
        }
    }

//    fun sendMessage(messageText: String, currentDateTime: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            chatRepository.getChatMessage(
//                ChatMessage(
//                    messageText = messageText,
//                    currentDateTime = currentDateTime
//                )
//            )
//        }
//    }


    //////////////////////////////////////////////

    private val _messageText = mutableStateOf("")
    val messageText: State<String> = _messageText

    private val _state = mutableStateOf(ChatState())
    val state: State<ChatState> = _state

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    //////////////////////////////
    /////////////////////////////////


    /////////////////////////////////
    /////////////////////////////////



    // хуй пока закрыл ебаная ошибка
    fun observeMessages() {
        viewModelScope.launch {
            Log.d("4444", " connectToChat Resource.Success")
            chatSocketRepository.observeMessages()
                .onEach { message ->
                    Log.d("4444", " connectToChat Resource.Success message=" + message)


                    // connectToChat Resource.Success message=MessageWrapper(type=userList, payloadJson=[{"userPhone":"9203333333","onlineOrDate":"online"},{"userPhone":"9303454564","onlineOrDate":"offline"}])
                    // connectToChat Resource.Success message=MessageWrapper(type=singleMessage, payloadJson={"sender":"5551234567","recipient":"9303454564","textMessage":"Пппп","createdAt":"2023-12-16T12:27:51.161455Z"})

//                    val newList = state.value.messages.toMutableList().apply {
//                        add(_state.value.messages.size, message)
//                    }
//                    _state.value = state.value.copy(
//                        messages = newList
//                    )
                }.launchIn(viewModelScope)
        }
    }
//
//// хуй пока закрыл ебаная ошибка
//    fun observeMessages() {
//        viewModelScope.launch {
//            Log.d("4444", " connectToChat Resource.Success")
//            chatSocketRepository.observeMessages()
//                .onEach { message ->
//                    Log.d("4444", " connectToChat Resource.Success message=" + message)
//                    val newList = state.value.messages.toMutableList().apply {
//                        add(_state.value.messages.size, message)
//                    }
//                    _state.value = state.value.copy(
//                        messages = newList
//                    )
//                }.launchIn(viewModelScope)
//        }
//    }


//    fun connectToChat() {
//        // сюда передать объект message
//        //getAllMessageChat()
//        //   getAllMessages()
//        Log.d("4444", " connectToChat execute")
//        // savedStateHandle.get<String>("username")?.let { username ->
//        viewModelScope.launch {
//            val result = chatSocketRepository.initSession(sender = _sender)
//            Log.d("4444", " connectToChat result.message=" + result.message)
//            when (result) {
//                is Resource.Success -> {
//                    Log.d("4444", " connectToChat Resource.Success")
//                    chatSocketRepository.observeMessages()
//                        .onEach { message ->
//                            Log.d("4444", " connectToChat Resource.Success message=" + message)
//                            val newList = state.value.messages.toMutableList().apply {
//                                add(_state.value.messages.size, message)
//                            }
//                            _state.value = state.value.copy(
//                                messages = newList
//                            )
//
//                            // тут вызвать как-то getChats
//
//                        }.launchIn(viewModelScope)
//
//                    /////////////////////////////
//                    // test ping pong
//
////                    chatSocketService.observePingPong()
////                        .onEach {
////                            Log.d("4444", " connectToChat Resource.Success ping pong=" + it)
////                        }
////
////                    val res = chatSocketService.method("ping")
////                    Log.d("4444", " connectToChat ping res=" + res)
//
//
//                }
//                is Resource.Error -> {
//                    Log.d("4444", " connectToChat Resource.Error")
//                    _toastEvent.emit(result.message ?: "Unknown error")
//                }
//            }
//        }
//
//        viewModelScope.launch(Dispatchers.IO) {
//           // chatSocketService.getStateUsersConnection()
//
//            //chatSocketService.observePing().collect {
//           //     Log.d("4444", " connectToChat Resource.Success ping pong=" + it)
//         //   }
//        }
//
//
//        // }
//    }

    fun onMessageChange(message: String) {
        _messageText.value = message
    }

    fun getAllMessageChat() {
        viewModelScope.launch {
            _state.value = state.value.copy(isLoading = true)

            val jobResponse: Deferred<List<Message>> = async {
                chatSocketRepository.getAllMessages(userPairChat = userPairChat.value)
            }

//            val result: List<Message> = messageService.getAllMessages(userPairChat = userPairChat.value)
//            _chatMessage.value = result

            _chatMessage.value = jobResponse.await()
            Log.d("4444", " getAllMessages result=" + jobResponse.await())

            _state.value = state.value.copy(
                messages = jobResponse.await(),
                isLoading = false
            )
        }
    }

    fun sendMessage() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("4444", " sendMessage=" + messageText.value)
            if (messageText.value.isNotEmpty()) {
                val messageToSend = MessageToSend(
                    sender = _sender,
                    recipient = _recipient,
                    textMessage = messageText.value,
                    refreshToken = _refreshToken
                )
                chatSocketRepository.sendMessage(messageToSend)

//                val intent = Intent("receiver_socket_action_2")
//                // intent.action = "receiver_socket_action_2"
//                intent.putExtra("sender", _sender)
//                intent.putExtra("recipient", _recipient)
//                intent.putExtra("messageText", messageText.value)
//                intent.putExtra("refreshToken", _refreshToken)
//                context.sendBroadcast(intent)

//                val serviceConnection = ServiceConnection()
//                val intent = Intent(context, SocketService::class.java)
//                intent.action = "send_message"
//                intent.putExtra("sender", _sender)
//                intent.putExtra("recipient", _recipient)
//                intent.putExtra("messageText", messageText.value)
//                intent.putExtra("refreshToken", _refreshToken)
//                context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            }
        }
    }

    fun clearMessageTextField() {
        _messageText.value = ""
    }

    fun saveToViewModel(recipient: String?, sender: String?) {
        recipient?.let { _recipient = it }
        sender?.let { _sender = it }
    }

    fun saveLocallyUserPairChat(senderPhone: String?, recipientPhone: String?) {
        _userPairChat.value = UserPairChat(
            sender = senderPhone ?: "",
            recipient = recipientPhone ?: ""
        )
    }

    fun receiveMessage(sender: String, recipient: String, textMessage1: String, createdAt: String) {
        val message = Message(
            sender = sender,
            recipient = recipient,
            textMessage = textMessage1,
            createdAt = createdAt
        )
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("4444", " receiveMessage message=" + message)
            val newList = state.value.messages.toMutableList().apply {
                add(_state.value.messages.size, message)
            }
            _state.value = state.value.copy(
                messages = newList
            )
        }
    }
}