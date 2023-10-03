package com.dev_marinov.chatalyze.data.auth

import android.util.Log
import com.dev_marinov.chatalyze.data.auth.dto.ForgotPasswordRequestDTO
import com.dev_marinov.chatalyze.data.auth.dto.RegisterRequestDTO
import com.dev_marinov.chatalyze.data.auth.dto.SignInRequestDTO
import com.dev_marinov.chatalyze.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import okhttp3.internal.http.HTTP_BAD_REQUEST
import okhttp3.internal.http.HTTP_CONFLICT
import okhttp3.internal.http.HTTP_INTERNAL_SERVER_ERROR
import okhttp3.internal.http.HTTP_OK
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService
) : AuthRepository {

    override suspend fun registerUser(email: String, password: String): String? {
        val response = authApiService.registerUser(
            RegisterRequestDTO(
                email = email,
                password = password
            )
        )
        Log.d("4444", " response.body().httpStatusCode=" + response.body()?.httpStatusCode)
        Log.d("4444", " response.body().message=" + response.body()?.message)
        Log.d("4444", " response.message()=" + response.message())
        Log.d("4444", " response.body()=" + response.body())
        // return response.message()

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

    private fun responseBody(response: Response<String>): String {
        val responseBody = response.body()
        return responseBody.toString()
    }

    override suspend fun signInUser(email: String, password: String) : String? {
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

    override suspend fun sendEmail(email: String) {
        authApiService.sendEmail(ForgotPasswordRequestDTO(email = email))
    }
}