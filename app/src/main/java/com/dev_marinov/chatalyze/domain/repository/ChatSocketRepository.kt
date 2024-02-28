package com.dev_marinov.chatalyze.domain.repository

import com.dev_marinov.chatalyze.BuildConfig
import com.dev_marinov.chatalyze.data.chatMessage.dto.MessageWrapper
import com.dev_marinov.chatalyze.domain.model.auth.MessageResponse
import com.dev_marinov.chatalyze.domain.model.chat.Message
import com.dev_marinov.chatalyze.domain.model.chat.MessageToSend
import com.dev_marinov.chatalyze.domain.model.chat.UserPairChat
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.call_screen.model.FirebaseCommand
import com.dev_marinov.chatalyze.presentation.util.Resource
import kotlinx.coroutines.flow.Flow

interface ChatSocketRepository {

    suspend fun initSession(sender: String): Resource<Unit>
    suspend fun closeSession()

    suspend fun sendMessage(messageToSend: MessageToSend)
    suspend fun getAllMessages(userPairChat: UserPairChat): List<Message>
    fun observeMessages(): Flow<MessageWrapper>

    suspend fun sendCommandToFirebase(firebaseCommand: FirebaseCommand): MessageResponse?

    sealed class Endpoints(val url: String) {
        object SocketChat : Endpoints("${BuildConfig.API_URL_WS}/chatsocket")
        object GetAllMessages: Endpoints("${BuildConfig.API_URL_HTTP}/messages")
    }
}