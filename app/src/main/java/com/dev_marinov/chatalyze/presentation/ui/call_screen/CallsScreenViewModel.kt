package com.dev_marinov.chatalyze.presentation.ui.call_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CallsScreenViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private var _fakeCalls: MutableStateFlow<List<String>> = MutableStateFlow(listOf())
    val fakeCalls: StateFlow<List<String>> = _fakeCalls

    fun getFakeCalls() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = chatRepository.getChatMessage()
            _fakeCalls.value = result
        }
    }
}