package com.dev_marinov.chatalyze.data.auth

import com.dev_marinov.chatalyze.data.auth.dto.ForgotPasswordRequest
import com.dev_marinov.chatalyze.data.auth.dto.RegisterRequest
import com.dev_marinov.chatalyze.data.auth.dto.SignInRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("register")
    suspend fun registerUser(@Body registerRequest: RegisterRequest): Response<String>

    @POST("login")
    suspend fun signInUser(@Body signInRequest: SignInRequest): Response<String>

    @POST("password")
    suspend fun sendEmail(@Body forgotPasswordRequest: ForgotPasswordRequest)
}