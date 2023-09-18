package com.dev_marinov.chatalyze.presentation.ui.auth_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {


    fun getToken(login: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = authRepository.signInUser(
                login = login, password = password
            )

        }
    }
}