package com.dev_marinov.chatalyze.domain.repository

import com.dev_marinov.chatalyze.domain.model.chats.Chat

interface ChatsRepository {
    suspend fun getChats(sender: String, refreshToken: String): List<Chat>
}