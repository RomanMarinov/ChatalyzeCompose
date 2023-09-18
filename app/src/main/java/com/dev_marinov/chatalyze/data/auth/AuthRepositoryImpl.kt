package com.dev_marinov.chatalyze.data.auth

import android.util.Log
import com.dev_marinov.chatalyze.data.ApiService
import com.dev_marinov.chatalyze.data.model.auth.RegisterRequest
import com.dev_marinov.chatalyze.data.model.auth.SignInRequest
import com.dev_marinov.chatalyze.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AuthRepository {

    override suspend fun registerUser(login: String, password: String, email: String) {
        val response = apiService.registerUser(RegisterRequest(
            login = login,
            password = password,
            email = email
        ))

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
        val response = apiService.signInUser(
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
}