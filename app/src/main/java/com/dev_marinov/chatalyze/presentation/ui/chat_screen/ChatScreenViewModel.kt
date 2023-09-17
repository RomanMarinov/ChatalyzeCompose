package com.dev_marinov.chatalyze.presentation.ui.chat_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.domain.repository.DataStoreRepository
import com.dev_marinov.chatalyze.presentation.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatScreenViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private var _chatPosition = MutableStateFlow<Int?>(null)
    val chatPosition: StateFlow<Int?> = _chatPosition

    fun getChatPosition(userName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = dataStoreRepository.getScrollChatPosition(keyUserName = userName)
            result.collectLatest { position ->
                position?.let {
                    Log.d("4444", " viewmodel chatPosition=" + it)
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
}