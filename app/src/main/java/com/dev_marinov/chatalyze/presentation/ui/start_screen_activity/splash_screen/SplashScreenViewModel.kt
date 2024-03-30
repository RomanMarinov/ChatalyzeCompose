package com.dev_marinov.chatalyze.presentation.ui.start_screen_activity.splash_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.domain.repository.AuthRepository
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.presentation.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val refreshToken = authRepository.getRefreshTokensFromDataStore

    private val refreshTokenSingleLiveEvent = SingleLiveEvent<String>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.getRefreshTokensFromDataStore.collect { token ->
                refreshTokenSingleLiveEvent.postValue(token)
            }
        }
    }

    fun getRefreshToken() : SingleLiveEvent<String> {
        return refreshTokenSingleLiveEvent
    }
}