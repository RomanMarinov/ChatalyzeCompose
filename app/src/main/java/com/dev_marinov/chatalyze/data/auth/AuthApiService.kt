package com.dev_marinov.chatalyze.data.auth

import com.dev_marinov.chatalyze.data.auth.dto.ForgotPasswordRequestDTO
import com.dev_marinov.chatalyze.data.auth.dto.MessageResponseDTO
import com.dev_marinov.chatalyze.data.auth.dto.RegisterRequestDTO
import com.dev_marinov.chatalyze.data.auth.dto.SignInRequestDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("register")
    suspend fun registerUser(@Body registerRequestDTO: RegisterRequestDTO) : Response<MessageResponseDTO>

    @POST("signin")
    suspend fun signInUser(@Body signInRequestDTO: SignInRequestDTO): Response<String>

    @POST("password")
    suspend fun sendEmail(@Body forgotPasswordRequestDTO: ForgotPasswordRequestDTO)
}