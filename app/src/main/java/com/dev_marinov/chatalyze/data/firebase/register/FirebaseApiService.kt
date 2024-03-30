package com.dev_marinov.chatalyze.data.firebase.register

import com.dev_marinov.chatalyze.data.auth.dto.MessageResponseDTO
import com.dev_marinov.chatalyze.data.firebase.register.dto.UserFirebaseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FirebaseApiService {
    @POST("firebase_register")
    suspend fun firebaseRegister(@Body userFirebaseDTO: UserFirebaseDTO) : Response<MessageResponseDTO>
}