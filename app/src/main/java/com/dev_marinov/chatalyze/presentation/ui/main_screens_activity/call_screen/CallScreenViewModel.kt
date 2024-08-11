package com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.call_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.domain.model.auth.MessageResponse
import com.dev_marinov.chatalyze.domain.repository.CallRepository
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.domain.repository.RoomRepository
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.call_screen.model.FirebaseCommand
import com.dev_marinov.chatalyze.presentation.util.Constants
import com.dev_marinov.chatalyze.presentation.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.internal.http.HTTP_INTERNAL_SERVER_ERROR
import okhttp3.internal.http.HTTP_NOT_FOUND
import okhttp3.internal.http.HTTP_OK
import javax.inject.Inject

@HiltViewModel
class CallScreenViewModel @Inject constructor(
    private val callRepository: CallRepository,
    private val preferencesDataStoreRepository: PreferencesDataStoreRepository,
    private val roomRepository: RoomRepository
) : ViewModel() {

    val isSessionState = preferencesDataStoreRepository.isSessionState

    val getReadyStream = roomRepository.getReadyStream


    private var _statusCode: MutableStateFlow<Int> = MutableStateFlow(0)
    val statusCode: StateFlow<Int> = _statusCode

    private var _isFinishCallScreen = SingleLiveEvent<Boolean>()
    val isFinishCallScreen: SingleLiveEvent<Boolean> = _isFinishCallScreen

    fun sendCommandToFirebase(firebaseCommand: FirebaseCommand) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = callRepository.sendCommandToFirebase(firebaseCommand = firebaseCommand)
            response?.let { messageResponse ->
                processTheResponse(response = messageResponse)
            }
        }
    }

    private suspend fun processTheResponse(response: MessageResponse) {
        Log.d("4444", " firebase processTheResponse response=" + response)
        when (response.httpStatusCode) {
            HTTP_OK -> {
                _statusCode.value = response.httpStatusCode
                delay(1000L)
                _statusCode.value = 0
            }

            HTTP_NOT_FOUND -> {
                _statusCode.value = response.httpStatusCode
                delay(1000L)
                _statusCode.value = 0
            }

            HTTP_INTERNAL_SERVER_ERROR -> {
                _statusCode.value = response.httpStatusCode
                delay(1000L)
                _statusCode.value = 0
            }
        }
    }

    fun executeFinishCallScreen(finish: Boolean) {
        _isFinishCallScreen.postValue(finish)
    }

    fun saveHideNavigationBar(isHide: Boolean) {
        viewModelScope.launch {
            preferencesDataStoreRepository.saveHideNavigationBar(
                Constants.HIDE_BOTTOM_BAR,
                isHide = isHide
            )
        }
    }

    fun sendStateReadyToStream(firebaseCommand: FirebaseCommand) {
        viewModelScope.launch(Dispatchers.IO) {
            callRepository.sendCommandToFirebase(firebaseCommand = firebaseCommand)
        }
    }
}