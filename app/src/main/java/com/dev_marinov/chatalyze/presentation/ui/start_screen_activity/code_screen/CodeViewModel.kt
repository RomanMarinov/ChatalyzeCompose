package com.dev_marinov.chatalyze.presentation.ui.start_screen_activity.code_screen

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dev_marinov.chatalyze.domain.model.auth.MessageResponse
import com.dev_marinov.chatalyze.domain.repository.AuthRepository
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.presentation.ui.start_screen_activity.code_screen.model.UserCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.internal.http.*
import javax.inject.Inject

@HiltViewModel
class CodeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferencesDataStoreRepository: PreferencesDataStoreRepository
) : ViewModel() {

    val getEmail = preferencesDataStoreRepository.getEmail
    var emailTemp = ""

    private var _statusCode: MutableStateFlow<Int> = MutableStateFlow(0)
    val statusCode: StateFlow<Int> = _statusCode
    private var _notice: MutableStateFlow<String> = MutableStateFlow("")
    val notice: StateFlow<String> = _notice

    init {
        getEmail()
    }

    fun sendCode(code: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = authRepository.sendCode(
                userCode = UserCode(
                    email = emailTemp,
                    code = code
                )
            )
            response?.let {
                processTheResponse(response = response)
            }
        }
    }

    private fun getEmail() {
        viewModelScope.launch(Dispatchers.IO) {
            getEmail.collect {
                emailTemp = it
            }
        }
    }

    private suspend fun processTheResponse(response: MessageResponse) {

        when (response.httpStatusCode) {
            HTTP_OK -> {
                Log.d("4444", " respons OK")
                _statusCode.value = response.httpStatusCode
                _notice.value = response.message
                delay(1000L)
                _statusCode.value = 0
            }
            HTTP_NOT_FOUND -> {
                Log.d("4444", " respons HTTP_NOT_FOUND")
                _statusCode.value = response.httpStatusCode
                _notice.value = response.message
                delay(1000L)
                _statusCode.value = 0
            }
            HTTP_GONE -> {
                _statusCode.value = response.httpStatusCode
                _notice.value = response.message
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
                _notice.value = response.message
                delay(1000L)
                _statusCode.value = 0
            }
        }
    }
}