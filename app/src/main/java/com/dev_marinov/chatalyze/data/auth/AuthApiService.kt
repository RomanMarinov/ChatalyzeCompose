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

    @POST("forgot_password/email")
    suspend fun sendEmail(@Body forgotPasswordEmailDTO: ForgotPasswordEmailDTO): Response<MessageResponseDTO>

    @POST("forgot_password/code")
    suspend fun sendCode(@Body forgotPasswordCodeDTO: ForgotPasswordCodeDTO): Response<MessageResponseDTO>

    @POST("forgot_password/password")
    suspend fun sendPassword(@Body forgotPasswordPasswordDTO: ForgotPasswordPasswordDTO): Response<MessageResponseDTO>

    @POST("delete_profile")
    suspend fun deleteProfile(@Body deleteProfileDTO: DeleteProfileDTO): Response<MessageResponseDTO>
}