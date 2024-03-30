package com.dev_marinov.chatalyze.data.chats

import android.util.Log
import com.dev_marinov.chatalyze.data.chats.dto.ChatDTO
import com.dev_marinov.chatalyze.data.chats.dto.SenderDTO
import com.dev_marinov.chatalyze.domain.model.chats.Chat
import com.dev_marinov.chatalyze.domain.model.chats.Sender
import com.dev_marinov.chatalyze.domain.repository.ChatsRepository
import com.dev_marinov.chatalyze.presentation.util.NavigateToAuthScreenHelper
import okhttp3.internal.http.HTTP_FORBIDDEN
import okhttp3.internal.http.HTTP_OK
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ChatsRepositoryImpl @Inject constructor(
    private val chatsApiService: ChatsApiService
) : ChatsRepository {

    override suspend fun getChats(sender: String, refreshToken: String): List<Chat> {
        val response = chatsApiService.getChats(senderDTO = SenderDTO(sender = sender, refreshToken = refreshToken))
        return response.body()?.map {
            it.mapToDomain()
        } ?: listOf()
    }

//    override suspend fun getChats(sender: String, refreshToken: String): List<Chat> {
//        //val senderDTO = Sender(sender = sender).mapToData()
//        val response = chatsApiService.getChats(senderDTO = SenderDTO(sender = sender, refreshToken = refreshToken))
//
//        if (response.isSuccessful) {
//            when(response.code()) {
//                HTTP_OK -> {
//                    return response.body()?.map {
//                        it.mapToDomain()
//                    } ?: listOf()
//                }
//                HTTP_FORBIDDEN -> {
//                    //короче перенести во вью модель
//                  //  NavigateToAuthScreenHelper.execute(context = ?)
//                }
//                else -> { }
//            }
//        }
//
//
//
//       // Log.d("4444", " response=" + response.body())
//
//    }
}