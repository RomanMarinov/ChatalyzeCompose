package com.dev_marinov.chatalyze.data.chat

import com.dev_marinov.chatalyze.data.auth.dto.MessageResponseDTO
import com.dev_marinov.chatalyze.data.call.dto.FirebaseCommandDTO
import com.dev_marinov.chatalyze.data.chat.dto.ChatCompanionDTO
import com.dev_marinov.chatalyze.data.chat.dto.ChatMessageDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatApiService {
    @POST("sendMessage")
    suspend fun getChatMessage(@Body chatMessageDTO: ChatMessageDTO): Response<List<ChatMessageDTO>>

    @POST("chat_companion")
    suspend fun sendChatCompanion(@Body body: ChatCompanionDTO): Response<MessageResponseDTO>

    @POST("firebase_command")
    suspend fun sendCommandToFirebase(@Body firebaseCommandDTO: FirebaseCommandDTO) : Response<MessageResponseDTO>
}