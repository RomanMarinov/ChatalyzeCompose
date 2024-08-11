package com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chat_screen

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.domain.model.chat.ChatCompanion
import com.dev_marinov.chatalyze.domain.model.chat.Message
import com.dev_marinov.chatalyze.domain.model.chat.MessageToSend
import com.dev_marinov.chatalyze.domain.model.chat.UserPairChat
import com.dev_marinov.chatalyze.domain.repository.AuthRepository
import com.dev_marinov.chatalyze.domain.repository.ChatRepository
import com.dev_marinov.chatalyze.domain.repository.ChatSocketRepository
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.domain.repository.RoomRepository
import com.dev_marinov.chatalyze.presentation.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatScreenViewModel @Inject constructor(
    private val preferencesDataStoreRepository: PreferencesDataStoreRepository,
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,
    private val chatSocketRepository: ChatSocketRepository,
    private val roomRepository: RoomRepository,
) : ViewModel() {

    val onlineUserStateList = roomRepository.onlineUserStateList

    val isSessionState = preferencesDataStoreRepository.isSessionState
    val getStateUnauthorized = preferencesDataStoreRepository.getStateUnauthorized
    val refreshToken = authRepository.getRefreshTokensFromDataStore

    private var _userPairChat = mutableStateOf(UserPairChat())
    private val userPairChat: State<UserPairChat> = _userPairChat

    private var _chatPosition = MutableStateFlow(0)
    val chatPosition: StateFlow<Int> = _chatPosition

    private var _chatMessage: MutableStateFlow<List<Message>> = MutableStateFlow(listOf())
    val chatMessage: StateFlow<List<Message>> = _chatMessage

    private val _messageText = mutableStateOf("")
    val messageText: State<String> = _messageText

    private val _state = mutableStateOf(ChatState())
    val state: State<ChatState> = _state

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    private var _recipient = ""
    private var _sender = ""
    private var _refreshToken = ""

    init {
        saveRefreshTokenToViewModel()
    }

    private fun saveRefreshTokenToViewModel() {
        viewModelScope.launch(Dispatchers.IO) {
            refreshToken.collect {
                _refreshToken = it
            }
        }
    }

    fun saveHideNavigationBar(isHide: Boolean) {
        viewModelScope.launch {
            preferencesDataStoreRepository.saveHideNavigationBar(
                Constants.HIDE_BOTTOM_BAR,
                isHide = isHide
            )
        }
    }

    fun getChatPosition(keyUserName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result =
                preferencesDataStoreRepository.getScrollChatPosition(keyUserName = keyUserName)
            result.collectLatest { position ->
                position?.let {
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

    fun observeMessages2(
        sender: String,
        recipient: String,
        textMessage: String,
        createdAt: String,
    ) {
        val message = Message(
            sender = sender,
            recipient = recipient,
            textMessage = textMessage,
            createdAt = createdAt
        )

        val newList = state.value.messages.toMutableList().apply {
            add(_state.value.messages.size, message)
        }
        _state.value = state.value.copy(
            messages = newList
        )
    }

    fun onMessageChange(message: String) {
        _messageText.value = message
    }

    fun getAllMessageChat() {
        viewModelScope.launch {
            _state.value = state.value.copy(isLoading = true)

            val jobResponse: Deferred<List<Message>> = async {
                chatSocketRepository.getAllMessages(userPairChat = userPairChat.value)
            }

            _chatMessage.value = jobResponse.await()

            _state.value = state.value.copy(
                messages = jobResponse.await(),
                isLoading = false
            )
        }
    }

    fun sendMessage() { // клик отправки сообщения на сервер
        viewModelScope.launch(Dispatchers.IO) {
            if (messageText.value.isNotEmpty()) {
                val messageToSend = MessageToSend(
                    sender = _sender,
                    recipient = _recipient,
                    textMessage = messageText.value,
                    refreshToken = _refreshToken
                )
                chatSocketRepository.sendMessage(messageToSend)
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

    fun saveCompanionOnTheServer(senderPhone: String, recipientPhone: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val chatCompanion = ChatCompanion(
                senderPhone = senderPhone,
                companionPhone = recipientPhone
            )
            val response = chatRepository.saveCompanionOnTheServer(chatCompanion = chatCompanion)
        }
    }
}