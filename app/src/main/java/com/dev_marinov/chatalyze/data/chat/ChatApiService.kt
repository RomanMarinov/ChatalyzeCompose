package com.dev_marinov.chatalyze.data.chat

import com.dev_marinov.chatalyze.data.chat.dto.ChatMessageDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatApiService {
    @POST("sendMessage")
    suspend fun getChatMessage(@Body chatMessageDTO: ChatMessageDTO): Response<List<ChatMessageDTO>>

}