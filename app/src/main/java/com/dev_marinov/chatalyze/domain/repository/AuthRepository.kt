package com.dev_marinov.chatalyze.domain.repository

import com.dev_marinov.chatalyze.domain.model.auth.MessageResponse
import com.dev_marinov.chatalyze.domain.model.auth.PairTokens
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
}