package com.dev_marinov.chatalyze.presentation.ui.forgot_password_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {


    fun sendEmail(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.sendEmail(email = email)
        }
    }
}