package com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.call_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.domain.model.auth.MessageResponse
import com.dev_marinov.chatalyze.domain.repository.CallRepository
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.call_screen.model.UserCall
import com.dev_marinov.chatalyze.presentation.util.Constants
import com.dev_marinov.chatalyze.presentation.util.IfLetHelper
import com.dev_marinov.chatalyze.presentation.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import okhttp3.internal.http.HTTP_INTERNAL_SERVER_ERROR
import okhttp3.internal.http.HTTP_NOT_FOUND
import okhttp3.internal.http.HTTP_OK
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class CallScreenViewModel @Inject constructor(
    private val callRepository: CallRepository,
    private val preferencesDataStoreRepository: PreferencesDataStoreRepository
) : ViewModel() {

    val isSessionState = preferencesDataStoreRepository.isSessionState

    private var _callTimeDuration: MutableStateFlow<String> = MutableStateFlow("")
    val callTimeDuration: StateFlow<String> = _callTimeDuration

    private var currentTimeUnix: Long = 0L
    private var currentTimeUnixCount: Long = 0L

    private var jobCallingLoop: Job? = null

    private var _statusCode: MutableStateFlow<Int> = MutableStateFlow(0)
    val statusCode: StateFlow<Int> = _statusCode

    private var _isFinishCallScreen = SingleLiveEvent<Boolean>()
    val isFinishCallScreen: SingleLiveEvent<Boolean> = _isFinishCallScreen

    fun currentTimeUnix(epochSeconds: Long) {
        currentTimeUnix = epochSeconds
    }

    private fun currentTimeUnixCount(epochSeconds: Long) {
        currentTimeUnixCount = epochSeconds
    }

    private fun calculateTimeDifference() {
        val callTimeDuration = calculateTimeDifference(
            currentTimeUnix,
            currentTimeUnixCount
        )
        _callTimeDuration.value = callTimeDuration
    }

    private fun calculateTimeDifference(start: Long, end: Long): String {
        val duration = Duration.ofSeconds(end - start)
        val hours = duration.toHours()
        val minutes = duration.toMinutes() % 60
        val seconds = duration.seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun startCallingLoop() {
        //Log.d("4444", " _callTimeDuratin вызов startCallingLoop=")
         viewModelScope.launch(Dispatchers.Default) {
             jobCallingLoop = launch {
                 while (isActive) {
                     currentTimeUnixCount(Clock.System.now().epochSeconds)
                     calculateTimeDifference()
                     delay(1000L)
                     //Log.d("4444", " _callTimeDuratin=" + _callTimeDuration.value)
                 }
             }
        }
    }

    fun cancelCallingLoop() {
        viewModelScope.launch(Dispatchers.Default) {
            jobCallingLoop?.cancel()
        }
    }

    fun makeCall(senderPhone: String?, recipientPhone: String?) {
        IfLetHelper.execute(senderPhone, recipientPhone) {
            val userCall = UserCall(
                topic = "",
                sender = it[0],
                recipient = it[1]
            )
            viewModelScope.launch(Dispatchers.IO) {
                val response = callRepository.makeCall(userCall = userCall)
                response?.let { messageResponse ->
                    processTheResponse(response = messageResponse)
                }
            }
        } ?: run {  }
    }

//    private inline fun <T: Any, R: Any> ifLet(vararg elements: T?, closure: (List<T>) -> R): R? {
//        return if (elements.all { it != null }) {
//            closure(elements.filterNotNull())
//        } else null
//    }

    // дублирование
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
}