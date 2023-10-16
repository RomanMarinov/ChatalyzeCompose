package com.dev_marinov.chatalyze.presentation.ui.signup_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.domain.model.auth.MessageResponse
import com.dev_marinov.chatalyze.domain.repository.AuthRepository
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
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
class SignUpScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataStoreRepository: PreferencesDataStoreRepository
) : ViewModel() {

    private var _notice: MutableStateFlow<String> = MutableStateFlow("")
    val notice: StateFlow<String> = _notice

    private var _statusCode: MutableStateFlow<Int> = MutableStateFlow(0)
    val statusCode: StateFlow<Int> = _statusCode

    fun registerUser(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = authRepository.registerUser(
                email = email, password = password
            )
            processTheResponse(response = response)
        }
    }

    private suspend fun processTheResponse(response: MessageResponse) {
        Log.d("4444", " response.httpStatusCode="
                + response.httpStatusCode
                + " response.message=" + response.message)

        when (response.httpStatusCode) {


            HTTP_OK -> {
                _statusCode.value = response.httpStatusCode
                _notice.value = response.message
                delay(1000L)
                _statusCode.value = 0
                _notice.value = ""
            }
            HTTP_CONFLICT -> {
                _statusCode.value = response.httpStatusCode
                _notice.value = response.message
                delay(1000L)
                _statusCode.value = 0
                _notice.value = ""
            }
            HTTP_BAD_REQUEST -> {
                _statusCode.value = response.httpStatusCode
                _notice.value = response.message
                delay(1000L)
                _statusCode.value = 0
                _notice.value = ""
            }
            HTTP_INTERNAL_SERVER_ERROR -> {
                _statusCode.value = response.httpStatusCode
                _notice.value = response.message
                delay(1000L)
                _statusCode.value = 0
                _notice.value = ""
            }
        }
    }

//    private fun saveTokenRegister(tokenRegister: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            dataStoreRepository.saveTokenRegister(
//                keyTokenRegister = Constants.KEY_TOKEN_REGISTER,
//                tokenRegister = tokenRegister
//            )
//        }
//    }
}