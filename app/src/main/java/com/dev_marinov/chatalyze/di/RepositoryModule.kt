package com.dev_marinov.chatalyze.di

import com.dev_marinov.chatalyze.data.auth.AuthRepositoryImpl
import com.dev_marinov.chatalyze.data.call.remote.CallRepositoryImpl
import com.dev_marinov.chatalyze.data.chat.ChatRepositoryImpl
import com.dev_marinov.chatalyze.domain.repository.ChatSocketRepository
import com.dev_marinov.chatalyze.data.chatMessage.ChatSocketRepositoryImpl
import com.dev_marinov.chatalyze.data.chats.ChatsRepositoryImpl

import com.dev_marinov.chatalyze.domain.repository.AuthRepository
import com.dev_marinov.chatalyze.domain.repository.CallRepository
import com.dev_marinov.chatalyze.domain.repository.ChatRepository
import com.dev_marinov.chatalyze.domain.repository.ChatsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindChatsRepository(chatsRepositoryImpl: ChatsRepositoryImpl): ChatsRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(chatRepositoryImpl: ChatRepositoryImpl) : ChatRepository

    @Binds
    @Singleton
    abstract fun bindChatSocketRepository(chatSocketRepositoryImpl: ChatSocketRepositoryImpl) : ChatSocketRepository

    @Binds
    @Singleton
    abstract fun bindCallRepository(callRepositoryImpl: CallRepositoryImpl) : CallRepository
}