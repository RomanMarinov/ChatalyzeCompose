package com.dev_marinov.chatalyze.presentation.ui.signup_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun registerUser(login: String, email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = authRepository.registerUser(
                login = login, password = password, email = email
            )
        }
    }
}