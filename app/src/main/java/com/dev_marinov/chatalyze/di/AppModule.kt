package com.dev_marinov.chatalyze.di

import android.app.Application
import android.content.Context
import android.util.Log
import com.dev_marinov.chatalyze.data.firebase.notification.PushNotificationManagerImpl
import com.dev_marinov.chatalyze.domain.repository.AuthRepository
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.domain.repository.PushNotificationManager
import com.dev_marinov.chatalyze.domain.repository.RoomRepository
import com.dev_marinov.chatalyze.presentation.util.ConnectivityObserver
import com.dev_marinov.chatalyze.presentation.util.NetworkObserverHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import javax.inject.Provider
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideConnectivityObserver(application: Application): ConnectivityObserver {
        return NetworkObserverHelper(application)
    }

    @Provides
    @Singleton
    fun provideHttpClient(
        @ApplicationContext context: Context,
        authRepositoryProvider: Provider<AuthRepository>,
        preferencesDataStoreRepositoryProvider: Provider<PreferencesDataStoreRepository>,
    ): HttpClient {
        val httpClient = HttpClient(CIO) {
            install(KtorNetworkInterceptor(context = context,
                preferencesDataStoreRepositoryProvider = preferencesDataStoreRepositoryProvider))
            install(KtorUpdateTokenInterceptor(authRepositoryProvider = authRepositoryProvider,
                preferencesDataStoreRepositoryProvider = preferencesDataStoreRepositoryProvider))
            install(KtorAuthInterceptor(authRepositoryProvider = authRepositoryProvider,
                preferencesDataStoreRepositoryProvider = preferencesDataStoreRepositoryProvider))
            install(ResponseObserver) {
                onResponse { response ->
                    Log.d("4444", " ResponseObserver HTTP onResponse=" + response.status.value)
                }
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("HTTP Client", message)
                    }
                }
                level = LogLevel.ALL
            }
            install(WebSockets)
            install(ContentNegotiation) {
                json()
            }

            install(HttpTimeout) {
                socketTimeoutMillis = 100000
                requestTimeoutMillis = 100000
                connectTimeoutMillis = 100000
            }
        }
        return httpClient
    }

    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context,
        preferencesDataStoreRepository: PreferencesDataStoreRepository,
        roomRepository: RoomRepository,
    ): PushNotificationManager {
        return PushNotificationManagerImpl(context, preferencesDataStoreRepository, roomRepository)
    }
}