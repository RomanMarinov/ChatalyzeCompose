package com.dev_marinov.chatalyze.data.auth

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.dev_marinov.chatalyze.data.auth.dto.*
import com.dev_marinov.chatalyze.data.util.PairTokensDTOSerializer
import com.dev_marinov.chatalyze.domain.model.auth.MessageResponse
import com.dev_marinov.chatalyze.domain.model.auth.PairTokens
import com.dev_marinov.chatalyze.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.internal.http.HTTP_BAD_REQUEST
import okhttp3.internal.http.HTTP_CONFLICT
import okhttp3.internal.http.HTTP_INTERNAL_SERVER_ERROR
import okhttp3.internal.http.HTTP_OK
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore by dataStore("data_store_pair_tokens", PairTokensDTOSerializer)

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val dataStore: DataStore<PairTokensDTO>
) : AuthRepository {

    override val getPairTokensFromDataStore: Flow<PairTokens> = pairTokensMapping()
    override val getRefreshTokensFromDataStore: Flow<String> = getRefreshToken()

    override suspend fun registerUser(email: String, password: String): MessageResponse {
        var statusCode = 0
        var message = ""

        val response = authApiService.registerUser(
            RegisterRequestDTO(
                email = email,
                password = password
            )
        )

        response.body()?.let {
            statusCode = it.httpStatusCode
            message = it.message
        }
        return MessageResponse(statusCode, message)
    }

    override suspend fun signInUser(email: String, password: String): String? {
        val response = authApiService.signInUser(
            SignInRequestDTO(
                email = email, password = password
            )
        )
        return when (response.code()) {
            HTTP_OK -> {
                response.body()?.message
            }
            HTTP_CONFLICT -> {
                response.body()?.message
            }
            HTTP_BAD_REQUEST -> {
                response.body()?.message
            }
            HTTP_INTERNAL_SERVER_ERROR -> {
                response.body()?.message
            }
            else -> {
                Log.d("4444", " jbkbkbkbkbkbkbkbkbkbkb")
                ""
            }

        }
    }

    override suspend fun sendEmail(email: String) : MessageResponse? {
        val response = authApiService.sendEmail(ForgotPasswordRequestDTO(email = email))
        return response.body()?.mapToDomain()

    }

    override suspend fun savePairTokens(pairTokens: PairTokens) {
        dataStore.updateData {
            it.copy(
                accessToken = pairTokens.accessToken,
                refreshToken = pairTokens.refreshToken
            )
        }
    }

    override suspend fun logout(token: String) : MessageResponse? {
        val response = authApiService.logoutUser(LogoutRequestDTO(refreshToken = token))
        return response.body()?.mapToDomain()
    }

    override suspend fun deleteProfile(token: String): MessageResponse? {
        val response = authApiService.deleteProfile(DeleteProfileDTO(refreshToken = token))
        return response.body()?.mapToDomain()
    }

    override suspend fun deletePairTokensToDataStore() {
        dataStore.updateData {
            it.copy(
                accessToken = "",
                refreshToken = ""
            )
        }
    }

    private fun pairTokensMapping() : Flow<PairTokens> {
        return dataStore.data.map {
            it.mapToDomain()
        }
    }

    private fun getRefreshToken() : Flow<String> {
        return dataStore.data.map {
            it.refreshToken
        }
    }
}