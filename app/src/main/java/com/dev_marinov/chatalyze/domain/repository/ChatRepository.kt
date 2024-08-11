package com.dev_marinov.chatalyze.domain.repository

import com.dev_marinov.chatalyze.domain.model.auth.MessageResponse
import com.dev_marinov.chatalyze.domain.model.chat.ChatCompanion

interface ChatRepository {
    suspend fun saveCompanionOnTheServer(chatCompanion: ChatCompanion): MessageResponse?
}