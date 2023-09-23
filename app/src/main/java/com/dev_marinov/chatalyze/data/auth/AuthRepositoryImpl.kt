package com.dev_marinov.chatalyze.data.auth

import android.util.Log
import com.dev_marinov.chatalyze.data.auth.dto.ForgotPasswordRequest
import com.dev_marinov.chatalyze.data.auth.dto.RegisterRequest
import com.dev_marinov.chatalyze.data.auth.dto.SignInRequest
import com.dev_marinov.chatalyze.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService
) : AuthRepository {

    override suspend fun registerUser(login: String, password: String, email: String) {
        val response = authApiService.registerUser(
            RegisterRequest(
            login = login,
            password = password,
            email = email
        )
        )

        when (response.code()) {
            200 -> {
                Log.d("4444", " registerUser 200 response.body()=" + response.body())
            }
            400 -> {
                Log.d("4444", " registerUser 400 response.body()=" + response.body())
            }
            403 -> {
                Log.d("4444", " registerUser 403 response.body()=" + response.body())
            }
            404 -> {
                Log.d("4444", " registerUser 404 response.body()=" + response.body())
            }

        }

    }

    override suspend fun signInUser(login: String, password: String) {
        val response = authApiService.signInUser(
            SignInRequest(
                login = login, password = password
            )
        )
        when (response.code()) {
            200 -> {
                Log.d("4444", " signInUser 200 response.body()=" + response.body())
            }
            400 -> {
                Log.d("4444", " signInUser 400 response.body()=" + response.body())
            }
            403 -> {
                Log.d("4444", " signInUser 403 response.body()=" + response.body())
            }
            404 -> {
                Log.d("4444", " signInUser 404 response.body()=" + response.body())
            }
        }
    }

    override suspend fun sendEmail(email: String) {
        authApiService.sendEmail(ForgotPasswordRequest(email = email))
    }
}