package com.dev_marinov.chatalyze.data.store.room

import com.dev_marinov.chatalyze.data.chatMessage.dto.OnlineUserState
import com.dev_marinov.chatalyze.data.store.room.local.contacts.ContactsDao
import com.dev_marinov.chatalyze.data.store.room.local.contacts.ContactsEntity
import com.dev_marinov.chatalyze.data.store.room.local.online_users.OnlineUsersDao
import com.dev_marinov.chatalyze.data.store.room.local.online_users.OnlineUsersEntity
import com.dev_marinov.chatalyze.domain.repository.RoomRepository
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chats_screen.model.Contact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomRepositoryImpl @Inject constructor(
    private val contactsDao: ContactsDao,
    private val onlineUsersDao: OnlineUsersDao,
) : RoomRepository {

    override fun contactBySenderPhone(sender: String): Flow<Contact> {
        val response = contactsDao.getContactBySenderPhoneFlow(sender = sender)
        val result = response.map {
            Contact(
                name = it.name,
                phoneNumber = it.phoneNumber,
                photo = it.photo
            )
        }
        return result
    }

    override suspend fun saveContacts(contacts: List<Contact>) {
        val contactsEntity: List<ContactsEntity> = contacts.map { contact ->
            ContactsEntity(
                id = 0,
                name = contact.name,
                phoneNumber = contact.phoneNumber,
                photo = contact.photo
            )
        }
        contactsDao.insert(contactsEntity = contactsEntity)
    }

    override val onlineUserStateList: Flow<List<OnlineUserState>> = fetchOnlineUserStateList()


    override suspend fun saveOnlineUserStateList(onlineUserStateList: List<OnlineUserState>) {
        val onlineUsersEntity: List<OnlineUsersEntity> = onlineUserStateList.map {
            OnlineUsersEntity(
                id = 0,
                userPhone = it.userPhone,
                onlineOrOffline = it.onlineOrOffline
            )
        }

        onlineUsersDao.insert(onlineUsersEntity = onlineUsersEntity)
    }

    private fun fetchOnlineUserStateList(): Flow<List<OnlineUserState>> {
        val onlineUsersEntity: Flow<List<OnlineUsersEntity>> = onlineUsersDao.getAllFlow()

        val onlineUserStateList: Flow<List<OnlineUserState>> = onlineUsersEntity.map {
            it.map { onlineUsersEntity ->
                OnlineUserState(
                    userPhone = onlineUsersEntity.userPhone,
                    onlineOrOffline = onlineUsersEntity.onlineOrOffline
                )
            }
        }
        return onlineUserStateList
    }
}