package com.dev_marinov.chatalyze.presentation.ui.chats_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.domain.repository.ChatSocketRepository
import com.dev_marinov.chatalyze.data.chatMessage.dto.MessageWrapper
import com.dev_marinov.chatalyze.data.chatMessage.dto.OnlineUserState
import com.dev_marinov.chatalyze.domain.model.chats.Chat
import com.dev_marinov.chatalyze.domain.repository.ChatsRepository
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.presentation.ui.chats_screen.model.CombineChat
import com.dev_marinov.chatalyze.presentation.ui.chats_screen.model.Contact
import com.dev_marinov.chatalyze.presentation.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class ChatsScreenViewModel @Inject constructor(
    private val preferencesDataStoreRepository: PreferencesDataStoreRepository,
    private val chatsRepository: ChatsRepository,
    private val chatSocketRepository: ChatSocketRepository,
    // private val socketService: SocketService
) : ViewModel() {

    val isSessionState = preferencesDataStoreRepository.isSessionState

    val isGrantedPermissions = preferencesDataStoreRepository.isGrantedPermissions
    val isTheLifecycleEventNow = preferencesDataStoreRepository.isTheLifecycleEventNow
    val getOwnPhoneSender = preferencesDataStoreRepository.getOwnPhoneSender


    //var onlineUserStateList: StateFlow<List<OnlineUserState>> = _onlineUserStateList
    //private var _combineChatListObserver: MutableStateFlow<List<CombineChat>> = MutableStateFlow(listOf())
    // val combineChatListObserver: StateFlow<List<CombineChat>> = _combineChatListObserver

    var ownPhoneSender = ""

    private var _isOpenModalBottomSheet: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isOpenModalBottomSheet: StateFlow<Boolean> = _isOpenModalBottomSheet

    private var _getChatListFlag: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val getChatListFlag: StateFlow<Boolean> = _getChatListFlag

    private var _combineChatList: MutableStateFlow<List<CombineChat>> =
        MutableStateFlow(emptyList())
    val combineChatList: StateFlow<List<CombineChat>> = _combineChatList

    private var messageWrapper = MutableStateFlow<MessageWrapper?>(null)

    private var _onlineUserStateList = MutableStateFlow(emptyList<OnlineUserState>())

    private var _chatList = MutableStateFlow(emptyList<Chat>())
    val chatList: StateFlow<List<Chat>> = _chatList

    private var _contacts: MutableStateFlow<List<Contact>> = MutableStateFlow(listOf())
    val contacts: StateFlow<List<Contact>> = _contacts

    init {
        Log.d("4444", " ChatsScreenViewModel сработал init")
        saveLocalOwnPhoneSender()
    }

    //    var combineChatList: Flow<List<CombineChat>> =
//        combine(_chatList, _onlineUserStateList, _contacts) { chats, stateList, cont ->
//            chats.map { chat ->
//                val onlineUserState = stateList.firstOrNull {
//                    it.userPhone == chat.recipient
//                }?.onlineOrDate
//                val name = cont.firstOrNull { it.phoneNumber == chat.recipient }?.name
//                CombineChat(
//                    sender = chat.sender,
//                    recipient = chat.recipient,
//                    textMessage = chat.textMessage,
//                    createdAt = chat.createdAt,
//                    onlineOrDate = onlineUserState,
//                    name = name
//                )
//            }
//        }
    //.shareIn(viewModelScope, SharingStarted.WhileSubscribed())
    //.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())
    // .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())


    fun createCombineFlow() {
            val combineChatListFlow: Flow<List<CombineChat>> =
                combine(_chatList, _onlineUserStateList, _contacts) { chats, stateList, cont ->
                    chats.map { chat ->
                        val onlineUserState = stateList.firstOrNull {
                            it.userPhone == chat.recipient
                        }?.onlineOrDate
                        val name = cont.firstOrNull { it.phoneNumber == chat.recipient }?.name
                        CombineChat(
                            sender = chat.sender,
                            recipient = chat.recipient,
                            textMessage = chat.textMessage,
                            createdAt = chat.createdAt,
                            onlineOrDate = onlineUserState,
                            name = name
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
        Log.d("4444", " ChatsScreenViewModel createContactsFlow contacts=" + contacts)
        _contacts.value = contacts
    }

    // поток нужен для получения списка последних сообщений
    fun createChatListFlow() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = chatsRepository.getChats(sender = ownPhoneSender)
            _chatList.value = response
            // getOnlineUserStateList()
            Log.d("4444", " ChatsScreenViewModel createChatListFlow _chatList=" + _chatList.value)
        }
    }

//    fun createOnlineUserStateList() {
//        viewModelScope.launch(Dispatchers.IO) {
//            chatSocketRepository.observeOnlineUserState().collect { value ->
//                if (value.type == "userList") {
//                    val json = Json { ignoreUnknownKeys = true }
//                    val parsedJson = json.decodeFromString<List<OnlineUserState>>(value.payloadJson)
//                    _onlineUserStateList.value = parsedJson
//                    Log.d(
//                        "4444",
//                        " ChatsScreenViewModel createOnlineUserStateList _onlineUserStateList=" + _onlineUserStateList.value
//                    )
//                }
//            }
//        }
//    }
//

    fun createOnlineUserStateList() {

//        val flow1: Flow<List<OnlineUserState>> = flow {
//            val onlineUserList = listOf(
//                OnlineUserState(userPhone = "9203333333", onlineOrDate = "online"),
//                OnlineUserState(userPhone = "9303454564", onlineOrDate = "offline")
//            )
//
//            emit(onlineUserList)
//        }
//        viewModelScope.launch(Dispatchers.IO) {
//            while (true) {
//                delay(5000L)
//                flow1.collect {
//                    _onlineUserStateList.value = it
//                }
//            }
//        }
//////////////////////////////////////////////////////
//        viewModelScope.launch(Dispatchers.IO) {
//            chatSocketRepository.observeOnlineUserState().collect { value: MessageWrapper ->
//                if (value.type == "userList") {
//                    val json = Json { ignoreUnknownKeys = true }
//                    val parsedJson = json.decodeFromString<List<OnlineUserState>>(value.payloadJson)
//
//
//                }
//            }
//        }


//        viewModelScope.launch(Dispatchers.IO) {
//            chatSocketRepository.observeOnlineUserState().collect { value: MessageWrapper ->
//                if (value.type == "userList") {
//
//                    messageWrapper.value = value
////                    val json = Json { ignoreUnknownKeys = true }
////                    val parsedJson = json.decodeFromString<List<OnlineUserState>>(value.payloadJson)
////                    emit(parsedJson)
//                }
//            }
//        }
//
//        viewModelScope.launch(Dispatchers.IO) {
//
//            messageWrapper.collect { messageWrapper ->
//                val json = Json { ignoreUnknownKeys = true }
//                messageWrapper?.let {
//                    val parsedJson = json.decodeFromString<List<OnlineUserState>>(it.payloadJson)
//                    _onlineUserStateList.value = parsedJson
//                }
//
//            }
//            //delay(5000L)
////            flow.collect {
////                _onlineUserStateList.value = it
////            }
//        }
        ////////////////////////////////////////


        viewModelScope.launch(Dispatchers.IO) {
            chatSocketRepository.observeOnlineUserState().collect { value ->
                if (value.type == "userList") {
                    val json = Json { ignoreUnknownKeys = true }
                    val parsedJson = json.decodeFromString<List<OnlineUserState>>(value.payloadJson)
                    _onlineUserStateList.value = parsedJson
                    Log.d(
                        "4444",
                        " ChatsScreenViewModel createOnlineUserStateList _onlineUserStateList=" + _onlineUserStateList.value
                    )


                    createCombineFlow()
                }
            }
        }


        //////////////////////////////////////////
//        chatSocketRepository.observeOnlineUserState()
//            .onEach { value ->
//                // Обработка значения value
//                Log.d("4444", " getOnlineUserStateList value зашел=" + value)
//                if (value.type == "userList") {
//                    val json = Json { ignoreUnknownKeys = true }
//                    val parsedJson = json.decodeFromString<List<OnlineUserState>>(value.payloadJson)
//                    _onlineUserStateList.value = parsedJson
//                    Log.d("4444", " getOnlineUserStateList userList parsedJson зашел=" + parsedJson)
//                }
//
//            }.launchIn(viewModelScope)


//////////////////////////////////////////////////////
//        try {
//            //Log.d("4444", " ChatsScreenViewModel выполнился getOnlineUserStateList")
//            viewModelScope.launch(Dispatchers.IO) {
//                val onlineUserStateFlow: Flow<MessageWrapper> = chatSocketRepository.observeOnlineUserState()
//
//              //  viewModelScope.launch(Dispatchers.IO) {
//                    onlineUserStateFlow.collect { value ->
//                        // Обработка значения value
//                        Log.d("4444", " getOnlineUserStateList value зашел=" + value)
//                        if (value.type == "userList") {
//                            val json = Json { ignoreUnknownKeys = true }
//                            val parsedJson = json.decodeFromString<List<OnlineUserState>>(value.payloadJson)
//                            _onlineUserStateList.value = parsedJson
//                            Log.d("4444", " getOnlineUserStateList userList parsedJson зашел=" + parsedJson)
//                        }
//                    }
//               // }
//            }
//        } catch (e: Exception) {
//            Log.d("4444", " getOnlineUserStateList try catch e=" + e)
//        }
    }

//    fun getOnlineUserStateList() {
//        try {
//            //Log.d("4444", " ChatsScreenViewModel выполнился getOnlineUserStateList")
//            viewModelScope.launch(Dispatchers.IO) {
//               val res =  chatSocketRepository.observeOnlineUserState()
//                    .onEach { value ->
//                        // getOnlineUserStateList value=MessageWrapper(type=userList, payloadJson=[{"userPhone":"9203333333","onlineOrDate":"online"},{"userPhone":"9303454564","onlineOrDate":"offline"}])
//                        Log.d("4444", " getOnlineUserStateList value зашел=" + value)
//                        if (value.type == "userList") {
//                            val json = Json { ignoreUnknownKeys = true }
//                            val parsedJson = json.decodeFromString<List<OnlineUserState>>(value.payloadJson)
//                            Log.d("4444", " getOnlineUserStateList userList parsedJson зашел=" + parsedJson)
//                            _onlineUserStateList.value = parsedJson
//                        }
//
//
//                    }
//                    .launchIn(viewModelScope)
//            }
//        } catch (e: Exception) {
//            Log.d("4444", " getOnlineUserStateList try catch e=" + e)
//        }
//    }

//
//    public override fun onCleared() {
//        // Вызывается при уничтожении ViewModel
//        super.onCleared()
//        _combineChatList.value = emptyList()
//
//        // Ваш код здесь
//    }




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

    fun saveOwnPhoneSender(ownPhoneSender: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("4444", " saveOwnPhoneSender ownPhoneSender=" + ownPhoneSender)
            preferencesDataStoreRepository.saveOwnPhoneSender(
                key = Constants.OWN_PHONE_SENDER,
                ownPhoneSender = ownPhoneSender
            )
        }
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

    fun ebnutCombine() {
        //_combineChatList.value = emptyList()
    }



    //    fun getOnlineUserStateList() {
////        viewModelScope.launch(Dispatchers.IO) {
////            val result =
////            Log.d("4444", " ChatsScreenViewModel getOnlineUserStateList result=" + result)
////        }
//
//        try {
//            Log.d("4444", " ChatsScreenViewModel выполнился getOnlineUserStateList")
//            viewModelScope.launch {
//                chatSocketRepository.observeOnlineUserState()
//                    .onEach { value ->
//
//                        Log.d("4444", " getOnlineUserStateList value=" + value)
//
//                       // _onlineUserStateList.value = value
//
////                    count++
////
////                    _onlineUserStateList.value = listOf(
////                        OnlineUserState(
////                            userPhone = "9203333333" + count,
////                            onlineOrDate = "online"
////                        ),
////                        OnlineUserState(
////                            userPhone = "9303454564",
////                            onlineOrDate = "offline"
////                        )
////                    )
//
//
//                        Log.d("4444", " ChatsScreenViewModel getOnlineUserStateList value=" + value)
//                    }.launchIn(viewModelScope)
//            }
//        } catch (e: Exception) {
//            Log.d("4444", " getOnlineUserStateList try catch e=" + e)
//        }
//
//    }


}