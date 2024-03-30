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

//    val pairTokens = authRepository.getPairOfTokensFromDataStore.asLiveData()
    val refreshToken: Flow<String> = authRepository.getRefreshTokensFromDataStore

    //private val refreshTokenSingleLiveEvent = SingleLiveEvent<String>()

    private var _notice: MutableStateFlow<String> = MutableStateFlow("")
    val notice: StateFlow<String> = _notice

    init {
//        viewModelScope.launch(Dispatchers.IO) {
//            authRepository.getRefreshTokensFromDataStore.collect { token ->
//                refreshTokenSingleLiveEvent.postValue(token)
//            }
//        }

        saveEmail(email = "")
    }

//    fun getRefreshToken() : SingleLiveEvent<String> {
//        return refreshTokenSingleLiveEvent
//    }

    fun signInAndSaveTokenSignIn(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            var accessToken = ""
            var refreshToken = ""
            val response = authRepository.signInUser(email = email, password = password)
            // registerUser response={"token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJodHRwOi8vMC4wLjAuMDo4MDgwIiwiaXNzIjoiaHR0cDovLzAuMC4wLjA6ODA4MCIsImV4cCI6MTcyODIxMDQ2MiwiZW1haWxJZCI6IjExNThtbm1ubWRAeWFuZGV4LnJ1In0.JkRGqRcg0bLkE8uXIX1_b6Qvhdg268nfSeLwFedkx0c"}
            Log.d("4444", " registerUser response=" + response)
            // Проверяем, содержит ли response поле "token"
            if (response?.contains("\"accessToken\"") == true && response.contains("\"refreshToken\"")) {
                val jsonResponse = JSONObject(response)
                accessToken = jsonResponse.optString("accessToken")
                refreshToken = jsonResponse.optString("refreshToken")
                Log.d("4444", " registerUser заходит")
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
        Log.d("4444", " AuthScreenViewModel saveStartStateForTokens")
        viewModelScope.launch(Dispatchers.IO) {
            preferencesDataStoreRepository.saveStateNotFoundRefreshToken(isNotFound = false)
            preferencesDataStoreRepository.saveFailureUpdatePairToken(isFailure = false)
            preferencesDataStoreRepository.saveStateUnauthorized(isUnauthorized = false)
            preferencesDataStoreRepository.saveInternalServerError(isError = false)
        }
    }
}