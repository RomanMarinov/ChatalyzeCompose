package com.dev_marinov.chatalyze.presentation.ui.signup_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.domain.repository.AuthRepository
import com.dev_marinov.chatalyze.domain.repository.DataStoreRepository
import com.dev_marinov.chatalyze.presentation.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class SignUpScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private var _notice: MutableStateFlow<String> = MutableStateFlow("")
    val notice: StateFlow<String> = _notice

    private var _hasToken: MutableStateFlow<String> = MutableStateFlow("")
    val hasToken: StateFlow<String> = _hasToken

    fun registerUserAndSaveToken(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response: String? = authRepository.registerUser(
                email = email, password = password
            )

            Log.d("4444", " registerUser response=" + response)
            // Проверяем, содержит ли response поле "token"
            if (response?.contains("\"token\"") == true) {
                val jsonResponse = JSONObject(response)
                val token = jsonResponse.optString("token")
                _hasToken.value = token
                saveTokenRegister(tokenRegister = token)
            } else {
                _notice.value = response.toString()
                delay(1000L)
                _notice.value = ""

            }
        }
    }

    private fun saveTokenRegister(tokenRegister: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveTokenRegister(
                keyTokenRegister = Constants.KEY_TOKEN_REGISTER,
                tokenRegister = tokenRegister
            )
        }
    }
}