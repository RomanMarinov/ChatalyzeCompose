package com.dev_marinov.chatalyze.data.chats

import com.dev_marinov.chatalyze.data.chats.dto.ChatDTO
import com.dev_marinov.chatalyze.data.chats.dto.SenderDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatsApiService {
    @POST("chats")
    suspend fun getChats(@Body senderDTO: SenderDTO) : Response<List<ChatDTO>>
}