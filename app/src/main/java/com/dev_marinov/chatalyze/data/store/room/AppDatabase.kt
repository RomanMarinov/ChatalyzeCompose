package com.dev_marinov.chatalyze.data.store.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dev_marinov.chatalyze.data.call.local.HistoryCallsDao
import com.dev_marinov.chatalyze.data.call.local.HistoryCallsEntity
import com.dev_marinov.chatalyze.data.store.room.local.contacts.ContactsEntity
import com.dev_marinov.chatalyze.data.store.room.local.contacts.ContactsDao
import com.dev_marinov.chatalyze.data.store.room.local.online_users.OnlineUsersDao
import com.dev_marinov.chatalyze.data.store.room.local.online_users.OnlineUsersEntity
import com.dev_marinov.chatalyze.data.store.room.local.ready_stream.ReadyStreamDao
import com.dev_marinov.chatalyze.data.store.room.local.ready_stream.ReadyStreamEntity
import com.dev_marinov.chatalyze.data.util.DataConvertersForList

@Database(
    entities = [
        ContactsEntity::class,
        OnlineUsersEntity::class,
        ReadyStreamEntity::class,
        HistoryCallsEntity::class
               ],
    version = 1
)

@TypeConverters(DataConvertersForList::class)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        const val NAME = "chatalyze_database"
    }

    abstract fun contactsDao(): ContactsDao
    abstract fun onlineUsersDao(): OnlineUsersDao
    abstract fun readyStreamDao(): ReadyStreamDao
    abstract fun historyCallsDao() : HistoryCallsDao
}