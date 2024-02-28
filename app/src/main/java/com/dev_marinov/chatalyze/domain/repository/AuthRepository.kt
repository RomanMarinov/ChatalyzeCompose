package com.dev_marinov.chatalyze.domain.repository

import com.dev_marinov.chatalyze.domain.model.auth.MessageResponse
import com.dev_marinov.chatalyze.domain.model.auth.PairTokens
import com.dev_marinov.chatalyze.presentation.ui.start_screen_activity.code_screen.model.UserCode
import com.dev_marinov.chatalyze.presentation.ui.start_screen_activity.create_password_screen.model.ForgotPasswordPassword
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun registerUser(email: String, password: String) : MessageResponse
    suspend fun signInUser(email: String, password: String) : String?
    suspend fun sendEmail(email: String) : MessageResponse?

    suspend fun sendCode(userCode: UserCode) : MessageResponse?
    suspend fun sendRefreshPassword(forgotPasswordPassword: ForgotPasswordPassword) : MessageResponse?

    val getPairTokensFromDataStore: Flow<PairTokens>
    val getRefreshTokensFromDataStore: Flow<String>
    suspend fun savePairTokens(pairTokens: PairTokens)
    suspend fun deletePairTokensToDataStore()

    suspend fun logout(token: String, senderPhone: String) : MessageResponse?
    suspend fun deleteProfile(token: String) : MessageResponse?
}