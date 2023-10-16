package com.dev_marinov.chatalyze.presentation.ui.splash_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dev_marinov.chatalyze.domain.repository.AuthRepository
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val refreshToken = authRepository.getRefreshTokensFromDataStore

}