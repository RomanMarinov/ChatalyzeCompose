package com.dev_marinov.chatalyze.data.store.room

import android.util.Log
import com.dev_marinov.chatalyze.data.chatMessage.dto.OnlineUserState
import com.dev_marinov.chatalyze.data.firebase.model.StateReadyStream
import com.dev_marinov.chatalyze.data.store.room.local.contacts.ContactsDao
import com.dev_marinov.chatalyze.data.store.room.local.contacts.ContactsEntity
import com.dev_marinov.chatalyze.data.store.room.local.online_users.OnlineUsersDao
import com.dev_marinov.chatalyze.data.store.room.local.online_users.OnlineUsersEntity
import com.dev_marinov.chatalyze.data.store.room.local.ready_stream.ReadyStreamDao
import com.dev_marinov.chatalyze.data.store.room.local.ready_stream.ReadyStreamEntity
import com.dev_marinov.chatalyze.domain.repository.RoomRepository
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chats_screen.model.Contact
import com.dev_marinov.chatalyze.presentation.util.EditFormatPhoneHelper
import com.dev_marinov.chatalyze.presentation.util.IfLetHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomRepositoryImpl @Inject constructor(
    private val contactsDao: ContactsDao,
    private val onlineUsersDao: OnlineUsersDao,
    private val readyStreamDao: ReadyStreamDao,
) : RoomRepository {

    override val filteredContacts: Flow<List<Contact>> = fetchFilteredContacts()

    override suspend fun contactBySenderPhone(sender: String): Contact {
        val response = contactsDao.getContactBySenderPhoneFlow(sender = sender)
        return Contact(
            name = response?.name?.let { it }.run { "" },
            phoneNumber = response.phoneNumber,
            photo = response.photo
        )
    }

    override suspend fun saveContacts(contacts: List<Contact>) {
        val contactsEntity: List<ContactsEntity> = contacts.mapIndexed { index, contact ->
            ContactsEntity(
                id = index,
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

    override val getReadyStream: Flow<StateReadyStream> = fetchReadyStream()

    override suspend fun saveStateReadyStream(
        senderPhone: String,
        recipientPhone: String,
        typeFirebaseCommand: String,
    ) {
        val readyStreamEntity = ReadyStreamEntity(
            id = 0,
            senderPhone = senderPhone,
            recipientPhone = recipientPhone,
            typeFirebaseCommand = typeFirebaseCommand
        )
        readyStreamDao.insert(readyStreamEntity = readyStreamEntity)
        delay(1000L)
        readyStreamDao.delete()
    }

    private fun fetchReadyStream(): Flow<StateReadyStream> {
        val readyStreamEntityFlow = readyStreamDao.getReadyStream()
        return readyStreamEntityFlow.map {
            IfLetHelper.execute(it?.senderPhone, it?.recipientPhone, it?.typeFirebaseCommand) { listArg ->
                StateReadyStream(
                    senderPhone = listArg[0],
                    recipientPhone = listArg[1],
                    typeFirebaseCommand = listArg[2]
                )
            } ?: StateReadyStream("", "", "")
        }
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

    private fun fetchFilteredContacts(): Flow<List<Contact>> {
        val filteredContacts: Flow<List<Contact>> = contactsDao.getAllFlow().map {
            it.map { contactsEntity ->
                Contact(
                    name = contactsEntity.name,
                    phoneNumber = contactsEntity.phoneNumber,
                    photo = contactsEntity.photo
                )
            }
        }
        return filteredContacts
    }
}