package com.dev_marinov.chatalyze.presentation.ui.start_screen_activity.forgot_password_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.domain.model.auth.MessageResponse
import com.dev_marinov.chatalyze.domain.repository.AuthRepository
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.presentation.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.internal.http.HTTP_BAD_REQUEST
import okhttp3.internal.http.HTTP_CONFLICT
import okhttp3.internal.http.HTTP_INTERNAL_SERVER_ERROR
import okhttp3.internal.http.HTTP_OK
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferencesDataStoreRepository: PreferencesDataStoreRepository
) : ViewModel() {

    val getEmail = preferencesDataStoreRepository.getEmail

    private var _statusCode: MutableStateFlow<Int> = MutableStateFlow(0)
    val statusCode: StateFlow<Int> = _statusCode
    private var _notice: MutableStateFlow<String> = MutableStateFlow("")
    val notice: StateFlow<String> = _notice

    fun sendAndSaveEmail(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = authRepository.sendEmail(email = email)
            response?.let {
                processTheResponse(it)
            }
        }
        saveEmail(email = email)
    }

    fun saveEmail(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesDataStoreRepository.saveEmail(key = Constants.KEY_EMAIL, email = email)
        }
    }

    private suspend fun processTheResponse(response: MessageResponse) {

        when (response.httpStatusCode) {
            HTTP_OK -> {
                _statusCode.value = response.httpStatusCode
                _notice.value = response.message
                delay(1000L)
                _statusCode.value = 0
            }
            HTTP_CONFLICT -> {
                _statusCode.value = response.httpStatusCode
                delay(1000L)
                _statusCode.value = 0
            }
            HTTP_BAD_REQUEST -> {
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
}