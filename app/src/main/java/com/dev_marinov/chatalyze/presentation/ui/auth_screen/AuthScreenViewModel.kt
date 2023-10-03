package com.dev_marinov.chatalyze.presentation.ui.auth_screen

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
class AuthScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val tokenRegister = dataStoreRepository.getTokenRegister

    private var _isGoToChatsScreen: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isGoToChatsScreen: StateFlow<Boolean> = _isGoToChatsScreen

    private var _notice: MutableStateFlow<String> = MutableStateFlow("")
    val notice: StateFlow<String> = _notice

    fun signInAndSaveTokenSignIn(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = authRepository.signInUser(email = email, password = password)

            Log.d("4444", " registerUser response=" + response)
            // Проверяем, содержит ли response поле "token"
            if (response?.contains("\"token\"") == true) {
                val jsonResponse = JSONObject(response)
                val token = jsonResponse.optString("token")

                tokenRegister.collect {
                    _isGoToChatsScreen.value = it == token
                    saveTokenSignIn(tokenSignIn = token)
                }
            } else {
                _notice.value = response.toString()
                delay(1000L)
                _notice.value = ""

            }


            // тут делать провеку token из дата стор
            // совпадает ли tokenRegister при регистрации с токеном при входе


            // далее сохранять совпадение как tokenSignIn


            // потом делать проверку на пустоту tokenSignIn в splashScreen
            // и давать доступ на экран chatsScreen


            Log.d("4444", " signIn response=" + response)
        }
    }

    private fun saveTokenSignIn(tokenSignIn: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveTokenSignIn(
                keyTokenSignIn = Constants.KEY_TOKEN_SIGN_IN,
                tokenSignIn = tokenSignIn
            )
        }
    }
}