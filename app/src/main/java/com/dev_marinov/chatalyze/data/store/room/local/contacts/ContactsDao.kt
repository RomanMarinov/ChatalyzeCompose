package com.dev_marinov.chatalyze.data.store.room.local.contacts

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chats_screen.model.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contactsEntity: List<ContactsEntity>)

    @Query("DELETE FROM contacts")
    suspend fun deleteAll()

    @Query("SELECT * FROM contacts")
    fun getAllFlow(): Flow<List<ContactsEntity>>

    @Query("SELECT * FROM contacts WHERE phone_number = :sender")
    suspend fun getContactBySenderPhoneFlow(sender: String): ContactsEntity
}