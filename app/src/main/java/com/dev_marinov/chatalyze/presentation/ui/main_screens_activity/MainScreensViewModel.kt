package com.dev_marinov.chatalyze.presentation.ui.main_screens_activity

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.data.chatMessage.dto.OnlineUserState
import com.dev_marinov.chatalyze.domain.repository.ChatSocketRepository
import com.dev_marinov.chatalyze.domain.repository.FirebaseRegisterRepository
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chat_screen.ChatState
import com.dev_marinov.chatalyze.domain.model.firebase.UserFirebase
import com.dev_marinov.chatalyze.domain.repository.RoomRepository
import com.dev_marinov.chatalyze.presentation.util.Constants
import com.dev_marinov.chatalyze.presentation.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class MainScreensViewModel @Inject constructor(
    private val preferencesDataStoreRepository: PreferencesDataStoreRepository,
    private val chatSocketRepository: ChatSocketRepository,
    private val firebaseRegisterRepository: FirebaseRegisterRepository,
    private val roomRepository: RoomRepository,
) : ViewModel() {

    val isGrantedPermissions = preferencesDataStoreRepository.isGrantedPermissions
    val isTheLifecycleEventNow = preferencesDataStoreRepository.isTheLifecycleEventNow

    private val firebaseToken = preferencesDataStoreRepository.firebaseToken

    private val ownPhoneSender = preferencesDataStoreRepository.getOwnPhoneSender
    private var phoneSenderLocal = ""

    val isHideBottomBar = preferencesDataStoreRepository.getHideBottomBar

    private var _canStartService: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val canStartService: StateFlow<Boolean> = _canStartService

    private val _messageText = mutableStateOf("")
    val messageText: State<String> = _messageText

    private val _state = mutableStateOf(ChatState())
    val state: State<ChatState> = _state

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    private var firebaseTokenLocal = ""

    init {
        savePhoneInViewModel()
        saveLocalFirebaseToken()
    }

    private fun saveLocalFirebaseToken() {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseTokenLocal = firebaseToken.first()
        }
    }

    private fun savePhoneInViewModel() {
        viewModelScope.launch(Dispatchers.IO) {
            ownPhoneSender.collect {
                if (it.isNotEmpty()) {
                    phoneSenderLocal = it
                }
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

    private fun registerUserFirebase() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = firebaseRegisterRepository.firebaseRegister(
                UserFirebase(
                    registerSenderPhone = phoneSenderLocal,
                    firebaseToken = firebaseTokenLocal
                )
            )
            Log.d(
                "4444",
                " ChatalyzeScreenViewModel registerUserFirebase firebaseTokenLocal=" + firebaseTokenLocal
            )
            Log.d(
                "4444",
                " ChatalyzeScreenViewModel registerUserFirebase response=" + response?.message
            )
        }
    }
// cf2jBrO0TbmWNhVkaQX7vc:APA91bE2AbQBOzpQJpFW2TIastWXyjmTWjS6zMMrADNhy5hIXHt2bXlT62V_LCb-mraeLI_LFTBomJ7rvzdrQcY4rnz1aJKZ3--FVTnFR5Dkj0Jz3ut38aJ_0kinzlMxS8bO-1V7AfK7


    fun saveOwnPhoneSender(ownPhoneSender: String) {
        viewModelScope.launch(Dispatchers.IO) {
            //  Log.d("4444", " saveOwnPhoneSender ownPhoneSender=" + ownPhoneSender)
            preferencesDataStoreRepository.saveOwnPhoneSender(
                key = Constants.OWN_PHONE_SENDER,
                ownPhoneSender = ownPhoneSender
            )
        }
    }

    fun savePermissions(key: String, isGranted: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesDataStoreRepository.saveGrantedPermissions(
                key = key,
                isGranted = isGranted
            )
        }
    }

    fun saveLifecycleEvent(eventType: String) {
        Log.d("4444", " ChatalyzeScreenViewModel saveLifecycleEvent eventType=" + eventType)
        viewModelScope.launch(Dispatchers.IO) {
            preferencesDataStoreRepository.saveLifecycleEvent(eventType = eventType)
        }
    }

    fun canStartWebSocket(can: Boolean) {
        _canStartService.value = can
    }

    fun openServerWebSocketConnection(ownPhoneSender: String) {
        Log.d("4444", " выполнился openServerConnection")
        viewModelScope.launch(Dispatchers.IO) {
            val result = chatSocketRepository.initSession(sender = ownPhoneSender)
            when (result) {
                is Resource.Success -> {
                    Log.d("4444", " openServerConnection Success")
                    saveSessionState(sessionState = Constants.SESSION_SUCCESS)
                    createOnlineUserStateListAndSave()
                    registerUserFirebase()
                }

                is Resource.Error -> {
                    Log.d("4444", " openServerConnection Error")
                    _toastEvent.emit(result.message ?: "Unknown error")

                    saveSessionState(sessionState = Constants.SESSION_ERROR)
                }

            }
        }
    }

    private fun createOnlineUserStateListAndSave() {
        viewModelScope.launch(Dispatchers.IO) {
            chatSocketRepository.observeOnlineUserState().collect { value ->
                if (value.type == "userList") {
                    val json = Json { ignoreUnknownKeys = true }
                    val parsedJson = json.decodeFromString<List<OnlineUserState>>(value.payloadJson)
                    //Log.d("4444", " parsedJson=" + parsedJson)
                    saveOnlineUserStateListToDb(onlineUserStateList = parsedJson)
                }
            }
        }
    }

    private fun saveOnlineUserStateListToDb(onlineUserStateList: List<OnlineUserState>) {
        viewModelScope.launch(Dispatchers.IO) {
            roomRepository.saveOnlineUserStateList(onlineUserStateList = onlineUserStateList)
        }
    }

    private fun saveSessionState(sessionState: String) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesDataStoreRepository.saveSessionState(sessionState = sessionState)
        }
    }

    fun closeWebSocketConnection() {
        saveSessionState(sessionState = Constants.SESSION_CLOSE)

        viewModelScope.launch(Dispatchers.IO) {
            chatSocketRepository.closeSession()
        }
    }

    // пока сделал копию
//    private fun getOnlineUserStateList() {
//        try {
//            //Log.d("4444", " ChatsScreenViewModel выполнился getOnlineUserStateList")
//            viewModelScope.launch(Dispatchers.IO) {
//                chatSocketRepository.observeOnlineUserState()
//                    .onEach { value ->
//
//                        Log.d("4444", " getOnlineUserStateList value=" + value)
//                    }.launchIn(viewModelScope)
//            }
//        } catch (e: Exception) {
//            Log.d("4444", " getOnlineUserStateList try catch e=" + e)
//        }
//    }

//    private fun getOnlineUserStateList() {
//        try {
//            //Log.d("4444", " ChatsScreenViewModel выполнился getOnlineUserStateList")
//            viewModelScope.launch(Dispatchers.IO) {
//                chatSocketRepository.observeOnlineUserState()
//                    .onEach { value ->
//
//                        Log.d("4444", " getOnlineUserStateList value=" + value)
//
//                        // getOnlineUserStateList value=MessageWrapper(type=userList, payloadJson=[{"userPhone":"9203333333","onlineOrDate":"online"},{"userPhone":"9303454564","onlineOrDate":"offline"}])
//
//                        if (value.type == "userList") {
//                            val json = Json { ignoreUnknownKeys = true }
//                            val parsedJson =
//                                json.decodeFromString<List<OnlineUserState>>(value.payloadJson)
//
//                        }
//
//
//                    }.launchIn(viewModelScope)
//            }
//        } catch (e: Exception) {
//            Log.d("4444", " getOnlineUserStateList try catch e=" + e)
//        }
//    }
}


//        fun connectToChat() {
//        // сюда передать объект message
////        getAllMessageChat()
//        //   getAllMessages()
//        viewModelScope.launch(Dispatchers.IO) {
//            val result = chatSocketRepository.initSession(sender = phoneSender)
//            Log.d("4444", " connectToChat result.message=" + result.message)
//            when (result) {
//                is Resource.Success -> {
//
//                    Log.d("4444", " connectToChat Resource.Success")
//                    chatSocketRepository.observeMessages()
//                        .onEach { message ->
//                            Log.d("4444", " connectToChat Resource.Success message=" + message)
//                            val newList = state.value.messages.toMutableList().apply {
//                                add(_state.value.messages.size, message)
//                            }
//                            _state.value = state.value.copy(
//                                messages = newList
//                            )
//
//                            // тут вызвать как-то getChats
//
//                        }
//
//                }
//                is Resource.Error -> {
//                    Log.d("4444", " connectToChat Resource.Error")
//                    _toastEvent.emit(result.message ?: "Unknown error")
//                }
//            }
//        }
//
//        viewModelScope.launch(Dispatchers.IO) {
//           // chatSocketService.getStateUsersConnection()
//
//            //chatSocketService.observePing().collect {
//           //     Log.d("4444", " connectToChat Resource.Success ping pong=" + it)
//         //   }
//        }
//
//
//        // }
//    }
