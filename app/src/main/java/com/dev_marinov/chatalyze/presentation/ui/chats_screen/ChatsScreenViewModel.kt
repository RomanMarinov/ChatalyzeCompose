package com.dev_marinov.chatalyze.presentation.ui.chats_screen

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.data.chatMessage.ChatSocketRepository
import com.dev_marinov.chatalyze.data.socket_service.SocketService
import com.dev_marinov.chatalyze.domain.model.chats.Chat
import com.dev_marinov.chatalyze.domain.repository.ChatsRepository
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.presentation.ui.chats_screen.model.Contact
import com.dev_marinov.chatalyze.presentation.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatsScreenViewModel @Inject constructor(
    private val preferencesDataStoreRepository: PreferencesDataStoreRepository,
    private val chatsRepository: ChatsRepository,
    private val chatSocketRepository: ChatSocketRepository,
    private val socketService: SocketService
) : ViewModel() {

    val isSessionState = preferencesDataStoreRepository.isSessionState

    val isGrantedPermissions  = preferencesDataStoreRepository.isGrantedPermissions
    val isTheLifecycleEventNow = preferencesDataStoreRepository.isTheLifecycleEventNow
    val getOwnPhoneSender = preferencesDataStoreRepository.getOwnPhoneSender

    var ownPhoneSender = ""

    private var _isOpenModalBottomSheet: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isOpenModalBottomSheet: StateFlow<Boolean> = _isOpenModalBottomSheet

    private var _canGetChats: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val canGetChats: StateFlow<Boolean> = _canGetChats

    private var _chatList = MutableStateFlow<List<Chat>>(listOf())
    val chatList: StateFlow<List<Chat>> = _chatList

    init {
        saveLocalOwnPhoneSender()
    }

    private fun getStateUsersConnection() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = chatSocketRepository.getStateUsersConnection()
            Log.d("4444", " responseget StateUsersConnection =" + response)
        }
    }

    fun getChats() {
        viewModelScope.launch(Dispatchers.IO) {
            //Log.d("4444", " ChatsScreenViewModel ownPhoneSender=" + ownPhoneSender)
            val response = chatsRepository.getChats(sender = ownPhoneSender)
           // Log.d("4444", " ChatsScreenViewModel response=" + response)
            _chatList.value = response
            //_chatList.value = response + getFakeChats()
        }

        getStateUsersConnection()
    }

    private var _contacts: MutableStateFlow<List<Contact>> = MutableStateFlow(listOf())
    val contacts: StateFlow<List<Contact>> = _contacts

    fun onClickHideNavigationBar(isHide: Boolean) {
        viewModelScope.launch {
            preferencesDataStoreRepository.saveHideNavigationBar(Constants.HIDE_BOTTOM_BAR,
                isHide = isHide)
        }
    }

    fun transferContacts(contacts: List<Contact>) {
        _contacts.value = contacts

//        val contacts2 = listOf(Contact(
//            name = "АртурМег",
//            phoneNumber = "9203333333",
//            photo = ""
//        ))
        //  _contacts.value = contacts2
    }

    fun openModalBottomSheet(isOpen: Boolean) {
        _isOpenModalBottomSheet.value = isOpen
    }

    private fun getFakeChats(): List<Chat> {
        return listOf(
            Chat(
                sender = "1234567891",
                recipient = "1234567891",
                textMessage = "test1",
                createdAt = "120"
            ),
            Chat(
                sender = "1234567891",
                recipient = "1234567891",
                textMessage = "test2",
                createdAt = "120"
            ), Chat(
                sender = "1234567891",
                recipient = "1234567891",
                textMessage = "test3",
                createdAt = "120"
            ), Chat(
                sender = "1234567891",
                recipient = "1234567891",
                textMessage = "test4",
                createdAt = "120"
            ), Chat(
                sender = "1234567891",
                recipient = "1234567891",
                textMessage = "test5",
                createdAt = "120"
            ), Chat(
                sender = "1234567891",
                recipient = "1234567891",
                textMessage = "test6",
                createdAt = "120"
            ), Chat(
                sender = "1234567891",
                recipient = "1234567891",
                textMessage = "test7",
                createdAt = "120"
            ), Chat(
                sender = "1234567891",
                recipient = "1234567891",
                textMessage = "test8",
                createdAt = "120"
            ), Chat(
                sender = "1234567891",
                recipient = "1234567891",
                textMessage = "test9",
                createdAt = "120"
            ), Chat(
                sender = "1234567891",
                recipient = "1234567891",
                textMessage = "test10",
                createdAt = "120"
            ), Chat(
                sender = "1234567891",
                recipient = "1234567891",
                textMessage = "test11",
                createdAt = "120"
            ), Chat(
                sender = "1234567891",
                recipient = "1234567891",
                textMessage = "test12",
                createdAt = "120"
            ), Chat(
                sender = "1234567891",
                recipient = "1234567891",
                textMessage = "test13",
                createdAt = "120"
            ), Chat(
                sender = "1234567891",
                recipient = "1234567891",
                textMessage = "test14",
                createdAt = "120"
            ), Chat(
                sender = "1234567891",
                recipient = "1234567891",
                textMessage = "test15",
                createdAt = "120"
            ))
    }

    fun saveOwnPhoneSender(ownPhoneSender: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("4444", " saveOwnPhoneSender ownPhoneSender=" + ownPhoneSender)
            preferencesDataStoreRepository.saveOwnPhoneSender(
                key = Constants.OWN_PHONE_SENDER,
                ownPhoneSender = ownPhoneSender)
        }
    }

    private fun saveLocalOwnPhoneSender() {
        viewModelScope.launch(Dispatchers.IO) {
            getOwnPhoneSender.collect {
                ownPhoneSender = it
            }
        }
    }

    // тут надо вернуть результат подключения вебсокета

    fun connectToChat() {
        Log.d("4444", " ChatsScreenViewModel connectToChat execute")
        // savedStateHandle.get<String>("username")?.let { username ->
        viewModelScope.launch {
//            val result = chatSocketRepository.initSession(sender = ownPhoneSenderCorrectFormat)
//            Log.d("4444", " ChatsScreenViewModel connectToChat result.message=" + result.message)
//            when (result) {
//                is Resource.Success -> {
//                    Log.d("4444", " ChatsScreenViewModel connectToChat Resource.Success")
//
//
//                }
//                is Resource.Error -> {
//                    Log.d("4444", " connectToChat Resource.Error")
//                   // _toastEvent.emit(result.message ?: "Unknown error")
//                }
//
//                else -> {}
//            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            // chatSocketService.getStateUsersConnection()

            //chatSocketService.observePing().collect {
            //     Log.d("4444", " connectToChat Resource.Success ping pong=" + it)
            //   }
        }
    }

    fun canGetChats(can: Boolean) {
        _canGetChats.value = can
    }
    // }
}