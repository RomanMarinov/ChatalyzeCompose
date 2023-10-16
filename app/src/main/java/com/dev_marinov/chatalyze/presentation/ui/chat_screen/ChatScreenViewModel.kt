package com.dev_marinov.chatalyze.presentation.ui.chat_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.domain.repository.ChatRepository
import com.dev_marinov.chatalyze.presentation.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatScreenViewModel @Inject constructor(
    private val preferencesDataStoreRepository: PreferencesDataStoreRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private var _chatPosition = MutableStateFlow(0)
    val chatPosition: StateFlow<Int> = _chatPosition

    private var _chatMessage: MutableStateFlow<List<String>> = MutableStateFlow(listOf())
    val chatMessage: StateFlow<List<String>> = _chatMessage

    init {
        getFakeChatMessage()
    }

    private fun getFakeChatMessage() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = chatRepository.getChatMessage()
            _chatMessage.value = result
        }
    }

    fun saveHideNavigationBar(isHide: Boolean) {
        viewModelScope.launch {
            preferencesDataStoreRepository.saveHideNavigationBar(Constants.HIDE_BOTTOM_BAR, isHide = isHide)
        }
    }

//    private fun getFakeChatMessage() {
//        viewModelScope.launch(Dispatchers.Default) {
//            val jobChat: Deferred<List<String>> = async {
//                getMessages()
//            }
//            _chatMessage.value = jobChat.await()
//            Log.d("4444", " _chatMessage=" + _chatMessage.value)
//        }
//
//
//    }
//
//    private fun getMessages() : List<String> {
//        val listItems = mutableListOf<String>()
//        for (i in 0..150) {
//            listItems.add(i, i.toString())
//        }
//        return listItems
//    }


    fun getChatPosition(userName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = preferencesDataStoreRepository.getScrollChatPosition(keyUserName = userName)
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
            preferencesDataStoreRepository.saveScrollChatPosition(key = keyUserName, position = position)
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
}