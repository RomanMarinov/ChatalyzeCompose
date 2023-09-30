package com.dev_marinov.chatalyze.presentation.ui.signup_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.domain.repository.AuthRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class SignUpScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private var _notice: MutableStateFlow<String> = MutableStateFlow("")
    val notice: StateFlow<String> = _notice

    private var _hasToken: MutableStateFlow<String> = MutableStateFlow("")
    val hasToken: StateFlow<String> = _hasToken

    fun registerUser(email: String, password: String) {
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
            } else {
              _notice.value = response.toString()

            }
        }
    }
}