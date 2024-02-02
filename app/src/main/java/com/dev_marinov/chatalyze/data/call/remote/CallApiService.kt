package com.dev_marinov.chatalyze.data.call.remote

import com.dev_marinov.chatalyze.data.auth.dto.MessageResponseDTO
import com.dev_marinov.chatalyze.data.call.dto.FirebaseCommandDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CallApiService {
    @POST("firebase_command")
    suspend fun sendCommandToFirebase(@Body firebaseCommandDTO: FirebaseCommandDTO) : Response<MessageResponseDTO>
}