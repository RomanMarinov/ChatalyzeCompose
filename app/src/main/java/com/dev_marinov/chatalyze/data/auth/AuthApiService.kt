package com.dev_marinov.chatalyze.data.auth

import com.dev_marinov.chatalyze.data.auth.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("register")
    suspend fun registerUser(@Body registerRequestDTO: RegisterRequestDTO) : Response<MessageResponseDTO>

    @POST("signin")
    suspend fun signInUser(@Body signInRequestDTO: SignInRequestDTO): Response<MessageResponseDTO>

    @POST("logout")
    suspend fun logoutUser(@Body logoutRequestDTO: LogoutRequestDTO): Response<MessageResponseDTO>

    @POST("password")
    suspend fun sendEmail(@Body forgotPasswordRequestDTO: ForgotPasswordRequestDTO): Response<MessageResponseDTO>

    @POST("delete_profile")
    suspend fun deleteProfile(@Body deleteProfileDTO: DeleteProfileDTO): Response<MessageResponseDTO>
}