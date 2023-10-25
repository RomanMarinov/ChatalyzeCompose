package com.dev_marinov.chatalyze.domain.repository

import com.dev_marinov.chatalyze.domain.model.auth.MessageResponse
import com.dev_marinov.chatalyze.domain.model.auth.PairTokens
import com.dev_marinov.chatalyze.presentation.ui.code_screen.model.UserCode
import com.dev_marinov.chatalyze.presentation.ui.create_password_screen.model.ForgotPasswordPassword
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun registerUser(email: String, password: String) : MessageResponse
    suspend fun signInUser(email: String, password: String) : String?
    suspend fun sendEmail(email: String) : MessageResponse?

    val getPairTokensFromDataStore: Flow<PairTokens>
    val getRefreshTokensFromDataStore: Flow<String>

    suspend fun savePairTokens(pairTokens: PairTokens)
    suspend fun logout(token: String) : MessageResponse?
    suspend fun deleteProfile(token: String) : MessageResponse?

    suspend fun deletePairTokensToDataStore()

    suspend fun sendCode(userCode: UserCode) : MessageResponse?

    suspend fun sendRefreshPassword(forgotPasswordPassword: ForgotPasswordPassword) : MessageResponse?
}