package com.dev_marinov.chatalyze.presentation.ui.chat_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.domain.model.chat.ChatMessage
import com.dev_marinov.chatalyze.domain.repository.DataStoreRepository
import com.dev_marinov.chatalyze.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatScreenViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private var _chatPosition = MutableStateFlow<Int?>(null)
    val chatPosition: StateFlow<Int?> = _chatPosition

    fun getChatPosition(userName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = dataStoreRepository.getScrollChatPosition(keyUserName = userName)
            result.collectLatest { position ->
                position?.let {
                    _chatPosition.value = it
                }
            }
        }
    }

    fun saveScrollChatPosition(keyUserName: String, position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveScrollChatPosition(key = keyUserName, position = position)
        }
    }

    fun sendMessage(messageText: String, currentDateTime: String) {
        viewModelScope.launch(Dispatchers.IO) {
            chatRepository.sendMessage(
                ChatMessage(
                    messageText = messageText,
                    currentDateTime = currentDateTime
                )
            )
        }
    }
}