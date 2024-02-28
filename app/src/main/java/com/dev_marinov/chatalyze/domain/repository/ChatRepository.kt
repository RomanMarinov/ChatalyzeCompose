package com.dev_marinov.chatalyze.domain.repository

import com.dev_marinov.chatalyze.domain.model.auth.MessageResponse
import com.dev_marinov.chatalyze.domain.model.chat.ChatCompanion
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chats_screen.model.Contact

interface ChatRepository {
    suspend fun getChatMessage() : List<Contact>
    suspend fun saveCompanionOnTheServer(chatCompanion: ChatCompanion): MessageResponse?
}