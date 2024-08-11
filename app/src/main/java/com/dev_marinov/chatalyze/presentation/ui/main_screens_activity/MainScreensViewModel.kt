package com.dev_marinov.chatalyze.presentation.ui.main_screens_activity

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.data.chatMessage.dto.OnlineUserState
import com.dev_marinov.chatalyze.domain.model.chat.Message
import com.dev_marinov.chatalyze.domain.repository.ChatSocketRepository
import com.dev_marinov.chatalyze.domain.repository.FirebaseRegisterRepository
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chat_screen.ChatState
import com.dev_marinov.chatalyze.domain.model.firebase.UserFirebase
import com.dev_marinov.chatalyze.domain.repository.RoomRepository
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.call_screen.model.FirebaseCommand
import com.dev_marinov.chatalyze.presentation.util.ConnectivityObserver
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
    connectivityObserver: ConnectivityObserver
) : ViewModel() {

    val isGrantedPermissions = preferencesDataStoreRepository.isGrantedPermissions
    val isTheLifecycleEventNow = preferencesDataStoreRepository.isTheLifecycleEventNow

    private val firebaseToken = preferencesDataStoreRepository.firebaseToken

    val connectivity = connectivityObserver.observe()

    private val ownPhoneSender = preferencesDataStoreRepository.getOwnPhoneSender
    private var phoneSenderLocal = ""

    val isHideBottomBar = preferencesDataStoreRepository.getHideBottomBar

    val isExitFromApp = preferencesDataStoreRepository.getExitFromApp

    private var _canStartService: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val canStartService: StateFlow<Boolean> = _canStartService

    private val _toastEvent = MutableSharedFlow<String>()

    private var firebaseTokenLocal = ""

    init {
        savePhoneInViewModel()
        saveLocalFirebaseToken()
        onExitFromApp(isExit = false)
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
        }
    }


    fun saveOwnPhoneSender(ownPhoneSender: String) {
        viewModelScope.launch(Dispatchers.IO) {
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
        viewModelScope.launch(Dispatchers.IO) {
            preferencesDataStoreRepository.saveLifecycleEvent(eventType = eventType)
        }
    }

    fun canStartWebSocket(can: Boolean) {
        _canStartService.value = can
    }

    fun openServerWebSocketConnection(ownPhoneSender: String, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = chatSocketRepository.initSession(sender = ownPhoneSender)
            when (result) {
                is Resource.Success -> {
                    saveSessionState(sessionState = Constants.SESSION_SUCCESS)
                    createOnlineUserStateListAndSave(context = context)
                    registerUserFirebase()
                }

                is Resource.Error -> {
                    _toastEvent.emit(result.message ?: "Unknown error")

                    saveSessionState(sessionState = Constants.SESSION_ERROR)
                }

                else -> {}
            }
        }
    }

    private fun createOnlineUserStateListAndSave(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            chatSocketRepository.observeMessages().collect { value ->
                if (value.type == "userList") {
                    val json = Json { ignoreUnknownKeys = true }
                    val parsedJson = json.decodeFromString<List<OnlineUserState>>(value.payloadJson)
                    saveOnlineUserStateListToDb(onlineUserStateList = parsedJson)
                }

                if (value.type == "singleMessage") {
                    val json = Json { ignoreUnknownKeys = true }
                    val message = json.decodeFromString<Message>(value.payloadJson)

                    val intent = Intent("receiver_single_message")
                    intent.putExtra("sender", message.sender)
                    intent.putExtra("recipient", message.recipient)
                    intent.putExtra("textMessage", message.textMessage)
                    intent.putExtra("createdAt", message.createdAt)
                    context.sendBroadcast(intent)
                }

                if (value.type == "companionOffline") {
                    val json = Json { ignoreUnknownKeys = true }
                    val message = json.decodeFromString<Message>(value.payloadJson)

                    val firebaseCommand = FirebaseCommand(
                        topic = "",
                        senderPhone = message.sender,
                        recipientPhone = message.recipient,
                        textMessage = message.textMessage,
                        typeFirebaseCommand = Constants.TYPE_FIREBASE_MESSAGE_MESSAGE
                    )
                    viewModelScope.launch(Dispatchers.IO) {
                        val response = chatSocketRepository.sendCommandToFirebase(firebaseCommand = firebaseCommand)
                        response?.let { messageResponse ->
                        }
                    }
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

    private fun onExitFromApp(isExit: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesDataStoreRepository.onExitFromApp(isExit = isExit)
        }
    }

    fun savePreferencesState() {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesDataStoreRepository.saveStateNotFoundRefreshToken(isNotFound = false)
            preferencesDataStoreRepository.saveFailureUpdatePairToken(isFailure = false)
            preferencesDataStoreRepository.saveStateUnauthorized(isUnauthorized = false)
            preferencesDataStoreRepository.saveInternalServerError(isError = false)
        }
    }
}