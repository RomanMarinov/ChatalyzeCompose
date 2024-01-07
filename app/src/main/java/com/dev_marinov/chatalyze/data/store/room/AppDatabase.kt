package com.dev_marinov.chatalyze.data.store.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dev_marinov.chatalyze.data.store.room.local.contacts.ContactsEntity
import com.dev_marinov.chatalyze.data.store.room.local.contacts.ContactsDao
import com.dev_marinov.chatalyze.data.store.room.local.online_users.OnlineUsersDao
import com.dev_marinov.chatalyze.data.store.room.local.online_users.OnlineUsersEntity
import com.dev_marinov.chatalyze.data.util.DataConvertersForList

@Database(
    entities = [ContactsEntity::class, OnlineUsersEntity::class],
    version = 2
)

@TypeConverters(DataConvertersForList::class)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        const val NAME = "chatalyze_database"
    }

    abstract fun contactsDao(): ContactsDao
    abstract fun onlineUsersDao(): OnlineUsersDao
}