package com.dev_marinov.chatalyze.di

import com.dev_marinov.chatalyze.data.chatMessage.ChatSocketRepository
import com.dev_marinov.chatalyze.data.socket_service.SocketService

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.features.websocket.*
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(Logging)
            install(WebSockets)
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }
    }

//    @Provides
//    @Singleton
//    fun provideMessageService(client: HttpClient): MessageRepository {
//        return MessageRepositoryImpl(client)
//    }



    @Provides
    @Singleton
    fun provideChatSocketService(socketService: SocketService): ChatSocketRepository {
        return socketService
    }
}