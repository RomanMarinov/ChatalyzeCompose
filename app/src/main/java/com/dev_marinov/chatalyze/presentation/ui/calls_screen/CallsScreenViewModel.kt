package com.dev_marinov.chatalyze.presentation.ui.calls_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.domain.repository.ChatRepository
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.presentation.ui.chats_screen.model.CombineChat
import com.dev_marinov.chatalyze.presentation.ui.chats_screen.model.Contact
import com.dev_marinov.chatalyze.presentation.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CallsScreenViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val preferencesDataStoreRepository: PreferencesDataStoreRepository
) : ViewModel() {

    private var _combineChatList: MutableStateFlow<List<CombineChat>> = MutableStateFlow(listOf())
    val combineChatList: StateFlow<List<CombineChat>> = _combineChatList

    val isSessionState = preferencesDataStoreRepository.isSessionState

    private var _contacts: MutableStateFlow<List<Contact>> = MutableStateFlow(listOf())
    val contacts: StateFlow<List<Contact>> = _contacts

    private var _isOpenModalBottomSheet: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isOpenModalBottomSheet: StateFlow<Boolean> = _isOpenModalBottomSheet

    private var _getChatListFlag: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val getChatListFlag: StateFlow<Boolean> = _getChatListFlag

    val getOwnPhoneSender = preferencesDataStoreRepository.getOwnPhoneSender
    var ownPhoneSender = ""

//    fun getFakeCalls() {
//        viewModelScope.launch(Dispatchers.IO) {
//            val result = chatRepository.getChatMessage()
//           // _fakeCalls.value = result
//        }
//    }

    init {
        Log.d("4444", " ChatsScreenViewModel сработал init")
        saveLocalOwnPhoneSender()
    }

    fun onClickHideNavigationBar(isHide: Boolean) {
        viewModelScope.launch {
            preferencesDataStoreRepository.saveHideNavigationBar(
                Constants.HIDE_BOTTOM_BAR,
                isHide = isHide
            )
        }
    }

    fun openModalBottomSheet(isOpen: Boolean) {
        _isOpenModalBottomSheet.value = isOpen
    }

    // походу из бд надо брать
    fun canGetChatList(can: Boolean) {
        _getChatListFlag.value = can
    }

    // походу из бд надо брать ИЛИ ЭТО
    fun createContactsFlow(contacts: List<Contact>) {
        Log.d("4444", " ChatsScreenViewModel createContactsFlow contacts=" + contacts)
        _contacts.value = contacts
    }

    private fun saveLocalOwnPhoneSender() {
        viewModelScope.launch(Dispatchers.IO) {
            getOwnPhoneSender.collect {
                ownPhoneSender = it
            }
        }
    }
}