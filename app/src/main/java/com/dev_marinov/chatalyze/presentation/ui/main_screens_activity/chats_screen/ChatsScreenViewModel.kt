package com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chats_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.domain.repository.ChatSocketRepository
import com.dev_marinov.chatalyze.data.chatMessage.dto.OnlineUserState
import com.dev_marinov.chatalyze.domain.model.chats.Chat
import com.dev_marinov.chatalyze.domain.repository.ChatsRepository
import com.dev_marinov.chatalyze.domain.repository.RoomRepository
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chats_screen.model.CombineChat
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chats_screen.model.Contact
import com.dev_marinov.chatalyze.presentation.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatsScreenViewModel @Inject constructor(
    private val preferencesDataStoreRepository: PreferencesDataStoreRepository,
    private val chatsRepository: ChatsRepository,
    private val chatSocketRepository: ChatSocketRepository,
    private val roomRepository: RoomRepository,
) : ViewModel() {

    private val onlineUserStateList = roomRepository.onlineUserStateList

    val isSessionState = preferencesDataStoreRepository.isSessionState

    val isGrantedPermissions = preferencesDataStoreRepository.isGrantedPermissions
    val isTheLifecycleEventNow = preferencesDataStoreRepository.isTheLifecycleEventNow
    val getOwnPhoneSender = preferencesDataStoreRepository.getOwnPhoneSender

    var ownPhoneSender = ""

    private var _isOpenModalBottomSheet: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isOpenModalBottomSheet: StateFlow<Boolean> = _isOpenModalBottomSheet

    private var _getChatListFlag: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val getChatListFlag: StateFlow<Boolean> = _getChatListFlag

    private var _combineChatList: MutableStateFlow<List<CombineChat>> =
        MutableStateFlow(emptyList())
    val combineChatList: StateFlow<List<CombineChat>> = _combineChatList

    private var _onlineUserStateList = MutableStateFlow(emptyList<OnlineUserState>())

    private var _chatList = MutableStateFlow(emptyList<Chat>())
    val chatList: StateFlow<List<Chat>> = _chatList

    private var _contacts: MutableStateFlow<List<Contact>> = MutableStateFlow(listOf())
    val contacts: StateFlow<List<Contact>> = _contacts

    init {
        Log.d("4444", " ChatsScreenViewModel сработал init")
        saveLocalOwnPhoneSender()
    }

    fun createCombineFlow() {
        val combineChatListFlow: Flow<List<CombineChat>> =
            combine(_chatList, onlineUserStateList, _contacts) { chats, stateList, cont ->
                chats.map { chat ->
                    val onlineUserState = stateList.firstOrNull {
                        it.userPhone == chat.recipient
                    }?.onlineOrOffline
                    val name = cont.firstOrNull { it.phoneNumber == chat.recipient }?.name
                    CombineChat(
                        sender = chat.sender,
                        recipient = chat.recipient,
                        textMessage = chat.textMessage,
                        createdAt = chat.createdAt,
                        onlineOrOffline = onlineUserState,
                        name = name,
                        typeEvent = Constants.OUTGOING_CALL_EVENT
                    )
                }
            }

        viewModelScope.launch(Dispatchers.IO) {
            combineChatListFlow.collect {
                _combineChatList.value = it
                Log.d(
                    "4444",
                    " ChatsScreenViewModel createCombineFlow _combineChatList=" + _combineChatList.value
                    )
            }

        }
    }

    // поток нужен для сопоставления имен
    fun createContactsFlow(contacts: List<Contact>) {
        Log.d("4444", " ChatsScreenViewModel сопоставление имен createContactsFlow contacts=" + contacts)
        _contacts.value = contacts

        saveContactsToDb(contacts = contacts)
    }

    private fun saveContactsToDb(contacts: List<Contact>) {
        viewModelScope.launch(Dispatchers.IO) {
            roomRepository.saveContacts(contacts = contacts)
        }
    }

    // поток нужен для получения списка последних сообщений
    fun createChatListFlow() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = chatsRepository.getChats(sender = ownPhoneSender)
            _chatList.value = response
            // getOnlineUserStateList()
            Log.d("4444", " ChatsScreenViewModel список послед сообщ createChatListFlow _chatList=" + _chatList.value)
        }
    }

    fun createOnlineUserStateList() {
//        viewModelScope.launch(Dispatchers.IO) {
//            chatSocketRepository.observeOnlineUserState().collect { value ->
//                if (value.type == "userList") {
//                    val json = Json { ignoreUnknownKeys = true }
//                    val parsedJson = json.decodeFromString<List<OnlineUserState>>(value.payloadJson)
//                    _onlineUserStateList.value = parsedJson
//                    Log.d(
//                        "4444",
//                        " ChatsScreenViewModel онлайн createOnlineUserStateList _onlineUserStateList=" + _onlineUserStateList.value
//                    )
//                }
//            }
//        }
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

    private fun saveLocalOwnPhoneSender() {
        viewModelScope.launch(Dispatchers.IO) {
            getOwnPhoneSender.collect {
                ownPhoneSender = it
            }
        }
    }

    fun canGetChatList(can: Boolean) {
        _getChatListFlag.value = can
    }
}