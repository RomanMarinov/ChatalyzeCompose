package com.dev_marinov.chatalyze.di

import android.content.Context
import androidx.room.Room.databaseBuilder
import com.dev_marinov.chatalyze.data.call.local.HistoryCallsDao
import com.dev_marinov.chatalyze.data.store.room.AppDatabase
import com.dev_marinov.chatalyze.data.store.room.local.contacts.ContactsDao
import com.dev_marinov.chatalyze.data.store.room.local.online_users.OnlineUsersDao
import com.dev_marinov.chatalyze.data.store.room.local.ready_stream.ReadyStreamDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DBModule {

    @Provides
    @Singleton
    fun provideContactsDao(appDatabase: AppDatabase): ContactsDao = appDatabase.contactsDao()

    @Provides
    @Singleton
    fun provideOnlineUsersDao(appDatabase: AppDatabase): OnlineUsersDao = appDatabase.onlineUsersDao()

    @Provides
    @Singleton
    fun provideReadyStreamDao(appDatabase: AppDatabase): ReadyStreamDao = appDatabase.readyStreamDao()

    @Provides
    @Singleton
    fun provideHistoryCallsDao(appDatabase: AppDatabase): HistoryCallsDao = appDatabase.historyCallsDao()


    @Provides
    @Singleton
    fun provideDataBase(@ApplicationContext context: Context): AppDatabase {
        return databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.NAME
        ).fallbackToDestructiveMigration().build()
    }
}