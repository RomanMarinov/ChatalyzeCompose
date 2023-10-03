package com.dev_marinov.chatalyze.presentation.ui.splash_screen

import androidx.lifecycle.ViewModel
import com.dev_marinov.chatalyze.domain.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    val getTokenSignIn = dataStoreRepository.getTokenSignIn

}