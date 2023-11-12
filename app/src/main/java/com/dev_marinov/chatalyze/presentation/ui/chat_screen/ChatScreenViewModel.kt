package com.dev_marinov.chatalyze.presentation.ui.chat_screen

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.data.chatMessage.ChatSocketService
import com.dev_marinov.chatalyze.data.chatMessage.MessageService
import com.dev_marinov.chatalyze.domain.model.chat.Message
import com.dev_marinov.chatalyze.domain.model.chat.MessageToSend
import com.dev_marinov.chatalyze.domain.model.chat.UserPairChat
import com.dev_marinov.chatalyze.domain.repository.AuthRepository
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.domain.repository.ChatRepository
import com.dev_marinov.chatalyze.presentation.util.Constants
import com.dev_marinov.chatalyze.presentation.util.CorrectNumberFormatHelper
import com.dev_marinov.chatalyze.presentation.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatScreenViewModel @Inject constructor(
    private val preferencesDataStoreRepository: PreferencesDataStoreRepository,
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,
    private val messageService: MessageService,
    private val chatSocketService: ChatSocketService,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val refreshToken = authRepository.getRefreshTokensFromDataStore

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

    fun connectToChat() {
        // сюда передать объект message
        getAllMessages()
        //   getAllMessages()
        Log.d("4444", " connectToChat execute")
        // savedStateHandle.get<String>("username")?.let { username ->
        viewModelScope.launch {
            val result = chatSocketService.initSession(sender = _sender)
            Log.d("4444", " connectToChat result.message=" + result.message)
            when (result) {
                is Resource.Success -> {
                    Log.d("4444", " connectToChat Resource.Success")
                    chatSocketService.observeMessages()
                        .onEach { message ->
                            Log.d("4444", " connectToChat Resource.Success message=" + message)
                            val newList = state.value.messages.toMutableList().apply {
                                add(_state.value.messages.size, message)
                            }
                            _state.value = state.value.copy(
                                messages = newList
                            )
                        }.launchIn(viewModelScope)
                }
                is Resource.Error -> {
                    Log.d("4444", " connectToChat Resource.Error")
                    _toastEvent.emit(result.message ?: "Unknown error")
                }
            }
        }
        // }
    }

    fun onMessageChange(message: String) {
        _messageText.value = message
    }

    fun disconnect() {
        viewModelScope.launch {
            chatSocketService.closeSession()
        }
    }

    fun getAllMessages() {

        val userPairChat = UserPairChat(
            sender = _sender,
            recipient = _recipient
        )
        viewModelScope.launch {
            _state.value = state.value.copy(isLoading = true)


            val result: List<Message> = messageService.getAllMessages(userPairChat = userPairChat)
            //////
            _chatMessage.value = result

            ////////
            Log.d("4444", " getAllMessages result=" + result)
            _state.value = state.value.copy(
                messages = result,
                isLoading = false
            )
        }
    }

    fun sendMessage() {
        viewModelScope.launch {
            Log.d("4444", " sendMessage=" + messageText.value)
            if (messageText.value.isNotEmpty()) {
                val messageToSend = MessageToSend(
                    sender = _sender,
                    recipient = _recipient,
                    textMessage = messageText.value,
                    refreshToken = _refreshToken
                )
                chatSocketService.sendMessage(messageToSend)

                _messageText.value = ""
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }


    fun saveToViewModel(recipient: String?, sender: String?) {
        recipient?.let { _recipient = it }
        sender?.let { _sender = it }
    }

//    fun saveToViewModel(recipient: String?, sender: String?) {
//        recipient?.let {
//            if (CorrectNumberFormatHelper.getCorrectNumber(number = it).isNotEmpty()) {
//                _recipient = CorrectNumberFormatHelper.getCorrectNumber(it)
//            }
//        }
//        sender?.let {
//            if (CorrectNumberFormatHelper.getCorrectNumber(number = it).isNotEmpty()) {
//                _sender = CorrectNumberFormatHelper.getCorrectNumber(it)
//            }
//        }
//    }
}