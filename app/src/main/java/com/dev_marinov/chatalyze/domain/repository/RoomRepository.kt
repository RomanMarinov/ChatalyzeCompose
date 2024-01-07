package com.dev_marinov.chatalyze.domain.repository

import com.dev_marinov.chatalyze.data.chatMessage.dto.OnlineUserState
import com.dev_marinov.chatalyze.data.store.room.local.online_users.OnlineUsersEntity
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chats_screen.model.Contact
import kotlinx.coroutines.flow.Flow

interface RoomRepository {
    fun contactBySenderPhone(sender: String): Flow<Contact>
    suspend fun saveContacts(contacts: List<Contact>)

    val onlineUserStateList: Flow<List<OnlineUserState>>
    suspend fun saveOnlineUserStateList(onlineUserStateList: List<OnlineUserState>)
}