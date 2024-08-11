package com.dev_marinov.chatalyze.presentation.ui.start_screen_activity.auth_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.domain.model.auth.PairOfTokens
import com.dev_marinov.chatalyze.domain.repository.AuthRepository
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.presentation.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class AuthScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferencesDataStoreRepository: PreferencesDataStoreRepository
) : ViewModel() {

    val refreshToken: Flow<String> = authRepository.getRefreshTokensFromDataStore

    private var _notice: MutableStateFlow<String> = MutableStateFlow("")
    val notice: StateFlow<String> = _notice

    init {
        saveEmail(email = "")
    }

    fun signInAndSaveTokenSignIn(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            var accessToken = ""
            var refreshToken = ""
            val response = authRepository.signInUser(email = email, password = password)
            if (response?.contains("\"accessToken\"") == true && response.contains("\"refreshToken\"")) {
                val jsonResponse = JSONObject(response)
                accessToken = jsonResponse.optString("accessToken")
                refreshToken = jsonResponse.optString("refreshToken")
                savePairOfTokens(PairOfTokens(accessToken = accessToken, refreshToken = refreshToken))
            } else {
                _notice.value = response.toString()
                delay(1000L)
                _notice.value = ""
            }
        }
    }

    private fun savePairOfTokens(pairOfTokens: PairOfTokens) {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.savePairTokens(pairOfTokens = pairOfTokens)
        }
    }

    private fun saveEmail(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesDataStoreRepository.saveEmail(key = Constants.KEY_EMAIL, email = email)
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