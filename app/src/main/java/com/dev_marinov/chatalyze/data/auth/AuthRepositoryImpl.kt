package com.dev_marinov.chatalyze.data.auth

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.dev_marinov.chatalyze.data.auth.dto.*
import com.dev_marinov.chatalyze.data.util.PairTokensDTOSerializer
import com.dev_marinov.chatalyze.domain.model.auth.MessageResponse
import com.dev_marinov.chatalyze.domain.model.auth.PairOfTokens
import com.dev_marinov.chatalyze.domain.repository.AuthRepository
import com.dev_marinov.chatalyze.presentation.ui.start_screen_activity.code_screen.model.UserCode
import com.dev_marinov.chatalyze.presentation.ui.start_screen_activity.create_password_screen.model.ForgotPasswordPassword
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.internal.http.HTTP_BAD_REQUEST
import okhttp3.internal.http.HTTP_CONFLICT
import okhttp3.internal.http.HTTP_INTERNAL_SERVER_ERROR
import okhttp3.internal.http.HTTP_OK
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStorePairToken by dataStore("data_store_pair_tokens", PairTokensDTOSerializer)
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val dataStorePairToken: DataStore<PairTokensDTO>,
) : AuthRepository {

    override val getPairOfTokensFromDataStore: Flow<PairOfTokens> = pairTokensMapping()
    override val getRefreshTokensFromDataStore: Flow<String> = getRefreshToken()
    override val getAccessTokensFromDataStore: Flow<String> = getAccessToken()

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
        val response = authApiService.sendEmail(ForgotPasswordEmailDTO(email = email))
        return response.body()?.mapToDomain()

    }

    override suspend fun savePairTokens(pairOfTokens: PairOfTokens) {
        Log.d("4444", " savePairTokens сохранена новая пара токенов pairOfTokens=" + pairOfTokens)
        dataStorePairToken.updateData {
            it.copy(
                accessToken = pairOfTokens.accessToken,
                refreshToken = pairOfTokens.refreshToken
            )
        }
    }

    override suspend fun logout(token: String, senderPhone: String) : MessageResponse? {

        Log.d("4444", " AuthRepositoryImpl logout token=" + token + " senderPhone=" + senderPhone)
        val response = authApiService.logoutUser(LogoutRequestDTO(refreshToken = token, senderPhone = senderPhone))
        return response.body()?.mapToDomain()
    }

    override suspend fun deleteProfile(token: String): MessageResponse? {
        val response = authApiService.deleteProfile(DeleteProfileDTO(refreshToken = token))
        return response.body()?.mapToDomain()
    }

//    override suspend fun navigateToAuthScreen(navigate: Boolean) {
//        edwe
//    }

    override suspend fun deletePairTokensToDataStore() {
        dataStorePairToken.updateData {
            it.copy(
                accessToken = "",
                refreshToken = ""
            )
        }
    }

    override suspend fun sendCode(userCode: UserCode): MessageResponse? {
        val response = authApiService.sendCode(ForgotPasswordCodeDTO(
            email = userCode.email,
            code = userCode.code
        ))
        return response.body()?.mapToDomain()
    }

    override suspend fun sendRefreshPassword(forgotPasswordPassword: ForgotPasswordPassword): MessageResponse? {
        val forgotPasswordPasswordDTO = ForgotPasswordPasswordDTO(
            email = forgotPasswordPassword.email,
            password = forgotPasswordPassword.password
        )
        val response = authApiService.sendPassword(forgotPasswordPasswordDTO = forgotPasswordPasswordDTO)
        return response.body()?.mapToDomain()
    }

    private fun pairTokensMapping() : Flow<PairOfTokens> {
        return dataStorePairToken.data.map {
            it.mapToDomain()
        }
    }

    private fun getRefreshToken() : Flow<String> {
        return dataStorePairToken.data.map {
            it.refreshToken
        }
    }

    private fun getAccessToken() : Flow<String> {
        return dataStorePairToken.data.map {
            it.accessToken
        }
    }
}