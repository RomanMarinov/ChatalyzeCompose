package com.dev_marinov.chatalyze.data.store.room.local.contacts

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contactsEntity: List<ContactsEntity>)

    @Query("SELECT * FROM contacts WHERE phone_number = :sender")
    fun getContactBySenderPhoneFlow(sender: String): Flow<ContactsEntity>
}