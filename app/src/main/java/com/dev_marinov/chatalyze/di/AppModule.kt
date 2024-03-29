package com.dev_marinov.chatalyze.di

import android.content.Context
import com.dev_marinov.chatalyze.data.firebase.notification.PushNotificationManagerImpl
import com.dev_marinov.chatalyze.domain.repository.RoomRepository
import com.dev_marinov.chatalyze.domain.repository.PushNotificationManager
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json

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
            install(ContentNegotiation) {
                json()
            }
        }
    }


    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context,
        preferencesDataStoreRepository: PreferencesDataStoreRepository,
        roomRepository: RoomRepository): PushNotificationManager {
        return PushNotificationManagerImpl(context, preferencesDataStoreRepository, roomRepository)
    }

//
//    @Provides
//    fun provideMyViewModel(activity: MainScreensActivity): MainScreensViewModel {
//        return ViewModelProvider(activity)[MainScreensViewModel::class.java]
//    }
}