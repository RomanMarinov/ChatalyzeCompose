package com.dev_marinov.chatalyze.presentation.ui.profile_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.domain.model.auth.MessageResponse
import com.dev_marinov.chatalyze.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import okhttp3.internal.http.HTTP_CONFLICT
import okhttp3.internal.http.HTTP_INTERNAL_SERVER_ERROR
import okhttp3.internal.http.HTTP_OK
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val refreshToken = authRepository.getRefreshTokensFromDataStore

    private var _statusCode: MutableStateFlow<Int> = MutableStateFlow(0)
    val statusCode: StateFlow<Int> = _statusCode

    var refreshTokenTemp = ""

    init {
        writeRefreshTokenTemp()
    }

    fun executeLogout() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = authRepository.logout(token = refreshTokenTemp)
            response?.let {
                processTheResponse(response = response)
            }
        }
    }

    fun executeDeleteProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = authRepository.deleteProfile(token = refreshTokenTemp)
            response?.let {
                processTheResponse(response = response)
            }
        }
    }

    private fun writeRefreshTokenTemp() {
        viewModelScope.launch {
            refreshToken.collectLatest {
                refreshTokenTemp = it
            }
        }
    }

    private suspend fun processTheResponse(response: MessageResponse) {
        when (response.httpStatusCode) {
            HTTP_OK -> {
                authRepository.deletePairTokensToDataStore()
                _statusCode.value = response.httpStatusCode
                delay(1000L)
                _statusCode.value = 0
            }
            HTTP_CONFLICT -> {
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